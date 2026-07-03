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
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Programmatic JavaFX view for Reservation Booking and Invoicing logs.
 * Executes polymorphic pricing estimates in real-time.
 */
public class ReservationView extends HBox {

    private ComboBox<Guest> cmbGuest;
    private ComboBox<Room> cmbRoom;
    private DatePicker dpCheckIn;
    private DatePicker dpCheckOut;

    private Label lblDailyRate;
    private Label lblDuration;
    private Label lblEstimatedTotal;
    private Label lblFormulaHint;

    private Label lblError;
    private Label lblSuccess;

    private TableView<Reservation> tblReservations;
    private ObservableList<Reservation> reservationObservableList = FXCollections.observableArrayList();
    private ObservableList<Guest> guestObservableList = FXCollections.observableArrayList();
    private ObservableList<Room> availableRoomsList = FXCollections.observableArrayList();

    private Button btnBook;
    private Button btnCancel;
    private Button btnCheckout;

    public ReservationView() {
        this.setSpacing(30);
        this.setStyle("-fx-background-color: transparent;");

        // 1. Create Left Form
        VBox formCard = createBookingForm();

        // 2. Create Right Log Panel
        VBox logContainer = createReservationLog();

        // Assemble HBox
        this.getChildren().addAll(formCard, logContainer);
        HBox.setHgrow(logContainer, Priority.ALWAYS);

        // Configure ComboBox converters programmatically
        configureComboBoxConverters();

        // Attach listeners for real-time calculations
        cmbRoom.setOnAction(e -> calculateBill());
        dpCheckIn.setOnAction(e -> calculateBill());
        dpCheckOut.setOnAction(e -> calculateBill());

        // Initialize Defaults
        dpCheckIn.setValue(LocalDate.now());
        dpCheckOut.setValue(LocalDate.now().plusDays(1));

        // Load records
        refreshData();
        calculateBill();
        toggleActionButtons(null);
    }

