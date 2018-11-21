package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Controller {
    private HashMap<String, Integer> timeLookup;
    private HashSet<String> currentClasses;

    @FXML
    private Label title;

    @FXML
    private Label fullTitle;

    @FXML
    private Label description;

    @FXML
    private Label days;

    @FXML
    private Label time;

    @FXML
    private GridPane schedule;

    @FXML
    private URL location;

    @FXML
    private ResourceBundle resources;

    public Controller() {
        this.currentClasses = new HashSet<>();

        this.timeLookup = new HashMap<>();
        String[] times = new String[]{"8:00AM", "8:30AM", "9:00AM", "9:30AM", "10:00AM", "10:30AM", "11:00AM", "11:30AM",
                                      "12:00PM", "12:30PM", "1:00PM", "1:30PM", "2:00PM", "2:30PM", "3:00PM", "3:30PM", "4:00AM"};

        for (int i = 0; i < times.length; i++) {
            timeLookup.put(times[i], i+1);
        }
    }

    @FXML
    private void initialize() {
    }

    @FXML
    private void submitSchedule() {
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("submit.fxml")), 500, 500);
            Stage dialog = new Stage();
            dialog.setTitle("Submit Schedule");
            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void selectSection(MouseEvent event) {
        Pane card = (Pane) event.getSource();

        this.title.setText(((Label) card.lookup("#title")).getText());
        this.fullTitle.setText(((Label) card.lookup("#long-title")).getText());
        this.description.setText(((Label) card.lookup("#description")).getText());
        this.days.setText(((Label) card.lookup("#days")).getText());
        this.time.setText(((Label) card.lookup("#time")).getText());
    }

    @FXML
    private void addToSchedule(ActionEvent event) {
        AnchorPane card = (AnchorPane) ((Button) event.getSource()).getParent();
        String title = ((Label) card.lookup("#title")).getText();

        if (!this.currentClasses.contains(title)) {
            this.currentClasses.add(title);

            String fullTitle = ((Label) card.lookup("#long-title")).getText();
            String description = ((Label) card.lookup("#description")).getText();
            String days = ((Label) card.lookup("#days")).getText();
            String time = ((Label) card.lookup("#time")).getText();

            for (char c : days.toCharArray()) {
                GridPane pane = new GridPane();
                pane.getStyleClass().add("button");
                pane.setOnMouseClicked(this::selectSection);

                Label label;

                label = new Label(fullTitle);
                label.setId("long-title");
                pane.add(label, 0, 0);

                label = new Label(description);
                label.setId("description");
                pane.add(label, 0, 0);

                label = new Label(days);
                label.setId("days");
                pane.add(label, 0, 0);

                label = new Label(time);
                label.setId("time");
                pane.add(label, 0, 0);

                for (Node node : pane.getChildren()) {
                    node.setVisible(false);
                }

                label = new Label(title);
                label.setId("title");
                pane.add(label, 0, 0);

                String timeKey = time.split(" ")[0];

                if (c == 'M') {
                    schedule.add(pane, 1, this.timeLookup.get(timeKey), 1, 2);
                }
                else if (c == 'T') {
                    schedule.add(pane, 2, this.timeLookup.get(timeKey), 1, 3);
                }
                else if (c == 'W') {
                    schedule.add(pane, 3, this.timeLookup.get(timeKey), 1, 2);
                }
                else if (c == 'R') {
                    schedule.add(pane, 4, this.timeLookup.get(timeKey), 1, 3);
                }
                else if (c == 'F') {
                    schedule.add(pane, 5, this.timeLookup.get(timeKey), 1, 2);
                }
            }
        }
    }
}
