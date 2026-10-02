package rateshift.ui.component;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class Painting {

    private Painting() { }

    /** Copy of the graphics context with antialiasing; the caller must call {@code dispose()}. */
    public static Graphics2D smooth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }
}
