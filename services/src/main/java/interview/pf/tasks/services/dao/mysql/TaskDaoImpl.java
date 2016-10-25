package interview.pf.tasks.services.dao.mysql;

import interview.pf.tasks.model.Task;
import interview.pf.tasks.model.TaskPriority;
import interview.pf.tasks.services.dao.DaoUtility;
import interview.pf.tasks.services.dao.exception.DaoException;
import interview.pf.tasks.services.dao.interfaces.TaskDao;

import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskDaoImpl implements TaskDao {

    private final String FIND_ALL_SQL = "SELECT * FROM TASKS";
    private final String FIND_ALL_EXECUTED_SQL = "SELECT * FROM EXECUTED_TASKS";
    private final String INSERT_SQL = "INSERT INTO TASKS(TITLE, DEADLINE, PRIORITY) VALUES(?, ?, ?)";
    private final String DELETE_BY_ID_SQL = "DELETE FROM TASKS WHERE ID = ?";
    private final String MOVE_TO_EXECUTED_SQL = "INSERT INTO EXECUTED_TASKS(ID, TITLE, DEADLINE, PRIORITY) SELECT ID, TITLE, DEADLINE, PRIORITY FROM TASKS WHERE ID = ?";
    private final DataSource dataSource;

    public TaskDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Task> findAll() throws DaoException {
        return findAllTasks(FIND_ALL_SQL);
    }

    private List<Task> findAllTasks(String sql) throws DaoException {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Task> tasks = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()){
                Task task = new Task()
                        .setId(rs.getInt("ID"))
                        .setTitle(rs.getString("TITLE"))
                        .setPriority(TaskPriority.valueOf(rs.getInt("PRIORITY")))
                        .setDeadLine(rs.getTimestamp("DEADLINE").toLocalDateTime());
                tasks.add(task);
            }
            return tasks;
        }catch (SQLException e){
            throw new DaoException(e);
        }finally {
            DaoUtility.close(rs, pst, conn);
        }
    }

    @Override
    public List<Task> findAllExecuted() throws DaoException {
        return findAllTasks(FIND_ALL_EXECUTED_SQL);
    }

    public Task save(Task task) throws DaoException  {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet keyResultSet = null;
        try {
            conn = dataSource.getConnection();
            pst = conn.prepareStatement(INSERT_SQL);
            pst.setString(1, task.getTitle());
            pst.setTimestamp(2, Timestamp.from(task.getDeadLine().atZone(ZoneId.systemDefault()).toInstant()));
            pst.setInt(3, task.getPriority().getPriorityCode());
            pst.executeUpdate();
            keyResultSet = pst.getGeneratedKeys();
            Integer newKey = keyResultSet.first() ? keyResultSet.getInt(1) : null;
            return task.setId(newKey);
        }catch (SQLException e){
            throw new DaoException(e);
        }finally {
            DaoUtility.close(keyResultSet, pst, conn);
        }
    }

    public void execute(Integer id) throws DaoException {
        if (Objects.isNull(id)){
            throw new IllegalArgumentException("Task id can't be null.");
        }
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            pst = conn.prepareStatement(MOVE_TO_EXECUTED_SQL);
            pst.setInt(1, id);
            if (pst.executeUpdate() == 0 ){
                conn.rollback();
                return;
            }
            pst = conn.prepareStatement(DELETE_BY_ID_SQL);
            pst.setInt(1, id);
            if (pst.executeUpdate() > 0 ){
                conn.commit();
            }else {
                conn.rollback();
            }
        }catch (SQLException e){
            try {
                conn.rollback();
            } catch (SQLException e1) {
                System.out.println("Couldn't make rollback.");
            }
            throw new DaoException(e);
        }finally {
            DaoUtility.close(null, pst, conn);
        }
    }
}
