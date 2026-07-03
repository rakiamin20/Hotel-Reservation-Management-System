package com.hotel.controller;

import com.hotel.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for dashboard framework.
 * Implements routing/navigation using dynamic FXML sub-view loading.
 */
public class DashboardController {

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblUserRole;

    @FXML
    private Label lblHeaderTitle;

    @FXML
    private Label lblHeaderSubtitle;

    @FXML
    private Button btnNavDashboard;

    @FXML
    private Button btnNavRooms;

    @FXML
    private Button btnNavGuests;

    @FXML
    private Button btnNavReservations;

    @FXML
    private Button btnLogout;

    @FXML
    private StackPane contentArea;

    private User currentUser;

    /**
     * Initializes the view. Standard practice in JavaFX.
     * Loads the default home dashboard view immediately on start.
     */
    @FXML
    public void initialize() {
        // Load default sub-view (home/dashboard overview)
        loadSubView("/com/hotel/fxml/home.fxml", "Dashboard Overview", 
                    "Welcome to Legon Hill Hotel administrative control center.");
    }

    /**
     * Sets the active session user and updates sidebar labels.
     */
    public void setSessionUser(User user) {
        this.currentUser = user;
        if (user != null) {
            lblUsername.setText(user.getFullName());
            lblUserRole.setText(user.getRole() + " Staff");
        }
    }

    // ==========================================
    // ROUTING METHODS (Navigation Handlers)
    // ==========================================

    @FXML
    void showDashboardView(ActionEvent event) {
        setActiveNavButton(btnNavDashboard);
        loadSubView("/com/hotel/fxml/home.fxml", "Dashboard Overview", 
                    "Welcome to Legon Hill Hotel administrative control center.");
    }

    @FXML
    void showRoomsView(ActionEvent event) {
        setActiveNavButton(btnNavRooms);
        loadSubView("/com/hotel/fxml/rooms.fxml", "Room Management", 
                    "Create, update, and manage hotel room configurations.");
    }

    @FXML
    void showGuestsView(ActionEvent event) {
        setActiveNavButton(btnNavGuests);
        loadSubView("/com/hotel/fxml/guests.fxml", "Guest Directory", 
                    "Register and manage customer profiles, phone numbers, and IDs.");
    }

    @FXML
    void showReservationsView(ActionEvent event) {
        setActiveNavButton(btnNavReservations);
        loadSubView("/com/hotel/fxml/reservations.fxml", "Bookings & Billings", 
                    "Reserve rooms for guests, calculate pricing models, check out, and cancel reservations.");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/hotel/fxml/login.fxml"));
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Login - Hotel Reservation Management System");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading login screen on logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================
    // HELPERS
    // ==========================================

    /**
     * Dynamically loads an FXML sub-view into the StackPane content area.
     */
    private void loadSubView(String fxmlPath, String headerTitle, String headerSubtitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // Clear current view and set new
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
            // Update Headers
            lblHeaderTitle.setText(headerTitle);
            lblHeaderSubtitle.setText(headerSubtitle);
            
        } catch (IOException e) {
            System.err.println("Failed to load sub-view (" + fxmlPath + "): " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Manages CSS active state styling classes for sidebar nav buttons.
     */
    private void setActiveNavButton(Button activeButton) {
        // Reset all buttons to default styling class
        btnNavDashboard.getStyleClass().remove("nav-button-active");
        btnNavRooms.getStyleClass().remove("nav-button-active");
        btnNavGuests.getStyleClass().remove("nav-button-active");
        btnNavReservations.getStyleClass().remove("nav-button-active");
        
        // Ensure nav-button default style class exists
        if (!btnNavDashboard.getStyleClass().contains("nav-button")) btnNavDashboard.getStyleClass().add("nav-button");
        if (!btnNavRooms.getStyleClass().contains("nav-button")) btnNavRooms.getStyleClass().add("nav-button");
        if (!btnNavGuests.getStyleClass().contains("nav-button")) btnNavGuests.getStyleClass().add("nav-button");
        if (!btnNavReservations.getStyleClass().contains("nav-button")) btnNavReservations.getStyleClass().add("nav-button");

        // Add active style class to current button
        activeButton.getStyleClass().add("nav-button-active");
    }
}
