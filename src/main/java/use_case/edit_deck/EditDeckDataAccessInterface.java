package use_case.edit_deck;

import entity.Deck;

import java.util.Optional;

public interface EditDeckDataAccessInterface {
    Optional<Deck> findDeck(int deckId);
    // rename of existing deck
    void renameDeck(int deckId, String name, String username);
}
