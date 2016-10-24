package interview.pf.tasks.services.dao.interfaces;

import interview.pf.tasks.model.Task;
import interview.pf.tasks.services.dao.exception.DaoException;

public interface TaskDao {
    Task save(Task task) throws DaoException;
    void execute(Task task) throws DaoException;
}
