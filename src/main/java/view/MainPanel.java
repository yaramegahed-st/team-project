package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import view.style.*;
import view.components.*;

public class MainPanel extends JPanel {
    private static final double BASE_W = 651.0;
    private static final double BASE_H = 394.0;
    private final double s;

    private final OutlinedLabel titleFlash;
    private final OutlinedLabel titleAbout;
    private final SpinningIconButton settingsButton;
    private JPanel box;

    private Runnable onTitleClicked;
    private Runnable onAboutClicked;
    private Runnable onSettingsClicked;
    private Runnable onBotClicked;
    private Runnable onNewFolderClicked;
    private Runnable onFreeAccessClicked;
    private Runnable onNewDeckClicked;
    private java.util.function.Consumer<String> onMenuClicked;
    private java.util.function.Consumer<TileButton> onFolderOpened;

    public void setOnTitleClicked(Runnable r) { this.onTitleClicked = r; }
    public void setOnAboutClicked(Runnable r) { this.onAboutClicked = r; }
    public void setOnSettingsClicked(Runnable r) { this.onSettingsClicked = r;}
    public void setOnBotClicked(Runnable r) { this.onBotClicked = r; }
    public void setOnNewFolderClicked(Runnable r) { this.onNewFolderClicked = r; }
    public void setOnFreeAccessClicked(Runnable r) { this.onFreeAccessClicked = r; }
    public void setOnMenuClicked(java.util.function.Consumer<String> c) { this.onMenuClicked = c; }
    public void setOnNewDeckClicked(Runnable r) { this.onNewDeckClicked = r; }
    public void setOnFolderOpened(java.util.function.Consumer<TileButton> c) { this.onFolderOpened = c; }

    private final java.util.List<TileButton> tiles = new ArrayList<>();
    private final ArrayList<PillButton> Optionbuttons = new ArrayList<PillButton>();
    private JPanel tileBox;              // inner panel that holds the tiles and scrolls
    private JScrollPane tileScroll;
    private Font tileFont;
    private int tileW, tileH, tileGap, tileStartX, tileTopY, viewW, viewH;


    private int sc(double v) {
        return (int) Math.round(v * s);
    }

    // Styles a label and sizes it to fit its text at the given design-time font size
    private void styleAndFit(OutlinedLabel label, double strokeW,  Font f, int shadowX, int shadowY) {
        label.setForeground(Color.decode("#386782"));
        label.setStrokeWidth((float) (strokeW * s));
        label.setShadowColor(new Color(0, 20, 40, 150));
        label.setShadowOffset(sc(shadowX), sc(shadowY));

        FontMetrics fm = label.getFontMetrics(f);
        int pad = sc(8);                                     // room for the outline and shadow
        label.setSize(fm.stringWidth(label.getText()) + 2 * pad,
                fm.getHeight() + 2 * pad);
    }

    public MainPanel(int width) {

        s = width / BASE_W;
        int height = sc(BASE_H);

        setLayout(null);
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.decode("#F1FBFE"));

        int margin = sc(10);
        int settingsSlot = sc(40);
        int gap = sc(10);
        int lift = sc(8);

        // --- Flash title (top-left) ---
        titleFlash = new OutlinedLabel("FlashShare");
        Font f1 = FontLoader.loadDamion((float) (26f * s));      // font size
        titleFlash.setFont(f1);
        styleAndFit(titleFlash, 0.9, f1, 2, 3);  // stroke width
        titleFlash.setStrokeColor(Color.decode("#C7C7C7"));
        titleFlash.setLocation(sc(0), sc(0));

        // --- About (top-right, left of the settings slot) ---
        titleAbout = new OutlinedLabel("About");
        Font f2 = FontLoader.loadAdlam((float) (16f * s));       // font size
        titleAbout.setFont(f2);
        styleAndFit(titleAbout, 0.6, f2, 1, 2);   // stroke width
        titleAbout.setStrokeColor(Color.decode("#00000"));
        int aboutX = width - margin - settingsSlot - gap - titleAbout.getWidth();
        int aboutY = margin - lift + (titleFlash.getHeight() - titleAbout.getHeight()) / 2;
        titleAbout.setLocation(aboutX, aboutY);


