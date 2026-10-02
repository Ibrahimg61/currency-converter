package rateshift;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import rateshift.data.HttpRateSource;
import rateshift.data.RateStore;
import rateshift.service.ExchangeRateService;
import rateshift.ui.MainWindow;

public final class Main {

    /** Can be overridden with {@code -Drateshift.data=/path}. */
    private static final String DATA_DIR_PROPERTY = "rateshift.data";

    private Main() { }

    public static void main(String[] args) {
        ExchangeRateService service = new ExchangeRateService(new HttpRateSource(), new RateStore(dataDirectory()));
        SwingUtilities.invokeLater(() -> {
            try {
                // Cross-platform look and feel so the custom design looks the same everywhere
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                // the default look and feel is good enough
            }
            new MainWindow(service).setVisible(true);
        });
    }

    private static Path dataDirectory() {
        String configured = System.getProperty(DATA_DIR_PROPERTY);
        if (configured != null && !configured.isBlank()) {
            return Paths.get(configured);
        }
        return Paths.get(System.getProperty("user.home"), ".rateshift");
    }
}
