package sdlc.sdlcd.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TaskTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String colorTheme;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "track_id")
    @OrderColumn(name = "task_order")
    private List<TodoItem> tasks = new ArrayList<>();

    public TaskTrack() {}

    public TaskTrack(String name) {
        this.name = name;
    }

    public TaskTrack(String name, String colorTheme) {
        this.name = name;
        this.colorTheme = colorTheme;
    }

    // Getters and Setters
    public String getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(String colorTheme) {
        this.colorTheme = colorTheme;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TodoItem> getTasks() {
        return tasks;
    }

    public void setTasks(List<TodoItem> tasks) {
        this.tasks = tasks;
    }

    public void addTask(TodoItem task) {
        this.tasks.add(task);
        task.setTrack(this);
    }

    @Transient
    public boolean isComplete() {
        if (this.tasks.isEmpty()) {
            return false;
        }
        return this.tasks.stream().allMatch(TodoItem::isCompleted);
    }
}
