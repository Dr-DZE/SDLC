package sdlc.sdlcd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sdlc.sdlcd.model.Project;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByName(String name);
}
