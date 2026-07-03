package com.hotel.controller;

import com.hotel.database.DBHelper;
import com.hotel.model.Guest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * Controller class for the Guest view.
 * Handles customer registrations, directory searches, and profile updates.
 */
public class GuestController {

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtIdCard;

    @FXML
    private Label lblError;

    @FXML
    private Label lblSuccess;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<Guest> tblGuests;

    @FXML
    private TableColumn<Guest, Integer> colId;

    @FXML
    private TableColumn<Guest, String> colFirstName;

    @FXML
    private TableColumn<Guest, String> colLastName;

    @FXML
    private TableColumn<Guest, String> colEmail;

    @FXML
    private TableColumn<Guest, String> colPhone;

    @FXML
    private TableColumn<Guest, String> colIdCard;

    private ObservableList<Guest> guestsObservableList = FXCollections.observableArrayList();
    private int selectedGuestId = -1; // -1 means no guest is selected (new guest mode)

    @FXML
    public void initialize() {
        // 1. Bind Columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colIdCard.setCellValueFactory(new PropertyValueFactory<>("idCard"));

        // 2. Load Directory
        loadGuestsData();
        clearForm();
    }

    private void loadGuestsData() {
        guestsObservableList.setAll(DBHelper.getAllGuests());
        tblGuests.setItems(guestsObservableList);
    }

    @FXML
    void handleSaveGuest(ActionEvent event) {
        lblError.setText("");
        lblSuccess.setText("");

        String first = txtFirstName.getText().trim();
        String last = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String idCard = txtIdCard.getText().trim();

        // 1. Input Validation
        if (first.isEmpty() || last.isEmpty() || email.isEmpty() || phone.isEmpty() || idCard.isEmpty()) {
            lblError.setText("All profile fields are mandatory.");
            return;
        }

        // Simple Email regex check
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            lblError.setText("Please enter a valid email address.");
            return;
        }

        // Phone number validation: allows digits, dashes, spaces, parentheses
        if (!phone.matches("^[0-9+\\(\\)\\s-]{5,20}$")) {
            lblError.setText("Please enter a valid phone number (min 5 digits).");
            return;
        }

        // 2. Instantiate and Save
        boolean result;
        if (selectedGuestId == -1) {
            // New Guest mode
            Guest guest = new Guest(first, last, email, phone, idCard);
            result = DBHelper.addGuest(guest);
            if (result) lblSuccess.setText("Profile for " + guest.getFullName() + " created successfully!");
        } else {
            // Edit Mode
            Guest guest = new Guest(selectedGuestId, first, last, email, phone, idCard);
            result = DBHelper.updateGuest(guest);
            if (result) lblSuccess.setText("Profile for " + guest.getFullName() + " updated successfully!");
        }

        if (!result) {
            lblError.setText("Database execution error. Check if ID Card is duplicated.");
        } else {
            loadGuestsData();
            clearForm();
        }
    }

    @FXML
    void handleDeleteGuest(ActionEvent event) {
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
                loadGuestsData();
                clearForm();
            } else {
                lblError.setText("Database delete operation failed.");
            }
        }
    }

    @FXML
    void handleClearForm(ActionEvent event) {
        clearForm();
    }

    @FXML
    void handleSearch(KeyEvent event) {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            loadGuestsData();
        } else {
            // Calls overloaded searchGuests(String term) method
            List<Guest> searchResults = DBHelper.searchGuests(query);
            guestsObservableList.setAll(searchResults);
            tblGuests.setItems(guestsObservableList);
        }
    }

    @FXML
    void handleTableClick(MouseEvent event) {
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
}
