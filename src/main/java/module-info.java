module com.hotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.hotel to javafx.fxml;
    opens com.hotel.view to javafx.fxml;
    opens com.hotel.model to javafx.base, javafx.fxml;

    exports com.hotel;
    exports com.hotel.model;
    exports com.hotel.database;
    exports com.hotel.view;
}
