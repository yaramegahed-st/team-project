package interface_adapter.chatbot;

public class ChatbotState {
    private String prompt = "";
    private String response = "";
    private String error = "";
    private boolean waiting = false;          // true while the assistant is working

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public boolean isWaiting() { return waiting; }
    public void setWaiting(boolean waiting) { this.waiting = waiting; }
}
