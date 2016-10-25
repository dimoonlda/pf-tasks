package interview.pf.tasks.services.dao.interfaces;

import interview.pf.tasks.model.Task;
import interview.pf.tasks.services.dao.exception.DaoException;

import java.util.List;

public interface TaskDao {
    List<Task> findAll() throws DaoException;
    List<Task> findAllExecuted() throws DaoException;
    Task save(Task task) throws DaoException;
    void execute(Integer id) throws DaoException;
}
