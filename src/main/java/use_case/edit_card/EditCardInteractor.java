package use_case.edit_card;

import entity.Card;

import java.util.Optional;

public class EditCardInteractor implements EditCardInputBoundary{
    private final EditCardDataAccessInterface dataAccessInterface;
    private final EditCardOutputBoundary presenter;

    public EditCardInteractor(EditCardDataAccessInterface dataAccessInterface, EditCardOutputBoundary presenter) {
        this.dataAccessInterface = dataAccessInterface;
        this.presenter = presenter;
    }

    @Override
    public void execute(EditCardInputData inputData) {
        Card updatedCard;

        try {
            if (!dataAccessInterface.deckExists((inputData.getDeckId()))) {
                presenter.prepareFailView("Deck does not exist");
                return;
            }

            Optional<Card> existingCard = dataAccessInterface.findCard(inputData.getDeckId(), inputData.getCardId());

            if (existingCard.isEmpty()) {
                presenter.prepareFailView("Card does not exist in this deck");
                return;
            }

            try {
                updatedCard = new Card(existingCard.get().getId(),
                        inputData.getQuestion(),
                        inputData.getAnswer()
                );
            } catch (IllegalArgumentException exception) {
                presenter.prepareFailView(exception.getMessage());
                return;
            }

            dataAccessInterface.updateCard(inputData.getDeckId(), updatedCard);

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
