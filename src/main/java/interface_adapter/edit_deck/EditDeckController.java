package interface_adapter.edit_deck;

import use_case.edit_deck.EditDeckInputBoundary;
import use_case.edit_deck.EditDeckInputData;

public class EditDeckController {
    private final EditDeckInputBoundary interactor;

    public EditDeckController(EditDeckInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(int deckId, String name) {
        interactor.execute(new EditDeckInputData(deckId, name));
    }
}
