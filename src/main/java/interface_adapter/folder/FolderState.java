package interface_adapter.folder;
import interface_adapter.main.TileInfo;

import java.util.ArrayList;
import java.util.List;

public class FolderState {
    private int folderId;
    private String folderName = "";
    private List<TileInfo> decks = new ArrayList<>();
    private String error = "";
}
