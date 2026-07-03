package com.hotel.controller;

import com.hotel.database.DBHelper;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class for the HomeController (Dashboard Home view).
 * Aggregates database statistics and shows active check-ins.
 */
public class HomeController {

    @FXML
    private Label lblTotalRooms;

    @FXML
    private Label lblAvailableRooms;

    @FXML
    private Label lblOccupiedRooms;

    @FXML
    private Label lblTotalRevenue;

    @FXML
    private TableView<Reservation> tblRecentReservations;

    @FXML
    private TableColumn<Reservation, Integer> colResId;

    @FXML
    private TableColumn<Reservation, String> colGuest;

    @FXML
    private TableColumn<Reservation, String> colRoom;

    @FXML
    private TableColumn<Reservation, String> colRoomType;

    @FXML
    private TableColumn<Reservation, String> colCheckIn;

    @FXML
    private TableColumn<Reservation, String> colCheckOut;

    @FXML
    private TableColumn<Reservation, Double> colPrice;

    private ObservableList<Reservation> activeReservationsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Bind Table Columns to Reservation Fields (Encapsulation)
        colResId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colGuest.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        colRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        
        // Custom format for price column
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

        // 2. Load Stats and Populate List
        loadDashboardStats();
    }

    /**
     * Queries database to calculate high-level statistics and populate the TableView.
     */
    private void loadDashboardStats() {
        try {
            List<Room> allRooms = DBHelper.getAllRooms();
            List<Reservation> allReservations = DBHelper.getAllReservations();

            // Total rooms
            int totalRooms = allRooms.size();
            lblTotalRooms.setText(String.valueOf(totalRooms));

            // Available rooms
            long availableRooms = allRooms.stream().filter(r -> "Available".equalsIgnoreCase(r.getStatus())).count();
            lblAvailableRooms.setText(String.valueOf(availableRooms));

            // Occupied rooms
            long occupiedRooms = allRooms.stream().filter(r -> "Occupied".equalsIgnoreCase(r.getStatus())).count();
            lblOccupiedRooms.setText(String.valueOf(occupiedRooms));

            // Total revenue (from all active and checked-out reservations)
            double revenue = allReservations.stream()
                    .filter(res -> !"Cancelled".equalsIgnoreCase(res.getStatus()))
                    .mapToDouble(Reservation::getTotalPrice)
                    .sum();
            lblTotalRevenue.setText(String.format("$%,.2f", revenue));

            // Populate table with active check-ins only
            List<Reservation> activeCheckins = allReservations.stream()
                    .filter(res -> "Active".equalsIgnoreCase(res.getStatus()))
                    .collect(Collectors.toList());
            
            activeReservationsList.setAll(activeCheckins);
            tblRecentReservations.setItems(activeReservationsList);

        } catch (Exception e) {
            System.err.println("Error loading dashboard stats: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
