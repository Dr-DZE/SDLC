package sdlc.sdlcd.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String colorTheme;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_id")
    private List<TaskTrack> tracks = new ArrayList<>();

    public Project() {}

    public Project(String name, String colorTheme) {
        this.name = name;
        this.colorTheme = colorTheme;
    }

    // Getters and Setters
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

    public List<TaskTrack> getTracks() {
        return tracks;
    }

    public void setTracks(List<TaskTrack> tracks) {
        this.tracks = tracks;
    }

    public String getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(String colorTheme) {
        this.colorTheme = colorTheme;
    }

    @Transient
    public boolean isComplete() {
        if (this.tracks.isEmpty()) {
            return false;
        }
        return this.tracks.stream().allMatch(TaskTrack::isComplete);
    }
}
