package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.home.HomeViewModel;
import interface_adapter.settings.SettingsViewModel;
import view.components.LabelHoverZoom;
import view.components.OutlinedLabel;
import view.components.PromptBar;
import view.components.SpinningIconButton;
import view.style.FontLoader;
import view.style.IconLoader;
import view.style.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.util.function.Consumer;

/** The assistant screen: a heading and a prompt bar. */
public class ChatbotPanel extends JPanel {
    private static final double BASE_W = 651.0;
    private static final double BASE_H = 394.0;
    private final double s;

    private final OutlinedLabel titleFlash;
    private final OutlinedLabel heading;
    private final SpinningIconButton settingsButton;
    private final PromptBar promptBar;

    // TEMPORARY hook: replaced by AskAssistantController
    private Consumer<String> onPromptSubmitted;
    public void setOnPromptSubmitted(Consumer<String> c) { this.onPromptSubmitted = c; }

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

    public ChatbotPanel(int width, ViewManagerModel viewManagerModel) {
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
        heading = new OutlinedLabel("What can I help you with today?");
        styleAndFit(heading, 1.0, FontLoader.loadAdlam((float) (26f * s)), 1, 2);
        heading.setLocation((width - heading.getWidth()) / 2, (int) (height * 0.17));

        // --- Prompt bar ---
        int barW = (int) Math.round(width * 0.60);
        int barH = (int) Math.round(height * 0.22);
        promptBar = new PromptBar(barW, barH, FontLoader.loadAdlam((float) (14f * s)), "Ask Anything....");
        promptBar.setLocation((width - barW) / 2, heading.getY() + heading.getHeight() + sc(18));
        promptBar.setOnSubmit(text -> {
            if (onPromptSubmitted != null) onPromptSubmitted.accept(text);
        });
        promptBar.setOnAttach(() -> System.out.println("Attach clicked"));   // replace later

        // --- Cards image (bottom-right) ---
        ImageIcon cardsRaw = IconLoader.load("cards2.jpeg");
        int cardsW = (int) Math.round(width * 0.14);
        int cardsH = cardsRaw.getIconHeight() * cardsW / cardsRaw.getIconWidth();
        Image cardsScaled = cardsRaw.getImage().getScaledInstance(cardsW, cardsH, Image.SCALE_SMOOTH);
        JLabel cardsLabel = new JLabel(new ImageIcon(cardsScaled));
        cardsLabel.setSize(cardsW, cardsH);
        cardsLabel.setLocation(width - sc(30) - cardsW, height - sc(30) - cardsH);

        add(titleFlash);
        add(settingsButton);
        add(heading);
        add(promptBar);
        add(cardsLabel);

        // put the cursor in the prompt bar whenever this screen is shown
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                SwingUtilities.invokeLater(promptBar::focusInput);
            }
        });

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
