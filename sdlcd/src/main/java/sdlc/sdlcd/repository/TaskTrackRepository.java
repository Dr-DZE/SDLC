package sdlc.sdlcd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sdlc.sdlcd.model.TaskTrack;

public interface TaskTrackRepository extends JpaRepository<TaskTrack, Long> {
}
