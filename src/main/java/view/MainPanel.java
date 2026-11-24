package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.about.AboutViewModel;
import interface_adapter.chatbot.ChatbotViewModel;
import interface_adapter.free_access_decks.FreeAccessViewModel;
import interface_adapter.home.HomeViewModel;
import interface_adapter.new_deck.NewDeckViewModel;
import interface_adapter.settings.SettingsViewModel;
import view.components.*;
import view.style.FontLoader;
import view.style.IconLoader;
import view.style.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/** The home screen. Navigation goes through the ViewManagerModel. */
public class MainPanel extends JPanel {
    private static final double BASE_W = 651.0;
    private static final double BASE_H = 394.0;
    private final double s;

    private final ViewManagerModel viewManagerModel;
    private final NewDeckViewModel newDeckViewModel;

    private final OutlinedLabel titleFlash;
    private final OutlinedLabel titleAbout;
    private final SpinningIconButton settingsButton;
    private final JPanel box;

    // TEMPORARY hooks: replaced by controllers as each use case is implemented
    private Runnable onNewFolderClicked;
    private Consumer<String> onMenuClicked;
    private Consumer<TileButton> onFolderOpened;

    public void setOnNewFolderClicked(Runnable r) { this.onNewFolderClicked = r; }
    public void setOnMenuClicked(Consumer<String> c) { this.onMenuClicked = c; }
    public void setOnFolderOpened(Consumer<TileButton> c) { this.onFolderOpened = c; }

    private final java.util.List<TileButton> tiles = new java.util.ArrayList<>();
    private final JPanel tileBox;                // inner panel that holds the tiles and scrolls
    private final Font tileFont;
    private final int tileW, tileH, tileGap, tileStartX, tileTopY, viewW, viewH;

    private int sc(double v) {
        return (int) Math.round(v * s);
    }

    // Sizes a label to fit its text; colours come from Theme (see applyTheme)
    private void styleAndFit(OutlinedLabel label, double strokeW, Font f, int shadowX, int shadowY) {
        label.setFont(f);
        label.setStrokeWidth((float) (strokeW * s));
        label.setShadowColor(new Color(0, 20, 40, 150));
        label.setShadowOffset(sc(shadowX), sc(shadowY));

        FontMetrics fm = label.getFontMetrics(f);
        int pad = sc(8);                                     // room for the outline and shadow
        label.setSize(fm.stringWidth(label.getText()) + 2 * pad,
                fm.getHeight() + 2 * pad);
    }

    public MainPanel(int width, ViewManagerModel viewManagerModel, NewDeckViewModel newDeckViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.newDeckViewModel = newDeckViewModel;

        s = width / BASE_W;
        int height = sc(BASE_H);

        setLayout(null);
        setPreferredSize(new Dimension(width, height));

        int margin = sc(10);
        int settingsSlot = sc(40);
        int gap = sc(10);
        int lift = sc(8);

        // --- Flash title (top-left) ---
        titleFlash = new OutlinedLabel("FlashShare");
        Font f1 = FontLoader.loadDamion((float) (26f * s));
        styleAndFit(titleFlash, 0.9, f1, 2, 3);
        titleFlash.setLocation(0, 0);

        // --- About (top-right, left of the settings slot) ---
        titleAbout = new OutlinedLabel("About");
        Font f2 = FontLoader.loadAdlam((float) (16f * s));
        styleAndFit(titleAbout, 0.6, f2, 1, 2);
        int aboutX = width - margin - settingsSlot - gap - titleAbout.getWidth();
        int aboutY = margin - lift + (titleFlash.getHeight() - titleAbout.getHeight()) / 2;
        titleAbout.setLocation(aboutX, aboutY);

        LabelHoverZoom.attach(titleFlash, f1, (float) (0.9 * s), sc(2), sc(3), sc(8), 1.08,
                () -> viewManagerModel.navigate(HomeViewModel.VIEW_NAME, false));
        LabelHoverZoom.attach(titleAbout, f2, (float) (0.6 * s), sc(1), sc(2), sc(8), 1.12,
                () -> viewManagerModel.navigate(AboutViewModel.VIEW_NAME, true));

        // --- Settings button (top-right corner) ---
        int iconSize = settingsSlot - sc(6);                       // leave a little breathing room
        ImageIcon rawIcon = IconLoader.load("settings2.jpeg");
        Image scaledIcon = rawIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);

        settingsButton = new SpinningIconButton(scaledIcon);
        settingsButton.setSize(settingsSlot, settingsSlot);
        settingsButton.setToolTipText("Settings");
        int btnX = width - margin - settingsSlot;                                   // the reserved slot
        int btnY = titleAbout.getY() + (titleAbout.getHeight() - settingsSlot) / 2; // centered with About
        settingsButton.setLocation(btnX, btnY);
        settingsButton.addActionListener(e ->
                viewManagerModel.navigate(SettingsViewModel.VIEW_NAME, true));

        // --- Container (centered, below the title row) ---
        int boxW = width - 2 * sc(15);
        int boxH = sc(120);

