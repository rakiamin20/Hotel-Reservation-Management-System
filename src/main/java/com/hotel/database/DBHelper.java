package com.hotel.database;

import com.hotel.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DBHelper executes CRUD operations against the SQLite database,
 * converting relational rows into object models polymorphically.
 */
public class DBHelper {

    // ==========================================
    // USER OPERATIONS (Authentication)
    // ==========================================

    public static User authenticate(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
    }

    // ==========================================
    // ROOM OPERATIONS (Polymorphism in action)
    // ==========================================

    public static List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms ORDER BY room_number ASC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String roomNumber = rs.getString("room_number");
                String type = rs.getString("type");
                double baseRate = rs.getDouble("base_rate");
                String status = rs.getString("status");

                // Polymorphic instantiation based on Room Type
                Room room;
                if ("Deluxe".equalsIgnoreCase(type)) {
                    room = new DeluxeRoom(roomNumber, status, baseRate);
                } else if ("Suite".equalsIgnoreCase(type)) {
                    room = new SuiteRoom(roomNumber, status, baseRate);
                } else {
                    room = new StandardRoom(roomNumber, status, baseRate);
                }
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rooms: " + e.getMessage());
        }
        return rooms;
    }

    public static boolean addRoom(Room room) {
        String query = "INSERT INTO rooms (room_number, type, base_rate, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());
            pstmt.setDouble(3, room.getBaseRate());
            pstmt.setString(4, room.getStatus());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding room: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateRoom(Room room) {
        String query = "UPDATE rooms SET type = ?, base_rate = ?, status = ? WHERE room_number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, room.getRoomType());
            pstmt.setDouble(2, room.getBaseRate());
            pstmt.setString(3, room.getStatus());
            pstmt.setString(4, room.getRoomNumber());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating room: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteRoom(String roomNumber) {
        String query = "DELETE FROM rooms WHERE room_number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, roomNumber);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting room: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // GUEST OPERATIONS
    // ==========================================

    public static List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests ORDER BY first_name ASC, last_name ASC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                guests.add(new Guest(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("id_card")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching guests: " + e.getMessage());
        }
        return guests;
    }

    /**
     * Overloaded Guest search method (search by term)
     */
    public static List<Guest> searchGuests(String searchTerm) {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests WHERE first_name LIKE ? OR last_name LIKE ? OR id_card LIKE ? OR phone LIKE ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String wildcard = "%" + searchTerm + "%";
            pstmt.setString(1, wildcard);
            pstmt.setString(2, wildcard);
            pstmt.setString(3, wildcard);
            pstmt.setString(4, wildcard);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    guests.add(new Guest(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("id_card")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching guests: " + e.getMessage());
        }
        return guests;
    }

    /**
     * Overloaded Guest search method (search by integer ID)
     */
    public static Guest searchGuests(int guestId) {
        String query = "SELECT * FROM guests WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, guestId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Guest(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("id_card")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching guest by ID: " + e.getMessage());
        }
        return null;
    }

    public static boolean addGuest(Guest guest) {
        String query = "INSERT INTO guests (first_name, last_name, email, phone, id_card) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, guest.getFirstName());
            pstmt.setString(2, guest.getLastName());
            pstmt.setString(3, guest.getEmail());
            pstmt.setString(4, guest.getPhone());
            pstmt.setString(5, guest.getIdCard());
            
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        guest.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding guest: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateGuest(Guest guest) {
        String query = "UPDATE guests SET first_name = ?, last_name = ?, email = ?, phone = ?, id_card = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, guest.getFirstName());
            pstmt.setString(2, guest.getLastName());
            pstmt.setString(3, guest.getEmail());
            pstmt.setString(4, guest.getPhone());
            pstmt.setString(5, guest.getIdCard());
            pstmt.setInt(6, guest.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating guest: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteGuest(int guestId) {
        String query = "DELETE FROM guests WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, guestId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting guest: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // RESERVATION OPERATIONS
    // ==========================================

    public static List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, (g.first_name || ' ' || g.last_name) AS guest_name, rm.type AS room_type " +
                       "FROM reservations r " +
                       "JOIN guests g ON r.guest_id = g.id " +
                       "JOIN rooms rm ON r.room_number = rm.room_number " +
                       "ORDER BY r.id DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                reservations.add(new Reservation(
                    rs.getInt("id"),
                    rs.getInt("guest_id"),
                    rs.getString("guest_name"),
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getString("check_in_date"),
                    rs.getString("check_out_date"),
                    rs.getDouble("total_price"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reservations: " + e.getMessage());
        }
        return reservations;
    }

    /**
     * Books a room. Operates inside a transaction:
     * 1. Inserts the reservation.
     * 2. Sets the room status to 'Occupied'.
     */
    public static boolean createReservation(Reservation res) {
        String insertQuery = "INSERT INTO reservations (guest_id, room_number, check_in_date, check_out_date, total_price, status) " +
                             "VALUES (?, ?, ?, ?, ?, ?)";
        String updateRoomQuery = "UPDATE rooms SET status = 'Occupied' WHERE room_number = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Insert reservation
            try (PreparedStatement pstmtRes = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmtRes.setInt(1, res.getGuestId());
                pstmtRes.setString(2, res.getRoomNumber());
                pstmtRes.setString(3, res.getCheckInDate());
                pstmtRes.setString(4, res.getCheckOutDate());
                pstmtRes.setDouble(5, res.getTotalPrice());
                pstmtRes.setString(6, res.getStatus());
                
                int affected = pstmtRes.executeUpdate();
                if (affected == 0) {
                    conn.rollback();
                    return false;
                }
                
                try (ResultSet keys = pstmtRes.getGeneratedKeys()) {
                    if (keys.next()) {
                        res.setId(keys.getInt(1));
                    }
                }
            }

            // 2. Update room status if checking in immediately (Active)
            if ("Active".equalsIgnoreCase(res.getStatus())) {
                try (PreparedStatement pstmtRoom = conn.prepareStatement(updateRoomQuery)) {
                    pstmtRoom.setString(1, res.getRoomNumber());
                    pstmtRoom.executeUpdate();
                }
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating reservation (transacted): " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Checks out a guest. Operates inside a transaction:
     * 1. Updates reservation status to 'Checked Out'.
     * 2. Sets room status back to 'Available'.
     */
    public static boolean checkOutReservation(int resId, String roomNumber) {
        String updateResQuery = "UPDATE reservations SET status = 'Checked Out' WHERE id = ?";
        String updateRoomQuery = "UPDATE rooms SET status = 'Available' WHERE room_number = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Update reservation status
            try (PreparedStatement pstmtRes = conn.prepareStatement(updateResQuery)) {
                pstmtRes.setInt(1, resId);
                int affected = pstmtRes.executeUpdate();
                if (affected == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 2. Free the room
            try (PreparedStatement pstmtRoom = conn.prepareStatement(updateRoomQuery)) {
                pstmtRoom.setString(1, roomNumber);
                pstmtRoom.executeUpdate();
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            System.err.println("Error executing checkout (transacted): " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Cancels a reservation. Operates inside a transaction:
     * 1. Updates reservation status to 'Cancelled'.
     * 2. Sets room status back to 'Available'.
     */
    public static boolean cancelReservation(int resId, String roomNumber) {
        String updateResQuery = "UPDATE reservations SET status = 'Cancelled' WHERE id = ?";
        String updateRoomQuery = "UPDATE rooms SET status = 'Available' WHERE room_number = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Update reservation status
            try (PreparedStatement pstmtRes = conn.prepareStatement(updateResQuery)) {
                pstmtRes.setInt(1, resId);
                int affected = pstmtRes.executeUpdate();
                if (affected == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 2. Free the room
            try (PreparedStatement pstmtRoom = conn.prepareStatement(updateRoomQuery)) {
                pstmtRoom.setString(1, roomNumber);
                pstmtRoom.executeUpdate();
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            System.err.println("Error cancelling reservation: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}
