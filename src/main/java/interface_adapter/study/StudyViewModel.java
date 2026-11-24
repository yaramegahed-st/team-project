package interface_adapter.study;

import java.util.ArrayList;
import java.util.List;

/** View model for the Study view. */
public class StudyViewModel {
    public static final String VIEW_NAME = "study";

    private final List<StudyViewModelListener> listeners = new ArrayList<>();
    private StudyState state = new StudyState();

    public StudyState getState() { return state; }
    public void setState(StudyState state) { this.state = state; }
    public void addListener(StudyViewModelListener l) { listeners.add(l); }

    public void notifyStateChanged() {
        for (StudyViewModelListener l : listeners) l.stateChanged();
    }
}
