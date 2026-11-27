package use_case.edit_card;

import entity.Card;
import use_case.access_deck.DeckPermissionAccess;
import use_case.access_deck.DeckPermissionException;
import use_case.session.CurrentUserAccess;

import java.util.Optional;

public class EditCardInteractor implements EditCardInputBoundary{
    private final EditCardDataAccessInterface dataAccessInterface;
    private final EditCardOutputBoundary presenter;
    private final CurrentUserAccess currentUserAccess;
    private final DeckPermissionAccess deckPermissionAccess;

    public EditCardInteractor(
        EditCardDataAccessInterface dataAccessInterface, 
        EditCardOutputBoundary presenter, 
        CurrentUserAccess currentUserAccess, 
        DeckPermissionAccess deckPermissionAccess) {
        this.dataAccessInterface = dataAccessInterface;
        this.presenter = presenter;
        this.currentUserAccess = currentUserAccess;
        this.deckPermissionAccess = deckPermissionAccess;
    }

    @Override
    public void execute(EditCardInputData inputData) {

        String username = currentUserAccess.getCurrentUserName();

        if (username == null || username.isBlank()) {
            presenter.prepareFailView(
                    "Please sign in before editing cards."
            );
            return;
        }

        Card updatedCard;
    
        try {
            if (!dataAccessInterface.deckExists((inputData.getDeckId()))) {
                presenter.prepareFailView("Deck does not exist");
                return;
            }

            if (!deckPermissionAccess.canEditDeck(
                    inputData.getDeckId(), username)) {
                presenter.prepareFailView(
                        "You do not have permission to edit this deck."
                );
                return;
            }

            Optional<Card> existingCard = dataAccessInterface.findCard(inputData.getDeckId(), inputData.getCardId());

            if (existingCard.isEmpty()) {
                presenter.prepareFailView("Card does not exist in this deck");
                return;
            }

            try {
                updatedCard = new Card(
                        existingCard.get().getId(),
                        inputData.getQuestion(),
                        inputData.getAnswer()
                );
            } catch (IllegalArgumentException exception) {
                presenter.prepareFailView(exception.getMessage());
                return;
            }

            // deck must exist to edit a card in it
            if (!dataAccessInterface.deckExists(inputData.getDeckId())) {
                presenter.prepareFailView("Deck does not exist");
                return;
            }

        dataAccessInterface.updateCard(inputData.getDeckId(), updatedCard, username);

        } catch (DeckPermissionException exception) {
            presenter.prepareFailView(exception.getMessage());
            return;
        } catch (EditCardDataAccessException exception) {
            presenter.prepareFailView("Unable to update card");
            return;
        } 


        // otherwise we are ok with change
        presenter.prepareSuccessView(new EditCardOutputData(
                inputData.getDeckId(),
                updatedCard.getId(),
                updatedCard.getQuestion(),
                updatedCard.getAnswer()
        ));
    }
}
