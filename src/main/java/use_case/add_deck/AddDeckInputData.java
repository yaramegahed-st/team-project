package use_case.add_deck;

import entity.Card;
import entity.User;

// import java.util.ArrayList;

public class AddDeckInputData {
  private String deckName;
  // private String owner;
  // private ArrayList<User> editors;
  // private ArrayList<Card> cards;

  public AddDeckInputData (String deckName){
    this.deckName = deckName;
    // this.owner = owner;
    // this.editors = new ArrayList<>();
    // this.cards = new ArrayList<>();
  }

  public String getDeckName() {
    return deckName;
  }

}