        int titleRowBottom = Math.max(titleFlash.getY() + titleFlash.getHeight(),
                Math.max(titleAbout.getY() + titleAbout.getHeight(),
                        settingsButton.getY() + settingsButton.getHeight()));
        int boxY = titleRowBottom + sc(15);                      // gap under the titles

        box = new JPanel(null);
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

        JScrollPane tileScroll = new JScrollPane(tileBox,
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
        ImageIcon plusRaw = IconLoader.load("plus2.jpeg");
        int plusW = (int) Math.round(boxH * 0.20);
        int plusH = plusRaw.getIconHeight() * plusW / plusRaw.getIconWidth();   // keeps aspect ratio

        ZoomIconButton plus = new ZoomIconButton(plusRaw.getImage(), plusW, plusH, 1.12);
        int ex = (plus.getWidth() - plusW) / 2;      // the button is a bit bigger than the image,
        int ey = (plus.getHeight() - plusH) / 2;     // so shift it to keep the image in place
        plus.setLocation(boxW - sc(8) - plusW - ex, boxH - sc(8) - plusH - ey);
        plus.addActionListener(e -> showAddMenu(plus));
        box.add(plus);

        // --- Cards image (bottom-left) ---
        ImageIcon cardsRaw = IconLoader.load("cards2.jpeg");
        int cardsW = (int) Math.round(width / 8.0 * 1.5);
        int cardsH = cardsRaw.getIconHeight() * cardsW / cardsRaw.getIconWidth();   // keeps aspect ratio
        Image cardsScaled = cardsRaw.getImage().getScaledInstance(cardsW, cardsH, Image.SCALE_SMOOTH);

        JLabel cardsLabel = new JLabel(new ImageIcon(cardsScaled));
        cardsLabel.setSize(cardsW, cardsH);
        cardsLabel.setLocation(margin, height - margin - cardsH);

        // --- Chatbot image (bottom-right) ---
        ImageIcon botRaw = IconLoader.load("chatbot2.jpeg");
        int botW = (int) Math.round(width / 8.0 * 0.65);
        int botH = botRaw.getIconHeight() * botW / botRaw.getIconWidth();

        ZoomIconButton botButton = new ZoomIconButton(botRaw.getImage(), botW, botH, 1.12);
        int botMargin = sc(30);
        int extraX = (botButton.getWidth() - botW) / 2;
        int extraY = (botButton.getHeight() - botH) / 2;
        botButton.setLocation(width - botMargin - botW - extraX, height - botMargin - botH - extraY);
        botButton.addActionListener(e ->
                viewManagerModel.navigate(ChatbotViewModel.VIEW_NAME, true));

        // --- Menu buttons (centered, near the bottom) ---
        String[] labels = {"Explore Decks", "Import Deck", "Export Deck", "Study Mode"};
        Font menuFont = FontLoader.loadAdlam((float) (17f * s));
        Color pillColor = Color.decode("#43687F");                   // unused by PillButton, kept for its signature

        int btnW = sc(240), btnH = sc(34), btnGap = sc(10);
        int groupH = labels.length * btnH + (labels.length - 1) * btnGap;
        int bottomGap = sc(35);
        int y0 = height - bottomGap - groupH;
        int btnX1 = (width - btnW) / 2;

        for (int i = 0; i < labels.length; i++) {
            final String name = labels[i];
            PillButton b = new PillButton(name, menuFont, pillColor, (float) (1.6 * s));
            b.setBounds(btnX1, y0 + i * (btnH + btnGap), btnW, btnH);
            b.addActionListener(e -> {
                if (name.equals("Explore Decks")) {
                    viewManagerModel.navigate(FreeAccessViewModel.VIEW_NAME, true);
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

    private void showTileMenu(TileButton tile, boolean isDeck) {
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

        menu.show(tile, (int) (tile.getWidth() * 0.1), (int) (tile.getHeight() * 0.3));
    }

    private void showAddMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem newDeck = new JMenuItem("Add new deck");
        newDeck.addActionListener(e -> {
            newDeckViewModel.getState().setOriginView(HomeViewModel.VIEW_NAME);     // so New Deck returns here
            viewManagerModel.navigate(NewDeckViewModel.VIEW_NAME, true);
        });
        menu.add(newDeck);

        JMenuItem newFolder = new JMenuItem("Add new folder");
        newFolder.addActionListener(e -> {
            if (onNewFolderClicked != null) onNewFolderClicked.run();
        });
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
        tile.setOnMenu(() -> showTileMenu(tile, isDeck));
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
        java.util.List<String> names = new java.util.ArrayList<>();
        for (TileButton t : tiles) {
            if ("Deck".equals(t.getKind())) names.add(t.getText());
        }
        return names;
    }

    public TileButton addFolder(String name) {
        return addTile(name, false);
    }

    public void addDeck(DeckDialogs.NewDeck info) {
        addTile(info.name, true).setAccess(info.isPublic ? "Public" : "Private");
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
