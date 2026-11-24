package interface_adapter.study;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudyState {
    private int deckId;
    private String deckName = "";
    private String mode = "";                               // "Standard" or "Card select"
    private List<Integer> cardPositions = new ArrayList<>();
    private int currentIndex = 0;                           // which of the chosen cards is showing
    private boolean showingBack = false;
    private String error = "";

    public int getDeckId() { return deckId; }
    public void setDeckId(int deckId) { this.deckId = deckId; }

    public String getDeckName() { return deckName; }
    public void setDeckName(String deckName) { this.deckName = deckName; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public List<Integer> getCardPositions() { return Collections.unmodifiableList(cardPositions); }
    public void setCardPositions(List<Integer> positions) { this.cardPositions = new ArrayList<>(positions); }

    public int getCurrentIndex() { return currentIndex; }
    public void setCurrentIndex(int currentIndex) { this.currentIndex = currentIndex; }

    public boolean isShowingBack() { return showingBack; }
    public void setShowingBack(boolean showingBack) { this.showingBack = showingBack; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
