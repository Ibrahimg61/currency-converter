package de.waehrungsumrechner.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.waehrungsumrechner.format.Formats;
import de.waehrungsumrechner.model.RateHistory.Point;
import de.waehrungsumrechner.ui.component.Card;
import de.waehrungsumrechner.ui.component.Painting;

/** Liniendiagramm des gespeicherten Kursverlaufs für das gewählte Währungspaar. */
final class HistoryCard extends Card {

    private static final long serialVersionUID = 1L;

    private final JLabel caption = new JLabel("VERLAUF");
    private final Chart chart = new Chart();

    HistoryCard() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        caption.setFont(Theme.font(11, java.awt.Font.BOLD));
        caption.setForeground(Theme.MUTED);
        add(caption, BorderLayout.NORTH);
        add(chart, BorderLayout.CENTER);
    }

    void show(String from, String to, List<Point> points) {
        String days = points.size() + (points.size() == 1 ? " Tag" : " Tage");
        caption.setText("VERLAUF  " + from + " → " + to + "  ·  " + days);
        chart.points = points;
        chart.repaint();
    }

    private static final class Chart extends JPanel {
        private static final long serialVersionUID = 1L;
        private static final int PAD_X = 6;
        private static final int PAD_TOP = 14;
        private static final int PAD_BOTTOM = 22;

        // nur im Event-Dispatch-Thread benutzt
        private transient List<Point> points = List.of();

        Chart() {
            setOpaque(false);
            setPreferredSize(new Dimension(100, 130));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Painting.smooth(g);
            if (points.size() < 2) {
                paintPlaceholder(g2);
            } else {
                paintLine(g2);
            }
            g2.dispose();
        }

        private void paintPlaceholder(Graphics2D g2) {
            String message = "Noch zu wenig Daten – bei jedem Start wird ein Tageskurs gespeichert.";
            g2.setColor(Theme.MUTED);
            g2.setFont(Theme.font(12, java.awt.Font.PLAIN));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(message, (getWidth() - fm.stringWidth(message)) / 2, getHeight() / 2);
        }

        private void paintLine(Graphics2D g2) {
            int w = getWidth();
            int h = getHeight();
            double min = points.stream().mapToDouble(Point::value).min().orElse(0);
            double max = points.stream().mapToDouble(Point::value).max().orElse(1);
            if (max - min < 1e-12) {
                min -= 0.5;
                max += 0.5;
            }
            double stepX = (w - 2.0 * PAD_X) / (points.size() - 1);
            double baseline = h - PAD_BOTTOM;

            Path2D line = new Path2D.Double();
            Path2D area = new Path2D.Double();
            area.moveTo(PAD_X, baseline);
            for (int i = 0; i < points.size(); i++) {
                double x = PAD_X + i * stepX;
                double y = PAD_TOP + (baseline - PAD_TOP) * (1 - (points.get(i).value() - min) / (max - min));
                if (i == 0) {
                    line.moveTo(x, y);
                } else {
                    line.lineTo(x, y);
                }
                area.lineTo(x, y);
            }
            area.lineTo(PAD_X + (points.size() - 1) * stepX, baseline);
            area.closePath();

            Color fillTop = new Color(Theme.ACCENT.getRed(), Theme.ACCENT.getGreen(), Theme.ACCENT.getBlue(), 90);
            Color fillBottom = new Color(Theme.ACCENT.getRed(), Theme.ACCENT.getGreen(), Theme.ACCENT.getBlue(), 0);
            g2.setPaint(new GradientPaint(0, PAD_TOP, fillTop, 0, (float) baseline, fillBottom));
            g2.fill(area);
            g2.setColor(Theme.ACCENT);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(line);

            g2.setFont(Theme.font(11, java.awt.Font.PLAIN));
            g2.setColor(Theme.MUTED);
            g2.drawString(Formats.date(points.get(0).date()), PAD_X, h - 6);
            String last = Formats.date(points.get(points.size() - 1).date());
            g2.drawString(last, w - PAD_X - g2.getFontMetrics().stringWidth(last), h - 6);
        }
    }
}
