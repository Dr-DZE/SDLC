package sdlc.sdlcd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sdlc.sdlcd.model.Project;
import sdlc.sdlcd.model.TaskTrack;
import sdlc.sdlcd.model.TodoItem;
import sdlc.sdlcd.repository.ProjectRepository;
import sdlc.sdlcd.repository.TaskTrackRepository;
import sdlc.sdlcd.repository.TodoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TodoController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskTrackRepository taskTrackRepository;

    @Autowired
    private TodoRepository todoRepository; // The JpaRepository for TodoItem

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false) String difficulty) {
        List<TodoItem> todos = todoRepository.findSimpleTodos();
        if (difficulty != null && !difficulty.isEmpty()) {
            todos = todos.stream()
                    .filter(todo -> difficulty.equals(todo.getDifficulty()))
                    .collect(Collectors.toList());
        }
        model.addAttribute("todos", todos);
        return "todo";
    }

    @PostMapping("/add")
    public String addTodo(@RequestParam String title,
            @RequestParam String details,
            @RequestParam String difficulty,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        todoRepository.save(new TodoItem(title, details, difficulty, dueDate));
        return "redirect:/";
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public TodoItem updateTodo(@PathVariable Long id) {
        TodoItem todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo Id:" + id));

        todo.setCompleted(!todo.isCompleted());
        return todoRepository.save(todo);
    }

    @PostMapping("/delete/{id}")
    public String deleteTodo(@PathVariable Long id) {
        todoRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/new-page")
    public String newPage(Model model, @RequestParam(required = false, defaultValue = "user") String mode, @RequestParam(required = false) Long projectId) {
        List<Project> allProjects = projectRepository.findAll();
        model.addAttribute("allProjects", allProjects);
        model.addAttribute("mode", mode);

        Project selectedProject = null;
        if (projectId != null) {
            selectedProject = projectRepository.findById(projectId).orElse(null);
        } else if (!allProjects.isEmpty()) {
            selectedProject = allProjects.get(0);
        }
        model.addAttribute("selectedProject", selectedProject);

        return "new_page";
    }

    @PostMapping("/project/task/update/{id}")
    public String updateProjectTask(@PathVariable Long id, @RequestParam String mode) {
        TodoItem todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo Id:" + id));

        if (mode.equals("user") && todo.isCompleted()) {
            // In user mode, do not allow un-completing a task
            return "redirect:/new-page?mode=user";
        }

        todo.setCompleted(!todo.isCompleted());
        todoRepository.save(todo);
        return "redirect:/new-page?mode=" + mode;
    }

    @PostMapping("/project/create")
    public String createProject(@RequestParam String name, @RequestParam String colorTheme) {
        projectRepository.save(new Project(name, colorTheme));
        return "redirect:/new-page?mode=dev";
    }

    @PostMapping("/project/{id}/track/create")
    public String createTrack(@PathVariable Long id, @RequestParam String name, @RequestParam String colorTheme) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid project Id:" + id));
        project.getTracks().add(new TaskTrack(name, colorTheme));
        projectRepository.save(project);
        return "redirect:/new-page?mode=dev";
    }

    @PostMapping("/track/{id}/task/create")
    public String createTask(@PathVariable Long id, @RequestParam String title, @RequestParam String details,
            @RequestParam String difficulty) {
        TaskTrack track = taskTrackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid track Id:" + id));
        track.addTask(new TodoItem(title, details, difficulty, null));
        taskTrackRepository.save(track);
        return "redirect:/new-page?mode=dev";
    }

    @PostMapping("/task/delete/{id}")
    public String deleteTask(@PathVariable Long id, @RequestParam String mode) {
        todoRepository.deleteById(id);
        return "redirect:/new-page?mode=" + mode;
    }

    @PostMapping("/track/delete/{id}")
    public String deleteTrack(@PathVariable Long id, @RequestParam String mode) {
        taskTrackRepository.deleteById(id);
        return "redirect:/new-page?mode=" + mode;
    }

    @PostMapping("/project/delete/{id}")
    public String deleteProject(@PathVariable Long id, @RequestParam String mode) {
        projectRepository.deleteById(id);
        return "redirect:/new-page?mode=" + mode;
    }
}
