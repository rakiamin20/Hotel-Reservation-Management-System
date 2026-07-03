package com.hotel.view;

import com.hotel.database.DBHelper;
import com.hotel.model.Guest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Programmatic JavaFX view for the Guest Directory.
 * Incorporates search filtering and profile validations.
 */
public class GuestView extends HBox {

    private TextField txtFirstName;
    private TextField txtLastName;
    private TextField txtEmail;
    private TextField txtPhone;
    private TextField txtIdCard;

    private Label lblError;
    private Label lblSuccess;

    private TextField txtSearch;
    private TableView<Guest> tblGuests;
    private ObservableList<Guest> guestsObservableList = FXCollections.observableArrayList();

    private int selectedGuestId = -1; // -1 means new guest, positive means edit mode

    public GuestView() {
        this.setSpacing(30);
        this.setStyle("-fx-background-color: transparent;");

        // 1. Create Left Form
        VBox formCard = createFormCard();

        // 2. Create Right Directory
        VBox tableContainer = createTablePanel();

        // Assemble HBox
        this.getChildren().addAll(formCard, tableContainer);
        HBox.setHgrow(tableContainer, Priority.ALWAYS);

        // Load records
        refreshData();
    }

    private VBox createFormCard() {
        VBox formCard = new VBox(20);
        formCard.setPrefWidth(350);
        formCard.setMinWidth(350);
        formCard.setPadding(new Insets(24));
        formCard.setStyle(
            "-fx-background-color: #1e293b;" +
            "-fx-background-radius: 16px;" +
            "-fx-border-color: #334155;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 16px;"
        );

        Label lblFormTitle = new Label("Guest Profile Form");
        lblFormTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 18px; -fx-font-weight: bold;");

        // First Name Input
        VBox firstBox = new VBox(8);
        Label lblFirst = new Label("First Name");
        lblFirst.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        txtFirstName = createTextField("Enter first name");
        firstBox.getChildren().addAll(lblFirst, txtFirstName);

        // Last Name Input
        VBox lastBox = new VBox(8);
        Label lblLast = new Label("Last Name");
        lblLast.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        txtLastName = createTextField("Enter last name");
        lastBox.getChildren().addAll(lblLast, txtLastName);

        // Email Input
        VBox emailBox = new VBox(8);
        Label lblEmail = new Label("Email Address");
        lblEmail.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        txtEmail = createTextField("e.g. guest@domain.com");
        emailBox.getChildren().addAll(lblEmail, txtEmail);

        // Phone Input
        VBox phoneBox = new VBox(8);
        Label lblPhone = new Label("Phone Number");
        lblPhone.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        txtPhone = createTextField("e.g. 555-0100");
        phoneBox.getChildren().addAll(lblPhone, txtPhone);

        // ID Card Input
        VBox idCardBox = new VBox(8);
        Label lblIdCard = new Label("ID Card / Passport No.");
        lblIdCard.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        txtIdCard = createTextField("e.g. ID-4492");
        idCardBox.getChildren().addAll(lblIdCard, txtIdCard);

        // Errors & Success
        lblError = new Label("");
        lblError.setWrapText(true);
        lblError.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: 500;");

        lblSuccess = new Label("");
        lblSuccess.setWrapText(true);
        lblSuccess.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: 500;");

        // Buttons
        VBox actionContainer = new VBox(10);
        HBox doubleButtons = new HBox(10);
        
        Button btnSave = new Button("Save Profile");
        btnSave.setPrefHeight(40);
        btnSave.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnSave, Priority.ALWAYS);
        btnSave.setStyle("-fx-background-color: linear-gradient(to right, #d97706, #f59e0b); -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-cursor: hand;");
        btnSave.setOnAction(e -> handleSaveGuest());

        Button btnClear = new Button("Clear");
        btnClear.setPrefHeight(40);
        btnClear.setStyle("-fx-background-color: transparent; -fx-text-fill: #f8fafc; -fx-border-color: #475569; -fx-border-width: 1px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");
        btnClear.setOnAction(e -> clearForm());

        doubleButtons.getChildren().addAll(btnSave, btnClear);

        Button btnDelete = new Button("Delete Guest Profile");
        btnDelete.setPrefHeight(40);
        btnDelete.setPrefWidth(302);
        btnDelete.setStyle("-fx-background-color: #ef4444; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> handleDeleteGuest());

        actionContainer.getChildren().addAll(doubleButtons, btnDelete);

        formCard.getChildren().addAll(lblFormTitle, firstBox, lastBox, emailBox, phoneBox, idCardBox, lblError, lblSuccess, actionContainer);
        return formCard;
    }

    private VBox createTablePanel() {
        VBox tableContainer = new VBox(15);

        // Header Panel containing Title and Search Box
        HBox headerRow = new HBox(20);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label lblTitle = new Label("Guest Directory");
        lblTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 18px; -fx-font-weight: bold;");

        txtSearch = new TextField();
        txtSearch.setPromptText("Search by name, phone or ID Card...");
        txtSearch.setPrefWidth(300);
        txtSearch.setStyle(
            "-fx-background-color: #162032;" +
            "-fx-text-fill: #ffffff;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: #334155;" +
            "-fx-border-radius: 8px;" +
            "-fx-padding: 8px 12px 8px 12px;"
        );
        txtSearch.setOnKeyReleased(e -> handleSearch());
        
        headerRow.getChildren().addAll(lblTitle, txtSearch);

        // Table initialization
        tblGuests = new TableView<>();
        tblGuests.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: #334155;" +
            "-fx-border-radius: 12px;" +
            "-fx-background-radius: 12px;"
        );
        VBox.setVgrow(tblGuests, Priority.ALWAYS);

