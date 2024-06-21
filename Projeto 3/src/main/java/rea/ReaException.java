package rea;

import java.io.Serializable;

/**
 * <p>An exception in the game. It is thrown when something goes wrong in the game.
 * It is a checked exception, so it must be caught or declared. It is a subclass of {@link Exception}.</p>
 */
public class ReaException
        extends Exception
        implements Serializable {

    /**
     * Create a new ReaException with no message.
     */
    public ReaException() {
    }

    /**
     * Create a new ReaException with a message.
     * @param message the message
     */
    public ReaException(String message){
        super(message);
    }

    /**
     * Create a new ReaException with a message and a cause.
     * @param message the message
     * @param cause the cause
     */
    public ReaException(String message,
                        Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new ReaException with a cause.
     * @param cause the cause
     */
    public ReaException(Throwable cause) {
        super(cause);
    }

    /**
     * Create a new ReaException with a message, a cause, and flags for suppression and stack trace.
     * @param message the message
     * @param cause the cause
     * @param enableSuppression whether suppression is enabled or disabled
     * @param writableStackTrace whether the stack trace should be writable
     */
    public ReaException(String message,
                        Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
