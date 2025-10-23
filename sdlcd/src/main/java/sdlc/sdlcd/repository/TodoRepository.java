package sdlc.sdlcd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sdlc.sdlcd.model.TodoItem;

import java.util.List;

public interface TodoRepository extends JpaRepository<TodoItem, Long> {
    @Query("SELECT t FROM TodoItem t WHERE t.track IS NULL")
    List<TodoItem> findSimpleTodos();
}
