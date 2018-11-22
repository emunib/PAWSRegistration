package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

public class SecondaryController {
    // ...

    public GridPane schedule;
    public HashSet<String> currentClasses;

    @FXML
    private URL location;

    @FXML
    private ResourceBundle resources;

    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void removeClass(ActionEvent event) {
        String title = ((Label) ((Button) event.getSource()).getParent().getParent().getParent().lookup("#title")).getText();
        System.out.println(schedule);
        for (Node node : schedule.lookupAll("#title")) {
            if (((Label) node).getText().equals(title)) {
                schedule.getChildren().remove(node.getParent());
            }
        }

        currentClasses.remove(title);

        this.closeWindow(event);
    }
}

