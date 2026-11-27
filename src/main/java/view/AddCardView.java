package view;

import interface_adapter.add_card.AddCardController;
import interface_adapter.add_card.AddCardViewModel;

import javax.swing.*;
import java.awt.GridLayout;

public class AddCardView extends JPanel {
    public static final String VIEW_NAME = "addCard";

    private final JTextField questionField = new JTextField();
    private final JTextField answerField = new JTextField();
    private final JLabel resultLabel = new JLabel(" ");
    private final JButton createButton = new JButton("Create card");

    private Integer selectedDeckId;

    public AddCardView(AddCardController controller,
                       AddCardViewModel viewModel) {
        setLayout(new GridLayout(0, 1, 8, 8));

        add(new JLabel("Question"));
        add(questionField);
        add(new JLabel("Answer"));
        add(answerField);
        add(createButton);
        add(resultLabel);

        // Enable creating after a deck has been selected.
        createButton.setEnabled(false);

        createButton.addActionListener(event -> {
            if (selectedDeckId == null) {
                return;
            }

            controller.execute(
              selectedDeckId,
              questionField.getText(),
              answerField.getText()
            );
        });

        viewModel.addPropertyChangeListener(
          event -> {
            if (AddCardViewModel.CARD_ADDED.equals(event.getPropertyName())) {
                resultLabel.setText(viewModel.getMessage());
            } else if (AddCardViewModel.ERROR.equals(event.getPropertyName())) {
                resultLabel.setText(viewModel.getError());
            }
        });
    }

    public void openDeck(int deckId) {
        selectedDeckId = deckId;
        questionField.setText("");
        answerField.setText("");
        resultLabel.setText(" ");
        createButton.setEnabled(true);
    }

}