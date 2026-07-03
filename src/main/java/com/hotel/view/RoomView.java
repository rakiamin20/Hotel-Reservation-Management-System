package com.hotel.view;

import com.hotel.database.DBHelper;
import com.hotel.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * Programmatic JavaFX view for Room Configuration.
 * Incorporates form inputs, validation, and a TableView.
 */
public class RoomView extends HBox {

    private TextField txtRoomNumber;
    private ComboBox<String> cmbRoomType;
    private TextField txtBaseRate;
    private ComboBox<String> cmbRoomStatus;

    private Label lblError;
    private Label lblSuccess;

    private TableView<Room> tblRooms;
    private ObservableList<Room> roomsObservableList = FXCollections.observableArrayList();

    public RoomView() {
        this.setSpacing(30);
        this.setStyle("-fx-background-color: transparent;");

        // 1. Create Left Form panel
        VBox formCard = createFormCard();

        // 2. Create Right Table panel
        VBox tableContainer = createTablePanel();

        // Assemble HBox
        this.getChildren().addAll(formCard, tableContainer);
        HBox.setHgrow(tableContainer, Priority.ALWAYS);

        // Load Initial Records
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

        Label lblFormTitle = new Label("Room Details Form");
        lblFormTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Room Number Input
        VBox numBox = new VBox(8);
        Label lblNum = new Label("Room Number");
        lblNum.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        txtRoomNumber = createTextField("e.g. 101, 204");
        numBox.getChildren().addAll(lblNum, txtRoomNumber);

        // Room Type ComboBox
        VBox typeBox = new VBox(8);
        Label lblType = new Label("Room Type");
        lblType.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        cmbRoomType = new ComboBox<>(FXCollections.observableArrayList("Standard", "Deluxe", "Suite"));
        cmbRoomType.setPromptText("Select Type");
        cmbRoomType.setPrefWidth(302);
        styleComboBox(cmbRoomType);
        typeBox.getChildren().addAll(lblType, cmbRoomType);

        // Base Rate Input
        VBox rateBox = new VBox(8);
        Label lblRate = new Label("Base Rate ($ / Night)");
        lblRate.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        txtBaseRate = createTextField("e.g. 85.00");
        rateBox.getChildren().addAll(lblRate, txtBaseRate);

        // Room Status ComboBox
        VBox statusBox = new VBox(8);
        Label lblStatus = new Label("Current Status");
        lblStatus.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        cmbRoomStatus = new ComboBox<>(FXCollections.observableArrayList("Available", "Occupied", "Maintenance"));
        cmbRoomStatus.setPromptText("Select Status");
        cmbRoomStatus.setPrefWidth(302);
        styleComboBox(cmbRoomStatus);
        statusBox.getChildren().addAll(lblStatus, cmbRoomStatus);

        // Notification Feedback
        lblError = new Label("");
        lblError.setWrapText(true);
        lblError.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: 500;");

        lblSuccess = new Label("");
        lblSuccess.setWrapText(true);
        lblSuccess.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: 500;");

        // Action Buttons Setup
        VBox actionContainer = new VBox(10);
        HBox doubleButtons = new HBox(10);
        
        Button btnSave = new Button("Save Room");
        btnSave.setPrefHeight(40);
        btnSave.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnSave, Priority.ALWAYS);
        btnSave.setStyle("-fx-background-color: linear-gradient(to right, #d97706, #f59e0b); -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-cursor: hand;");
        btnSave.setOnAction(e -> handleSaveRoom());

        Button btnClear = new Button("Clear");
        btnClear.setPrefHeight(40);
        btnClear.setStyle("-fx-background-color: transparent; -fx-text-fill: #f8fafc; -fx-border-color: #475569; -fx-border-width: 1px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");
        btnClear.setOnAction(e -> clearForm());

        doubleButtons.getChildren().addAll(btnSave, btnClear);

        Button btnDelete = new Button("Delete Room");
        btnDelete.setPrefHeight(40);
        btnDelete.setPrefWidth(302);
        btnDelete.setStyle("-fx-background-color: #ef4444; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> handleDeleteRoom());

        actionContainer.getChildren().addAll(doubleButtons, btnDelete);

        formCard.getChildren().addAll(lblFormTitle, numBox, typeBox, rateBox, statusBox, lblError, lblSuccess, actionContainer);
        return formCard;
    }

