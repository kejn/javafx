package com.capgemini.starterkit.javafx.imageviewer.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.capgemini.starterkit.javafx.imageviewer.dataprovider.DataProvider;
import com.capgemini.starterkit.javafx.imageviewer.dataprovider.data.ImageFileVO;
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

public class ImageViewController {

	private static final Logger LOG = Logger.getLogger(ImageViewController.class);

	/*
	 * REV: te stale powinny byc uzyte w klasie Startup
	 */
	private static final String FXMLFILE = "/com/capgemini/starterkit/javafx/imageviewer/view/image-view.fxml";

	private static final String BUNDLEFILE = "com/capgemini/starterkit/javafx/imageviewer/bundle/bundle";

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

	private void initializeMenuLanguage() {
		if (Locale.getDefault().equals(Locale.forLanguageTag("PL"))) {
			menuLanguagePolish.setDisable(true);
		} else {
			menuLanguageEnglish.setDisable(true);
		}
	}

	private void initializeImageViewPort() {
		imageViewPort.fitWidthProperty().bind(imageBounds.widthProperty());
		imageViewPort.fitHeightProperty().bind(imageBounds.heightProperty());
	}

	private void initializeResultTable() {
		nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
		/*
		 * REV: lepiej zdefiniowac w FXMLu
		 */
		nameColumn.setMinWidth(200.0);

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
		/*
		 * REV: lepiej zrobic to bindem
		 * nameColumn.maxWidthProperty().bind(resultTable.widthProperty().subtract(5));
		 */
		resultTable.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				nameColumn.setMaxWidth(newValue.doubleValue() - 5);
			}
		});

	}

	@FXML
	private void closeAppAction() {
		LOG.debug("Menu > Exit");
		Stage stage = (Stage) window.getScene().getWindow();
		stage.close();
	}

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
		/*
		 * REV: to jest troche niebezpieczne
		 * FXMLLoader tworzy nowe instancje obiektow zdefiniowanych w fxmlu (w tym tego kontrolera)
		 * Przy bardziej skomplikowanych konfiguracjach kontroler-model moze to prowadzic do memory leakow.
		 */
		window.getScene()
				.setRoot(FXMLLoader.load(getClass().getResource(FXMLFILE), ResourceBundle.getBundle(BUNDLEFILE)));
	}

	@FXML
	private void chooseDirectory() {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle(resources.getString("dirchoose.title"));
		chooser.setInitialDirectory(new File(model.getDirPath()));
		/*
		 * REV: dialog wyboru katalogu powinien byc modalny w stosunku do glownego okna
		 * showDialog(primaryStage)
		 */
		File file = chooser.showDialog(null);
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

	private void listImagesInTable() {
		Task<Collection<ImageFileVO>> backgroundTask = new Task<Collection<ImageFileVO>>() {

			@Override
			protected Collection<ImageFileVO> call() throws Exception {
				LOG.debug("call() called");
				Collection<ImageFileVO> result = dataProvider.findImages(model.getDirPath());
				return result;
			}

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
