package use_case.edit_card;

import entity.Card;

import java.util.Optional;

public interface EditCardDataAccessInterface {
    boolean deckExists(int deckId);

    // if card is not found
    Optional<Card> findCard(int deckId, int cardId);

    void updateCard(int deckId, Card card, String actingUsername);
}
