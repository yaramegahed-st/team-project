package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.new_deck.NewDeckViewModel;
import interface_adapter.settings.SettingsViewModel;
import view.components.*;
import view.style.FontLoader;
import view.style.IconLoader;
import view.style.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/** "Do you prefer to..?": create a new deck or access an existing one. */
public class NewDeckPanel extends JPanel {
    private static final double BASE_W = 651.0;
    private static final double BASE_H = 394.0;
    private final double s;

    private final OutlinedLabel titleFlash;
    private final OutlinedLabel heading;
    private final SpinningIconButton settingsButton;

    // TEMPORARY hooks: replaced by AddDeckController / AccessDeckController
    private Consumer<DeckDialogs.NewDeck> onDeckCreated;
    private Consumer<String> onAccessDeck;

    public void setOnDeckCreated(Consumer<DeckDialogs.NewDeck> c) { this.onDeckCreated = c; }
    public void setOnAccessDeck(Consumer<String> c)               { this.onAccessDeck = c; }

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

    public NewDeckPanel(int width, ViewManagerModel viewManagerModel, NewDeckViewModel newDeckViewModel) {
        s = width / BASE_W;
        int height = sc(BASE_H);

        setLayout(null);
        setPreferredSize(new Dimension(width, height));

        int margin = sc(10);
        int settingsSlot = sc(40);
        int lift = sc(8);

        // --- Flash title (top-left): back to whichever view opened this one ---
        titleFlash = new OutlinedLabel("FlashShare");
        Font f1 = FontLoader.loadDamion((float) (26f * s));
        styleAndFit(titleFlash, 0.9, f1, 2, 3);
        titleFlash.setLocation(0, 0);
        LabelHoverZoom.attach(titleFlash, f1, (float) (0.9 * s), sc(2), sc(3), sc(8), 1.08,
                () -> viewManagerModel.navigate(newDeckViewModel.getState().getOriginView(), false));

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

        // --- Heading ---
        heading = new OutlinedLabel("Do you prefer to..?");
        styleAndFit(heading, 1.2, FontLoader.loadAdlam((float) (34f * s)), 1, 2);
        heading.setLocation((width - heading.getWidth()) / 2,
                (int) Math.round(height * 0.22) - heading.getHeight() / 2);

        // --- Two big choice tiles ---
        int tileW = (int) Math.round(width * 0.32);
        int tileH = (int) Math.round(height * 0.39);
        int gap   = (int) Math.round(width * 0.076);
        int tileX = (width - (2 * tileW + gap)) / 2;
        int tileY = (int) Math.round(height * 0.33);
        Font tileFont = FontLoader.loadAdlam((float) (26f * s)).deriveFont(Font.BOLD);

        ChoiceTile create = new ChoiceTile(new String[]{"CREATE", "NEW DECK"}, tileFont, (float) (1.6 * s));
        create.setBounds(tileX, tileY, tileW, tileH);
        create.setOnClick(() -> {
            DeckDialogs.NewDeck info = DeckDialogs.showCreate(SwingUtilities.getWindowAncestor(this));
            if (info != null && onDeckCreated != null) onDeckCreated.accept(info);
        });

        ChoiceTile access = new ChoiceTile(new String[]{"ACCESS", "NEW DECK"}, tileFont, (float) (1.6 * s));
        access.setBounds(tileX + tileW + gap, tileY, tileW, tileH);
        access.setOnClick(() -> {
            String code = DeckDialogs.showAccess(SwingUtilities.getWindowAncestor(this));
            if (code != null && onAccessDeck != null) onAccessDeck.accept(code);
        });

        // --- Cards image (bottom-left) ---
        ImageIcon cardsRaw = IconLoader.load("cards2.jpeg");
        int cardsW = (int) Math.round(width * 0.14);
        int cardsH = cardsRaw.getIconHeight() * cardsW / cardsRaw.getIconWidth();
        Image cardsScaled = cardsRaw.getImage().getScaledInstance(cardsW, cardsH, Image.SCALE_SMOOTH);
        JLabel cardsLabel = new JLabel(new ImageIcon(cardsScaled));
        cardsLabel.setSize(cardsW, cardsH);
        cardsLabel.setLocation(sc(20), height - sc(12) - cardsH);

        add(titleFlash);
        add(settingsButton);
        add(heading);
        add(create);
        add(access);
        add(cardsLabel);

        Theme.addListener(this::applyTheme);
        applyTheme();
    }

    private void applyTheme() {
        setBackground(Theme.background());
        titleFlash.setForeground(Theme.flashFill());
        titleFlash.setStrokeColor(Theme.flashStroke());
        heading.setForeground(Theme.headingFill());
        heading.setStrokeColor(Theme.headingStroke());
        repaint();
    }
}