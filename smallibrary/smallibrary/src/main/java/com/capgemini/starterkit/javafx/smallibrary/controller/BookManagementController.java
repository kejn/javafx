package com.capgemini.starterkit.javafx.smallibrary.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.capgemini.starterkit.javafx.smallibrary.dataprovider.DataProvider;
import com.capgemini.starterkit.javafx.smallibrary.dataprovider.data.BookVO;
import com.capgemini.starterkit.javafx.smallibrary.dataprovider.data.ErrorVO;
import com.capgemini.starterkit.javafx.smallibrary.dataprovider.impl.DataProviderImpl;
import com.capgemini.starterkit.javafx.smallibrary.model.BookManagement;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Controller for book management.
 *
 * @author KNIEMCZY
 */
public class BookManagementController {

	private static final Logger LOG = Logger.getLogger(BookManagementController.class);

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private GridPane searchAddForm;

	@FXML
	private TextField titleField;

	@FXML
	private TextField authorsField;

	@FXML
	private Button searchButton;

	@FXML
	private Button addButton;

	@FXML
	private Button deleteButton;

	@FXML
	private Label progressLabel;

	@FXML
	private TableView<BookVO> resultTable;

	@FXML
	private TableColumn<BookVO, String> titleColumn;

	@FXML
	private TableColumn<BookVO, String> authorsColumn;

	private final DataProvider dataProvider = DataProvider.INSTANCE;

	private final BookManagement model = new BookManagement();

	public BookManagementController() {
	}

	@FXML
	private void initialize() {
		initializeForm();
		initializeResultTable();
		LOG.debug("initializing -- finished");
	}

	/**
	 * Sets bindings to text fields and buttons corresponding to application
	 * form.
	 */
	private void initializeForm() {
		titleField.textProperty().bindBidirectional(model.titleProperty());
		authorsField.textProperty().bindBidirectional(model.authorProperty());
		searchButton.disableProperty().bind(titleField.textProperty().isEmpty());
		addButton.disableProperty()
				.bind(Bindings.or(titleField.textProperty().isEmpty(), authorsField.textProperty().isEmpty()));
		deleteButton.disableProperty().bind(resultTable.selectionModelProperty().get().selectedItemProperty().isNull());
		progressLabel.setVisible(false);
		LOG.debug("form initialized");
	}

