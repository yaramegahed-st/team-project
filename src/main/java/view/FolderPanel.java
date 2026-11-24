package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.home.HomeViewModel;
import interface_adapter.new_deck.NewDeckViewModel;
import interface_adapter.settings.SettingsViewModel;
import view.components.*;
import view.style.FontLoader;
import view.style.IconLoader;
import view.style.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/** One folder's contents. Each folder gets its own instance, registered under its own view name. */
public class FolderPanel extends JPanel {
    private static final double BASE_W = 651.0;
    private static final double BASE_H = 394.0;
    private final double s;

    private final OutlinedLabel titleFlash;
    private final SpinningIconButton settingsButton;
    private final JPanel box;

    private final java.util.List<TileButton> tiles = new ArrayList<>();
    private final JPanel tileBox;
    private final Font tileFont;
    private final int tileW, tileH, tileGap, tileStartX, tileTopY, viewW, viewH;

    private int sc(double v) { return (int) Math.round(v * s); }

    private void styleAndFit(OutlinedLabel label, double strokeW, Font f, int shadowX, int shadowY) {
        label.setFont(f);
        label.setStrokeWidth((float) (strokeW * s));
        label.setShadowColor(new Color(0, 20, 40, 150));
        label.setShadowOffset(sc(shadowX), sc(shadowY));
        FontMetrics fm = label.getFontMetrics(f);
        int pad = sc(8);
        label.setSize(fm.stringWidth(label.getText()) + 2 * pad, fm.getHeight() + 2 * pad);
    }

    /**
     * @param viewName the name this panel is registered under; New Deck uses it to return here
     */
    public FolderPanel(int width, ViewManagerModel viewManagerModel,
                       NewDeckViewModel newDeckViewModel, String viewName) {
        s = width / BASE_W;
        int height = sc(BASE_H);

        setLayout(null);
        setPreferredSize(new Dimension(width, height));

        int margin = sc(10);
        int settingsSlot = sc(40);
        int lift = sc(8);

        // --- Flash title (top-left, click to go home) ---
        titleFlash = new OutlinedLabel("FlashShare");
        Font f1 = FontLoader.loadDamion((float) (26f * s));
        styleAndFit(titleFlash, 0.9, f1, 2, 3);
        titleFlash.setLocation(0, 0);
        LabelHoverZoom.attach(titleFlash, f1, (float) (0.9 * s), sc(2), sc(3), sc(8), 1.08,
                () -> viewManagerModel.navigate(HomeViewModel.VIEW_NAME, false));

        // --- Settings button (top-right) ---
        int iconSize = settingsSlot - sc(6);
        ImageIcon rawIcon = IconLoader.load("settings2.jpeg");
        Image scaledIcon = rawIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        settingsButton = new SpinningIconButton(scaledIcon);
        settingsButton.setSize(settingsSlot, settingsSlot);
        settingsButton.setToolTipText("Settings");
        settingsButton.setLocation(width - margin - settingsSlot,
                margin - lift + (titleFlash.getHeight() - settingsSlot) / 2);
        settingsButton.addActionListener(e ->
                viewManagerModel.navigate(SettingsViewModel.VIEW_NAME, true));

        // --- Container (below the title row) ---
        int boxW = width - 2 * sc(15);
        int boxH = sc(120);
        int titleRowBottom = Math.max(titleFlash.getY() + titleFlash.getHeight(),
                settingsButton.getY() + settingsButton.getHeight());
        int boxY = titleRowBottom + sc(15);

        box = new JPanel(null);
        box.setBounds((width - boxW) / 2, boxY, boxW, boxH);
        add(box);

        // --- Scrollable deck tiles inside the container ---
        tileFont = new Font(Font.SERIF, Font.PLAIN, 1).deriveFont((float) (boxH * 0.20));
        int barH = sc(10);
        int plusReserve = (int) Math.round(boxH * 0.20);
        viewW = boxW - plusReserve - sc(16);
        viewH = boxH - 2 - barH;
        tileH = (int) Math.round(boxH * 0.68);
        tileW = (int) Math.round(tileH * 1.17);
        tileGap = (int) Math.round(tileW * 0.55);
        tileStartX = sc(20);
        tileTopY = (viewH - tileH) / 2;

        tileBox = new JPanel(null);
        tileBox.setOpaque(false);
        tileBox.setPreferredSize(new Dimension(viewW, viewH));

        JScrollPane tileScroll = new JScrollPane(tileBox,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tileScroll.setBorder(null);
        tileScroll.setOpaque(false);
        tileScroll.getViewport().setOpaque(false);
        tileScroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, barH));
        tileScroll.getHorizontalScrollBar().setUnitIncrement(sc(20));
        tileScroll.setBounds(0, 0, viewW, boxH - 2);
        box.add(tileScroll);

