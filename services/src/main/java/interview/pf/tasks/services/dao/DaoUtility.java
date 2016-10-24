package interview.pf.tasks.services.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Created by admin on 24.10.2016.
 */
public class DaoUtility {
    public static void close(ResultSet rs, PreparedStatement pst, Connection conn){
        if (Objects.nonNull(rs)) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println("Couldn't close ResultSet.");
            }
        }
        if (Objects.nonNull(pst)) {
            try {
                pst.close();
            } catch (SQLException e) {
                System.out.println("Couldn't close PreparedStatement.");
            }
        }
        if (Objects.nonNull(conn)) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Couldn't close Connection.");
            }
        }
    }
}
