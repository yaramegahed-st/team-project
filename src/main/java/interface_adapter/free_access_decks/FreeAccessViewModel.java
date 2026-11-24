package interface_adapter.free_access_decks;

import java.util.ArrayList;
import java.util.List;

/** View model for the Free Access Decks view. */
public class FreeAccessViewModel {
    public static final String VIEW_NAME = "free access";

    private final List<FreeAccessViewModelListener> listeners = new ArrayList<>();
    private FreeAccessState state = new FreeAccessState();

    public FreeAccessState getState() { return state; }
    public void setState(FreeAccessState state) { this.state = state; }
    public void addListener(FreeAccessViewModelListener l) { listeners.add(l); }

    public void notifyStateChanged() {
        for (FreeAccessViewModelListener l : listeners) l.stateChanged();
    }
}