        titleFlash.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        titleFlash.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (onTitleClicked != null) onTitleClicked.run();
            }
        });

        titleAbout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        titleAbout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (onAboutClicked != null) onAboutClicked.run();
            }
        });

        LabelHoverZoom.attach(titleFlash, f1, (float) (1.0 * s), sc(2), sc(3), sc(8), 1.08,
                () -> { if (onTitleClicked != null) onTitleClicked.run(); });

        LabelHoverZoom.attach(titleAbout, f2, (float) (0.7 * s), sc(1), sc(2), sc(8), 1.12,
                () -> { if (onAboutClicked != null) onAboutClicked.run(); });

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

        int btnX = width - margin - settingsSlot;                                   // the reserved slot
        int btnY = titleAbout.getY() + (titleAbout.getHeight() - settingsSlot) / 2; // centered with About
        settingsButton.setLocation(btnX, btnY);

        settingsButton.addActionListener(e -> {
            if (onSettingsClicked != null) onSettingsClicked.run();
        });

        // --- Container (centered, below the title row) ---
        int boxW = width - 2 * sc(15);
        int boxH = sc(120);

        // bottom edge of the lowest item in the top row
        int titleRowBottom = Math.max(titleFlash.getY() + titleFlash.getHeight(),
                Math.max(titleAbout.getY() + titleAbout.getHeight(),
                        settingsButton.getY() + settingsButton.getHeight()));

        int boxY = titleRowBottom + sc(15);                      // 15 = gap under the titles; raise to move it further down

        box = new JPanel(null);
        box.setBackground(Color.decode("#C9DFEA"));
        box.setBorder(BorderFactory.createLineBorder(Color.decode("#43687F"), 1));
        box.setBounds((width - boxW) / 2, boxY, boxW, boxH);
        add(box);

        // --- Scrollable tiles inside the container ---
        tileFont = new Font(Font.SERIF, Font.PLAIN, 1).deriveFont((float) (boxH * 0.20));

        int barH = sc(10);                                          // thin scrollbar
        int plusReserve = (int) Math.round(boxH * 0.20);            // keep the plus button clear
        viewW = boxW - plusReserve - sc(16);                        // visible width for tiles
        viewH = boxH - 2 - barH;                                    // visible height above the scrollbar

        tileH = (int) Math.round(boxH * 0.68);
        tileW = (int) Math.round(tileH * 1.17);
        tileGap = (int) Math.round(tileW * 0.55);
        tileStartX = sc(20);
        tileTopY = (viewH - tileH) / 2;

        tileBox = new JPanel(null);
        tileBox.setOpaque(false);
        tileBox.setPreferredSize(new Dimension(viewW, viewH));

        tileScroll = new JScrollPane(tileBox,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tileScroll.setBorder(null);
        tileScroll.setOpaque(false);
        tileScroll.getViewport().setOpaque(false);
        tileScroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, barH));
        tileScroll.getHorizontalScrollBar().setUnitIncrement(sc(20));
        tileScroll.setBounds(0, 0, viewW, boxH - 2);
        box.add(tileScroll);

        // --- Plus inside the container ---
        ImageIcon plusRaw = new ImageIcon("src/main/resources/icons/plus2.jpeg");               // change to your file name/path
        int plusW = (int) Math.round(boxH * 0.20);
        int plusH = plusRaw.getIconHeight() * plusW / plusRaw.getIconWidth();   // keeps aspect ratio

        ZoomIconButton plus = new ZoomIconButton(plusRaw.getImage(), plusW, plusH, 1.12);
        plus.setBounds(boxW - plusW - sc(8), boxH - plusH - sc(8), plusW, plusH);
        plus.setMargin(new Insets(0, 0, 0, 0));
        plus.setBorderPainted(false);
        plus.setContentAreaFilled(false);
        plus.setFocusPainted(false);
        plus.setOpaque(false);
        plus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        int ex = (plus.getWidth() - plusW) / 2;      // the button is a bit bigger than the image,
        int ey = (plus.getHeight() - plusH) / 2;     // so shift it to keep the image in place
        plus.setLocation(boxW - sc(8) - plusW - ex, boxH - sc(8) - plusH - ey);
        plus.addActionListener(e -> showAddMenu(plus));
        box.add(plus);


        // --- Cards image (bottom-left) ---
        ImageIcon cardsRaw = new ImageIcon("src/main/resources/icons/cards2.jpeg");
        int cardsW = (int) Math.round(width / 8.0 * 1.5);              // a bit under 1/8 of the width
        int cardsH = cardsRaw.getIconHeight() * cardsW / cardsRaw.getIconWidth();   // keeps aspect ratio
        Image cardsScaled = cardsRaw.getImage().getScaledInstance(cardsW, cardsH, Image.SCALE_SMOOTH);

        JLabel cardsLabel = new JLabel(new ImageIcon(cardsScaled));
        cardsLabel.setSize(cardsW, cardsH);
        cardsLabel.setLocation(margin, height - margin - cardsH);       // bottom-left, same margin as the top elements

        // --- Chatbot image (bottom-right) ---
        ImageIcon botRaw = new ImageIcon("src/main/resources/icons/chatbot2.jpeg");
        int botW = (int) Math.round(width / 8.0 * 0.65);
        int botH = botRaw.getIconHeight() * botW / botRaw.getIconWidth();

        ZoomIconButton botButton = new ZoomIconButton(botRaw.getImage(), botW, botH, 1.12);

        int botMargin = sc(30);
        int extraX = (botButton.getWidth() - botW) / 2;
        int extraY = (botButton.getHeight() - botH) / 2;
        botButton.setLocation(width - botMargin - botW - extraX, height - botMargin - botH - extraY);

        botButton.addActionListener(e -> {
            if (onBotClicked != null) onBotClicked.run();
        });

        // --- Menu buttons (centered, below the title bar) ---
        String[] labels = {"Explore Decks", "Import Deck", "Export Deck", "Study Mode"};
        Font menuFont = FontLoader.loadAdlam((float) (17f * s));
        Color pillColor = Color.decode("#43687F");                       // approximate; adjust to taste

        int btnW = sc(240), btnH = sc(34), btnGap = sc(10);
        int groupH = labels.length * btnH + (labels.length - 1) * btnGap;
        int bottomGap = sc(35);        // space below the top bar
        int y0 = height - bottomGap - groupH;              // centered in the remaining space
        int btnX1 = (width - btnW) / 2;                                   // centered horizontally

        for (int i = 0; i < labels.length; i++) {              // <-- the new loop starts here
            final String name = labels[i];
            PillButton b = new PillButton(name, menuFont, pillColor, (float) (1.6 * s));
            b.setBounds(btnX1, y0 + i * (btnH + btnGap), btnW, btnH);
            b.addActionListener(e -> {
                if (name.equals("Explore Decks")) {
                    if (onFreeAccessClicked != null) onFreeAccessClicked.run();
                } else if (onMenuClicked != null) {
                    onMenuClicked.accept(name);
                }
            });
            add(b);
        }

        add(titleFlash);
        add(titleAbout);
        add(settingsButton);
        add(cardsLabel);
        add(botButton);

        Theme.addListener(this::applyTheme);
        applyTheme();
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
                tiles.remove(tile);
                tileBox.remove(tile);
                layoutTiles();
            }
        });
        menu.add(delete);

        // open just below the three dots
        menu.show(tile, (int) (tile.getWidth() * 0.1), (int) (tile.getHeight() * 0.3));
    }

    private void showAddMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem newDeck = new JMenuItem("Add new deck");
        newDeck.addActionListener(e -> {
            if (onNewDeckClicked != null) onNewDeckClicked.run();
        });
        menu.add(newDeck);

        JMenuItem newFolder = new JMenuItem("Add new folder");
        newFolder.addActionListener(e -> {
            if (onNewFolderClicked != null) onNewFolderClicked.run();
        } );
        menu.add(newFolder);

        menu.show(invoker, 0, -menu.getPreferredSize().height);   // opens upward, since the plus sits at the bottom
    }

    private TileButton addTile(String name, boolean isDeck) {
        TileButton tile = new TileButton(name, tileFont, (float) (0.8 * s));
        tile.setKind(isDeck ? "Deck" : "Folder");
        tile.setOnOpen(() -> {
            if (!isDeck && onFolderOpened != null) onFolderOpened.accept(tile);
            else System.out.println("Open " + tile.getText());
        });
        tile.setOnMenu(() -> showTileMenu(tile, tileBox, isDeck));
        tiles.add(tile);
        tileBox.add(tile);
        layoutTiles();

        // scroll so the new tile is visible
        SwingUtilities.invokeLater(() ->
                tile.scrollRectToVisible(new Rectangle(0, 0, tile.getWidth(), tile.getHeight())));
        return tile;
    }

    private void layoutTiles() {
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).setBounds(tileStartX + i * (tileW + tileGap), tileTopY, tileW, tileH);
        }
        int contentW = 2 * tileStartX + tiles.size() * tileW + Math.max(0, tiles.size() - 1) * tileGap;
        tileBox.setPreferredSize(new Dimension(Math.max(viewW, contentW), viewH));
        tileBox.revalidate();
        tileBox.repaint();
    }

    public java.util.List<String> getDeckNames() {
        java.util.List<String> names = new ArrayList<>();
        collectDecks(this, names);
        return names;
    }

    // searches nested containers too, so it works whether or not the tiles sit in a scroll pane
    private void collectDecks(Container c, java.util.List<String> out) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof TileButton) {
                TileButton t = (TileButton) comp;
                if ("Deck".equals(t.getKind())) out.add(t.getText());
            } else if (comp instanceof Container) {
                collectDecks((Container) comp, out);
            }
        }
    }

    /** Called by FolderPanel once the user has named the folder. */
    public TileButton addFolder(String name) {
        return addTile(name, false);
    }

    public void addDeck(DeckDialogs.NewDeck info) {
        addTile(info.name, true);
        tiles.get(tiles.size() - 1).setAccess(info.isPublic ? "Public" : "Private");
    }

    private void applyTheme() {
        setBackground(Theme.background());
        titleFlash.setForeground(Theme.flashFill());
        titleFlash.setStrokeColor(Theme.flashStroke());
        titleAbout.setForeground(Theme.flashFill());
        titleAbout.setStrokeColor(Theme.aboutStroke());
        box.setBackground(Theme.containerFill());
        box.setBorder(BorderFactory.createLineBorder(Theme.containerBorder(), 1));
        repaint();
    }
}
