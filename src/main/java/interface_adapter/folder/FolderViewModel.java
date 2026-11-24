package interface_adapter.folder;

import java.util.ArrayList;
import java.util.List;

public class FolderViewModel {
    public static final String VIEW_NAME = "folder";

    private final List<FolderViewModelListener> listeners = new ArrayList<>();
    private FolderState state = new FolderState();

    public FolderState getState() { return state; }
    public void setState(FolderState state) { this.state = state; }
    public void addListener(FolderViewModelListener listener) { listeners.add(listener); }

    public void notifyStateChanged() {
        for (FolderViewModelListener listener : listeners) listener.stateChanged();
    }

    /** Transitional: only needed while there is still one FolderPanel per folder. */
    public static String viewNameFor(int folderId) { return "folder:" + folderId; }
}
