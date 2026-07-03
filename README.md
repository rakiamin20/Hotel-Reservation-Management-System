# Hotel Reservation Management System

A premium, fully-functional **GUI-based Java Desktop Application** for hotel reservation and billing management. The application is styled programmatically with a sleek dark-slate theme and amber-gold highlights — **zero CSS files, zero FXML files**. All layouts, components, and styling are defined entirely in Java.

This project demonstrates a strong understanding of **Object-Oriented Programming (OOP)** including Encapsulation, Inheritance, Polymorphism, and Abstraction, along with event-driven programming, exception handling, collections, input validation, and modular GUI component design.

---

## 📸 Screenshots

### 1. Staff Authentication Interface
![Staff Login](screenshots/login.png)

### 2. Dashboard Analytics & Active Check-Ins
![Dashboard](screenshots/dashboard.png)

### 3. Bookings & Real-Time Polymorphic Billing
![Bookings](screenshots/booking.png)

---

##  Key Features

1. **Secure Login** — Credential validation against the embedded SQLite database.
2. **Room Management** — Add, update, and delete rooms (Standard / Deluxe / Suite) with badge status display.
3. **Guest Directory** — Searchable guest profiles with regex phone and email validation.
4. **Booking & Billing Engine** —
   - Only available rooms shown in dropdowns.
   - Real-time price calculation using polymorphic `calculateTotalPrice()` per room type.
   - Billing formula hint label (e.g., "Base × Nights + 8% Fee" for Deluxe).
5. **Check-Out & Cancellation** — Transactional DB updates to both the reservation and the room vacancy status simultaneously.
6. **Zero-Config SQLite** — `hotel_reservation.db` auto-creates on first launch with mock rooms, guests, and reservations.

---

##  OOP Concepts Demonstrated

### A. Abstraction
- **`Person`** (Abstract Class): Abstract `getDetails()` method implemented by subclasses.
- **`Room`** (Abstract Class): Abstract `getRoomType()` and `getAmenities()` methods; concrete `calculateTotalPrice(int nights)` overridden by subclasses.

### B. Inheritance
- `Guest` and `User` extend `Person`.
- `StandardRoom`, `DeluxeRoom`, `SuiteRoom` extend `Room`.

### C. Polymorphism
- **Method Overriding**: `DeluxeRoom.calculateTotalPrice()` adds 8% surcharge. `SuiteRoom.calculateTotalPrice()` adds $50 flat fee and 12% luxury tax.
- **Method Overloading**: `DBHelper.searchGuests(String term)` vs `DBHelper.searchGuests(int id)`.
- **Runtime dispatch**: `ReservationView` calls `room.calculateTotalPrice(nights)` on a `Room` reference, automatically invoking the correct subclass pricing logic.

### D. Encapsulation
- All fields in model classes are `private`.
- Full public getter/setter access control enforced.
- Form input validations prevent malformed data from entering the object graph.

---

## Project Structure

```
HotelReservationSystem/
├── src/main/java/
│   ├── module-info.java
│   └── com/hotel/
│       ├── MainApp.java          # JavaFX Application entry point
│       ├── Launcher.java         # Fallback main() wrapper
│       ├── model/                # OOP domain models
│       │   ├── Person.java       (Abstract)
│       │   ├── Guest.java
│       │   ├── User.java
│       │   ├── Room.java         (Abstract)
│       │   ├── StandardRoom.java
│       │   ├── DeluxeRoom.java
│       │   ├── SuiteRoom.java
│       │   └── Reservation.java
│       ├── database/
│       │   ├── DatabaseManager.java  # SQLite connection & schema init
│       │   └── DBHelper.java         # CRUD operations & transactions
│       └── view/                 # Pure-Java programmatic GUI (NO FXML/CSS)
│           ├── LoginView.java
│           ├── DashboardView.java
│           ├── HomeView.java
│           ├── RoomView.java
│           ├── GuestView.java
│           └── ReservationView.java
├── screenshots/
├── pom.xml                       # Maven build config (JavaFX + SQLite deps)
├── README.md
└── TECHNICAL_REPORT.md
```

---

## Quick Start

### Requirements
- **JDK 17+** installed
- **IntelliJ IDEA** (recommended) or any Maven-aware IDE

### Steps in IntelliJ IDEA
1. Open IntelliJ IDEA → **File > Open** → select the extracted folder (containing `pom.xml`).
2. IntelliJ detects the Maven project and downloads dependencies automatically.
3. Run via Maven toolbar: **Plugins → javafx → javafx:run**, or right-click `Launcher.java` → **Run**.

### Command Line (requires Maven installed)
```bash
mvn compile javafx:run
```

### Default Admin Credentials
| Username | Password |
|----------|----------|
| `admin`  | `admin`  |

*(Database and seed data auto-generated on first launch.)*
