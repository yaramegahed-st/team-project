package use_case.edit_deck;

public class EditDeckOutputData {
    private final int deckId;
    private final String name;
    private final String owner;

    public EditDeckOutputData(int deckId, String name, String owner) {
        this.deckId = deckId;
        this.name = name;
        this.owner = owner;
    }

    public int getDeckId() {
        return deckId;
    }

    public String getName() {
        return this.name;
    }

    public String getOwner() { return this.owner; }
}