	/**
	 * Sets binding between result table and model.
	 */
	private void initializeResultTable() {
		titleColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTitle()));
		authorsColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAuthors()
				.stream().map(author -> author.getFirstName() + " " + author.getLastName())
				.collect(Collectors.joining(",\n"))));
		resultTable.setPlaceholder(new Label(resources.getString("table.emptyText")));
		resultTable.itemsProperty().bind(model.resultProperty());
		LOG.debug("resultTable initialized");
	}

	/**
	 * The JavaFX runtime calls this method when the <b>Search</b> button is
	 * clicked. Shows label informing user about pending operation.
	 */
	@FXML
	public void searchButtonAction() {
		Task<Collection<BookVO>> backgroundTask = new Task<Collection<BookVO>>() {

			/**
			 * Calls {@link DataProviderImpl#findBooks(String)} in task thread.
			 *
			 * @return Collection of books from server database matching
			 *         <b>title</b> provided by user.
			 */
			@Override
			protected Collection<BookVO> call() throws Exception {
				LOG.debug("searchButtonAction call() called");
				Collection<BookVO> result = dataProvider.findBooks(model.getTitle());
				return result;
			}

			/**
			 * Hides label with progress message. If obtained result is not null
			 * it is parsed into the result table.
			 */
			@Override
			protected void succeeded() {
				LOG.debug("searchButtonAction succeeded() called");
				resetProgressMessage();
				if (getValue() == null) {
					return;
				}
				model.setResult(new ArrayList<BookVO>(getValue()));
				resultTable.getSortOrder().clear();
			}

			/**
			 * Hides label with progress message and pops up Alert describing
			 * the error.
			 */
			@Override
			protected void failed() {
				LOG.debug("searchButtonAction failed() called");
				resetProgressMessage();
				showErrorAlert(ErrorVO.SEARCH, resources.getString("error.server"));
			}

		};

		setProgressMessage(resources.getString("label.searching"), true);
		Thread thread = new Thread(backgroundTask);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * The JavaFX runtime calls this method when the <b>Add</b> button is
	 * clicked. Shows label informing user about pending operation.
	 */
	@FXML
	public void addButtonAction() {
		Task<BookVO> backgroundTask = new Task<BookVO>() {

			/**
			 * Calls {@link DataProviderImpl#addBook(String,Collection)} in task
			 * thread.
			 *
			 * @return BookVO object which was added to the server database.
			 */
			@Override
			protected BookVO call() throws Exception {
				LOG.debug("addButtonAction call() called");
				BookVO result = dataProvider.addBook(model.getTitle(), Arrays.asList(model.getAuthor().split(",")));
				return result;
			}

			/**
			 * Hides label with progress message. If obtained result is not null
			 * it is appended to the result table omitting unnecessary server
			 * call.
			 */
			@Override
			protected void succeeded() {
				LOG.debug("addButtonAction succeeded() called");
				resetProgressMessage();
				List<BookVO> books = model.getResult();
				if (getValue() == null) {
					return;
				}
				books.add(new BookVO(getValue()));
			}

			/**
			 * Hides label with progress message and pops up Alert describing
			 * the error.
			 */
			@Override
			protected void failed() {
				LOG.debug("addButtonAction failed() called");
				resetProgressMessage();
				showErrorAlert(ErrorVO.ADD, resources.getString("error.server"));
			}

		};

		setProgressMessage(resources.getString("label.adding"), true);
		Thread thread = new Thread(backgroundTask);
		thread.setDaemon(true);
		thread.start();
	}

	@FXML
	public void deleteButtonAction() {
		BookVO bookToDelete = resultTable.getSelectionModel().getSelectedItem();
		Task<Void> backgroundTask = new Task<Void>() {

			/**
			 * Calls {@link DataProviderImpl#deleteBook(Long)} in task thread.
			 */
			@Override
			protected Void call() throws Exception {
				LOG.debug("deleteButtonAction call() called");
				dataProvider.deleteBook(bookToDelete.getId());
				return null;
			}

			/**
			 * Hides label with progress message. Deletes selected book from the
			 * result table omitting unnecessary server call.
			 */
			@Override
			protected void succeeded() {
				LOG.debug("deleteButtonAction succeeded() called");
				resetProgressMessage();
				List<BookVO> result = model.getResult();
				result.remove(bookToDelete);
				resultTable.getSelectionModel().clearSelection();
			}

			/**
			 * Hides label with progress message and pops up Alert describing
			 * the error.
			 */
			@Override
			protected void failed() {
				LOG.debug("deleteButtonAction failed() called");
				resetProgressMessage();
				if (getException() instanceof IOException) {
					showErrorAlert(ErrorVO.DELETE, resources.getString("error.server"));
				} else {
					showErrorAlert(ErrorVO.DELETE, getException().getMessage());
				}
			}

		};

		setProgressMessage(resources.getString("label.deleting"), true);
		Thread thread = new Thread(backgroundTask);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Sets progress label message and makes it visible or not, depending on
	 * <b>isVisible</b> parameter.
	 *
	 * @param message
	 *            text to be set in progress label
	 * @param isVisible
	 *            if <code>true</code> the message will be shown.
	 */
	private void setProgressMessage(String message, boolean isVisible) {
		progressLabel.setText(message);
		progressLabel.setVisible(isVisible);
	}

	/**
	 * Sets progress label message to <b>null</b> and makes it invisible.
	 */
	private void resetProgressMessage() {
		setProgressMessage(null, false);
	}

	/**
	 * Displays Alert with error message.
	 * 
	 * @param resourceErrorType
	 *            defines type of operation error category
	 * @param moreInformation
	 *            gives more detailed information about the error.
	 */
	private void showErrorAlert(ErrorVO resourceErrorType, String moreInformation) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(resources.getString("error.title"));
		alert.setHeaderText(null);
		alert.setContentText(resources.getString("error." + resourceErrorType) + "\n" + moreInformation);
		alert.showAndWait();
	}
}
