package rateshift.ui.component;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import rateshift.ui.Theme;

/** Rounded surface with a gradient in the accent colors. */
public class GradientCard extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int RADIUS = 20;

    public GradientCard() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Painting.smooth(g);
        g2.setPaint(new GradientPaint(0, 0, Theme.ACCENT, getWidth(), getHeight(), Theme.ACCENT_ALT));
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), RADIUS, RADIUS));
        g2.dispose();
        super.paintComponent(g);
    }
}
