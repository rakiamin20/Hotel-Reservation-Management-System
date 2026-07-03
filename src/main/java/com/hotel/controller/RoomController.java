package com.hotel.controller;

import com.hotel.database.DBHelper;
import com.hotel.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * Controller class for the Room view.
 * Handles adding, editing, deleting, and displaying hotel rooms.
 */
public class RoomController {

    @FXML
    private TextField txtRoomNumber;

    @FXML
    private ComboBox<String> cmbRoomType;

    @FXML
    private TextField txtBaseRate;

    @FXML
    private ComboBox<String> cmbRoomStatus;

    @FXML
    private Label lblError;

    @FXML
    private Label lblSuccess;

    @FXML
    private TableView<Room> tblRooms;

    @FXML
    private TableColumn<Room, String> colRoomNumber;

    @FXML
    private TableColumn<Room, String> colType;

    @FXML
    private TableColumn<Room, Double> colBaseRate;

    @FXML
    private TableColumn<Room, String> colStatus;

    private ObservableList<Room> roomsObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Populate Dropdowns
        cmbRoomType.setItems(FXCollections.observableArrayList("Standard", "Deluxe", "Suite"));
        cmbRoomStatus.setItems(FXCollections.observableArrayList("Available", "Occupied", "Maintenance"));

        // 2. Bind Columns
        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colType.setCellValueFactory(new PropertyValueFactory<>("roomType")); // calls getRoomType() polymorphically
        colBaseRate.setCellValueFactory(new PropertyValueFactory<>("baseRate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Format Currency in Table Column
        colBaseRate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double rate, boolean empty) {
                super.updateItem(rate, empty);
                if (empty || rate == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.2f", rate));
                }
            }
        });

        // Format Room Status cells with badge colors
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(status);
                    label.getStyleClass().add("badge");
                    if ("Available".equalsIgnoreCase(status)) {
                        label.getStyleClass().add("badge-available");
                    } else if ("Occupied".equalsIgnoreCase(status)) {
                        label.getStyleClass().add("badge-occupied");
                    } else {
                        label.getStyleClass().add("badge-maintenance");
                    }
                    setGraphic(label);
                }
            }
        });

        // 3. Load Data
        loadRoomsData();
        clearForm();
    }

    private void loadRoomsData() {
        roomsObservableList.setAll(DBHelper.getAllRooms());
        tblRooms.setItems(roomsObservableList);
    }

    @FXML
    void handleSaveRoom(ActionEvent event) {
        lblError.setText("");
        lblSuccess.setText("");

        String roomNum = txtRoomNumber.getText().trim();
        String type = cmbRoomType.getValue();
        String rateText = txtBaseRate.getText().trim();
        String status = cmbRoomStatus.getValue();

        // 1. Input Validation
        if (roomNum.isEmpty() || type == null || rateText.isEmpty() || status == null) {
            lblError.setText("Please fill in all room fields.");
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateText);
            if (rate <= 0) {
                lblError.setText("Base rate must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            lblError.setText("Base rate must be a valid decimal number.");
            return;
        }

        // 2. Polymorphic Room Object Instantiation
        Room room;
        if ("Deluxe".equalsIgnoreCase(type)) {
            room = new DeluxeRoom(roomNum, status, rate);
        } else if ("Suite".equalsIgnoreCase(type)) {
            room = new SuiteRoom(roomNum, status, rate);
        } else {
            room = new StandardRoom(roomNum, status, rate);
        }

        // 3. Check if Room already exists
        boolean exists = roomsObservableList.stream()
                .anyMatch(r -> r.getRoomNumber().equalsIgnoreCase(roomNum));

        boolean result;
        if (exists) {
            result = DBHelper.updateRoom(room);
            if (result) lblSuccess.setText("Room " + roomNum + " updated successfully!");
        } else {
            result = DBHelper.addRoom(room);
            if (result) lblSuccess.setText("Room " + roomNum + " created successfully!");
        }

        if (!result) {
            lblError.setText("Database operation failed.");
        } else {
            loadRoomsData();
            clearForm();
        }
    }

    @FXML
    void handleDeleteRoom(ActionEvent event) {
        lblError.setText("");
        lblSuccess.setText("");
        String roomNum = txtRoomNumber.getText().trim();

        if (roomNum.isEmpty()) {
            lblError.setText("No room selected for deletion.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                "Are you sure you want to delete Room " + roomNum + "?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Delete Room Confirmation");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            boolean success = DBHelper.deleteRoom(roomNum);
            if (success) {
                lblSuccess.setText("Room deleted successfully.");
                loadRoomsData();
                clearForm();
            } else {
                lblError.setText("Cannot delete room. It might be referenced in an active reservation.");
            }
        }
    }

    @FXML
    void handleClearForm(ActionEvent event) {
        clearForm();
    }

    @FXML
    void handleTableClick(MouseEvent event) {
        Room selected = tblRooms.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtRoomNumber.setText(selected.getRoomNumber());
            txtRoomNumber.setDisable(true); // Disable room number edit (acts as PK)
            cmbRoomType.setValue(selected.getRoomType());
            txtBaseRate.setText(String.valueOf(selected.getBaseRate()));
            cmbRoomStatus.setValue(selected.getStatus());
        }
    }

    private void clearForm() {
        txtRoomNumber.clear();
        txtRoomNumber.setDisable(false);
        cmbRoomType.setValue(null);
        txtBaseRate.clear();
        cmbRoomStatus.setValue(null);
        lblError.setText("");
    }
}
