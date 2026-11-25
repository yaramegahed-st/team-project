package data_access;

import entity.Card;
import entity.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.edit_card.EditCardDataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryDeckRepositoryTest {

    private Deck deck1;
    private Deck deck2;
    private InMemoryDeckRepository repo;

    @BeforeEach
    void setUp() {
        deck1 = new Deck(1, "One", "alice");
        deck1.add_card(new Card(4, "q4", "a4"));
        deck2 = new Deck(2, "Two", "bob");
        deck2.add_card(new Card(9, "q9", "a9"));
        repo = new InMemoryDeckRepository(List.of(deck1, deck2));
    }

    @Test
    void deckExists_trueOnlyForKnownDecks() {
        assertTrue(repo.deckExists(1));
        assertTrue(repo.deckExists(2));
        assertFalse(repo.deckExists(3));
    }

    @Test
    void constructor_rejectsDuplicateDeckIds() {
        Deck dup = new Deck(1, "Dup", "x");
        assertThrows(IllegalArgumentException.class,
                () -> new InMemoryDeckRepository(List.of(deck1, dup)));
    }

    @Test
    void generateCardId_startsAboveHighestExistingId() {
        assertEquals(10, repo.generateCardId());
    }

    @Test
    void generateCardId_startsAtOneForEmptyRepository() {
        InMemoryDeckRepository empty = new InMemoryDeckRepository(List.of());
        assertEquals(1, empty.generateCardId());
    }

    @Test
    void generateCardId_neverRepeats() {
        int a = repo.generateCardId();
        int b = repo.generateCardId();
        assertNotEquals(a, b);
        assertEquals(a + 1, b);
    }

    @Test
    void saveCard_addsCardToCorrectDeck() {
        repo.saveCard(1, new Card(100, "q", "a"));
        assertEquals(2, deck1.getCards().size());
        assertEquals(1, deck2.getCards().size());
    }

    @Test
    void saveCard_toUnknownDeck_throws() {
        assertThrows(IllegalArgumentException.class, () -> repo.saveCard(77, new Card(100, "q", "a")));
    }

    @Test
    void saveCard_withIdUsedInAnyDeck_throws() {
        assertThrows(IllegalArgumentException.class, () -> repo.saveCard(1, new Card(9, "q", "a")));
        assertEquals(1, deck1.getCards().size());
    }

    @Test
    void findCard_returnsCopyOfStoredCard() {
        Optional<Card> found = repo.findCard(1, 4);
        assertTrue(found.isPresent());
        assertEquals("q4", found.get().getQuestion());

        found.get().updateContent("changed", "changed");
        assertEquals("q4", deck1.getCards().get(0).getQuestion());
    }

    @Test
    void findCard_unknownDeckOrCard_returnsEmpty() {
        assertTrue(repo.findCard(99, 4).isEmpty());
        assertTrue(repo.findCard(1, 999).isEmpty());
    }

    @Test
    void findCard_doesNotCrossDecks() {
        assertTrue(repo.findCard(1, 9).isEmpty());
    }

    @Test
    void updateCard_changesStoredContent() {
        repo.updateCard(1, new Card(4, "new q", "new a"));
        assertEquals("new q", deck1.getCards().get(0).getQuestion());
        assertEquals("new a", deck1.getCards().get(0).getAnswer());
    }

    @Test
    void updateCard_unknownDeck_throwsDataAccessException() {
        assertThrows(EditCardDataAccessException.class, () -> repo.updateCard(99, new Card(4, "q", "a")));
    }

    @Test
    void updateCard_unknownCard_throwsDataAccessExceptionAndDoesNotInsert() {
        assertThrows(EditCardDataAccessException.class, () -> repo.updateCard(1, new Card(555, "q", "a")));
        assertEquals(1, deck1.getCards().size());
    }
}
