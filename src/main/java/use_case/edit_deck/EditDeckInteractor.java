package use_case.edit_deck;

import entity.Deck;
import use_case.session.CurrentUserAccess;

import java.util.Optional;

public class EditDeckInteractor 
    implements EditDeckInputBoundary {
        private final EditDeckDataAccessInterface dataAccess;
        private final EditDeckOutputBoundary presenter;
        private final CurrentUserAccess currentUserAccess;

    public EditDeckInteractor(
        EditDeckDataAccessInterface dataAccess, 
        EditDeckOutputBoundary presenter, 
        CurrentUserAccess currentUserAccess) 
        {
            this.dataAccess = dataAccess;
            this.presenter = presenter;
            this.currentUserAccess = currentUserAccess;
        }

    @Override
    public void execute(EditDeckInputData inputData) {
        int deckId = inputData.getDeckId();
        String newName = inputData.getName();
        String username = currentUserAccess.getCurrentUserName();

        if (username == null || username.isEmpty()) {
            presenter.prepareFailureView("Please login first.");
            return;
        }

        Deck deck;

        try {
            Optional<Deck> result =
                    dataAccess.findDeck(inputData.getDeckId());

            if (result.isEmpty()) {
                presenter.prepareFailureView("Deck does not exist.");
                return;
            }

            // initialize only after finding the deck to avoid null exception
            deck = result.get();

            if (!username.equals(deck.getOwner())) {
                presenter.prepareFailureView(
                        "Only the owner can rename this deck."
                );
                return;
            }

            try {
                deck.rename(inputData.getName());
            } catch (IllegalArgumentException exception) {
                presenter.prepareFailureView(exception.getMessage());
                return;
            }

            dataAccess.renameDeck(
                    deck.getId(),
                    deck.getName(),
                    username
            );
        } catch (EditDeckDataAccessException exception) {
            presenter.prepareFailureView(
                    "Unable to rename the deck. Please try again."
            );
            return;
        }

        presenter.prepareSuccessView(new EditDeckOutputData(
                deck.getId(),
                deck.getName(),
                deck.getOwner()
        ));
    }
}