    private VBox createTablePanel() {
        VBox tableContainer = new VBox(15);
        
        Label lblTitle = new Label("Room Registry");
        lblTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Table Configuration
        tblRooms = new TableView<>();
        tblRooms.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: #334155;" +
            "-fx-border-radius: 12px;" +
            "-fx-background-radius: 12px;"
        );
        VBox.setVgrow(tblRooms, Priority.ALWAYS);

        // Columns
        TableColumn<Room, String> colNumber = new TableColumn<>("Room Number");
        colNumber.setPrefWidth(120);
        colNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Room, String> colType = new TableColumn<>("Room Type");
        colType.setPrefWidth(180);
        colType.setCellValueFactory(new PropertyValueFactory<>("roomType")); // calls getRoomType() polymorphically

        TableColumn<Room, Double> colRate = new TableColumn<>("Base Rate ($ / Night)");
        colRate.setPrefWidth(180);
        colRate.setCellValueFactory(new PropertyValueFactory<>("baseRate"));
        colRate.setCellFactory(column -> new TableCell<>() {
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

        TableColumn<Room, String> colStatus = new TableColumn<>("Status");
        colStatus.setPrefWidth(160);
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        // Status Badges format
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.setPadding(new Insets(4, 8, 4, 8));
                    badge.setStyle("-fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");
                    
                    if ("Available".equalsIgnoreCase(status)) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: rgba(34, 197, 94, 0.15); -fx-text-fill: #4ade80;");
                    } else if ("Occupied".equalsIgnoreCase(status)) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: rgba(239, 68, 68, 0.15); -fx-text-fill: #f87171;");
                    } else {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: rgba(234, 179, 8, 0.15); -fx-text-fill: #facc15;");
                    }
                    setGraphic(badge);
                }
            }
        });

        tblRooms.getColumns().addAll(colNumber, colType, colRate, colStatus);
        
        // Listen to Row Click
        tblRooms.setOnMouseClicked(e -> handleTableClick());

        tableContainer.getChildren().addAll(lblTitle, tblRooms);
        return tableContainer;
    }

    private void handleSaveRoom() {
        lblError.setText("");
        lblSuccess.setText("");

        String roomNum = txtRoomNumber.getText().trim();
        String type = cmbRoomType.getValue();
        String rateText = txtBaseRate.getText().trim();
        String status = cmbRoomStatus.getValue();

        // Validation
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

        // Polymorphic instantiations
        Room room;
        if ("Deluxe".equalsIgnoreCase(type)) {
            room = new DeluxeRoom(roomNum, status, rate);
        } else if ("Suite".equalsIgnoreCase(type)) {
            room = new SuiteRoom(roomNum, status, rate);
        } else {
            room = new StandardRoom(roomNum, status, rate);
        }

        boolean exists = roomsObservableList.stream().anyMatch(r -> r.getRoomNumber().equalsIgnoreCase(roomNum));
        boolean success;

        if (exists) {
            success = DBHelper.updateRoom(room);
            if (success) lblSuccess.setText("Room " + roomNum + " updated successfully!");
        } else {
            success = DBHelper.addRoom(room);
            if (success) lblSuccess.setText("Room " + roomNum + " created successfully!");
        }

        if (!success) {
            lblError.setText("Database save failed.");
        } else {
            refreshData();
            clearForm();
        }
    }

    private void handleDeleteRoom() {
        lblError.setText("");
        lblSuccess.setText("");
        String roomNum = txtRoomNumber.getText().trim();

        if (roomNum.isEmpty()) {
            lblError.setText("No room selected for deletion.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                "Are you sure you want to delete Room " + roomNum + "?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Delete Room");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            boolean success = DBHelper.deleteRoom(roomNum);
            if (success) {
                lblSuccess.setText("Room deleted successfully.");
                refreshData();
                clearForm();
            } else {
                lblError.setText("Cannot delete room. It might be referenced in an active reservation.");
            }
        }
    }

    private void handleTableClick() {
        Room selected = tblRooms.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtRoomNumber.setText(selected.getRoomNumber());
            txtRoomNumber.setDisable(true); // Treat room number as primary key
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

    public void refreshData() {
        roomsObservableList.setAll(DBHelper.getAllRooms());
        tblRooms.setItems(roomsObservableList);
    }

    // Helper styling utilities
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

    private void styleComboBox(ComboBox<?> cb) {
        cb.setStyle(
            "-fx-background-color: #1e293b;" +
            "-fx-border-color: #334155;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 5px;"
        );
    }
}