        // Columns
        TableColumn<Guest, Integer> colId = new TableColumn<>("ID");
        colId.setPrefWidth(60);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Guest, String> colFirst = new TableColumn<>("First Name");
        colFirst.setPrefWidth(120);
        colFirst.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Guest, String> colLast = new TableColumn<>("Last Name");
        colLast.setPrefWidth(120);
        colLast.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Guest, String> colEmail = new TableColumn<>("Email");
        colEmail.setPrefWidth(180);
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Guest, String> colPhone = new TableColumn<>("Phone");
        colPhone.setPrefWidth(130);
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Guest, String> colIdCard = new TableColumn<>("ID Card");
        colIdCard.setPrefWidth(110);
        colIdCard.setCellValueFactory(new PropertyValueFactory<>("idCard"));

        tblGuests.getColumns().addAll(colId, colFirst, colLast, colEmail, colPhone, colIdCard);
        tblGuests.setOnMouseClicked(e -> handleTableClick());

        tableContainer.getChildren().addAll(headerRow, tblGuests);
        return tableContainer;
    }

    private void handleSaveGuest() {
        lblError.setText("");
        lblSuccess.setText("");

        String first = txtFirstName.getText().trim();
        String last = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String idCard = txtIdCard.getText().trim();

        // 1. Validation checks
        if (first.isEmpty() || last.isEmpty() || email.isEmpty() || phone.isEmpty() || idCard.isEmpty()) {
            lblError.setText("All profile fields are mandatory.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            lblError.setText("Please enter a valid email address.");
            return;
        }

        if (!phone.matches("^[0-9+\\(\\)\\s-]{5,20}$")) {
            lblError.setText("Please enter a valid phone number (min 5 digits).");
            return;
        }

        boolean result;
        if (selectedGuestId == -1) {
            Guest guest = new Guest(first, last, email, phone, idCard);
            result = DBHelper.addGuest(guest);
            if (result) lblSuccess.setText("Profile for " + guest.getFullName() + " created successfully!");
        } else {
            Guest guest = new Guest(selectedGuestId, first, last, email, phone, idCard);
            result = DBHelper.updateGuest(guest);
            if (result) lblSuccess.setText("Profile for " + guest.getFullName() + " updated successfully!");
        }

        if (!result) {
            lblError.setText("Database save failed. Check if ID Card is duplicated.");
        } else {
            refreshData();
            clearForm();
        }
    }

    private void handleDeleteGuest() {
        lblError.setText("");
        lblSuccess.setText("");

        if (selectedGuestId == -1) {
            lblError.setText("Please select a guest profile from the directory.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                "Are you sure you want to delete this guest profile? All their historical reservations will be deleted.", 
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Delete Guest Profile");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            boolean success = DBHelper.deleteGuest(selectedGuestId);
            if (success) {
                lblSuccess.setText("Guest profile deleted successfully.");
                refreshData();
                clearForm();
            } else {
                lblError.setText("Database delete operation failed.");
            }
        }
    }

    private void handleSearch() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            refreshData();
        } else {
            // Overloaded search method in Java
            List<Guest> searchResults = DBHelper.searchGuests(query);
            guestsObservableList.setAll(searchResults);
            tblGuests.setItems(guestsObservableList);
        }
    }

    private void handleTableClick() {
        Guest selected = tblGuests.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedGuestId = selected.getId();
            txtFirstName.setText(selected.getFirstName());
            txtLastName.setText(selected.getLastName());
            txtEmail.setText(selected.getEmail());
            txtPhone.setText(selected.getPhone());
            txtIdCard.setText(selected.getIdCard());
        }
    }

    private void clearForm() {
        selectedGuestId = -1;
        txtFirstName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtIdCard.clear();
        lblError.setText("");
    }

    public void refreshData() {
        guestsObservableList.setAll(DBHelper.getAllGuests());
        tblGuests.setItems(guestsObservableList);
    }

    private TextField createTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
            "-fx-background-color: #1e293b;" +
            "-fx-text-fill: #ffffff;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: #334155;" +
            "-fx-border-radius: 8px;" +
            "-fx-padding: 10px;"
        );
        tf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                tf.setStyle(tf.getStyle() + "-fx-border-color: #d97706; -fx-background-color: #0f172a;");
            } else {
                tf.setStyle(
                    "-fx-background-color: #1e293b;" +
                    "-fx-text-fill: #ffffff;" +
                    "-fx-background-radius: 8px;" +
                    "-fx-border-color: #334155;" +
                    "-fx-border-radius: 8px;" +
                    "-fx-padding: 10px;"
                );
            }
        });
        return tf;
    }
}
