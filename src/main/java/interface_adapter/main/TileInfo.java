package interface_adapter.main;

/** What the Home view needs to draw one tile. Plain data, no entities. */
public class TileInfo {
    private final int id;
    private final String name;
    private final boolean deck;            // false = folder
    private final String access;           // "Public", "Private", ... (decks only)

    public TileInfo(int id, String name, boolean deck, String access) {
        this.id = id;
        this.name = name;
        this.deck = deck;
        this.access = access;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isDeck() { return deck; }
    public String getAccess() { return access; }
}