    private VBox createBookingForm() {
        VBox formCard = new VBox(15);
        formCard.setPrefWidth(360);
        formCard.setMinWidth(360);
        formCard.setPadding(new Insets(24));
        formCard.setStyle(
            "-fx-background-color: #1e293b;" +
            "-fx-background-radius: 16px;" +
            "-fx-border-color: #334155;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 16px;"
        );

        Label lblFormTitle = new Label("New Reservation");
        lblFormTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Select Guest dropdown
        VBox guestBox = new VBox(6);
        Label lblGuest = new Label("Select Guest");
        lblGuest.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        cmbGuest = new ComboBox<>(guestObservableList);
        cmbGuest.setPromptText("Choose Guest");
        cmbGuest.setPrefWidth(312);
        styleComboBox(cmbGuest);
        guestBox.getChildren().addAll(lblGuest, cmbGuest);

        // Select Room dropdown
        VBox roomBox = new VBox(6);
        Label lblRoom = new Label("Select Room (Available Only)");
        lblRoom.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        cmbRoom = new ComboBox<>(availableRoomsList);
        cmbRoom.setPromptText("Choose Room");
        cmbRoom.setPrefWidth(312);
        styleComboBox(cmbRoom);
        roomBox.getChildren().addAll(lblRoom, cmbRoom);

        // Dates pickers
        VBox dateBox = new VBox(10);
        HBox datePickersRow = new HBox(10);
        
        VBox inBox = new VBox(6);
        Label lblIn = new Label("Check-In Date");
        lblIn.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        dpCheckIn = new DatePicker();
        dpCheckIn.setPrefWidth(151);
        styleDatePicker(dpCheckIn);
        inBox.getChildren().addAll(lblIn, dpCheckIn);

        VBox outBox = new VBox(6);
        Label lblOut = new Label("Check-Out Date");
        lblOut.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 500; -fx-font-size: 13px;");
        dpCheckOut = new DatePicker();
        dpCheckOut.setPrefWidth(151);
        styleDatePicker(dpCheckOut);
        outBox.getChildren().addAll(lblOut, dpCheckOut);

        datePickersRow.getChildren().addAll(inBox, outBox);
        dateBox.getChildren().addAll(datePickersRow);

        // Real-time Bill Breakdown Panel
        VBox billBreakdown = new VBox(8);
        billBreakdown.setPadding(new Insets(15));
        billBreakdown.setStyle(
            "-fx-background-color: #0f172a;" +
            "-fx-padding: 15px;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-color: #334155;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 10px;"
        );

        HBox dailyRow = new HBox();
        Label lblDText = new Label("Daily Rate: ");
        lblDText.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        lblDailyRate = new Label("$0.00");
        lblDailyRate.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 12px;");
        dailyRow.getChildren().addAll(lblDText, lblDailyRate);

        HBox durationRow = new HBox();
        Label lblDurText = new Label("Duration: ");
        lblDurText.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        lblDuration = new Label("0 Nights");
        lblDuration.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 12px;");
        durationRow.getChildren().addAll(lblDurText, lblDuration);

        // Border separator line
        Region borderLine = new Region();
        borderLine.setPrefHeight(1);
        borderLine.setStyle("-fx-background-color: #334155;");

        HBox totalRow = new HBox();
        Label lblTText = new Label("Estimated Total: ");
        lblTText.setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold; -fx-font-size: 14px;");
        lblEstimatedTotal = new Label("$0.00");
        lblEstimatedTotal.setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: 700; -fx-font-size: 16px;");
        totalRow.getChildren().addAll(lblTText, lblEstimatedTotal);

        lblFormulaHint = new Label("");
        lblFormulaHint.setWrapText(true);
        lblFormulaHint.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");

        billBreakdown.getChildren().addAll(dailyRow, durationRow, borderLine, totalRow, lblFormulaHint);

        // Feedback labels
        lblError = new Label("");
        lblError.setWrapText(true);
        lblError.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: 500;");

        lblSuccess = new Label("");
        lblSuccess.setWrapText(true);
        lblSuccess.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: 500;");

        // Book Submit Button
        btnBook = new Button("Confirm Booking");
        btnBook.setPrefHeight(40);
        btnBook.setPrefWidth(312);
        btnBook.setStyle("-fx-background-color: linear-gradient(to right, #d97706, #f59e0b); -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-cursor: hand;");
        btnBook.setOnAction(e -> handleCreateBooking());

        formCard.getChildren().addAll(lblFormTitle, guestBox, roomBox, dateBox, billBreakdown, lblError, lblSuccess, btnBook);
        return formCard;
    }

    private VBox createReservationLog() {
        VBox container = new VBox(15);

        Label lblLogTitle = new Label("Reservation Log");
        lblLogTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Table initialization
        tblReservations = new TableView<>();
        tblReservations.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: #334155;" +
            "-fx-border-radius: 12px;" +
            "-fx-background-radius: 12px;"
        );
        VBox.setVgrow(tblReservations, Priority.ALWAYS);

        // Columns binding
        TableColumn<Reservation, Integer> colId = new TableColumn<>("Res ID");
        colId.setPrefWidth(70);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Reservation, String> colGuest = new TableColumn<>("Guest Name");
        colGuest.setPrefWidth(160);
        colGuest.setCellValueFactory(new PropertyValueFactory<>("guestName"));

