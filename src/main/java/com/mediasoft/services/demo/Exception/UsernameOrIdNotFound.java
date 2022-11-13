package com.mediasoft.services.demo.Exception;

public class UsernameOrIdNotFound extends Exception {
    private static final long serialVersionUID = 1668398822129870029L;

    public UsernameOrIdNotFound() {
        super("Username or Id not exist");
    }

    public UsernameOrIdNotFound(String message) {
        super(message);
    }
}
