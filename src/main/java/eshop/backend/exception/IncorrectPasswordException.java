package eshop.backend.exception;

public class IncorrectPasswordException extends Exception {
    public IncorrectPasswordException() {
        super("Incorrectly entered current password.");
    }
}
