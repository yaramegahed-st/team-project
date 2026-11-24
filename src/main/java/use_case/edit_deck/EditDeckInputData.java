package use_case.edit_deck;

public class EditDeckInputData {
    private final int deckId;
    private final String name;

    public EditDeckInputData(int deckId, String name) {
        this.deckId = deckId;
        this.name = name;
    }

    public int getDeckId() {
        return deckId;
    }

    public String getName() {
        return name;
    }
}
