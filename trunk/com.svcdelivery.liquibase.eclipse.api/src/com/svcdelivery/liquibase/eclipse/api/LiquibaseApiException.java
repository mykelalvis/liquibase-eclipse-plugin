package com.svcdelivery.liquibase.eclipse.api;

/**
 * @author nick
 * 
 */
public class LiquibaseApiException extends Exception {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public LiquibaseApiException() {
		super();
	}

	/**
	 * @param message
	 *            The message.
	 * @param cause
	 *            The cause.
	 */
	public LiquibaseApiException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *            The message.
	 */
	public LiquibaseApiException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            The cause.
	 */
	public LiquibaseApiException(final Throwable cause) {
		super(cause);
	}

}
