package view;

import interface_adapter.edit_card.EditCardController;
import interface_adapter.edit_card.EditCardViewModel;

import javax.swing.*;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class EditCardView extends JPanel {
    private final JTextField questionField = new JTextField();
    private final JTextField answerField = new JTextField();
    private final JLabel resultLabel = new JLabel(" ");
    private final JButton saveButton = new JButton("Save changes");

    private Integer selectedDeckId;
    private Integer selectedCardId;

    private final EditCardViewModel viewModel;
    private final PropertyChangeListener listener = this::displayResult;

    public EditCardView(EditCardController controller,
                        EditCardViewModel viewModel) {
        this.viewModel = viewModel;

        setLayout(new GridLayout(0, 1, 8, 8));

        add(new JLabel("Question"));
        add(questionField);
        add(new JLabel("Answer"));
        add(answerField);
        add(saveButton);
        add(resultLabel);

        // Enable saving after a card has been selected.
        saveButton.setEnabled(false);

        saveButton.addActionListener(event -> {
            if (selectedDeckId == null || selectedCardId == null) {
                resultLabel.setText("Please select a card first.");
                return;
            }

            controller.execute(
              selectedDeckId,
              selectedCardId,
              questionField.getText(),
              answerField.getText()
            );
        });

        viewModel.addPropertyChangeListener(listener);
    }

    public void openCard(int deckId, int cardId,
                         String question, String answer) {
        selectedDeckId = deckId;
        selectedCardId = cardId;

        questionField.setText(question);
        answerField.setText(answer);
        resultLabel.setText(" ");
        saveButton.setEnabled(true);
    }

    private void displayResult(PropertyChangeEvent event) {
        if (EditCardViewModel.CARD_UPDATED.equals(
                event.getPropertyName())) {
            questionField.setText(viewModel.getQuestion());
            answerField.setText(viewModel.getAnswer());
            resultLabel.setText(viewModel.getMessage());
        } else if (EditCardViewModel.ERROR.equals(
                event.getPropertyName())) {
            resultLabel.setText(viewModel.getError());
        }
    }

    public void dispose() {
        viewModel.removePropertyChangeListener(listener);
    }
}