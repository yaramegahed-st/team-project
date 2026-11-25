package use_case.add_card;

import entity.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AddCardInteractorTest {

    private FakeDao dao;
    private FakePresenter presenter;
    private AddCardInteractor interactor;

    @BeforeEach
    void setUp() {
        dao = new FakeDao();
        dao.decks.add(1);
        presenter = new FakePresenter();
        interactor = new AddCardInteractor(dao, presenter);
    }

    @Test
    void validInput_savesCardAndReportsSuccess() {
        interactor.execute(new AddCardInputData(1, "Capital of France?", "Paris"));

        assertNull(presenter.error);
        assertNotNull(presenter.success);
        assertEquals(1, dao.saved.size());
        assertEquals(1, dao.savedDeckIds.get(0));
        assertEquals("Capital of France?", dao.saved.get(0).getQuestion());
        assertEquals("Paris", dao.saved.get(0).getAnswer());

        assertEquals(dao.saved.get(0).getId(), presenter.success.getCardId());
        assertEquals(1, presenter.success.getDeckId());
        assertEquals("Capital of France?", presenter.success.getQuestion());
        assertEquals("Paris", presenter.success.getAnswer());
    }

    @Test
    void validInput_trimsWhitespaceBeforeSavingAndReporting() {
        interactor.execute(new AddCardInputData(1, "  Q  ", "  A  "));
        assertEquals("Q", dao.saved.get(0).getQuestion());
        assertEquals("A", presenter.success.getAnswer());
    }

    @Test
    void unknownDeck_failsWithoutSaving() {
        interactor.execute(new AddCardInputData(99, "Q", "A"));
        assertEquals("Deck does not exist.", presenter.error);
        assertNull(presenter.success);
        assertTrue(dao.saved.isEmpty());
    }

    @Test
    void blankQuestion_failsWithoutSaving() {
        interactor.execute(new AddCardInputData(1, "   ", "A"));
        assertEquals("Question cannot be blank.", presenter.error);
        assertNull(presenter.success);
        assertTrue(dao.saved.isEmpty());
    }

    @Test
    void blankAnswer_failsWithoutSaving() {
        interactor.execute(new AddCardInputData(1, "Q", ""));
        assertEquals("Answer cannot be blank.", presenter.error);
        assertNull(presenter.success);
        assertTrue(dao.saved.isEmpty());
    }

    @Test
    void nullFields_failWithoutSaving() {
        interactor.execute(new AddCardInputData(1, null, null));
        assertNotNull(presenter.error);
        assertTrue(dao.saved.isEmpty());
    }

    @Test
    void multipleCards_getDistinctIds() {
        interactor.execute(new AddCardInputData(1, "Q1", "A1"));
        interactor.execute(new AddCardInputData(1, "Q2", "A2"));
        Set<Integer> ids = new HashSet<>();
        for (Card c : dao.saved) {
            ids.add(c.getId());
        }
        assertEquals(2, ids.size());
    }

    // ---- fakes ----
    private static class FakeDao implements AddCardDataAccessInterface {
        final Set<Integer> decks = new HashSet<>();
        final List<Card> saved = new ArrayList<>();
        final List<Integer> savedDeckIds = new ArrayList<>();
        int nextId = 1;

        public boolean deckExists(int deckId) { return decks.contains(deckId); }
        public int generateCardId() { return nextId++; }
        public void saveCard(int deckId, Card card) { savedDeckIds.add(deckId); saved.add(card); }
    }

    private static class FakePresenter implements AddCardOutputBoundary {
        AddCardOutputData success;
        String error;

        public void prepareSuccessView(AddCardOutputData outputData) { success = outputData; }
        public void prepareFailView(String error) { this.error = error; }
    }
}
