package eshop.backend.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(Long attributeId) {
        super(String.format("User with ID %d was not found.", attributeId));
    }

    public UserNotFoundException(String email) {
        super(String.format("User with e-mail %s was not found.", email));
    }
}
