package interview.pf.tasks.services;

import interview.pf.tasks.model.Task;
import interview.pf.tasks.services.dao.interfaces.TaskDao;
import interview.pf.tasks.services.exceptions.ServiceException;
import interview.pf.tasks.services.interfaces.TaskService;

/**
 * Created by admin on 24.10.2016.
 */
public class TaskServiceImpl implements TaskService {

    private TaskDao taskDao;

    public TaskServiceImpl(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public Task save(Task task) throws ServiceException {
        try{
            return taskDao.save(task);
        }catch (Exception e){
            throw new ServiceException(e);
        }
    }

    @Override
    public void execute(Task task) throws ServiceException {
        try{
            taskDao.execute(task);
        }catch (Exception e){
            throw new ServiceException(e);
        }
    }
}
