package use_case.add_card;

import entity.Card;
import use_case.access_deck.DeckPermissionAccess;
import use_case.access_deck.DeckPermissionException;
import use_case.session.CurrentUserAccess;

public class AddCardInteractor implements AddCardInputBoundary {
    private final AddCardDataAccessInterface dataAccess;
    private final AddCardOutputBoundary presenter;
    private final CurrentUserAccess currentUserAccess;
    private final DeckPermissionAccess deckPermissionAccess;

    public AddCardInteractor(
        AddCardDataAccessInterface dataAccess, 
        AddCardOutputBoundary presenter, 
        CurrentUserAccess currentUserAccess, 
        DeckPermissionAccess deckPermissionAccess) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.currentUserAccess = currentUserAccess;
        this.deckPermissionAccess = deckPermissionAccess;
    }

    // check the deck exists first
    @Override
    public void execute(AddCardInputData inputData) {

        // user must be signed in to add or create cards
        String username = currentUserAccess.getCurrentUserName();
        if (username == null || username.isBlank()) {
            presenter.prepareFailView("User must be signed in to add cards.");
            return;
        }

        // deck must exist to add a card to it
        if (!dataAccess.deckExists(inputData.getDeckId())) {
            presenter.prepareFailView("Deck does not exist.");
            return;
        }

        if (!deckPermissionAccess.canEditDeck(inputData.getDeckId(), username)) {
            presenter.prepareFailView("User does not have permission to edit this deck");
            return;
        }

        // create the new card
        Card card;
        try {
            card = new Card(
                dataAccess.generateCardId(),
                inputData.getQuestion(),
                inputData.getAnswer()
            );
        } catch (IllegalArgumentException exception) {
            presenter.prepareFailView(exception.getMessage());
            return;
        }

        // save the card through data access interface 
        try {
            dataAccess.saveCard(inputData.getDeckId(), card, username);
        } catch (DeckPermissionException exception) {
            presenter.prepareFailView(exception.getMessage());
            return;
        }

        // send our result to output boundary
        AddCardOutputData outputData = new AddCardOutputData(
            card.getId(),
            inputData.getDeckId(),
            card.getQuestion(),
            card.getAnswer()
        );

        // notify presenter of okay
        presenter.prepareSuccessView(outputData);
    }
}