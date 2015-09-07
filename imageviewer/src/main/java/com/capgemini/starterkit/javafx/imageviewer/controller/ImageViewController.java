package com.capgemini.starterkit.javafx.imageviewer.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.capgemini.starterkit.javafx.imageviewer.Startup;
import com.capgemini.starterkit.javafx.imageviewer.dataprovider.DataProvider;
import com.capgemini.starterkit.javafx.imageviewer.dataprovider.data.ImageFileVO;
import com.capgemini.starterkit.javafx.imageviewer.dataprovider.impl.DataProviderImpl;
import com.capgemini.starterkit.javafx.imageviewer.model.ImageSearch;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Controller for viewing images.
 *
 * @author KNIEMCZY
 */
public class ImageViewController {

	private static final Logger LOG = Logger.getLogger(ImageViewController.class);

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	AnchorPane window;

	@FXML
	MenuBar menuBar;

	@FXML
	private MenuItem menuLanguagePolish;

	@FXML
	private MenuItem menuLanguageEnglish;

	@FXML
	private TableColumn<ImageFileVO, String> nameColumn;

	@FXML
	private TableView<ImageFileVO> resultTable;

	@FXML
	private Pane imageBounds;

	@FXML
	private ImageView imageViewPort;

	private final DataProvider dataProvider = DataProvider.INSTANCE;

	private final ImageSearch model = new ImageSearch();

	public ImageViewController() {
	}

	private void setWindowTitle() {
		((Stage) window.getScene().getWindow()).setTitle(windowTitleWithPath());
	}

	@FXML
	private void initialize() {
		initializeMenuLanguage();
		initializeImageViewPort();
		initializeResultTable();
		model.setDirPath(System.getProperty("user.home") + "\\Pictures");
	}

	/**
	 * Disables chosen language. By default English is chosen.
	 */
	private void initializeMenuLanguage() {
		if (Locale.getDefault().equals(Locale.forLanguageTag("PL"))) {
			menuLanguagePolish.setDisable(true);
		} else {
			menuLanguageEnglish.setDisable(true);
		}
	}

	/**
	 * Sets binding to image view port.
	 */
	private void initializeImageViewPort() {
		imageViewPort.fitWidthProperty().bind(imageBounds.widthProperty());
		imageViewPort.fitHeightProperty().bind(imageBounds.heightProperty());
	}

	/**
	 * Sets bindings to result table column.
	 */
	private void initializeResultTable() {
		nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));

		resultTable.itemsProperty().bind(model.resultProperty());
		resultTable.setPlaceholder(new Label(resources.getString("table.emptyText")));
		resultTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		resultTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ImageFileVO>() {
			@Override
			public void changed(ObservableValue<? extends ImageFileVO> observable, ImageFileVO oldValue,
					ImageFileVO newValue) {
				if (observable.getValue() == null) {
					return;
				}
				LOG.debug("Opening image: " + model.getDirPath() + "\\" + newValue.getName());
				Image image = new Image("file:///" + model.getDirPath() + "\\" + newValue.getName());
				imageViewPort.setViewport(new Rectangle2D(0, 0, image.getWidth(), image.getHeight()));
				imageViewPort.setImage(image);
			}
		});
		nameColumn.prefWidthProperty().bind(resultTable.widthProperty().subtract(5));
	}

	/**
	 * Closes application.
	 */
	@FXML
	private void closeAppAction() {
		LOG.debug("Menu > Exit");
		Stage stage = (Stage) window.getScene().getWindow();
		stage.close();
	}

	/**
	 * Selects interface language and reloads FXML file.
	 *
	 * @param event
	 *            MenuItem which was clicked describing language to be loaded.
	 * @throws IOException
	 *             if reloading FXML file fails.
	 */
	@FXML
	private void selectLanguageAction(ActionEvent event) throws IOException {
		MenuItem item = (MenuItem) event.getSource();
		if (item.equals(menuLanguagePolish)) {
			LOG.debug("Language > Polish");
			Locale.setDefault(Locale.forLanguageTag("PL"));
			menuLanguageEnglish.setDisable(false);
			menuLanguagePolish.setDisable(true);
		} else {
			LOG.debug("Language > English");
			Locale.setDefault(Locale.forLanguageTag("EN"));
			menuLanguageEnglish.setDisable(true);
			menuLanguagePolish.setDisable(false);
		}
		window.getScene().setRoot(FXMLLoader.load(getClass().getResource(Startup.FXMLFILE),
				ResourceBundle.getBundle(Startup.BUNDLEFILE)));
	}

	/**
	 * Lets user choose directory which contains pictures.
	 */
	@FXML
	private void chooseDirectory() {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle(resources.getString("dirchoose.title"));
		chooser.setInitialDirectory(new File(model.getDirPath()));
		File file = chooser.showDialog(window.getScene().getWindow());
		if (file != null) {
			model.setDirPath(file.getPath());
			imageViewPort.setImage(null);
			listImagesInTable();
		}
		setWindowTitle();
	}

	/**
	 * @return String: %app.title (this.dirPath) - %app.titlesuffix
	 */
	private String windowTitleWithPath() {
		return resources.getString("app.title") + " (" + model.getDirPath() + ") "
				+ resources.getString("app.titlesuffix");
	}

	/**
	 * The JavaFX runtime calls this method when user chooses a directory.
	 */
	private void listImagesInTable() {
		Task<Collection<ImageFileVO>> backgroundTask = new Task<Collection<ImageFileVO>>() {

			/**
			 * Calls {@link DataProviderImpl#findImages(String)} in task thread.
			 *
			 * @return Collection of filenames (ImageFileVO) from directory
			 *         which are image files.
			 */
			@Override
			protected Collection<ImageFileVO> call() throws Exception {
				LOG.debug("call() called");
				Collection<ImageFileVO> result = dataProvider.findImages(model.getDirPath());
				return result;
			}

			/**
			 * Updates result table entries.
			 */
			@Override
			protected void succeeded() {
				LOG.debug("succeeded() called");
				model.setResult(new ArrayList<ImageFileVO>(getValue()));
				resultTable.getSortOrder().clear();
			}
		};
		new Thread(backgroundTask).start();
	}
}
