package com.example.netcap;

import Controller.NetCapHomeController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * @author HY
 */
public class NetCapHome extends Application {
@FXML
private TableView packetTable;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/fxml/netcaphome.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setTitle("MyNetCap");
        stage.setScene(scene);
        stage.show();

    }

    public void showPackets(){
//        packetTable.getColumns().add(new NetworkPacket("1","2","3","4","5"));
    }
}
