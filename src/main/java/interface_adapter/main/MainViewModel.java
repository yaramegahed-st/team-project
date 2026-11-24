package interface_adapter.main;

import java.util.ArrayList;
import java.util.List;

/**
 * View model information for the Home view.
 */
public class MainViewModel {
    public static final String VIEW_NAME = "home";

    private final List<MainViewModelListener> listeners = new ArrayList<>();
    private MainState state = new MainState();

    public MainState getState() { return this.state; }

    public void setState(MainState state) { this.state = state; }

    public void addListener(MainViewModelListener listener) { listeners.add(listener); }

    public void notifyStateChanged() {
        for (MainViewModelListener listener : listeners) {
            listener.stateChanged();
        }
    }
}
