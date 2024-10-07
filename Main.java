import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Main class that generates GUI windows and layouts
 *
 * @author Dewan Mukto
 * @version 2024-10-07 1.2
 */
public class Main {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    private Settings settings;

    /**
     * Main Constructor
     */
    public Main() {
        settings = new Settings();  // Initialize Settings with default mode as Fast Mode
        Renderer.setSettings(settings); // Ensure Renderer has access to settings

        JFrame frame = new JFrame("Mandelbrot Explorer");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Explorer explorer = new Explorer();
        explorer.setSettings(settings);  // Pass settings to explorer
        frame.add(explorer, BorderLayout.CENTER);

        // Add toolbar for controls
        JToolBar toolbar = createToolbar(explorer);
        frame.add(toolbar, BorderLayout.NORTH);

        // Status bar with zoom level and mode selection
        JPanel statusBar = createStatusBar(explorer);
        frame.add(statusBar, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    /**
     * Prepares the toolbar below the title bar
     *
     * @return toolbar component for the GUI window
     */
    private JToolBar createToolbar(Explorer explorer) {
        JToolBar toolbar = new JToolBar();
        toolbar.add(createButton("Zoom In (W)", e -> explorer.zoom(1.1)));
        toolbar.add(createButton("Zoom Out (S)", e -> explorer.zoom(1 / 1.1)));
        toolbar.add(createButton("Reset", e -> explorer.resetView()));
        toolbar.add(createButton("Save Image", e -> explorer.saveImage()));
        toolbar.add(createButton("Auto Zoom", e -> explorer.toggleAutoZoom()));
        toolbar.add(createButton("About", e -> showAboutDialog()));
        return toolbar;
    }

    /**
     * Creates the status bar with zoom level and radio buttons for mode selection
     */
    private JPanel createStatusBar(Explorer explorer) {
        JPanel statusBar = new JPanel(new BorderLayout());

        JLabel zoomLabel = new JLabel("Zoom: " + explorer.getZoom());
        zoomLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        explorer.setStatusBar(zoomLabel);

        // Radio buttons for "Fast Mode" and "Scaled Mode"
        JRadioButton fastModeButton = new JRadioButton("Fast Mode", true); // Default selected
        JRadioButton scaledModeButton = new JRadioButton("Scaled Mode");

        // Button group to allow only one mode to be selected at a time
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(fastModeButton);
        modeGroup.add(scaledModeButton);

        // Action listeners for radio buttons
        fastModeButton.addActionListener(e -> settings.setMode(Settings.Mode.FAST));
        scaledModeButton.addActionListener(e -> settings.setMode(Settings.Mode.SCALED));

        JPanel radioPanel = new JPanel();
        radioPanel.add(fastModeButton);
        radioPanel.add(scaledModeButton);

        statusBar.add(zoomLabel, BorderLayout.WEST);
        statusBar.add(radioPanel, BorderLayout.EAST);

        return statusBar;
    }

    /**
     * An example of a method - replace this comment with your own
     *
     * @param text   the label for the button
     * @param action the task carried out by the button
     * @return creates a button
     */
    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        button.setFocusable(false);
        return button;
    }

    /**
     * Displays the credits dialog box
     */
    private void showAboutDialog() {
        String aboutText = "<html><b>Mandelbrot Explorer</b><br>" +
                "&copy; Muxday 2024 <br><br><br>" +
                "Based on Benoit Mandelbrot's fractal discovery, programmed by Dewan Mukto</html>";
        JOptionPane.showMessageDialog(null, aboutText, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * The executable function
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
