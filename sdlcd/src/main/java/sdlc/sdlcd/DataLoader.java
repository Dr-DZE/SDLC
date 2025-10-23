package sdlc.sdlcd;

import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;

import sdlc.sdlcd.model.Project;

import sdlc.sdlcd.model.TaskTrack;

import sdlc.sdlcd.model.TodoItem;

import sdlc.sdlcd.repository.ProjectRepository;



import java.util.Arrays;



@Component

public class DataLoader implements CommandLineRunner {



    private final ProjectRepository projectRepository;



    public DataLoader(ProjectRepository projectRepository) {

        this.projectRepository = projectRepository;

    }



    @Override

    public void run(String... args) throws Exception {

                if (projectRepository.findByName("5th Semester").isEmpty()) {

                                Project project = new Project("5th Semester", "blue");

                    

                                TaskTrack track1 = new TaskTrack("Web Development", "green");

                                track1.addTask(new TodoItem("Lab 1", "HTML Basics", "Easy", null));

                                track1.addTask(new TodoItem("Lab 2", "CSS Styling", "Easy", null));

                                track1.addTask(new TodoItem("Lab 3", "JavaScript Events", "Medium", null));

                    

                                TaskTrack track2 = new TaskTrack("Databases", "red");

                                track2.addTask(new TodoItem("DB Lab 1", "SQL Queries", "Easy", null));

                                track2.addTask(new TodoItem("DB Lab 2", "Normalization", "Medium", null));

                                track2.addTask(new TodoItem("DB Lab 3", "Transactions", "Hard", null));

                    

                                TaskTrack track3 = new TaskTrack("Software Engineering", "purple");

            track3.addTask(new TodoItem("SE Lab 1", "UML Diagrams", "Easy", null));

            track3.addTask(new TodoItem("SE Lab 2", "Design Patterns", "Medium", null));

            track3.addTask(new TodoItem("SE Lab 3", "Agile vs Waterfall", "Medium", null));



            project.getTracks().addAll(Arrays.asList(track1, track2, track3));



            projectRepository.save(project);

        }

    }

}


