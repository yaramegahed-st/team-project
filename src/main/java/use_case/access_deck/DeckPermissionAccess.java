package use_case.access_deck;

public interface DeckPermissionAccess {
    boolean canEditDeck(int deckId, String username);
}