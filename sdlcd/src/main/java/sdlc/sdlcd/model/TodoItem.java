package sdlc.sdlcd.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String details;
    private String difficulty;
    private LocalDate dueDate;

    private boolean completed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    private TaskTrack track;

    public TodoItem() {
    }

    public TodoItem(String title, String details, String difficulty, LocalDate dueDate) {
        this.title = title;
        this.details = details;
        this.difficulty = difficulty;
        this.dueDate = dueDate;
        this.completed = false;
    }

    // Getters and Setters for track
    public TaskTrack getTrack() {
        return track;
    }

    public void setTrack(TaskTrack track) {
        this.track = track;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
