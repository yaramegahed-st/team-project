package view;

import javax.swing.*;
import java.awt.*;
import view.style.*;
import view.components.*;
import interface_adapter.*;

public class AboutPanel extends JPanel {
    private static final double BASE_W = 651.0;
    private static final double BASE_H = 394.0;
    private final double s;
    private JPanel box;
    private OutlinedLabel title;
    private final java.util.List<OutlinedLabel> bodyLabels = new java.util.ArrayList<>();

    private static final Color FILL    = Color.decode("#8AD1D1");   // teal text, approximate from your mockup
    private static final Color OUTLINE = Color.decode("#1F3A4D");   // dark outline

    private final OutlinedLabel titleFlash;

    private Runnable onTitleClicked;

    public void setOnTitleClicked(Runnable r) { this.onTitleClicked = r; }

    private final SpinningIconButton settingsButton;
    private Runnable onSettingsClicked;   // set from MyFrame

    public void setOnSettingsClicked(Runnable r) {
        this.onSettingsClicked = r;
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

    public AboutPanel(int width, ViewManagerModel viewManagerModel) {
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
        title = new OutlinedLabel("About");
        styleLabel(title, FontLoader.loadAdlam((float) (44f * s)), 1.2);
        title.setLocation((width - title.getWidth()) / 2, sc(4));
        add(title);

        // --- Container (centered, below the title) ---
        int boxW = width - 2 * sc(35);
        int boxH = sc(165);
        int boxY = title.getY() + title.getHeight() + sc(8);

        box = new JPanel(null);
        box.setBackground(Color.decode("#C9DFEA"));
        box.setBorder(BorderFactory.createLineBorder(Color.decode("#43687F"), 1));
        box.setBounds((width - boxW) / 2, boxY, boxW, boxH);
        add(box);

        // --- Text inside the container ---
        Font bodyFont = FontLoader.loadAdlam((float) (15f * s));
        int maxTextW = boxW - 2 * sc(25);
        int y = sc(14);
        y = addWrapped(box, "This application was designed for a school project......", bodyFont, y, maxTextW);
        y += sc(30);                                                    // gap between paragraphs
        addWrapped(box, "Here we'll explain the mechanics of how everything will work on the app for new users",
                bodyFont, y, maxTextW);

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

        add(titleFlash);
        add(settingsButton);
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

    // Wraps text into centered lines inside parent; returns the y position below the last line
    private int addWrapped(JPanel parent, String text, Font font, int y, int maxW) {
        FontMetrics fm = getFontMetrics(font);
        StringBuilder line = new StringBuilder();

        for (String word : text.split(" ")) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(test) > maxW && line.length() > 0) {
                y = addLine(parent, line.toString(), font, y, fm);
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0) y = addLine(parent, line.toString(), font, y, fm);
        return y;
    }

    private int addLine(JPanel parent, String text, Font font, int y, FontMetrics fm) {
        OutlinedLabel l = new OutlinedLabel(text);
        styleLabel(l, font, 0.8);
        int pad = sc(8);
        l.setLocation((parent.getWidth() - l.getWidth()) / 2, y - pad);  // pad offset keeps the text itself at y
        parent.add(l);
        bodyLabels.add(l);
        return y + fm.getHeight() + sc(4);                               // line spacing
    }

    private void applyTheme() {
        setBackground(Theme.background());
        titleFlash.setForeground(Theme.flashFill());
        titleFlash.setStrokeColor(Theme.flashStroke());
        title.setForeground(Theme.headingFill());
        title.setStrokeColor(Theme.headingStroke());
        box.setBackground(Theme.containerFill());
        box.setBorder(BorderFactory.createLineBorder(Theme.containerBorder(), 1));
        for (OutlinedLabel l : bodyLabels) {
            l.setForeground(Theme.bodyFill());
            l.setStrokeColor(Theme.bodyStroke());
        }
        repaint();
    }

}