        // --- Plus button: starts the New Deck flow for this folder ---
        ImageIcon plusRaw = IconLoader.load("plus2.jpeg");
        int plusW = (int) Math.round(boxH * 0.20);
        int plusH = plusRaw.getIconHeight() * plusW / plusRaw.getIconWidth();
        ZoomIconButton plus = new ZoomIconButton(plusRaw.getImage(), plusW, plusH, 1.12);
        int ex = (plus.getWidth() - plusW) / 2;
        int ey = (plus.getHeight() - plusH) / 2;
        plus.setLocation(boxW - sc(8) - plusW - ex, boxH - sc(8) - plusH - ey);
        plus.addActionListener(e -> {
            newDeckViewModel.setOriginView(viewName);                  // so New Deck returns here
            viewManagerModel.navigate(NewDeckViewModel.VIEW_NAME, true);
        });
        box.add(plus);

        // --- Cards image (bottom-left) ---
        ImageIcon cardsRaw = IconLoader.load("cards2.jpeg");
        int cardsW = (int) Math.round(width / 8.0 * 1.5);
        int cardsH = cardsRaw.getIconHeight() * cardsW / cardsRaw.getIconWidth();
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

    // ----- tiles -----
    public void addDeck(DeckDialogs.NewDeck info) {
        TileButton tile = new TileButton(info.name, tileFont, (float) (0.8 * s));
        tile.setKind("Deck");
        tile.setAccess(info.isPublic ? "Public" : "Private");
        tile.setOnOpen(() -> System.out.println("Open " + tile.getText()));
        tile.setOnMenu(() -> showTileMenu(tile));
        tiles.add(tile);
        tileBox.add(tile);
        layoutTiles();
        SwingUtilities.invokeLater(() ->
                tile.scrollRectToVisible(new Rectangle(0, 0, tile.getWidth(), tile.getHeight())));
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

    private void showTileMenu(TileButton tile) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem rename = new JMenuItem("Rename deck");
        rename.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "New name:", tile.getText());
            if (name != null && !name.trim().isEmpty()) tile.setText(name.trim());
        });
        menu.add(rename);

        JMenuItem access = new JMenuItem("Change deck access");
        access.addActionListener(e -> {
            String[] options = {"Private", "Shared (link)", "Public"};
            Object choice = JOptionPane.showInputDialog(this, "Who can access this deck?",
                    "Change access", JOptionPane.QUESTION_MESSAGE, null, options, tile.getAccess());
            if (choice != null) tile.setAccess(choice.toString());
        });
        menu.add(access);

        menu.addSeparator();

        JMenuItem delete = new JMenuItem("Delete deck");
        delete.addActionListener(e -> {
            int answer = JOptionPane.showConfirmDialog(this,
                    "Delete \"" + tile.getText() + "\"? This can't be undone.",
                    "Delete deck", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                tiles.remove(tile);
                tileBox.remove(tile);
                layoutTiles();
            }
        });
        menu.add(delete);

        menu.show(tile, (int) (tile.getWidth() * 0.1), (int) (tile.getHeight() * 0.3));
    }

    private void applyTheme() {
        setBackground(Theme.background());
        titleFlash.setForeground(Theme.flashFill());
        titleFlash.setStrokeColor(Theme.flashStroke());
        box.setBackground(Theme.containerFill());
        box.setBorder(BorderFactory.createLineBorder(Theme.containerBorder(), 1));
        repaint();
    }
}