package use_case.access_deck;

public interface DeckPermissionAccess {
    boolean hasPermission(String deckId, String username);
}