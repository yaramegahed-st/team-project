package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void constructor_setsFieldsAndStartsEmpty() {
        Deck deck = new Deck(1, "Biology", "alice");
        assertEquals(1, deck.getId());
        assertEquals("Biology", deck.getName());
        assertEquals("alice", deck.getOwner());
        assertTrue(deck.getCards().isEmpty());
        assertTrue(deck.getEditors().isEmpty());
    }

    @Test
    void addCard_appendsInOrder() {
        Deck deck = new Deck(1, "D", "o");
        Card a = new Card(1, "q1", "a1");
        Card b = new Card(2, "q2", "a2");
        deck.add_card(a);
        deck.add_card(b);
        assertEquals(2, deck.getCards().size());
        assertSame(a, deck.getCards().get(0));
        assertSame(b, deck.getCards().get(1));
    }

    @Test
    void removeCard_removesMatchingId() {
        Deck deck = new Deck(1, "D", "o");
        deck.add_card(new Card(1, "q1", "a1"));
        deck.add_card(new Card(2, "q2", "a2"));
        deck.add_card(new Card(3, "q3", "a3"));

        deck.remove_card(2);

        assertEquals(2, deck.getCards().size());
        assertEquals(1, deck.getCards().get(0).getId());
        assertEquals(3, deck.getCards().get(1).getId());
    }

    @Test
    void removeCard_withUnknownId_isNoOp() {
        Deck deck = new Deck(1, "D", "o");
        deck.add_card(new Card(1, "q1", "a1"));
        deck.remove_card(99);
        assertEquals(1, deck.getCards().size());
    }

    @Test
    void removeCard_onEmptyDeck_doesNotThrow() {
        Deck deck = new Deck(1, "D", "o");
        assertDoesNotThrow(() -> deck.remove_card(1));
    }
}
