package com.hotel.view;

import com.hotel.database.DBHelper;
import com.hotel.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Programmatic JavaFX view for secure staff login.
 * Replaces FXML and uses Java-based styles.
 */
public class LoginView extends StackPane {

    private TextField txtUsername;
    private PasswordField txtPassword;
    private Label lblError;
    private Button btnLogin;

    public LoginView() {
        // Background Gradient style (Deep Slate to Indigo)
        this.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f172a, #1e1b4b);");

        // Login Card Panel
        VBox card = new VBox(20);
        card.setMaxSize(400, 450);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.TOP_CENTER);
        // Premium card styling
        card.setStyle(
            "-fx-background-color: rgba(30, 41, 59, 0.75);" +
            "-fx-background-radius: 20px;" +
            "-fx-border-color: rgba(255, 255, 255, 0.1);" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 20px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.4), 15, 0, 0, 10);"
        );

        // Header Section
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        
        Label lblTitle = new Label("LEGON HILL HOTEL");
        lblTitle.setStyle(
            "-fx-font-family: 'Outfit', sans-serif;" +
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #ffffff;"
        );

        Label lblSubtitle = new Label("RESERVATION MANAGEMENT SYSTEM");
        lblSubtitle.setStyle(
            "-fx-font-family: 'Outfit', sans-serif;" +
            "-fx-text-fill: #d97706;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 11px;"
        );
        headerBox.getChildren().addAll(lblTitle, lblSubtitle);

        // Input Fields Layout
        VBox inputContainer = new VBox(15);
        inputContainer.setAlignment(Pos.CENTER_LEFT);

        // Username Block
        VBox userBlock = new VBox(6);
        Label lblUser = new Label("Username");
        lblUser.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        txtUsername = new TextField();
        txtUsername.setPromptText("Enter your username");
        txtUsername.setPrefHeight(40);
        txtUsername.setStyle(
            "-fx-background-color: #1e293b;" +
            "-fx-text-fill: #ffffff;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: #334155;" +
            "-fx-border-radius: 8px;" +
            "-fx-padding: 10px;"
        );
        userBlock.getChildren().addAll(lblUser, txtUsername);

        // Password Block
        VBox passBlock = new VBox(6);
        Label lblPass = new Label("Password");
        lblPass.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");
        txtPassword.setPrefHeight(40);
        txtPassword.setStyle(
            "-fx-background-color: #1e293b;" +
            "-fx-text-fill: #ffffff;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: #334155;" +
            "-fx-border-radius: 8px;" +
            "-fx-padding: 10px;"
        );
        passBlock.getChildren().addAll(lblPass, txtPassword);

        inputContainer.getChildren().addAll(userBlock, passBlock);

        // Error Feedbacks
        lblError = new Label("");
        lblError.setWrapText(true);
        lblError.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: 500; -fx-alignment: center;");

        // Submit Login Button
        btnLogin = new Button("Log In");
        btnLogin.setPrefHeight(40);
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setStyle(
            "-fx-background-color: linear-gradient(to right, #d97706, #f59e0b);" +
            "-fx-text-fill: #000000;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        );
        
        // Add Button Hover effects programmatically in Java
        btnLogin.setOnMouseEntered(e -> btnLogin.setStyle(
            "-fx-background-color: linear-gradient(to right, #f59e0b, #fbbf24);" +
            "-fx-text-fill: #000000;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        ));
        btnLogin.setOnMouseExited(e -> btnLogin.setStyle(
            "-fx-background-color: linear-gradient(to right, #d97706, #f59e0b);" +
            "-fx-text-fill: #000000;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        ));

        // Submit Trigger
        btnLogin.setOnAction(e -> handleLogin());

        // Footer info label
        Label lblInfo = new Label("Secure Administrative Access Only");
        lblInfo.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

        // Assembly
        card.getChildren().addAll(headerBox, new Region(), inputContainer, lblError, btnLogin, lblInfo);
        this.getChildren().add(card);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter both username and password.");
            return;
        }

        User user = DBHelper.authenticate(username, password);

        if (user != null) {
            lblError.setText("");
            transitionToDashboard(user);
        } else {
            lblError.setText("Invalid username or password.");
        }
    }

    private void transitionToDashboard(User user) {
        try {
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            DashboardView dashboardView = new DashboardView(user);
            Scene scene = new Scene(dashboardView, 1280, 768);
            stage.setScene(scene);
            stage.setTitle("Legon Hill Hotel Reservation System");
            stage.centerOnScreen();
        } catch (Exception e) {
            lblError.setText("Transition error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
