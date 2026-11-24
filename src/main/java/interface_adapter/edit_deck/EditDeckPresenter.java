package interface_adapter.edit_deck;

import use_case.edit_deck.EditDeckOutputBoundary;
import use_case.edit_deck.EditDeckOutputData;

public class EditDeckPresenter implements EditDeckOutputBoundary {
    private final EditDeckViewModel viewModel;

    public EditDeckPresenter(EditDeckViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(EditDeckOutputData outputData) {
        viewModel.setSuccess(
                outputData.getDeckId(),
                outputData.getName(),
                outputData.getOwner(),
                "Deck rename completed."
        );
    }

    @Override
    public void prepareFailureView(String errorMessage) {
        viewModel.setError(errorMessage);
    }
}
