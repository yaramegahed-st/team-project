package use_case.add_deck;

import entity.Deck;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AddDeckInteractor.execute(...) is currently an empty stub, so this test is disabled.
 * Remove @Disabled once the interactor saves the deck and notifies the presenter.
 * (Also note: AddDeckInputData has no getOwner(), and AddDeckOutputBoundary names its failure
 * method prepareFailureView while every other use case uses prepareFailView.)
 */
class AddDeckInteractorTest {

    @Test
    @Disabled("AddDeckInteractor.execute is not implemented yet")
    void validDeck_isSavedAndPresenterNotified() {
        List<Deck> saved = new ArrayList<>();
        List<AddDeckOutputData> successes = new ArrayList<>();
        AddDeckDataAccessInterface dao = new AddDeckDataAccessInterface() {
            public boolean deckExists(int deckId) { return false; }
            public int generateDeckId() { return 1; }
            public void saveDeck(Deck deck) { saved.add(deck); }
        };
        AddDeckOutputBoundary presenter = new AddDeckOutputBoundary() {
            public void prepareSuccessView(AddDeckOutputData d) { successes.add(d); }
            public void prepareFailureView(String error) { fail(error); }
        };

        new AddDeckInteractor(dao, presenter).execute(new AddDeckInputData(1, "Biology", "alice"));

        assertEquals(1, saved.size());
        assertEquals("Biology", saved.get(0).getName());
        assertEquals(1, successes.size());
    }
}
