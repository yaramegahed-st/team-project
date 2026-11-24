package interface_adapter.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** The state for the Home View Model: the tiles to show and any error to display. */
public class MainState {
    private List<TileInfo> tiles = new ArrayList<>();
    private String error = "";

    public List<TileInfo> getTiles() { return Collections.unmodifiableList(tiles); }
    public void setTiles(List<TileInfo> tiles) { this.tiles = new ArrayList<>(tiles); }
    public void addTile(TileInfo tile) { tiles.add(tile); }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
