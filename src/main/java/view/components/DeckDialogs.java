package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DeckDialogs {
    private static final Color BG    = new Color(0x93B5D1);
    private static final Color FIELD = new Color(0xD9D9D9);
    private static final Color TEXT  = new Color(0x1F3A4D);

    /** What the Create dialog returns. */
    public static class NewDeck {
        public final String name;
        public final boolean isPublic;
        public final String permission;          // empty when the deck is public

        NewDeck(String name, boolean isPublic, String permission) {
            this.name = name;
            this.isPublic = isPublic;
            this.permission = permission;
        }
    }

    // ---------- Create dialog ----------
    public static NewDeck showCreate(Window owner) {
        JDialog dialog = new JDialog(owner, "Create deck", Dialog.ModalityType.APPLICATION_MODAL);
        final NewDeck[] result = {null};

        JPanel content = newContent();
        JTextField nameField = field();
        JTextField permField = field();
        ToggleSwitch toggle = new ToggleSwitch();                // off = private
        JLabel permLabel = label("Create A Deck Permission:");

        content.add(label("Create a Deck Name:"));
        content.add(Box.createVerticalStrut(4));
        content.add(nameField);
        content.add(Box.createVerticalStrut(12));
        content.add(label("Do you want your deck to be public?"));
        content.add(Box.createVerticalStrut(6));
        content.add(toggle);
        content.add(Box.createVerticalStrut(12));
        content.add(permLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(permField);
        content.add(Box.createVerticalStrut(14));

        JButton cancel = new JButton("Cancel");
        JButton create = new JButton("Create");
        content.add(buttonRow(cancel, create));

        // the permission field only applies to private decks
        Runnable refresh = () -> {
            boolean showPerm = !toggle.isOn();
            permLabel.setVisible(showPerm);
            permField.setVisible(showPerm);
            dialog.pack();
        };
        toggle.setOnChange(refresh);

        cancel.addActionListener(e -> dialog.dispose());
        create.addActionListener(e -> {
            String name = nameField.getText().trim();
            boolean isPublic = toggle.isOn();
            String perm = permField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a deck name.");
                return;
            }
            if (!isPublic && perm.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a permission for a private deck.");
                return;
            }
            result[0] = new NewDeck(name, isPublic, isPublic ? "" : perm);
            dialog.dispose();
        });

        dialog.setContentPane(content);
        dialog.getRootPane().setDefaultButton(create);
        dialog.setResizable(false);
        refresh.run();                                           // hides/shows the permission field, then packs
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);                                 // blocks until closed
        return result[0];
    }

    // ---------- Access dialog ----------
    /** Returns the permission the user typed, or null if cancelled. */
    public static String showAccess(Window owner) {
        JDialog dialog = new JDialog(owner, "Access deck", Dialog.ModalityType.APPLICATION_MODAL);
        final String[] result = {null};

        JPanel content = newContent();
        JTextField permField = field();
        content.add(label("Enter Deck Permission:"));
        content.add(Box.createVerticalStrut(4));
        content.add(permField);
        content.add(Box.createVerticalStrut(14));

        JButton cancel = new JButton("Cancel");
        JButton access = new JButton("Access");
        content.add(buttonRow(cancel, access));

        cancel.addActionListener(e -> dialog.dispose());
        access.addActionListener(e -> {
            String perm = permField.getText().trim();
            if (perm.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a deck permission.");
                return;
            }
            result[0] = perm;
            dialog.dispose();
        });

        dialog.setContentPane(content);
        dialog.getRootPane().setDefaultButton(access);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
        return result[0];
    }

    // ---------- helpers ----------
    private static JPanel newContent() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));
        return p;
    }

    private static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 12f));
        l.setForeground(TEXT);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private static JTextField field() {
        JTextField f = new JTextField(18);
        f.setBackground(FIELD);
        f.setForeground(TEXT);
        f.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, f.getPreferredSize().height));
        f.setAlignmentX(Component.CENTER_ALIGNMENT);
        return f;
    }

    private static JPanel buttonRow(JButton... buttons) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        row.setOpaque(false);
        for (JButton b : buttons) row.add(b);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        return row;
    }

    /** Red (off) / green (on) switch like the mockup. */
    private static class ToggleSwitch extends JComponent {
        private boolean on = false;
        private Runnable onChange;

        ToggleSwitch() {
            Dimension d = new Dimension(56, 26);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    on = !on;
                    repaint();
                    if (onChange != null) onChange.run();
                }
            });
        }

        boolean isOn() { return on; }
        void setOnChange(Runnable r) { this.onChange = r; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(on ? new Color(0x8FD6AE) : new Color(0xD98B8B));
            g2.fillRoundRect(0, 0, w, h, h, h);
            int d = h - 6;
            g2.setColor(Color.WHITE);
            g2.fillOval(on ? w - d - 3 : 3, 3, d, d);
            g2.dispose();
        }
    }
}
