package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @author HY
 */
public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}