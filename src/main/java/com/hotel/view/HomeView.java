package com.hotel.view;

import com.hotel.database.DBHelper;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Programmatic JavaFX view for the Dashboard Summary.
 * Renders statistical metrics cards and active checked-in guest records.
 */
public class HomeView extends VBox {

    private Label lblTotalRooms;
    private Label lblAvailableRooms;
    private Label lblOccupiedRooms;
    private Label lblTotalRevenue;

    private TableView<Reservation> tblRecentReservations;
    private ObservableList<Reservation> activeReservationsList = FXCollections.observableArrayList();

    public HomeView() {
        this.setSpacing(30);
        this.setStyle("-fx-background-color: transparent;");

        // 1. Stats Cards Container
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(10);
        
        // Define Columns
        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            statsGrid.getColumnConstraints().add(col);
        }

        // Create individual cards
        VBox cardTotal = createStatCard("Total Rooms", lblTotalRooms = new Label("0"), "Configured in System", "#ffffff");
        VBox cardAvailable = createStatCard("Available Rooms", lblAvailableRooms = new Label("0"), "Ready for Check-In", "#22c55e");
        VBox cardOccupied = createStatCard("Occupied Rooms", lblOccupiedRooms = new Label("0"), "Currently booked", "#ef4444");
        VBox cardRevenue = createStatCard("Total Revenue", lblTotalRevenue = new Label("$0.00"), "Active earnings", "#fbbf24");
        cardRevenue.setStyle(cardRevenue.getStyle() + "-fx-border-color: #d97706;");

        statsGrid.add(cardTotal, 0, 0);
        statsGrid.add(cardAvailable, 1, 0);
        statsGrid.add(cardOccupied, 2, 0);
        statsGrid.add(cardRevenue, 3, 0);

        // 2. Table Section
        VBox tableContainer = new VBox(15);
        tableContainer.setPadding(new Insets(20));
        VBox.setVgrow(tableContainer, Priority.ALWAYS);
        tableContainer.setStyle(
            "-fx-background-color: #1e293b;" +
            "-fx-background-radius: 16px;" +
            "-fx-border-color: #334155;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 16px;"
        );

        Label lblTableTitle = new Label("Current Checked-In Guests");
        lblTableTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Initialize TableView
        tblRecentReservations = new TableView<>();
        tblRecentReservations.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: #334155;" +
            "-fx-border-radius: 12px;" +
            "-fx-background-radius: 12px;"
        );

        // Configure Columns
        TableColumn<Reservation, Integer> colResId = new TableColumn<>("Res ID");
        colResId.setPrefWidth(80);
        colResId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Reservation, String> colGuest = new TableColumn<>("Guest Name");
        colGuest.setPrefWidth(250);
        colGuest.setCellValueFactory(new PropertyValueFactory<>("guestName"));

        TableColumn<Reservation, String> colRoom = new TableColumn<>("Room No.");
        colRoom.setPrefWidth(120);
        colRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Reservation, String> colRoomType = new TableColumn<>("Type");
        colRoomType.setPrefWidth(120);
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        TableColumn<Reservation, String> colCheckIn = new TableColumn<>("Check-In");
        colCheckIn.setPrefWidth(160);
        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));

        TableColumn<Reservation, String> colCheckOut = new TableColumn<>("Check-Out");
        colCheckOut.setPrefWidth(160);
        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        TableColumn<Reservation, Double> colPrice = new TableColumn<>("Total Cost");
        colPrice.setPrefWidth(130);
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

        tblRecentReservations.getColumns().addAll(colResId, colGuest, colRoom, colRoomType, colCheckIn, colCheckOut, colPrice);
        tableContainer.getChildren().addAll(lblTableTitle, tblRecentReservations);

        // Assemble Summary Layout
        this.getChildren().addAll(statsGrid, tableContainer);

        // Load values
        refreshData();
    }

    private VBox createStatCard(String title, Label valLabel, String subtext, String valueColor) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: #1e293b;" +
            "-fx-background-radius: 16px;" +
            "-fx-border-color: #334155;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 16px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 5);"
        );

        Label titleLabel = new Label(title.toUpperCase());
        titleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8; -fx-font-weight: 500;");

        valLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 700; -fx-text-fill: " + valueColor + ";");

        Label subtextLabel = new Label(subtext);
        subtextLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: 500;");

        card.getChildren().addAll(titleLabel, valLabel, subtextLabel);
        return card;
    }

    /**
     * Refreshes stats counts and active check-ins list.
     */
    public void refreshData() {
        try {
            List<Room> allRooms = DBHelper.getAllRooms();
            List<Reservation> allReservations = DBHelper.getAllReservations();

            lblTotalRooms.setText(String.valueOf(allRooms.size()));

            long available = allRooms.stream().filter(r -> "Available".equalsIgnoreCase(r.getStatus())).count();
            lblAvailableRooms.setText(String.valueOf(available));

            long occupied = allRooms.stream().filter(r -> "Occupied".equalsIgnoreCase(r.getStatus())).count();
            lblOccupiedRooms.setText(String.valueOf(occupied));

            double revenue = allReservations.stream()
                    .filter(res -> !"Cancelled".equalsIgnoreCase(res.getStatus()))
                    .mapToDouble(Reservation::getTotalPrice)
                    .sum();
            lblTotalRevenue.setText(String.format("$%,.2f", revenue));

            List<Reservation> activeCheckins = allReservations.stream()
                    .filter(res -> "Active".equalsIgnoreCase(res.getStatus()))
                    .collect(Collectors.toList());
            
            activeReservationsList.setAll(activeCheckins);
            tblRecentReservations.setItems(activeReservationsList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
