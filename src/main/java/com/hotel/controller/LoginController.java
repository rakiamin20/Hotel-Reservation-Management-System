package com.hotel.controller;

import com.hotel.MainApp;
import com.hotel.database.DBHelper;
import com.hotel.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for login view.
 * Handles user input, validation, and authentication.
 */
public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    @FXML
    private Button btnLogin;

    @FXML
    public void initialize() {
        lblError.setText("");
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        // 1. Input Validation
        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter both username and password.");
            return;
        }

        // 2. Exception Handling is baked into DB queries, but check result
        User authenticatedUser = DBHelper.authenticate(username, password);

        if (authenticatedUser != null) {
            lblError.setText("");
            System.out.println("Login successful for user: " + authenticatedUser.getUsername());
            transitionToDashboard(authenticatedUser);
        } else {
            lblError.setText("Invalid username or password.");
        }
    }

    /**
     * Loads the dashboard and passes the authenticated user context.
     */
    private void transitionToDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hotel/fxml/dashboard.fxml"));
            Parent root = loader.load();

            // Access the dashboard controller and pass the user object (Encapsulation)
            DashboardController controller = loader.getController();
            controller.setSessionUser(user);

            // Get current stage and swap scene
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 768);
            stage.setScene(scene);
            stage.setTitle("Legon Hill Hotel Reservation Management System");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            lblError.setText("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
