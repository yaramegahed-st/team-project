package interface_adapter.chatbot;

import java.util.ArrayList;
import java.util.List;

/** View model for the Chatbot view. */
public class ChatbotViewModel {
    public static final String VIEW_NAME = "chatbot";

    private final List<ChatbotViewModelListener> listeners = new ArrayList<>();
    private ChatbotState state = new ChatbotState();

    public ChatbotState getState() { return state; }
    public void setState(ChatbotState state) { this.state = state; }
    public void addListener(ChatbotViewModelListener l) { listeners.add(l); }

    public void notifyStateChanged() {
        for (ChatbotViewModelListener l : listeners) l.stateChanged();
    }
}
