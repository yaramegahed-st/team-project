package use_case.edit_deck;

public interface EditDeckOutputBoundary {
    void prepareSuccessView(EditDeckOutputData outputData);
    void prepareFailureView(String errorMessage);
}
