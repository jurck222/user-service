package org.task.userservice.exceptions;

public class EmailAlreadyInUseException extends Exception{
    public EmailAlreadyInUseException(String message){
        super(message);
    }
}
