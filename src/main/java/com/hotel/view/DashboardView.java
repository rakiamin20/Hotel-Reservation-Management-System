package com.hotel.view;

import com.hotel.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Programmatic JavaFX view for the Dashboard container.
 * Houses the sidebar navigation and switches views in pure Java.
 */
public class DashboardView extends HBox {

    private final User currentUser;
    
    // UI elements
    private Label lblHeaderTitle;
    private Label lblHeaderSubtitle;
    private StackPane contentArea;

    private Button btnNavDashboard;
    private Button btnNavRooms;
    private Button btnNavGuests;
    private Button btnNavReservations;
    private Button btnLogout;

    // View instances
    private HomeView homeView;
    private RoomView roomView;
    private GuestView guestView;
    private ReservationView reservationView;

    public DashboardView(User currentUser) {
        this.currentUser = currentUser;
        this.setStyle("-fx-background-color: #0f172a;"); // Slate background

        // 1. Create Left Sidebar
        VBox sidebar = createSidebar();

        // 2. Create Right Content Area
        VBox workspace = new VBox();
        HBox.setHgrow(workspace, Priority.ALWAYS);

        // Header Panel
        VBox headerBar = new VBox(2);
        headerBar.setPadding(new Insets(16, 32, 16, 32));
        headerBar.setStyle(
            "-fx-background-color: #1e293b;" +
            "-fx-border-color: #334155;" +
            "-fx-border-width: 0 0 1px 0;"
        );

        lblHeaderTitle = new Label("Dashboard Overview");
        lblHeaderTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: 700; -fx-text-fill: #ffffff;");
        
        lblHeaderSubtitle = new Label("Welcome to Legon Hill Hotel administrative control center.");
        lblHeaderSubtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
        
        headerBar.getChildren().addAll(lblHeaderTitle, lblHeaderSubtitle);

        // Content Area Frame
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(30));
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        workspace.getChildren().addAll(headerBar, contentArea);

        // 3. Assemble HBox
        this.getChildren().addAll(sidebar, workspace);

