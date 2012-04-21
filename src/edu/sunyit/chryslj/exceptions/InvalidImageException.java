package edu.sunyit.chryslj.exceptions;

/**
 * All purpose invalid image exception. Thrown when we are unable to read the
 * bar code information.
 * 
 * @author Justin Chrysler
 * 
 */
public class InvalidImageException extends Exception
{
    private static final long serialVersionUID = -3828710295564289185L;

    public InvalidImageException(String message)
    {
        super(message);
    }
}
