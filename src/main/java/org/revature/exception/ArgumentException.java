package org.revature.exception;

/**
 * general exception for when something goes wrong with the cli for sending out neat output
 */
public class ArgumentException extends Exception{
    public ArgumentException(String msg){
        super(msg);
    }
}
