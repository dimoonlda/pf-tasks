package interview.pf.tasks.services.dao.mysql;

import interview.pf.tasks.model.Task;
import interview.pf.tasks.services.dao.DaoUtility;
import interview.pf.tasks.services.dao.interfaces.TaskDao;

import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneId;
import java.util.Optional;

public class TaskDaoImpl implements TaskDao {

    private final String INSERT_SQL = "INSERT VALUES(?, ?, ?) INTO TASKS('TITLE', 'DEADLINE', 'PRIORITY')";
    private final String MOVE_TO_EXECUTED_SQL = "INSERT VALUES(?, ?, ?, ?) INTO TASKS('ID', 'TITLE', 'DEADLINE', 'PRIORITY')";
    private final DataSource dataSource;

    public TaskDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<Task> save(Task task) {
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
            return Optional.of(task.setId(newKey));
        }catch (SQLException e){
            System.out.println("Couldn't insert Task.");
            return Optional.empty();
        }finally {
            DaoUtility.close(keyResultSet, pst, conn);
        }
    }

    public void execute(Task task) throws SQLException {
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = dataSource.getConnection();
            pst = conn.prepareStatement(MOVE_TO_EXECUTED_SQL);
            pst.setLong(0, task.getId());
            pst.setString(1, task.getTitle());
            pst.setTimestamp(2, Timestamp.from(task.getDeadLine().atZone(ZoneId.systemDefault()).toInstant()));
            pst.setInt(3, task.getPriority().getPriorityCode());
            pst.executeUpdate();
            //TODO: Delete in transaction
        }catch (SQLException e){
            System.out.println("Couldn't move Task.");
        }finally {
            DaoUtility.close(null, pst, conn);
        }
    }
}
