package interface_adapter.new_deck;

import interface_adapter.home.HomeViewModel;

public class NewDeckState {
    private String originView = HomeViewModel.VIEW_NAME;   // where to return to
    private Integer folderId = null;                        // null = not inside a folder
    private String error = "";

    public String getOriginView() { return originView; }
    public void setOriginView(String originView) { this.originView = originView; }

    public Integer getFolderId() { return folderId; }
    public void setFolderId(Integer folderId) { this.folderId = folderId; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
