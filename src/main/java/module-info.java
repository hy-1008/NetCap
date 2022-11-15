module com.example.netcap {
    requires javafx.controls;
    requires javafx.fxml;
    requires jpcap;


    opens com.example.netcap to javafx.fxml;
    exports com.example.netcap;
    exports Controller;
    opens Controller to javafx.fxml;
}