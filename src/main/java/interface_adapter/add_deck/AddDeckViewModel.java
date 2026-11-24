package interface_adapter.add_deck;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AddDeckViewModel {
  private static final String VIEW_NAME = "addDeck";
  private static final String DECK_ADDED = "deckAdded";
  private static final String ERROR = "error";

  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  private int deckId;
  private String name = "";
  private String owner = "";
  private String error = "";
  private String message = "";

  public void setSuccess(int deckId, String name, String owner, String message) {
    this.deckId = deckId;
    this.name = name;
    this.owner = owner;
    this.message = message;
    this.error = "";

    support.firePropertyChange("DECK_ADDED", null, deckId);
  }

  public void setError(String error) {
    this.error = error;
    this.message = "";

    support.firePropertyChange(ERROR, null, error);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    support.removePropertyChangeListener(listener);
  }

  public int getDeckId() {
    return deckId;
  }

  public String getName() {
    return name;
  }

  public String getOwner() {
    return owner;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }
}
