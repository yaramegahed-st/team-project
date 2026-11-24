package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.home.HomeViewModel;
import interface_adapter.settings.SettingsViewModel;
import view.components.LabelHoverZoom;
import view.components.OutlinedLabel;
import view.components.SpinningIconButton;
import view.components.TileButton;
import view.style.FontLoader;
import view.style.IconLoader;
import view.style.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/** Lists the public decks. */
public class FreeAccessDecksPanel extends JPanel {
    private static final double BASE_W = 651.0;
    private static final double BASE_H = 394.0;
    private final double s;

    private final OutlinedLabel titleFlash;
    private final OutlinedLabel title;
    private final SpinningIconButton settingsButton;
    private final JPanel box;
    private final JPanel tileBox;
    private final JLabel emptyLabel = new JLabel("No public decks yet", SwingConstants.CENTER);

    private final java.util.List<TileButton> tiles = new ArrayList<>();
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

    public FreeAccessDecksPanel(int width, ViewManagerModel viewManagerModel) {
        s = width / BASE_W;
        int height = sc(BASE_H);

        setLayout(null);
        setPreferredSize(new Dimension(width, height));

        int margin = sc(10);
        int settingsSlot = sc(40);
        int lift = sc(8);

        // --- Flash title (top-left) ---
        titleFlash = new OutlinedLabel("FlashShare");
        Font f1 = FontLoader.loadDamion((float) (26f * s));
        styleAndFit(titleFlash, 0.9, f1, 2, 3);
        titleFlash.setLocation(0, 0);
        LabelHoverZoom.attach(titleFlash, f1, (float) (0.9 * s), sc(2), sc(3), sc(8), 1.08,
                () -> viewManagerModel.navigate(HomeViewModel.VIEW_NAME, false));

        // --- Heading (centered at the top) ---
        title = new OutlinedLabel("Free Access Decks");
        styleAndFit(title, 1.2, FontLoader.loadAdlam((float) (44f * s)), 0, 0);
        title.setLocation((width - title.getWidth()) / 2, sc(4));
        add(title);

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

        // --- Container (below the heading) ---
        int boxW = width - 2 * sc(15);
        int boxH = sc(120);
        int titleRowBottom = Math.max(title.getY() + title.getHeight(),
                Math.max(titleFlash.getY() + titleFlash.getHeight(),
                        settingsButton.getY() + settingsButton.getHeight()));
        int boxY = titleRowBottom + sc(15);

        box = new JPanel(null);
        box.setBounds((width - boxW) / 2, boxY, boxW, boxH);
        add(box);

        // --- Scrollable tiles inside the container ---
        tileFont = new Font(Font.SERIF, Font.PLAIN, 1).deriveFont((float) (boxH * 0.20));
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

    // ----- public API -----
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
        for (TileButton t : new ArrayList<>(tiles)) {
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
        repaint();
    }
}