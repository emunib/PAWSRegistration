package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class Controller {
    private HashMap<String, Integer> timeLookup;
    private HashSet<String> currentClasses;

    @FXML
    private ComboBox<String> term;

    @FXML
    private ComboBox<String> subjects;

    @FXML
    private ComboBox<String> campus;

    @FXML
    private ComboBox<String> level;

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
    public GridPane schedule;

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
            timeLookup.put(times[i], i + 1);
        }
    }

    @FXML
    private void initialize() {
        try {
            this.term.getItems().add("Winter 2019 Term");

            String[] subjectList = new String[]{"Art History", "Classical Medieval Renaissance", "Computer Science",
                    "Economics", "English", "Kinesiology", "Linguistics", "Mathematics", "Nutrition", "Philosophy"};

            this.subjects.getItems().addAll(subjectList);

            String[] campusList = new String[]{"All", "U of S -Saskatoon Main Campus", "Carlton Trail Regional College",
                    "Cumberland Regional College", "Cypress Hills Regional College", "First Nations Univ of Canada",
                    "Great Plains College", "La Ronge", "Lakeland College"};

            this.campus.getItems().addAll(campusList);

            String[] levelList = new String[]{"100", "200", "300", "400"};

            this.level.getItems().addAll(levelList);
        } catch (NullPointerException ignored) {
        }
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void submitSchedule() {
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("submit.fxml")));
            VBox list = (VBox) scene.lookup("#class-list");
            for (String title : this.currentClasses) {
                list.getChildren().add(new Label(title));
            }

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

    private void selectSection(AnchorPane card) {
        this.title.setText(((Label) card.lookup("#title")).getText());
        this.fullTitle.setText(((Label) card.lookup("#long-title")).getText());
        this.description.setText(((Label) card.lookup("#description")).getText());
        this.days.setText(((Label) card.lookup("#days")).getText());
        this.time.setText(((Label) card.lookup("#time")).getText());
    }

    private boolean doesNotConflict(String title, String days, String time) {
        if (currentClasses.isEmpty()) return true;

        for (String str : this.currentClasses) {
            if (str.startsWith(title.split("-")[0])) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("conflict.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    SecondaryController c = (SecondaryController) fxmlLoader.getController();
                    c.schedule = this.schedule;
                    c.currentClasses = this.currentClasses;
                    Label prompt = (Label) scene.lookup("#message");
                    ((Label) scene.lookup("#title")).setText(str);
                    prompt.setText(title + " conflicts with the existing class " + str + ". Do you wish to replace " + str + "?");

                    Stage dialog = new Stage();
                    dialog.setTitle("Class Conflict");
                    dialog.setScene(scene);
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (this.currentClasses.contains(str)) {
                    return false;
                }
            }
        }

        for (Node node : schedule.lookupAll("#title")) {
            String otherTitle = ((Label) node).getText();
            String otherDays = ((Label) node.getParent().lookup("#days")).getText();
            String otherTime = ((Label) node.getParent().lookup("#time")).getText();

            if (!this.currentClasses.contains(otherTitle)) {
                continue;
            }

            if (!days.equals(otherDays)) {
                continue;
            }

            try {
                Instant from = new SimpleDateFormat("hh:mma").parse(time.split(" - ")[0]).toInstant();
                Instant to = new SimpleDateFormat("hh:mma").parse(time.split(" - ")[1]).toInstant().minusSeconds(600);
                Instant otherFrom = new SimpleDateFormat("hh:mma").parse(otherTime.split(" - ")[0]).toInstant();
                Instant otherTo = new SimpleDateFormat("hh:mma").parse(otherTime.split(" - ")[1]).toInstant().minusSeconds(600);

                if ((!otherFrom.isBefore(from) && !otherFrom.isAfter(to) || (!otherTo.isBefore(from) && !otherTo.isAfter(to)))) {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("conflict.fxml"));
                        Scene scene = new Scene(fxmlLoader.load());
                        SecondaryController c = (SecondaryController) fxmlLoader.getController();
                        c.schedule = this.schedule;
                        c.currentClasses = this.currentClasses;
                        Label prompt = (Label) scene.lookup("#message");
                        ((Label) scene.lookup("#title")).setText(otherTitle);
                        prompt.setText(title + " conflicts with the existing class " + otherTitle + ". Do you wish to replace " + otherTitle + "?");

                        Stage dialog = new Stage();
                        dialog.setTitle("Class Conflict");
                        dialog.setScene(scene);
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.showAndWait();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (this.currentClasses.contains(otherTitle)) {
                        return false;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @FXML
    private void addToSchedule(ActionEvent event) {
        AnchorPane card = (AnchorPane) ((Button) event.getSource()).getParent();
        selectSection(card);
        String title = ((Label) card.lookup("#title")).getText();

        if (!this.currentClasses.contains(title) && this.doesNotConflict(title, ((Label) card.lookup("#days")).getText(), ((Label) card.lookup("#time")).getText())) {
            this.currentClasses.add(title);

            String fullTitle = ((Label) card.lookup("#long-title")).getText();
            String description = ((Label) card.lookup("#description")).getText();
            String days = ((Label) card.lookup("#days")).getText();
            String time = ((Label) card.lookup("#time")).getText();

            for (char c : days.toCharArray()) {
                GridPane pane = new GridPane();
                pane.getStyleClass().add("button");
                pane.setId(title);
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

                Button cross = new Button("X");
                cross.setOnAction(this::removeFromSchedule);
                cross.getStyleClass().add("sm");
                cross.setId("drop");
                GridPane.setHalignment(cross, HPos.RIGHT);
                pane.add(cross, 0, 0);

                String timeKey = time.split(" ")[0];

                if (c == 'M') {
                    schedule.add(pane, 1, this.timeLookup.get(timeKey), 1, 2);
                } else if (c == 'T') {
                    schedule.add(pane, 2, this.timeLookup.get(timeKey), 1, 3);
                } else if (c == 'W') {
                    schedule.add(pane, 3, this.timeLookup.get(timeKey), 1, 2);
                } else if (c == 'R') {
                    schedule.add(pane, 4, this.timeLookup.get(timeKey), 1, 3);
                } else if (c == 'F') {
                    schedule.add(pane, 5, this.timeLookup.get(timeKey), 1, 2);
                }
            }
        }
    }

    @FXML
    private void removeFromSchedule(ActionEvent event) {
        Pane card = (Pane) ((Button) event.getSource()).getParent();

        String title = ((Label) card.lookup("#title")).getText();
        this.removeClass(title);
    }

    private void removeClass(String title) {
        for (Node node : schedule.lookupAll("#title")) {
            if (((Label) node).getText().equals(title)) {
                schedule.getChildren().remove(node.getParent());
            }
        }

        currentClasses.remove(title);
    }
}
