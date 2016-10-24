package interview.pf.tasks.services.dao.exception;

/**
 * Created by admin on 24.10.2016.
 */
public class DaoException extends RuntimeException {
    public DaoException(Throwable cause) {
        super(cause);
    }

    public DaoException(String message) {
        super(message);
    }
}
