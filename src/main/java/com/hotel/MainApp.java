package com.hotel;

import com.hotel.database.DatabaseManager;
import com.hotel.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class that boots the database
 * and loads the programmatic LoginView as the primary stage.
 * No FXML or CSS files are used.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Initialize SQLite Database & Seed Mock Data
            DatabaseManager.initializeDatabase();

            // 2. Instantiate LoginView programmatically (pure Java, no FXML)
            LoginView loginView = new LoginView();

            // 3. Configure Scene and Stage
            Scene scene = new Scene(loginView, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Staff Authentication - Legon Hill Hotel");
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Fatal Application Startup Error:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
