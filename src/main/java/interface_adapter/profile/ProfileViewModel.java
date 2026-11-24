package interface_adapter.profile;

import java.util.ArrayList;
import java.util.List;

/** View model for the Profile view. */
public class ProfileViewModel {
    public static final String VIEW_NAME = "profile";

    private final List<ProfileViewModelListener> listeners = new ArrayList<>();
    private ProfileState state = new ProfileState();

    public ProfileState getState() { return state; }
    public void setState(ProfileState state) { this.state = state; }
    public void addListener(ProfileViewModelListener l) { listeners.add(l); }

    public void notifyStateChanged() {
        for (ProfileViewModelListener l : listeners) l.stateChanged();
    }
}
