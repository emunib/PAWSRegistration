package sample;

public class ClassItem {
    private String id;
    private String subject;
    private String title;
    private String days;
    private String time;
    private String description;
    private String campus;
    private String level;

    public ClassItem(String id, String subject, String title, String days, String time, String description, String campus, String level) {
        this.id = id;
        this.subject = subject;
        this.title = title;
        this.days = days;
        this.time = time;
        this.description = description;
        this.campus = campus;
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getTitle() {
        return title;
    }

    public String getDays() {
        return days;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getCampus() {
        return campus;
    }

    public String getLevel() {
        return level;
    }
}