        // 4. Load initial default sub-view
        showHomeView();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(260);
        sidebar.setMaxWidth(260);
        sidebar.setPadding(new Insets(24, 16, 24, 16));
        sidebar.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #1e293b, #0f172a);" +
            "-fx-border-color: #334155;" +
            "-fx-border-width: 0 1px 0 0;"
        );

        // Brand Title Segment
        VBox brandBox = new VBox(2);
        brandBox.setPadding(new Insets(10, 0, 40, 10));
        Label lblBrandTitle = new Label("LEGON HILL HOTEL");
        lblBrandTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label lblBrandSub = new Label("Luxury Reservation");
        lblBrandSub.setStyle("-fx-text-fill: #d97706; -fx-font-weight: bold; -fx-font-size: 11px; -fx-text-transform: uppercase;");
        brandBox.getChildren().addAll(lblBrandTitle, lblBrandSub);

        // Navigation Buttons
        btnNavDashboard = createNavButton("Dashboard");
        btnNavRooms = createNavButton("Room Management");
        btnNavGuests = createNavButton("Guest Directory");
        btnNavReservations = createNavButton("Bookings & Billings");

        // Action routing events
        btnNavDashboard.setOnAction(e -> showHomeView());
        btnNavRooms.setOnAction(e -> showRoomsView());
        btnNavGuests.setOnAction(e -> showGuestsView());
        btnNavReservations.setOnAction(e -> showReservationsView());

        // Spacer to push staff card and logout to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Session Information Card
        VBox sessionCard = new VBox(5);
        sessionCard.setPadding(new Insets(12));
        sessionCard.setStyle("-fx-background-color: rgba(255, 255, 255, 0.03); -fx-background-radius: 8px;");
        VBox.setMargin(sessionCard, new Insets(0, 10, 10, 10));

        Label lblUser = new Label(currentUser.getFullName());
        lblUser.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 13px;");
        Label lblRole = new Label(currentUser.getRole() + " Staff");
        lblRole.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        sessionCard.getChildren().addAll(lblUser, lblRole);

        // Log out button
        btnLogout = new Button("Log Out");
        btnLogout.setPrefHeight(45);
        btnLogout.setPrefWidth(230);
        btnLogout.setAlignment(Pos.BASELINE_LEFT);
        btnLogout.setPadding(new Insets(12, 16, 12, 20));
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-cursor: hand;");
        btnLogout.setOnMouseEntered(e -> btnLogout.setStyle("-fx-background-color: rgba(239, 68, 68, 0.1); -fx-text-fill: #fca5a5; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-cursor: hand;"));
        btnLogout.setOnMouseExited(e -> btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-cursor: hand;"));
        btnLogout.setOnAction(e -> handleLogout());

        sidebar.getChildren().addAll(brandBox, btnNavDashboard, btnNavRooms, btnNavGuests, btnNavReservations, spacer, sessionCard, btnLogout);
        return sidebar;
    }

    private Button createNavButton(String title) {
        Button btn = new Button(title);
        btn.setPrefHeight(45);
        btn.setPrefWidth(230);
        btn.setAlignment(Pos.BASELINE_LEFT);
        btn.setPadding(new Insets(12, 16, 12, 20));
        setButtonStyleInactive(btn);
        return btn;
    }

    // Set styling of navigation buttons
    private void setButtonStyleActive(Button btn) {
        btn.setStyle(
            "-fx-background-color: linear-gradient(to right, #d97706, #f59e0b);" +
            "-fx-text-fill: #000000;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        );
    }

    private void setButtonStyleInactive(Button btn) {
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #94a3b8;" +
            "-fx-font-weight: 500;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.05);" +
            "-fx-text-fill: #ffffff;" +
            "-fx-font-weight: 500;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #94a3b8;" +
            "-fx-font-weight: 500;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        ));
    }

    private void resetAllNavButtons() {
        setButtonStyleInactive(btnNavDashboard);
        setButtonStyleInactive(btnNavRooms);
        setButtonStyleInactive(btnNavGuests);
        setButtonStyleInactive(btnNavReservations);
    }

    // ==========================================
    // ROUTING SWITCHING
    // ==========================================

    private void showHomeView() {
        resetAllNavButtons();
        setButtonStyleActive(btnNavDashboard);
        lblHeaderTitle.setText("Dashboard Overview");
        lblHeaderSubtitle.setText("Welcome to Legon Hill Hotel administrative control center.");

        if (homeView == null) {
            homeView = new HomeView();
        } else {
            homeView.refreshData();
        }
        setContent(homeView);
    }

    private void showRoomsView() {
        resetAllNavButtons();
        setButtonStyleActive(btnNavRooms);
        lblHeaderTitle.setText("Room Management");
        lblHeaderSubtitle.setText("Create, update, and manage hotel room configurations.");

        if (roomView == null) {
            roomView = new RoomView();
        } else {
            roomView.refreshData();
        }
        setContent(roomView);
    }

    private void showGuestsView() {
        resetAllNavButtons();
        setButtonStyleActive(btnNavGuests);
        lblHeaderTitle.setText("Guest Directory");
        lblHeaderSubtitle.setText("Register and manage customer profiles, phone numbers, and IDs.");

        if (guestView == null) {
            guestView = new GuestView();
        } else {
            guestView.refreshData();
        }
        setContent(guestView);
    }

    private void showReservationsView() {
        resetAllNavButtons();
        setButtonStyleActive(btnNavReservations);
        lblHeaderTitle.setText("Bookings & Billings");
        lblHeaderSubtitle.setText("Reserve rooms, run polymorphic estimations, check out, and cancel reservations.");

        if (reservationView == null) {
            reservationView = new ReservationView();
        } else {
            reservationView.refreshData();
        }
        setContent(reservationView);
    }

    private void setContent(Parent view) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    private void handleLogout() {
        try {
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            LoginView loginView = new LoginView();
            Scene scene = new Scene(loginView, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Staff Authentication - Legon Hill Hotel");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
