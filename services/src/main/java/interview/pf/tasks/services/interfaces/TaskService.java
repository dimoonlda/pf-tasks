package interview.pf.tasks.services.interfaces;

import interview.pf.tasks.model.Task;
import interview.pf.tasks.services.exceptions.ServiceException;

import java.util.List;

/**
 * Created by admin on 24.10.2016.
 */
public interface TaskService {
    Task save(Task task) throws ServiceException;
    void execute(Integer id) throws ServiceException;
    List<Task> findAll() throws ServiceException;
    List<Task> findAllExecuted() throws ServiceException;
}
