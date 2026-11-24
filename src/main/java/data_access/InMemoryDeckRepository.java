package data_access;

import entity.Card;
import entity.Deck;
import use_case.add_card.AddCardDataAccessInterface;
import use_case.edit_card.EditCardDataAccessInterface;
import use_case.edit_card.EditCardDataAccessException;
import use_case.add_deck.AddDeckDataAccessInterface;
import use_case.add_deck.AddDeckDataAccessInterfaceException;
import use_case.edit_deck.EditDeckDataAccessInterface;
import use_case.edit_deck.EditDeckDataAccessException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

public class InMemoryDeckRepository 
    implements 
    AddCardDataAccessInterface, 
    EditCardDataAccessInterface, 
    AddDeckDataAccessInterface,
    EditDeckDataAccessInterface
    {

    private final Map<Integer, Deck> decks = new HashMap<>();
    private int nextCardId = 1;
    private int nextDeckId = 1;

    public InMemoryDeckRepository(List<Deck> initialDecks) {
        for (Deck deck : initialDecks) {
            if (decks.putIfAbsent(deck.getId(), deck) != null) {
                throw new IllegalArgumentException("Duplicate deck id " + deck.getId());
            }

            for (Card card : deck.getCards()) {
                nextCardId = Math.max(nextCardId, card.getId() + 1);
            }
            nextDeckId = Math.max(nextDeckId, deck.getId() + 1);
        }
    }

    @Override
    public boolean deckExists(int deckId) {
        return decks.containsKey(deckId);
    }

    @Override
    public int generateDeckId() {
        return nextDeckId++;
    }

    @Override
    public void saveDeck(Deck deck) {
        if (decks.containsKey(deck.getId())) {
            throw new AddDeckDataAccessInterfaceException(
                "id " + deck.getId() + " already exists");
        }
        decks.put(deck.getId(), deck);
    }

    // edit_deck methods tbd
    @Override
    public Optional<Deck> findDeck(int deckId) {
        Deck stored = decks.get(deckId);

        if (stored == null) {
            return Optional.empty();
        }

        Deck snapshot = new Deck(
                stored.getId(),
                stored.getName(),
                stored.getOwner()
        );

        for (Card card : stored.getCards()) {
            snapshot.add_card(new Card(
                    card.getId(),
                    card.getQuestion(),
                    card.getAnswer()
            ));
        }

        snapshot.getEditors().addAll(stored.getEditors());

        return Optional.of(snapshot);
    }

@Override
public void renameDeck(int deckId, String name, String actingUsername) {
    Deck stored = decks.get(deckId);

    if (stored == null) {
        throw new EditDeckDataAccessException(
                "Deck no longer exists."
        );
    }

    if (!stored.getOwner().equals(actingUsername)) {
        throw new EditDeckDataAccessException(
                "User is no longer allowed to rename this deck."
        );
    }

    stored.rename(name);
}

    @Override
    public int generateCardId() {
        return nextCardId++;
    }

    @Override
    public Optional<Card> findCard(int deckId, int cardId) {
        Deck deck = decks.get(deckId);

        if (deck == null) {
            return Optional.empty();
        }

        for (Card card : deck.getCards()) {
            if (card.getId() == cardId) {
                return Optional.of(new Card(
                        card.getId(),
                        card.getQuestion(),
                        card.getAnswer()
                ));
            }
        }

        return Optional.empty();

    }

    @Override
    public void updateCard(int deckId,Card updatedCard) {
        Deck deck = decks.get(deckId);

        if (deck == null) {
            throw new EditCardDataAccessException("Deck with id " + deckId + " not found");
        }

        for (Card existingCard : deck.getCards()) {
            if (existingCard.getId() == updatedCard.getId()) {
                existingCard.updateContent(
                        updatedCard.getQuestion(),
                        updatedCard.getAnswer()
                );
                return;
            }
        }

        throw new EditCardDataAccessException("Card with id " + updatedCard.getId() + " not found in deck");

    }

    @Override
    public void saveCard(int deckId, Card card) {
        Deck deck = decks.get(deckId);

        if (deck == null) {
            throw new IllegalArgumentException("Deck with id " + deckId + " does not exist");
        }

        for (Deck existingDeck : decks.values()) {
            for (Card existingCard : existingDeck.getCards()) {
                if (existingCard.getId() == card.getId()) {
                    throw new IllegalArgumentException("Duplicate card id " + card.getId());
                }
            }
        }

        deck.add_card(card);

    }
}
