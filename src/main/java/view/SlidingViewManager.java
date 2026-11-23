package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.ViewManagerModelListener;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class SlidingViewManager implements ViewManagerModelListener {
    private static final int STEP = 15;               // pixels per tick; raise for a faster slide
    private static final int TICK_MS = 10;

    private final JPanel container;                   // null layout, w x h
    private final ViewManagerModel model;
    private final Map<String, JPanel> views = new HashMap<>();
    private final int w, h;

    private JPanel current;
    private boolean animating;
    private String pendingName;                       // latest request that arrived mid-animation
    private boolean pendingForward;

    public SlidingViewManager(JPanel container, ViewManagerModel model, int w, int h) {
        this.container = container;
        this.model = model;
        this.w = w;
        this.h = h;
        model.addListener(this);
    }

    public void register(String name, JPanel view) {
        views.put(name, view);
    }

    @Override
    public void viewChanged() {
        if (animating) {                              // remember it and play it when the slide ends
            pendingName = model.getCurrentViewName();
            pendingForward = model.isForward();
            return;
        }
        show(model.getCurrentViewName(), model.isForward());
    }

    private void show(String name, boolean forward) {
        JPanel next = views.get(name);
        if (next == null || next == current) return;

        if (current == null) {                        // first view: no animation
            next.setBounds(0, 0, w, h);
            container.add(next);
            container.revalidate();
            container.repaint();
            current = next;
            return;
        }
        slide(next, forward);
    }

    /** forward = true: the new view arrives from the right; false: from the left. */
    private void slide(JPanel next, boolean forward) {
        animating = true;

        final int dir = forward ? 1 : -1;
        final JPanel outgoing = current;

        next.setBounds(dir * w, 0, w, h);             // start just off-screen
        container.add(next);

        javax.swing.Timer timer = new javax.swing.Timer(TICK_MS, null);
        timer.addActionListener(e -> {
            int nx = next.getX() - dir * STEP;
            boolean done = forward ? nx <= 0 : nx >= 0;
            if (done) nx = 0;

            next.setLocation(nx, 0);
            outgoing.setLocation(nx - dir * w, 0);    // outgoing stays one screen-width behind

            if (done) {
                timer.stop();
                container.remove(outgoing);
                container.repaint();
                current = next;
                animating = false;

                if (pendingName != null) {            // play the request that arrived during the slide
                    String name = pendingName;
                    boolean fwd = pendingForward;
                    pendingName = null;
                    show(name, fwd);
                }
            }
        });
        timer.start();
    }
}
