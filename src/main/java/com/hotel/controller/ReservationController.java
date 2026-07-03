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
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class for the Reservation view.
 * Handles booking creations, real-time checkout computations,
 * cancellations, and polymorphic billing details.
 */
public class ReservationController {

    @FXML
    private ComboBox<Guest> cmbGuest;

    @FXML
    private ComboBox<Room> cmbRoom;

    @FXML
    private DatePicker dpCheckIn;

    @FXML
    private DatePicker dpCheckOut;

    @FXML
    private Label lblDailyRate;

    @FXML
    private Label lblDuration;

    @FXML
    private Label lblEstimatedTotal;

    @FXML
    private Label lblFormulaHint;

    @FXML
    private Label lblError;

    @FXML
    private Label lblSuccess;

    @FXML
    private Button btnBook;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnCheckout;

    @FXML
    private TableView<Reservation> tblReservations;

    @FXML
    private TableColumn<Reservation, Integer> colId;

    @FXML
    private TableColumn<Reservation, String> colGuestName;

    @FXML
    private TableColumn<Reservation, String> colRoomNumber;

    @FXML
    private TableColumn<Reservation, String> colRoomType;

    @FXML
    private TableColumn<Reservation, String> colCheckIn;

    @FXML
    private TableColumn<Reservation, String> colCheckOut;

    @FXML
    private TableColumn<Reservation, Double> colPrice;

    @FXML
    private TableColumn<Reservation, String> colStatus;

    private ObservableList<Reservation> reservationObservableList = FXCollections.observableArrayList();
    private ObservableList<Guest> guestObservableList = FXCollections.observableArrayList();
    private ObservableList<Room> availableRoomsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Bind Table Columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colGuestName.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        
        colPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colPrice.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.2f", price));
                }
            }
        });

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
                    if ("Active".equalsIgnoreCase(status)) {
                        label.getStyleClass().add("badge-active");
                    } else if ("Checked Out".equalsIgnoreCase(status)) {
                        label.getStyleClass().add("badge-checkout");
                    } else {
                        label.getStyleClass().add("badge-cancelled");
                    }
                    setGraphic(label);
                }
            }
        });

        // 2. Configure ComboBox Converters (for beautiful visual presentation)
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

        // 3. Set date pickers default values
        dpCheckIn.setValue(LocalDate.now());
        dpCheckOut.setValue(LocalDate.now().plusDays(1));

        // 4. Load Data
        loadData();
        calculateBill();
        toggleActionButtons(null);
    }

    private void loadData() {
        // Load reservations log
        reservationObservableList.setAll(DBHelper.getAllReservations());
        tblReservations.setItems(reservationObservableList);

        // Load guests
        guestObservableList.setAll(DBHelper.getAllGuests());
        cmbGuest.setItems(guestObservableList);

        // Load available rooms only
        List<Room> availableRooms = DBHelper.getAllRooms().stream()
                .filter(r -> "Available".equalsIgnoreCase(r.getStatus()))
                .collect(Collectors.toList());
        availableRoomsList.setAll(availableRooms);
        cmbRoom.setItems(availableRoomsList);
    }

    // Triggered when room selection changes
    @FXML
    void handleRoomSelection(ActionEvent event) {
        calculateBill();
    }

    // Triggered when check-in or check-out date changes
    @FXML
    void handleDateSelection(ActionEvent event) {
        calculateBill();
    }

    /**
     * Real-time billing calculation showing Polymorphism.
     * Invokes calculateTotalPrice polymorphically according to Room type.
     */
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

        // Polymorphic method execution (Standard Room vs Deluxe Room vs Suite Room)
        double totalCost = selectedRoom.calculateTotalPrice(nights);
        lblEstimatedTotal.setText(String.format("$%,.2f", totalCost));

        // Display formulas to demonstrate Polymorphism/Inheritance details
        if (selectedRoom instanceof DeluxeRoom) {
            lblFormulaHint.setText("Deluxe Rate: (Base Rate * Nights) + 8% Deluxe Fee");
        } else if (selectedRoom instanceof SuiteRoom) {
            lblFormulaHint.setText("Suite Rate: (Base Rate * Nights) + $50 Service Fee + 12% Luxury Tax");
        } else {
            lblFormulaHint.setText("Standard Rate: Base Rate * Nights");
        }
    }

    @FXML
    void handleCreateBooking(ActionEvent event) {
        lblError.setText("");
        lblSuccess.setText("");

        Guest guest = cmbGuest.getValue();
        Room room = cmbRoom.getValue();
        LocalDate checkIn = dpCheckIn.getValue();
        LocalDate checkOut = dpCheckOut.getValue();

        // 1. Validations
        if (guest == null || room == null || checkIn == null || checkOut == null) {
            lblError.setText("Please select a Guest, Room, and both Check-in/out dates.");
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

        // 2. Calculations
        double finalPrice = room.calculateTotalPrice(nights);

        // 3. Create reservation model
        Reservation res = new Reservation(
            guest.getId(),
            room.getRoomNumber(),
            checkIn.toString(),
            checkOut.toString(),
            finalPrice,
            "Active"
        );

        // 4. Save to Database (Transacted: inserts reservation and updates room status)
        boolean success = DBHelper.createReservation(res);
        if (success) {
            lblSuccess.setText("Booking created successfully!");
            loadData();
            clearForm();
        } else {
            lblError.setText("Booking failed. Please try again.");
        }
    }

    @FXML
    void handleCheckOut(ActionEvent event) {
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
            // Check out reservation (Updates booking status and frees the room)
            boolean success = DBHelper.checkOutReservation(selected.getId(), selected.getRoomNumber());
            if (success) {
                lblSuccess.setText("Guest checked out successfully! Room " + selected.getRoomNumber() + " is now vacant.");
                loadData();
                toggleActionButtons(null);
            } else {
                lblError.setText("Check-out transaction failed.");
            }
        }
    }

    @FXML
    void handleCancelBooking(ActionEvent event) {
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
                loadData();
                toggleActionButtons(null);
            } else {
                lblError.setText("Cancellation operation failed.");
            }
        }
    }

    @FXML
    void handleTableClick(MouseEvent event) {
        Reservation selected = tblReservations.getSelectionModel().getSelectedItem();
        toggleActionButtons(selected);
    }

    /**
     * Disable/Enable action buttons based on selected reservation status.
     */
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
}
