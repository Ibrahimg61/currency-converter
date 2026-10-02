package rateshift.ui.component;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import rateshift.ui.Theme;

/** Rounded surface with a border. */
public class Card extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int RADIUS = 16;

    public Card() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Painting.smooth(g);
        Shape shape = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, RADIUS, RADIUS);
        g2.setColor(Theme.CARD);
        g2.fill(shape);
        g2.setColor(Theme.BORDER);
        g2.draw(shape);
        g2.dispose();
        super.paintComponent(g);
    }
}
