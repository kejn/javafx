package com.capgemini.starterkit.javafx.smallibrary;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Startup extends Application {

	private static final Logger LOG = Logger.getLogger(Startup.class);

	public static void main(String[] args) {
		Application.launch(args);
	}

	/**
	 * Sets:
	 * <ul>
	 * <li>the default locale based on the '--lang' startup argument,</li>
	 * <li>window title and its initial size,</li>
	 * <li>Scene stylesheet</li>
	 * </ul>
	 * and loads the rest of layout and properties from FXML and .properties
	 * files.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		String langCode = getParameters().getNamed().get("lang");
		LOG.debug("langCode at startup: " + langCode);
		if (langCode != null && !langCode.isEmpty()) {
			Locale.setDefault(Locale.forLanguageTag(langCode));
		}

		primaryStage.setTitle("SmallLibrary - JavaFX");

		Parent root = FXMLLoader.load(
				getClass().getResource("/com/capgemini/starterkit/javafx/smallibrary/view/library-view.fxml"),
				ResourceBundle.getBundle("com/capgemini/starterkit/javafx/smallibrary/bundle/bundle"));

		Scene scene = new Scene(root);

		scene.getStylesheets().add(getClass()
				.getResource("/com/capgemini/starterkit/javafx/smallibrary/css/standard.css").toExternalForm());

		primaryStage.setScene(scene);
		primaryStage.setMinWidth(520);
		primaryStage.setMaxWidth(520);
		primaryStage.setMinHeight(440);

		primaryStage.show();
	}

}
