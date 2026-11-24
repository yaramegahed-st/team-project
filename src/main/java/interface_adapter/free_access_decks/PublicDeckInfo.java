package interface_adapter.free_access_decks;

public class PublicDeckInfo {
    private final int id;
    private final String name;
    private final String owner;

    public PublicDeckInfo(int id, String name, String owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getOwner() { return owner; }
}