        TableColumn<Reservation, String> colRoom = new TableColumn<>("Room");
        colRoom.setPrefWidth(90);
        colRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Reservation, String> colType = new TableColumn<>("Type");
        colType.setPrefWidth(90);
        colType.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        TableColumn<Reservation, String> colIn = new TableColumn<>("Check-In");
        colIn.setPrefWidth(110);
        colIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));

        TableColumn<Reservation, String> colOut = new TableColumn<>("Check-Out");
        colOut.setPrefWidth(110);
        colOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        TableColumn<Reservation, Double> colPrice = new TableColumn<>("Total Price");
        colPrice.setPrefWidth(100);
        colPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colPrice.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.2f", val));
                }
            }
        });

        TableColumn<Reservation, String> colStatus = new TableColumn<>("Status");
        colStatus.setPrefWidth(100);
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        // Status Badges format
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(val);
                    badge.setPadding(new Insets(4, 8, 4, 8));
                    badge.setStyle("-fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");
                    
                    if ("Active".equalsIgnoreCase(val)) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: rgba(59, 130, 246, 0.15); -fx-text-fill: #60a5fa;");
                    } else if ("Checked Out".equalsIgnoreCase(val)) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: rgba(107, 114, 128, 0.15); -fx-text-fill: #9ca3af;");
                    } else {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: rgba(244, 63, 94, 0.15); -fx-text-fill: #fb7185;");
                    }
                    setGraphic(badge);
                }
            }
        });

        tblReservations.getColumns().addAll(colId, colGuest, colRoom, colType, colIn, colOut, colPrice, colStatus);
        tblReservations.setOnMouseClicked(e -> handleTableClick());

        // Action Toolbar at Bottom
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_RIGHT);
        toolbar.setPadding(new Insets(15));
        toolbar.setStyle(
            "-fx-background-color: #1e293b;" +
            "-fx-background-radius: 12px;" +
            "-fx-border-color: #334155;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 12px;"
        );

        Label lblPrompt = new Label("Selected Booking Actions:");
        lblPrompt.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        btnCancel = new Button("Cancel Booking");
        btnCancel.setStyle("-fx-background-color: #ef4444; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-cursor: hand;");
        btnCancel.setPrefHeight(35);
        btnCancel.setOnAction(e -> handleCancelBooking());

        btnCheckout = new Button("Complete Check-Out");
        btnCheckout.setStyle("-fx-background-color: linear-gradient(to right, #d97706, #f59e0b); -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-cursor: hand;");
        btnCheckout.setPrefHeight(35);
        btnCheckout.setOnAction(e -> handleCheckOut());

        toolbar.getChildren().addAll(lblPrompt, spacer, btnCancel, btnCheckout);

        container.getChildren().addAll(lblLogTitle, tblReservations, toolbar);
        return container;
    }

    private void configureComboBoxConverters() {
        cmbGuest.setConverter(new StringConverter<>() {
            @Override
            public String toString(Guest guest) {
                if (guest == null) return "";
                return guest.getFullName() + " (ID Card: " + guest.getIdCard() + ")";
            }
            @Override
            public Guest fromString(String string) { return null; }
        });

        cmbRoom.setConverter(new StringConverter<>() {
            @Override
            public String toString(Room room) {
                if (room == null) return "";
                return "Room " + room.getRoomNumber() + " [" + room.getRoomType() + "] - $" + room.getBaseRate() + "/night";
            }
            @Override
            public Room fromString(String string) { return null; }
        });
    }

    private void calculateBill() {
        lblDailyRate.setText("$0.00");
        lblDuration.setText("0 Nights");
        lblEstimatedTotal.setText("$0.00");
        lblFormulaHint.setText("");

        Room selectedRoom = cmbRoom.getValue();
        LocalDate checkIn = dpCheckIn.getValue();
        LocalDate checkOut = dpCheckOut.getValue();

        if (selectedRoom == null || checkIn == null || checkOut == null) {
            return;
        }

        lblDailyRate.setText(String.format("$%,.2f", selectedRoom.getBaseRate()));

        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        int nights = (days <= 0) ? 0 : (int) days;
        lblDuration.setText(nights + " Night" + (nights == 1 ? "" : "s"));

        if (nights <= 0) {
            lblEstimatedTotal.setText("$0.00");
            lblFormulaHint.setText("Check-out date must be after Check-in date.");
            return;
        }

        // Polymorphic Pricing Calculation
        double totalCost = selectedRoom.calculateTotalPrice(nights);
        lblEstimatedTotal.setText(String.format("$%,.2f", totalCost));

        // Subclass validation details
        if (selectedRoom instanceof DeluxeRoom) {
            lblFormulaHint.setText("Deluxe Rate: (Base Rate * Nights) + 8% Deluxe Fee");
        } else if (selectedRoom instanceof SuiteRoom) {
            lblFormulaHint.setText("Suite Rate: (Base Rate * Nights) + $50 Service Fee + 12% Luxury Tax");
        } else {
            lblFormulaHint.setText("Standard Rate: Base Rate * Nights");
        }
    }

    private void handleCreateBooking() {
        lblError.setText("");
        lblSuccess.setText("");

        Guest guest = cmbGuest.getValue();
        Room room = cmbRoom.getValue();
        LocalDate checkIn = dpCheckIn.getValue();
        LocalDate checkOut = dpCheckOut.getValue();

        if (guest == null || room == null || checkIn == null || checkOut == null) {
            lblError.setText("Please select a Guest, Room, and both check-in/out dates.");
            return;
        }

        if (checkIn.isBefore(LocalDate.now())) {
            lblError.setText("Check-in date cannot be in the past.");
            return;
        }

        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        int nights = (int) days;
        if (nights <= 0) {
            lblError.setText("Check-out date must be after check-in date.");
            return;
        }

        double finalPrice = room.calculateTotalPrice(nights);

        Reservation res = new Reservation(
            guest.getId(),
            room.getRoomNumber(),
            checkIn.toString(),
            checkOut.toString(),
            finalPrice,
            "Active"
        );

        boolean success = DBHelper.createReservation(res);
        if (success) {
            lblSuccess.setText("Booking created successfully!");
            refreshData();
            clearForm();
        } else {
            lblError.setText("Booking failed. Please try again.");
        }
    }

    private void handleCheckOut() {
        lblError.setText("");
        lblSuccess.setText("");
        Reservation selected = tblReservations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            lblError.setText("Please select an active reservation from the log.");
            return;
        }

        if (!"Active".equalsIgnoreCase(selected.getStatus())) {
            lblError.setText("Only Active bookings can be checked out.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                "Proceed to check-out guest " + selected.getGuestName() + " from Room " + selected.getRoomNumber() + "?", 
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Complete Guest Check-Out");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            boolean success = DBHelper.checkOutReservation(selected.getId(), selected.getRoomNumber());
            if (success) {
                lblSuccess.setText("Guest checked out successfully! Room " + selected.getRoomNumber() + " is now vacant.");
                refreshData();
                toggleActionButtons(null);
            } else {
                lblError.setText("Check-out transaction failed.");
            }
        }
    }

    private void handleCancelBooking() {
        lblError.setText("");
        lblSuccess.setText("");
        Reservation selected = tblReservations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            lblError.setText("Please select a reservation to cancel.");
            return;
        }

        if (!"Active".equalsIgnoreCase(selected.getStatus())) {
            lblError.setText("Only Active bookings can be cancelled.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                "Are you sure you want to cancel the booking for " + selected.getGuestName() + "?", 
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Cancel Reservation");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            boolean success = DBHelper.cancelReservation(selected.getId(), selected.getRoomNumber());
            if (success) {
                lblSuccess.setText("Reservation cancelled successfully.");
                refreshData();
                toggleActionButtons(null);
            } else {
                lblError.setText("Cancellation operation failed.");
            }
        }
    }

    private void handleTableClick() {
        Reservation selected = tblReservations.getSelectionModel().getSelectedItem();
        toggleActionButtons(selected);
    }

    private void toggleActionButtons(Reservation res) {
        if (res == null || !"Active".equalsIgnoreCase(res.getStatus())) {
            btnCheckout.setDisable(true);
            btnCancel.setDisable(true);
        } else {
            btnCheckout.setDisable(false);
            btnCancel.setDisable(false);
        }
    }

    private void clearForm() {
        cmbGuest.setValue(null);
        cmbRoom.setValue(null);
        dpCheckIn.setValue(LocalDate.now());
        dpCheckOut.setValue(LocalDate.now().plusDays(1));
        calculateBill();
        lblError.setText("");
    }

    public void refreshData() {
        // Log Table list
        reservationObservableList.setAll(DBHelper.getAllReservations());
        tblReservations.setItems(reservationObservableList);

        // Guest Combo
        guestObservableList.setAll(DBHelper.getAllGuests());
        cmbGuest.setItems(guestObservableList);

        // Room Combo (only Available rooms)
        List<Room> availableRooms = DBHelper.getAllRooms().stream()
                .filter(r -> "Available".equalsIgnoreCase(r.getStatus()))
                .collect(Collectors.toList());
        availableRoomsList.setAll(availableRooms);
        cmbRoom.setItems(availableRoomsList);
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

    private void styleDatePicker(DatePicker dp) {
        dp.setStyle(
            "-fx-background-color: #1e293b;" +
            "-fx-border-color: #334155;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;"
        );
    }
}
