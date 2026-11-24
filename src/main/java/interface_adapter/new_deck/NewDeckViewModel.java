package interface_adapter.new_deck;

import java.util.ArrayList;
import java.util.List;

/** View model for the New Deck view. */
public class NewDeckViewModel {
    public static final String VIEW_NAME = "new deck";

    private final List<NewDeckViewModelListener> listeners = new ArrayList<>();
    private NewDeckState state = new NewDeckState();

    public NewDeckState getState() { return state; }
    public void setState(NewDeckState state) { this.state = state; }
    public void addListener(NewDeckViewModelListener l) { listeners.add(l); }

    public void notifyStateChanged() {
        for (NewDeckViewModelListener l : listeners) l.stateChanged();
    }
}
