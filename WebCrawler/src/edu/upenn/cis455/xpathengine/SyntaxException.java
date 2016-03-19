package edu.upenn.cis455.xpathengine;

public class SyntaxException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
    * Constructor for SyntaxException.
    */
	public SyntaxException(){
		
	}
	
    /**
     * Constructor for SyntaxException.
     * @param message An error message to include.
     */
    public SyntaxException(String message) {
        super(message);
    }

    /**
     * Constructor for SyntaxException.
     * @param cause The original Exception that occurred.
     */
    public SyntaxException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor for SyntaxException.
     * @param message An error message to include.
     * @param cause The original Exception that occurred.
     */
    public SyntaxException(String message, Throwable cause) {
        super(message, cause);
    }
}
