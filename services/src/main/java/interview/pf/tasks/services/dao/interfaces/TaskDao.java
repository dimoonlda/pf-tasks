package interview.pf.tasks.services.dao.interfaces;

import interview.pf.tasks.model.Task;

import java.sql.SQLException;
import java.util.Optional;

public interface TaskDao {
    Optional<Task> save(Task task) throws SQLException;
    void execute(Task task) throws SQLException;
}
