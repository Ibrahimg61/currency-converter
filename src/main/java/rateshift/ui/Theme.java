package rateshift.ui;

import java.awt.Color;
import java.awt.Font;

/** Colors and fonts of the dark theme. */
public final class Theme {

    public static final Color BACKGROUND = new Color(0x0F172A);
    public static final Color CARD = new Color(0x1E293B);
    public static final Color CARD_HOVER = new Color(0x2A3850);
    public static final Color BORDER = new Color(0x334155);
    public static final Color TEXT = new Color(0xF1F5F9);
    public static final Color MUTED = new Color(0x94A3B8);
    public static final Color ACCENT = new Color(0x6366F1);
    public static final Color ACCENT_ALT = new Color(0x8B5CF6);
    public static final Color SUCCESS = new Color(0x34D399);
    public static final Color WARNING = new Color(0xFBBF24);

    private Theme() { }

    public static Font font(int size, int style) {
        return new Font(Font.SANS_SERIF, style, size);
    }

    /** Color as an HTML hex value without {@code #}, for Swing HTML labels. */
    public static String hex(Color color) {
        return String.format("%06X", color.getRGB() & 0xFFFFFF);
    }
}
