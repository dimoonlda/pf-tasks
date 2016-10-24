package interview.pf.tasks.services.dao.mysql;

import interview.pf.tasks.model.Task;
import interview.pf.tasks.services.dao.DaoUtility;
import interview.pf.tasks.services.dao.exception.DaoException;
import interview.pf.tasks.services.dao.interfaces.TaskDao;

import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneId;
import java.util.Objects;

public class TaskDaoImpl implements TaskDao {

    private final String INSERT_SQL = "INSERT VALUES(?, ?, ?) INTO TASKS('TITLE', 'DEADLINE', 'PRIORITY')";
    private final String DELETE_BY_ID_SQL = "DELETE FROM TASKS WHERE ID = ?";
    private final String MOVE_TO_EXECUTED_SQL = "INSERT VALUES(?, ?, ?, ?) INTO TASKS('ID', 'TITLE', 'DEADLINE', 'PRIORITY')";
    private final DataSource dataSource;

    public TaskDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Task save(Task task) throws DaoException  {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet keyResultSet = null;
        try {
            conn = dataSource.getConnection();
            pst = conn.prepareStatement(INSERT_SQL);
            pst.setString(0, task.getTitle());
            pst.setTimestamp(1, Timestamp.from(task.getDeadLine().atZone(ZoneId.systemDefault()).toInstant()));
            pst.setInt(2, task.getPriority().getPriorityCode());
            pst.executeUpdate();
            keyResultSet = pst.getGeneratedKeys();
            Long newKey = keyResultSet.first() ? keyResultSet.getLong("ID") : null;
            return task.setId(newKey);
        }catch (SQLException e){
            //System.out.println("Couldn't insert Task.");
            throw new DaoException(e);
        }finally {
            DaoUtility.close(keyResultSet, pst, conn);
        }
    }

    public void execute(Task task) throws DaoException {
        if (Objects.isNull(task.getId())){
            throw new IllegalArgumentException("Task id can't be null.");
        }
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            pst = conn.prepareStatement(DELETE_BY_ID_SQL);
            pst.setLong(1, task.getId());
            if (pst.executeUpdate() == 0 ){
                conn.rollback();
                return;
            }
            pst = conn.prepareStatement(MOVE_TO_EXECUTED_SQL);
            pst.setLong(1, task.getId());
            pst.setString(2, task.getTitle());
            pst.setTimestamp(3, Timestamp.from(task.getDeadLine().atZone(ZoneId.systemDefault()).toInstant()));
            pst.setInt(4, task.getPriority().getPriorityCode());
            if (pst.executeUpdate() > 0 ){
                conn.commit();
            }else {
                conn.rollback();
            }
        }catch (SQLException e){
            //System.out.println("Couldn't move Task.");
            try {
                conn.rollback();
            } catch (SQLException e1) {
                //System.out.println("Couldn't make rollback.");
            }
            throw new DaoException(e);
        }finally {
            DaoUtility.close(null, pst, conn);
        }
    }
}
