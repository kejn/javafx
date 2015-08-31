package com.capgemini.starterkit.javafx.imageviewer;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Startup extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		/*
		 * Set the default locale based on the '--lang' startup argument.
		 */
		String langCode = getParameters().getNamed().get("lang");
		System.out.println("langCode at startup: " + langCode);
		if (langCode != null && !langCode.isEmpty()) {
			Locale.setDefault(Locale.forLanguageTag(langCode));
		}

		primaryStage.setTitle("ImageViewer - JavaFX");

		/*
		 * Load screen from FXML file with specific language bundle (derived
		 * from default locale).
		 */
		Parent root = FXMLLoader.load(
				getClass().getResource("/com/capgemini/starterkit/javafx/imageviewer/view/image-view.fxml"),
				ResourceBundle.getBundle("com/capgemini/starterkit/javafx/imageviewer/bundle/bundle"));

		Scene scene = new Scene(root);

		/*
		 * Set the style sheet(s) for application.
		 */
		scene.getStylesheets().add(getClass()
				.getResource("/com/capgemini/starterkit/javafx/imageviewer/css/standard.css").toExternalForm());

		primaryStage.setScene(scene);
		primaryStage.setMinWidth(825);
		primaryStage.setMinHeight(665);

		primaryStage.show();
	}

}
