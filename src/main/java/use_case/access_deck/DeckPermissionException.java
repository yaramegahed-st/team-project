package use_case.access_deck;

public class DeckPermissionException extends RuntimeException {
    public DeckPermissionException(String message) {
        super(message);
    }
}