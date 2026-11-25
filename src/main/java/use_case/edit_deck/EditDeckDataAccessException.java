package use_case.edit_deck;

public class EditDeckDataAccessException extends RuntimeException {
    public EditDeckDataAccessException(String message) {
        super(message);
    }

    public EditDeckDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
