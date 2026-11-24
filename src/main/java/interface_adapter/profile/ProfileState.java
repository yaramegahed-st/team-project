package interface_adapter.profile;

public class ProfileState {
    private String username = "";
    private String message = "";      // e.g. "Password updated"
    private String error = "";

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
