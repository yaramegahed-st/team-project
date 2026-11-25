package use_case.edit_card;

import entity.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EditCardInteractorTest {

    private FakeDao dao;
    private FakePresenter presenter;
    private EditCardInteractor interactor;

    @BeforeEach
    void setUp() {
        dao = new FakeDao();
        dao.deckId = 1;
        dao.card = new Card(5, "old q", "old a");
        presenter = new FakePresenter();
        interactor = new EditCardInteractor(dao, presenter);
    }

    @Test
    void validEdit_updatesCardAndReportsSuccess() {
        interactor.execute(new EditCardInputData(1, 5, "new q", "new a"));

        assertNull(presenter.error);
        assertNotNull(presenter.success);
        assertEquals(1, dao.updateCalls);
        assertEquals(5, dao.updated.getId());
        assertEquals("new q", dao.updated.getQuestion());
        assertEquals("new a", dao.updated.getAnswer());

        assertEquals(1, presenter.success.getDeckId());
        assertEquals(5, presenter.success.getCardId());
        assertEquals("new q", presenter.success.getQuestion());
        assertEquals("new a", presenter.success.getAnswer());
    }

    @Test
    void validEdit_trimsWhitespace() {
        interactor.execute(new EditCardInputData(1, 5, "  q ", " a  "));
        assertEquals("q", presenter.success.getQuestion());
        assertEquals("a", presenter.success.getAnswer());
    }

    @Test
    void unknownDeck_fails() {
        interactor.execute(new EditCardInputData(42, 5, "q", "a"));
        assertEquals("Deck does not exist", presenter.error);
        assertNull(presenter.success);
        assertEquals(0, dao.updateCalls);
    }

    @Test
    void unknownCard_fails() {
        interactor.execute(new EditCardInputData(1, 999, "q", "a"));
        assertEquals("Card does not exist in this deck", presenter.error);
        assertNull(presenter.success);
        assertEquals(0, dao.updateCalls);
    }

    @Test
    void blankQuestion_failsAndDoesNotUpdate() {
        interactor.execute(new EditCardInputData(1, 5, " ", "a"));
        assertEquals("Question cannot be blank.", presenter.error);
        assertNull(presenter.success);
        assertEquals(0, dao.updateCalls);
    }

    @Test
    void blankAnswer_failsAndDoesNotUpdate() {
        interactor.execute(new EditCardInputData(1, 5, "q", null));
        assertEquals("Answer cannot be blank.", presenter.error);
        assertNull(presenter.success);
        assertEquals(0, dao.updateCalls);
    }

    @Test
    void dataAccessFailure_reportsUnableToUpdate() {
        dao.failOnUpdate = true;
        interactor.execute(new EditCardInputData(1, 5, "q", "a"));
        assertEquals("Unable to update card", presenter.error);
        assertNull(presenter.success);
    }

    // ---- fakes ----
    private static class FakeDao implements EditCardDataAccessInterface {
        int deckId;
        Card card;
        Card updated;
        int updateCalls;
        boolean failOnUpdate;

        public boolean deckExists(int id) { return id == deckId; }
        public Optional<Card> findCard(int id, int cardId) {
            return (id == deckId && card.getId() == cardId) ? Optional.of(card) : Optional.empty();
        }
        public void updateCard(int id, Card c) {
            updateCalls++;
            if (failOnUpdate) throw new EditCardDataAccessException("boom");
            updated = c;
        }
    }

    private static class FakePresenter implements EditCardOutputBoundary {
        EditCardOutputData success;
        String error;
        public void prepareSuccessView(EditCardOutputData d) { success = d; }
        public void prepareFailView(String e) { error = e; }
    }
}
