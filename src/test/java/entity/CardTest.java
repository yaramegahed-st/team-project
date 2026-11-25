package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void constructor_storesIdQuestionAndAnswer() {
        Card card = new Card(7, "What is 2+2?", "4");
        assertEquals(7, card.getId());
        assertEquals("What is 2+2?", card.getQuestion());
        assertEquals("4", card.getAnswer());
    }

    @Test
    void constructor_stripsSurroundingWhitespace() {
        Card card = new Card(1, "  Q  ", "\tA\n");
        assertEquals("Q", card.getQuestion());
        assertEquals("A", card.getAnswer());
    }

    @Test
    void constructor_rejectsNullQuestion() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Card(1, null, "A"));
        assertEquals("Question cannot be blank.", e.getMessage());
    }

    @Test
    void constructor_rejectsBlankQuestion() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Card(1, "   ", "A"));
        assertEquals("Question cannot be blank.", e.getMessage());
    }

    @Test
    void constructor_rejectsNullAnswer() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Card(1, "Q", null));
        assertEquals("Answer cannot be blank.", e.getMessage());
    }

    @Test
    void constructor_rejectsBlankAnswer() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Card(1, "Q", ""));
        assertEquals("Answer cannot be blank.", e.getMessage());
    }

    @Test
    void updateContent_replacesQuestionAndAnswerButKeepsId() {
        Card card = new Card(3, "old q", "old a");
        card.updateContent("new q", "new a");
        assertEquals(3, card.getId());
        assertEquals("new q", card.getQuestion());
        assertEquals("new a", card.getAnswer());
    }

    @Test
    void updateContent_withInvalidAnswer_leavesCardUnchanged() {
        Card card = new Card(3, "old q", "old a");
        assertThrows(IllegalArgumentException.class, () -> card.updateContent("new q", " "));
        assertEquals("old q", card.getQuestion());
        assertEquals("old a", card.getAnswer());
    }

    @Test
    void updateContent_withInvalidQuestion_leavesCardUnchanged() {
        Card card = new Card(3, "old q", "old a");
        assertThrows(IllegalArgumentException.class, () -> card.updateContent(null, "new a"));
        assertEquals("old q", card.getQuestion());
        assertEquals("old a", card.getAnswer());
    }
}
