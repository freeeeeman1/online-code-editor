package com.netcracker.edu.logic.exception;

public class WrongPermissionException extends Exception{

    public WrongPermissionException() {
        super("You have wrong permission");
    }
}
