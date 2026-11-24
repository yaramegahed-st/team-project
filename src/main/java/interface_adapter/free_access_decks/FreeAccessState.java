package interface_adapter.free_access_decks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FreeAccessState {
    private List<PublicDeckInfo> decks = new ArrayList<>();
    private String error = "";

    public List<PublicDeckInfo> getDecks() { return Collections.unmodifiableList(decks); }
    public void setDecks(List<PublicDeckInfo> decks) { this.decks = new ArrayList<>(decks); }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
