package sample;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller {
    @FXML
    private URL location;

    @FXML
    private ResourceBundle resources;

    private HashMap<String, Integer> timeLookup;

    private HashMap<String, ClassItem> classes;
    private SortedSet<String> subjects;
    private SortedSet<String> campuses;
    private SortedSet<String> levels;

    private SortedSet<String> confirmedSchedule;
    private SortedMap<String, ArrayList<Node>> currentSchedule;

    @FXML
    private GridPane root;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> termsDropDown;

    @FXML
    private CheckComboBox<String> subjectsDropDown;

    @FXML
    private CheckComboBox<String> campusesDropDown;

    @FXML
    private CheckComboBox<String> levelsDropDown;

    @FXML
    private VBox coursesPane;

    @FXML
    private AnchorPane detailsPane;

    @FXML
    private Label detailIdLabel;

    @FXML
    private GridPane schedulePane;

    public Controller() {
        this.timeLookup = new HashMap<>();
        String[] times = new String[]{"8:00AM", "8:30AM", "9:00AM", "9:30AM", "10:00AM", "10:30AM", "11:00AM", "11:30AM",
                "12:00PM", "12:30PM", "1:00PM", "1:30PM", "2:00PM", "2:30PM", "3:00PM", "3:30PM", "4:00AM"};

        for (int i = 0; i < times.length; i++) {
            timeLookup.put(times[i], i + 1);
        }

        this.classes = new HashMap<>();
        this.subjects = new TreeSet<>();
        this.campuses = new TreeSet<>();
        this.levels = new TreeSet<>();

        try {
            Scanner scanner = new Scanner(new File("src/sample/course_data.txt"));
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split("\t");
                assert data.length == 8;

                this.classes.put(data[0], new ClassItem(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]));
                this.subjects.add(data[1]);
                this.campuses.add(data[6]);
                this.levels.add(data[7]);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.confirmedSchedule = new TreeSet<>();
        this.currentSchedule = new TreeMap<>();
    }

    @FXML
    private void initialize() {
        this.termsDropDown.getItems().add("Winter 2019 Term");
        this.termsDropDown.setValue("Winter 2019 Term");

        this.subjectsDropDown.getItems().clear();
        this.subjectsDropDown.getItems().addAll(this.subjects);
        for (int i = 0; i < this.subjects.size(); i++) {
            this.subjectsDropDown.getCheckModel().check(i);
        }
        this.subjectsDropDown.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> this.updateClasses());

        this.campusesDropDown.getItems().clear();
        this.campusesDropDown.getItems().addAll(this.campuses);
        for (int i = 0; i < this.campuses.size(); i++) {
            this.campusesDropDown.getCheckModel().check(i);
        }
        this.campusesDropDown.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> this.updateClasses());

        this.levelsDropDown.getItems().clear();
        this.levelsDropDown.getItems().addAll(this.levels);
        for (int i = 0; i < this.levels.size(); i++) {
            this.levelsDropDown.getCheckModel().check(i);
        }
        this.levelsDropDown.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> this.updateClasses());

        this.updateClasses();
    }

    @FXML
    private void updateClasses() {
        this.coursesPane.getChildren().clear();

        SortedSet<String> filteredClasses = this.filterClasses();
        if (filteredClasses.isEmpty()) {
            this.coursesPane.getChildren().add(new Label("No classes matched you search."));
        } else {
            SortedMap<String, ArrayList<String>> filteredSubjects = new TreeMap<>();

            for (String id : filteredClasses) {
                String subject = this.classes.get(id).getSubject();
                if (!filteredSubjects.containsKey(subject)) {
                    filteredSubjects.put(subject, new ArrayList<>());
                }
                filteredSubjects.get(subject).add(id);
            }

            for (String subject : filteredSubjects.keySet()) {
                Label subjectHeading = new Label(subject);
                subjectHeading.setFont(Font.font("System", FontWeight.BOLD, 12));

                Accordion subjectAccd = new Accordion();

                SortedMap<String, ArrayList<String>> filteredCourses = new TreeMap<>();

                for (String id : filteredSubjects.get(subject)) {
                    String course = id.split("-")[0];
                    if (!filteredCourses.containsKey(course)) {
                        filteredCourses.put(course, new ArrayList<>());
                    }
                    filteredCourses.get(course).add(id);
                }

                for (String course : filteredCourses.keySet()) {
                    TitledPane coursePane = null;
                    try {
                        coursePane = new FXMLLoader(getClass().getResource("coursePane.fxml")).load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (coursePane != null) {
                        coursePane.setText(course);
                        VBox classList = (VBox) ((ScrollPane) coursePane.getContent()).getContent();
                        for (String id : filteredClasses) {
                            ClassItem classItem = this.classes.get(id);

                            if (classItem.getSubject().equals(subject) && classItem.getId().split("-")[0].equals(course)) {

                                AnchorPane card = null;
                                try {
                                    card = new FXMLLoader(getClass().getResource("classCard.fxml")).load();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (card != null) {
                                    ((Label) card.lookup("#class-id")).setText(classItem.getId());
                                    ((Label) card.lookup("#class-title")).setText(classItem.getTitle());
                                    ((Label) card.lookup("#class-days")).setText(classItem.getDays());
                                    ((Label) card.lookup("#class-time")).setText(classItem.getTime());

                                    ((Button) card.lookup("#add-class-button")).setOnAction(event -> addToSchedule((AnchorPane) ((Button) event.getSource()).getParent()));
                                    card.setOnMouseClicked(event -> showDetails(((AnchorPane) event.getSource())));

                                    classList.getChildren().add(card);
                                }
                            }
                        }
                    }

                    subjectAccd.getPanes().add(coursePane);
                }

                this.coursesPane.getChildren().addAll(subjectHeading, subjectAccd, new Separator());
            }
        }
    }

    private SortedSet<String> filterClasses() {
        SortedSet<String> classes = new TreeSet<>(this.classes.keySet());

        if (this.searchField.getCharacters().length() == 0) {
            ObservableList<Integer> checkedSubjectIndices = this.subjectsDropDown.getCheckModel().getCheckedIndices();
            classes.removeIf(c -> !checkedSubjectIndices.contains(this.subjectsDropDown.getItems().indexOf(this.classes.get(c).getSubject())));

            ObservableList<Integer> checkedCampusIndices = this.campusesDropDown.getCheckModel().getCheckedIndices();
            classes.removeIf(c -> !checkedCampusIndices.contains(this.campusesDropDown.getItems().indexOf(this.classes.get(c).getCampus())));

            ObservableList<Integer> checkedLevelIndices = this.levelsDropDown.getCheckModel().getCheckedIndices();
            classes.removeIf(c -> !checkedLevelIndices.contains(this.levelsDropDown.getItems().indexOf(this.classes.get(c).getLevel())));
        } else {
            classes.removeIf(c -> !this.classes.get(c).getTitle().toLowerCase().contains(this.searchField.getCharacters().toString().toLowerCase()));
        }

        return classes;
    }

    private void showDetails(Pane card) {
        ClassItem classItem = this.classes.get(((Label) card.lookup("#class-id")).getText());

        ((Label) this.detailsPane.lookup("#class-id")).setText(classItem.getId());
        ((Label) this.detailsPane.lookup("#class-title")).setText(classItem.getTitle());
        ((Label) this.detailsPane.lookup("#class-time")).setText(classItem.getTime());
        ((Label) this.detailsPane.lookup("#class-days")).setText(classItem.getDays());
        ((Label) this.detailsPane.lookup("#class-description")).setText(classItem.getDescription());
        // subject, location, prof, campus, etc.
    }

    private boolean canAddClass(ClassItem classItem) {
        if (this.currentSchedule.containsKey(classItem.getId())) return false;
        if (this.currentSchedule.keySet().size() >= 5) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You cannot register for more than 5 courses per term.", ButtonType.OK);

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.lookupButton(ButtonType.OK).getStyleClass().add("primary");
            dialogPane.getStylesheets().add(getClass().getResource("bootstrap3.css").toExternalForm());

            this.root.setDisable(true);
            alert.showAndWait();
            this.root.setDisable(false);

            return false;
        }

        for (String otherClassId : this.currentSchedule.keySet()) {
            String course = classItem.getId().split("-")[0];
            String otherCourse = otherClassId.split("-")[0];
            if (course.equals(otherCourse)) {
                boolean replace = this.showConformationDialog("You are already registered for " + otherClassId + ". Would you like to replace " + otherClassId + " with " + classItem.getId() + " ?");

                if (replace) {
                    this.removeFromSchedule(this.classes.get(otherClassId));
                    return this.canAddClass(classItem);
                }

                return false;
            }
        }

        for (String classId : this.currentSchedule.keySet()) {
            ClassItem otherClass = this.classes.get(classId);

            if (classItem.getDays().equals(otherClass.getDays())) {

                try {
                    Date from = new SimpleDateFormat("hh:mma").parse(classItem.getTime().split(" - ")[0]);
                    Date to = new SimpleDateFormat("hh:mma").parse(classItem.getTime().split(" - ")[1]);
                    Date otherFrom = new SimpleDateFormat("hh:mma").parse(otherClass.getTime().split(" - ")[0]);
                    Date otherTo = new SimpleDateFormat("hh:mma").parse(otherClass.getTime().split(" - ")[1]);

                    if ((!otherFrom.before(from) && !otherFrom.after(to) || (!otherTo.before(from) && !otherTo.after(to)))) {
                        boolean replace = this.showConformationDialog(classItem.getId() + " conflicts with " + otherClass.getId() + ". Would you like to replace " + otherClass.getId() + " with " + classItem.getId() + " ?");

                        if (replace) {
                            this.removeFromSchedule(otherClass);
                            return this.canAddClass(classItem);
                        }

                        return false;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return true;
    }

    private void addToSchedule(Pane card) {
        this.showDetails(card);
        ClassItem classItem = this.classes.get(((Label) card.lookup("#class-id")).getText());

        this.addToSchedule(classItem);
    }

    private void addToSchedule(ClassItem classItem) {
        if (this.canAddClass(classItem)) {
            this.currentSchedule.put(classItem.getId(), new ArrayList<>(3));
            String timeKey = classItem.getTime().split(" ")[0];

            for (char c : classItem.getDays().toCharArray()) {
                AnchorPane scheduleCard = null;
                try {
                    scheduleCard = new FXMLLoader(getClass().getResource("scheduleCard.fxml")).load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (scheduleCard != null) {
                    ((Label) scheduleCard.lookup("#class-id")).setText(classItem.getId());
                    scheduleCard.setOnMouseClicked(event -> showDetails((AnchorPane) event.getSource()));
                    ((Button) scheduleCard.lookup("#drop-class-button")).setOnAction(event -> removeFromSchedule((AnchorPane) ((Button) event.getSource()).getParent().getParent()));

                    if (c == 'M') {
                        this.schedulePane.add(scheduleCard, 1, this.timeLookup.get(timeKey), 1, 2);
                    } else if (c == 'T') {
                        this.schedulePane.add(scheduleCard, 2, this.timeLookup.get(timeKey), 1, 3);
                    } else if (c == 'W') {
                        this.schedulePane.add(scheduleCard, 3, this.timeLookup.get(timeKey), 1, 2);
                    } else if (c == 'R') {
                        this.schedulePane.add(scheduleCard, 4, this.timeLookup.get(timeKey), 1, 3);
                    } else if (c == 'F') {
                        this.schedulePane.add(scheduleCard, 5, this.timeLookup.get(timeKey), 1, 2);
                    }

                    this.currentSchedule.get(classItem.getId()).add(scheduleCard);
                }
            }
        }
    }

    private void removeFromSchedule(Pane card) {
        ClassItem classItem = this.classes.get(((Label) card.lookup("#class-id")).getText());

        boolean dropClass = this.showConformationDialog("Are you sure you want to drop " + classItem.getId() + "?");
        if (dropClass) this.removeFromSchedule(classItem);
    }

    private void removeFromSchedule(ClassItem classItem) {
        for (Node node : this.currentSchedule.get(classItem.getId())) {
            this.schedulePane.getChildren().remove(node);
        }

        this.currentSchedule.remove(classItem.getId());
    }

    private boolean showConformationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.CANCEL, ButtonType.YES);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.lookupButton(ButtonType.YES).getStyleClass().add("primary");
        dialogPane.getStylesheets().add(getClass().getResource("bootstrap3.css").toExternalForm());

        this.root.setDisable(true);
        alert.showAndWait();
        this.root.setDisable(false);

        return alert.getResult() == ButtonType.YES;
    }

    @FXML
    private void submitSchedule(ActionEvent event) {
        StringBuilder message = new StringBuilder("Do you wish to register for the following courses?");

        for (String classId : this.currentSchedule.keySet()) {
            message.append("\n\t").append(classId);
        }

        boolean submit = this.showConformationDialog(message.toString());

        if (submit) {
            this.confirmedSchedule = new TreeSet<>();
            this.confirmedSchedule.addAll(this.currentSchedule.keySet());
        }
    }

    @FXML
    private void resetSchedule(ActionEvent event) {
        StringBuilder message = new StringBuilder("Are you sure you want to reset all changes and revert to the following schedule?");

        for (String classId : this.confirmedSchedule) {
            message.append("\n\t").append(classId);
        }
        boolean reset = this.showConformationDialog(message.toString());

        if (reset) {
            ArrayList<String> classes = new ArrayList<>(this.currentSchedule.keySet());

            for (String classId : classes) {
                this.removeFromSchedule(this.classes.get(classId));
            }

            for (String classId : this.confirmedSchedule) {
                this.addToSchedule(this.classes.get(classId));
            }
        }
    }
}