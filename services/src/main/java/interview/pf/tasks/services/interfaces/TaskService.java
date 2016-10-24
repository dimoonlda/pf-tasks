package interview.pf.tasks.services.interfaces;

import interview.pf.tasks.model.Task;
import interview.pf.tasks.services.exceptions.ServiceException;

/**
 * Created by admin on 24.10.2016.
 */
public interface TaskService {
    Task save(Task task) throws ServiceException;
    void execute(Task task) throws ServiceException;
}
