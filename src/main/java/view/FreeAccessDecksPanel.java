package view;

import javax.swing.*;
import java.awt.*;
import view.style.*;
import view.components.*;

public class FreeAccessDecksPanel extends JPanel {
    private static final double BASE_W = 651.0;
    private static final double BASE_H = 394.0;
    private final double s;
    private final JPanel box;
    private final OutlinedLabel title;
    private final java.util.List<OutlinedLabel> bodyLabels = new java.util.ArrayList<>();

    private static final Color FILL    = Color.decode("#8AD1D1");   // teal text, approximate from your mockup
    private static final Color OUTLINE = Color.decode("#1F3A4D");

    private final OutlinedLabel titleFlash;

    private Runnable onTitleClicked;

    public void setOnTitleClicked(Runnable r) { this.onTitleClicked = r; }

    private final SpinningIconButton settingsButton;
    private Runnable onSettingsClicked;   // set from MyFrame

    public void setOnSettingsClicked(Runnable r) {
        this.onSettingsClicked = r;
    }

    private java.util.function.Consumer<String> onFolderCreated;
    private Runnable onCancelled;

    public void setOnFolderCreated(java.util.function.Consumer<String> c) { this.onFolderCreated = c; }
    public void setOnCancelled(Runnable r) { this.onCancelled = r; }

    private final java.util.List<TileButton> tiles = new java.util.ArrayList<>();
    private JPanel tileBox;
    private Font tileFont;
    private int tileW, tileH, tileGap, tileStartX, tileTopY, viewW, viewH;
    private final JLabel emptyLabel = new JLabel("No public decks yet", SwingConstants.CENTER);

    /** Asks for the folder name; call this after the panel is on screen. */
    public void promptForName() {
        String name = JOptionPane.showInputDialog(this, "Name your new folder:", "New folder");
        if (name != null && !name.trim().isEmpty()) {
            if (onFolderCreated != null) onFolderCreated.accept(name.trim());
        } else {
            if (onCancelled != null) onCancelled.run();      // cancelled or blank: go back
        }
    }

    // Styles a label and sizes it to fit its text at the given design-time font size
    private void styleAndFit(OutlinedLabel label, double strokeW,  Font f, int shadowX, int shadowY) {
        label.setForeground(Color.decode("#386782"));
        label.setStrokeColor(Color.decode("#C7C7C7"));
        label.setStrokeWidth((float) (strokeW * s));
        label.setShadowColor(new Color(0, 20, 40, 150));
        label.setShadowOffset(sc(shadowX), sc(shadowY));

        FontMetrics fm = label.getFontMetrics(f);
        int pad = sc(8);                                     // room for the outline and shadow
        label.setSize(fm.stringWidth(label.getText()) + 2 * pad,
                fm.getHeight() + 2 * pad);
    }

    private int sc(double v) {
        return (int) Math.round(v * s);
    }

    public FreeAccessDecksPanel(int width) {
        s = width / BASE_W;
        int height = sc(BASE_H);

        setLayout(null);
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.decode("#F1FBFE"));

        int margin = sc(10);
        int settingsSlot = sc(40);
        int lift = sc(8);

        // --- Flash title (top-left) ---
        titleFlash = new OutlinedLabel("FlashShare");
        Font f1 = FontLoader.loadDamion((float) (26f * s));      // font size here
        titleFlash.setFont(f1);
        styleAndFit(titleFlash, 0.9, f1, 2, 3);                  // stroke width here
        titleFlash.setLocation(sc(0), sc(0));

