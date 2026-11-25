package use_case.add_deck;

import entity.Deck;
import use_case.session.CurrentUserAccess;

public class AddDeckInteractor implements AddDeckInputBoundary {
  private final AddDeckDataAccessInterface dataAccess;
  private final AddDeckOutputBoundary presenter;
  private final CurrentUserAccess currentUserAccess;

  public AddDeckInteractor( 
      AddDeckDataAccessInterface dataAccess,
      AddDeckOutputBoundary presenter, 
      CurrentUserAccess currentUserAccess
                          ) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
    this.currentUserAccess = currentUserAccess;
  }

  @Override
  public void execute(AddDeckInputData inputData) {
    String owner = currentUserAccess.getCurrentUserName();

    if (owner == null || owner.isEmpty()) {
      presenter.prepareFailureView("Please sign in before creating a deck.");
      return;
      }

  // save our new deck if valid
  Deck deck;

  try {
    int deckId = dataAccess.generateDeckId();

  // notify presenter new deck was un sucessful
    try { 
      deck = new Deck(deckId, inputData.getDeckName(), owner);
      } catch (IllegalArgumentException exception) {
        presenter.prepareFailureView("Error creating deck: " + exception.getMessage());
        return;
      }

    dataAccess.saveDeck(deck);
  } catch (AddDeckDataAccessInterfaceException exception) {
      presenter.prepareFailureView("Unable to save deck, please try again later.");
      return;
    }
  // notify presenter new deck was updated

  presenter.prepareSuccessView(new AddDeckOutputData(
      deck.getId(), 
      deck.getName(), 
      deck.getOwner()
      ));
  }
}
