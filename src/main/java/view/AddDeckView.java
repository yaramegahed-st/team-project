//test
package view;

import interface_adapter.add_deck.AddDeckController;
import interface_adapter.add_deck.AddDeckViewModel;

import javax.swing.*;
import java.awt.GridLayout;

public class AddDeckView extends JPanel {
    private final JTextField nameField = new JTextField();
    private final JLabel resultLabel = new JLabel(" ");

    private AddDeckController controller;

    public AddDeckView(AddDeckViewModel viewModel) {
        setLayout(new GridLayout(0, 1, 8, 8));

        JButton createButton = new JButton("Create deck");

        add(new JLabel("Deck name"));
        add(nameField);
        add(createButton);
        add(resultLabel);

        createButton.addActionListener(event -> {
            if (controller == null) {
                throw new IllegalStateException(
                        "Add Deck controller has not been configured."
                );
            }

            controller.execute(nameField.getText());
        });

        viewModel.addPropertyChangeListener(event -> {
            if (AddDeckViewModel.DECK_ADDED.equals(
                    event.getPropertyName())) {
                resultLabel.setText(viewModel.getMessage());
                nameField.setText("");
            } else if (AddDeckViewModel.ERROR.equals(
                    event.getPropertyName())) {
                resultLabel.setText(viewModel.getError());
            }
        });
    }

    public void setAddDeckController(AddDeckController controller) {
        this.controller = controller;
    }
}
