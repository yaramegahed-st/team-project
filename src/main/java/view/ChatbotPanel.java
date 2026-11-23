package view;

import javax.swing.*;
import java.awt.*;
import view.style.*;
import view.components.*;

public class ChatbotPanel extends JPanel {
    private static final double BASE_W = 651.0;
    private static final double BASE_H = 394.0;
    private final double s;
    private final OutlinedLabel heading;

    private final OutlinedLabel titleFlash;

    private Runnable onTitleClicked;

    public void setOnTitleClicked(Runnable r) { this.onTitleClicked = r; }

    private final SpinningIconButton settingsButton;
    private Runnable onSettingsClicked;   // set from MyFrame

    public void setOnSettingsClicked(Runnable r) {
        this.onSettingsClicked = r;
    }

    private PromptBar promptBar;
    private java.util.function.Consumer<String> onPromptSubmitted;

    public void setOnPromptSubmitted(java.util.function.Consumer<String> c) { this.onPromptSubmitted = c; }
    public void focusPrompt() { promptBar.focusInput(); }

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

    public ChatbotPanel(int width) {
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

        // --- Heading ---
        heading = new OutlinedLabel("What can I help you with today?");
        Font headingFont = FontLoader.loadAdlam((float) (26f * s));
        heading.setFont(headingFont);
        styleAndFit(heading, 1.0, headingFont, 1, 2);
        heading.setForeground(Color.decode("#8AD1D1"));            // teal fill
        heading.setStrokeColor(Color.decode("#1F3A4D"));           // dark outline
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
        ImageIcon cardsRaw = new ImageIcon("src/main/resources/icons/cards2.jpeg");
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
