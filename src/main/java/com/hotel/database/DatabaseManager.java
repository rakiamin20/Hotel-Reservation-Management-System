package com.hotel.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * DatabaseManager handles the SQLite connection lifecycle,
 * schema creation, and seeding of initial mock data.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:hotel_reservation.db";
    private static Connection connection = null;

    /**
     * Obtains the database connection.
     * Implements basic connection management and exception handling.
     */
    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            System.err.println("Error establishing database connection: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Initializes the database schema (tables) if they do not exist,
     * and seeds initial data if the tables are empty.
     */
    public static void initializeDatabase() {
        // Use getConnection() directly — do NOT wrap in try-with-resources,
        // as that would close the shared static connection and break all later queries.
        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("Database initialization failed: could not obtain connection.");
            return;
        }
        try (Statement stmt = conn.createStatement()) {

            // Enable Foreign Keys in SQLite
            stmt.execute("PRAGMA foreign_keys = ON;");

            // 1. Users Table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "first_name TEXT NOT NULL," +
                    "last_name TEXT NOT NULL," +
                    "email TEXT," +
                    "phone TEXT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "role TEXT NOT NULL" +
                    ");");

            // 2. Rooms Table
            stmt.execute("CREATE TABLE IF NOT EXISTS rooms (" +
                    "room_number TEXT PRIMARY KEY," +
                    "type TEXT NOT NULL," +
                    "base_rate REAL NOT NULL," +
                    "status TEXT NOT NULL" +
                    ");");

            // 3. Guests Table
            stmt.execute("CREATE TABLE IF NOT EXISTS guests (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "first_name TEXT NOT NULL," +
                    "last_name TEXT NOT NULL," +
                    "email TEXT NOT NULL," +
                    "phone TEXT NOT NULL," +
                    "id_card TEXT UNIQUE NOT NULL" +
                    ");");

            // 4. Reservations Table
            stmt.execute("CREATE TABLE IF NOT EXISTS reservations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "guest_id INTEGER NOT NULL," +
                    "room_number TEXT NOT NULL," +
                    "check_in_date TEXT NOT NULL," +
                    "check_out_date TEXT NOT NULL," +
                    "total_price REAL NOT NULL," +
                    "status TEXT NOT NULL," +
                    "FOREIGN KEY(guest_id) REFERENCES guests(id) ON DELETE CASCADE," +
                    "FOREIGN KEY(room_number) REFERENCES rooms(room_number) ON DELETE RESTRICT" +
                    ");");

            System.out.println("Database tables checked/created successfully.");

            // Seed Initial Data
            seedData(conn);

        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Seeds default values if database tables are empty.
     */
    private static void seedData(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            // Check if users exist. If not, insert default admin
            ResultSet rsUser = stmt.executeQuery("SELECT COUNT(*) FROM users;");
            if (rsUser.next() && rsUser.getInt(1) == 0) {
                stmt.execute("INSERT INTO users (first_name, last_name, email, phone, username, password, role) " +
                        "VALUES ('System', 'Admin', 'admin@hotel.com', '1234567890', 'admin', 'admin', 'ADMIN');");
                System.out.println("Seeded default admin user (admin/admin).");
            }
            rsUser.close();

            // Check if rooms exist. If not, insert default rooms
            ResultSet rsRoom = stmt.executeQuery("SELECT COUNT(*) FROM rooms;");
            if (rsRoom.next() && rsRoom.getInt(1) == 0) {
                // Seed Standard Rooms
                stmt.execute("INSERT INTO rooms (room_number, type, base_rate, status) VALUES ('101', 'Standard', 80.00, 'Available');");
                stmt.execute("INSERT INTO rooms (room_number, type, base_rate, status) VALUES ('102', 'Standard', 80.00, 'Available');");
                stmt.execute("INSERT INTO rooms (room_number, type, base_rate, status) VALUES ('103', 'Standard', 85.00, 'Maintenance');");

                // Seed Deluxe Rooms
                stmt.execute("INSERT INTO rooms (room_number, type, base_rate, status) VALUES ('201', 'Deluxe', 150.00, 'Available');");
                stmt.execute("INSERT INTO rooms (room_number, type, base_rate, status) VALUES ('202', 'Deluxe', 150.00, 'Available');");
                stmt.execute("INSERT INTO rooms (room_number, type, base_rate, status) VALUES ('203', 'Deluxe', 160.00, 'Available');");

                // Seed Suite Rooms
                stmt.execute("INSERT INTO rooms (room_number, type, base_rate, status) VALUES ('301', 'Suite', 300.00, 'Available');");
                stmt.execute("INSERT INTO rooms (room_number, type, base_rate, status) VALUES ('302', 'Suite', 350.00, 'Available');");

                System.out.println("Seeded default rooms.");
            }
            rsRoom.close();

            // Check if guests exist. If not, insert a couple of mock guests
            ResultSet rsGuest = stmt.executeQuery("SELECT COUNT(*) FROM guests;");
            if (rsGuest.next() && rsGuest.getInt(1) == 0) {
                stmt.execute("INSERT INTO guests (first_name, last_name, email, phone, id_card) VALUES ('John', 'Doe', 'john.doe@gmail.com', '555-0199', 'ID-9938');");
                stmt.execute("INSERT INTO guests (first_name, last_name, email, phone, id_card) VALUES ('Jane', 'Smith', 'jane.smith@yahoo.com', '555-0144', 'ID-1029');");
                stmt.execute("INSERT INTO guests (first_name, last_name, email, phone, id_card) VALUES ('Alice', 'Johnson', 'alice.j@hotmail.com', '555-0182', 'ID-8847');");
                System.out.println("Seeded default guests.");
            }
            rsGuest.close();

            // Check if reservations exist. If not, insert a couple of mock reservations
            ResultSet rsRes = stmt.executeQuery("SELECT COUNT(*) FROM reservations;");
            if (rsRes.next() && rsRes.getInt(1) == 0) {
                // Book Room 101 for John Doe (guest_id=1)
                stmt.execute("INSERT INTO reservations (guest_id, room_number, check_in_date, check_out_date, total_price, status) " +
                        "VALUES (1, '101', '2026-07-01', '2026-07-05', 320.00, 'Active');");
                stmt.execute("UPDATE rooms SET status = 'Occupied' WHERE room_number = '101';");

                // Book Room 201 for Jane Smith (guest_id=2)
                // Deluxe Room (base_rate = 150, total price with 8% charge = 150 * 2 * 1.08 = 324)
                stmt.execute("INSERT INTO reservations (guest_id, room_number, check_in_date, check_out_date, total_price, status) " +
                        "VALUES (2, '201', '2026-07-02', '2026-07-04', 324.00, 'Active');");
                stmt.execute("UPDATE rooms SET status = 'Occupied' WHERE room_number = '201';");

                System.out.println("Seeded default reservations.");
            }
            rsRes.close();
        }
    }
}
