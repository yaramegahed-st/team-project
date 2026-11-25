package use_case.add_deck;

public class AddDeckDataAccessInterfaceException extends RuntimeException {
    public AddDeckDataAccessInterfaceException(String message) {
        super(message);
    }

    public AddDeckDataAccessInterfaceException(String message, Throwable cause) {
        super(message, cause);
    }
}