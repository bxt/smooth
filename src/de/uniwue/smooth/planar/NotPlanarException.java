package de.uniwue.smooth.planar;

/**
 * Exception indicating that an algorithm which only works
 * for planar graphs detected a non planar graph. 
 */
public class NotPlanarException extends Exception {
	private static final long serialVersionUID = 1L;

	public NotPlanarException() {
		super();
	}

	public NotPlanarException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotPlanarException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotPlanarException(String message) {
		super(message);
	}

	public NotPlanarException(Throwable cause) {
		super(cause);
	}
}