        titleFlash.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        titleFlash.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (onTitleClicked != null) onTitleClicked.run();
            }
        });

        LabelHoverZoom.attach(titleFlash, f1, (float) (1.0 * s), sc(2), sc(3), sc(8), 1.08,
                () -> { if (onTitleClicked != null) onTitleClicked.run(); });


        // --- Title (centered at the top) ---
        title = new OutlinedLabel("Free Access Decks");
        styleLabel(title, FontLoader.loadAdlam((float) (44f * s)), 1.2);
        title.setLocation((width - title.getWidth()) / 2, sc(4));
        add(title);
        // --- Settings button (top-right corner) ---
        int iconSize = settingsSlot - sc(6);                       // leave a little breathing room
        ImageIcon rawIcon = new ImageIcon("src/main/resources/icons/settings2.jpeg");
        Image scaledIcon = rawIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);

        settingsButton = new SpinningIconButton(scaledIcon);
        settingsButton.setSize(settingsSlot, settingsSlot);
        settingsButton.setBorderPainted(false);       // no border
        settingsButton.setContentAreaFilled(false);   // no gray button background
        settingsButton.setFocusPainted(false);        // no focus ring
        settingsButton.setOpaque(false);
        settingsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        settingsButton.setToolTipText("Settings");

        int btnX = width - margin - settingsSlot;
        int btnY = margin - lift + (titleFlash.getHeight() - settingsButton.getHeight()) / 2;
        settingsButton.setLocation(btnX, btnY);

        settingsButton.addActionListener(e -> {
            if (onSettingsClicked != null) onSettingsClicked.run();
        });

        // --- Container (centered, below the title row) ---
        int boxW = width - 2 * sc(15);
        int boxH = sc(120);

        // bottom edge of the lowest item in the top row
        int titleRowBottom = Math.max(title.getY() + title.getHeight(),
                Math.max(titleFlash.getY() + titleFlash.getHeight(),
                        settingsButton.getY() + settingsButton.getHeight()));

        int boxY = titleRowBottom + sc(15);                      // 15 = gap under the titles; raise to move it further down

        box = new JPanel(null);
        box.setBackground(Color.decode("#C9DFEA"));
        box.setBorder(BorderFactory.createLineBorder(Color.decode("#43687F"), 1));
        box.setBounds((width - boxW) / 2, boxY, boxW, boxH);
        add(box);

        // --- Scrollable tiles inside the container ---
        tileFont = new Font(Font.SERIF, Font.PLAIN, 1).deriveFont((float) (boxH * 0.20));   // field, not a local

        int barH = sc(10);
        viewW = boxW - 2;
        viewH = boxH - 2 - barH;
        tileH = (int) Math.round(boxH * 0.68);
        tileW = (int) Math.round(tileH * 1.17);
        tileGap = (int) Math.round(tileW * 0.55);
        tileStartX = sc(20);
        tileTopY = (viewH - tileH) / 2;

        tileBox = new JPanel(null);
        tileBox.setOpaque(false);
        tileBox.setPreferredSize(new Dimension(viewW, viewH));

        emptyLabel.setFont(FontLoader.loadAdlam((float) (16f * s)));
        emptyLabel.setBounds(0, 0, viewW, viewH);
        tileBox.add(emptyLabel);

        JScrollPane scroll = new JScrollPane(tileBox,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, barH));
        scroll.getHorizontalScrollBar().setUnitIncrement(sc(20));
        scroll.setBounds(0, 0, boxW - 2, boxH - 2);
        box.add(scroll);


        // --- Cards image (bottom-left) ---
        ImageIcon cardsRaw = new ImageIcon("src/main/resources/icons/cards2.jpeg");
        int cardsW = (int) Math.round(width / 8.0 * 1.5);              // a bit under 1/8 of the width
        int cardsH = cardsRaw.getIconHeight() * cardsW / cardsRaw.getIconWidth();   // keeps aspect ratio
        Image cardsScaled = cardsRaw.getImage().getScaledInstance(cardsW, cardsH, Image.SCALE_SMOOTH);

        JLabel cardsLabel = new JLabel(new ImageIcon(cardsScaled));
        cardsLabel.setSize(cardsW, cardsH);
        cardsLabel.setLocation(margin, height - margin - cardsH);

        add(titleFlash);
        add(settingsButton);
        add(cardsLabel);

        Theme.addListener(this::applyTheme);
        applyTheme();
    }

    // Styles an OutlinedLabel and sizes it to fit its text
    private void styleLabel(OutlinedLabel label, Font font, double strokeW) {
        label.setFont(font);
        label.setForeground(FILL);
        label.setStrokeColor(OUTLINE);
        label.setStrokeWidth((float) (strokeW * s));
        label.setShadowOffset(0, 0);                                    // no shadow in this design
        label.setOpaque(false);

        FontMetrics fm = label.getFontMetrics(font);
        int pad = sc(8);
        label.setSize(fm.stringWidth(label.getText()) + 2 * pad, fm.getHeight() + 2 * pad);
    }

    private void showTileMenu(TileButton tile, JPanel box, boolean isDeck) {
        String noun = isDeck ? "deck" : "folder";
        JPopupMenu menu = new JPopupMenu();

        JMenuItem rename = new JMenuItem("Rename " + noun);
        rename.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "New name:", tile.getText());
            if (name != null && !name.trim().isEmpty()) {
                tile.setText(name.trim());
            }
        });
        menu.add(rename);

        if (isDeck) {                                            // access only applies to decks
            JMenuItem access = new JMenuItem("Change deck access");
            access.addActionListener(e -> {
                String[] options = {"Private", "Shared (link)", "Public"};
                Object choice = JOptionPane.showInputDialog(this, "Who can access this deck?",
                        "Change access", JOptionPane.QUESTION_MESSAGE, null, options, tile.getAccess());
                if (choice != null) {
                    tile.setAccess(choice.toString());
                }
            });
            menu.add(access);
        }

        menu.addSeparator();

        JMenuItem delete = new JMenuItem("Delete " + noun);
        delete.addActionListener(e -> {
            int answer = JOptionPane.showConfirmDialog(this,
                    "Delete \"" + tile.getText() + "\"? This can't be undone.",
                    "Delete " + noun, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                box.remove(tile);
                box.revalidate();
                box.repaint();
            }
        });
        menu.add(delete);

        // open just below the three dots
        menu.show(tile, (int) (tile.getWidth() * 0.1), (int) (tile.getHeight() * 0.3));
    }

    private void showAddMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem newDeck = new JMenuItem("Add new deck");
        newDeck.addActionListener(e -> System.out.println("Add new deck"));       // replace with real action later
        menu.add(newDeck);

        menu.show(invoker, 0, -menu.getPreferredSize().height);   // opens upward, since the plus sits at the bottom
    }

    public void addDeck(String name) {
        for (TileButton t : tiles) {
            if (t.getText().equals(name)) return;                 // already listed
        }
        TileButton tile = new TileButton(name, tileFont, (float) (0.8 * s));
        tile.setKind("Deck");
        tile.setAccess("Public");
        tile.setOnOpen(() -> System.out.println("Open public deck " + name));
        tiles.add(tile);
        tileBox.add(tile);
        layoutTiles();
    }

    public void removeDeck(String name) {
        for (TileButton t : new java.util.ArrayList<>(tiles)) {
            if (t.getText().equals(name)) {
                tiles.remove(t);
                tileBox.remove(t);
            }
        }
        layoutTiles();
    }

    private void layoutTiles() {
        emptyLabel.setVisible(tiles.isEmpty());
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).setBounds(tileStartX + i * (tileW + tileGap), tileTopY, tileW, tileH);
        }
        int contentW = 2 * tileStartX + tiles.size() * tileW + Math.max(0, tiles.size() - 1) * tileGap;
        tileBox.setPreferredSize(new Dimension(Math.max(viewW, contentW), viewH));
        tileBox.revalidate();
        tileBox.repaint();
    }

    private void applyTheme() {
        setBackground(Theme.background());
        titleFlash.setForeground(Theme.flashFill());
        titleFlash.setStrokeColor(Theme.flashStroke());
        title.setForeground(Theme.headingFill());
        title.setStrokeColor(Theme.headingStroke());
        emptyLabel.setForeground(Theme.textColor());
        box.setBackground(Theme.containerFill());
        box.setBorder(BorderFactory.createLineBorder(Theme.containerBorder(), 1));
        for (OutlinedLabel l : bodyLabels) {
            l.setForeground(Theme.bodyFill());
            l.setStrokeColor(Theme.bodyStroke());
        }
        repaint();
    }

}