import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Handles the "viewing" panel for rendering the Mandelbrot fractal
 *
 * @author Dewan Mukto
 * @version 2024-10-07 1.0
 */
public class Explorer extends JPanel implements KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    private double xOffset = -0.5;  
    private double yOffset = 0;     
    private double zoom = 300;      
    private double panSpeed = 20 / zoom;

    private BufferedImage mandelbrotImage;
    private ExecutorService executor;
    private JLabel statusBar;
    private boolean autoZooming = false; 
    private Timer autoZoomTimer;

    /**
     * Explorer Constructor
     *
     */
    public Explorer() {
        mandelbrotImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        
        // Initial rendering
        renderMandelbrot();
    }

    public void setStatusBar(JLabel statusBar) {
        this.statusBar = statusBar;
    }

    public double getZoom() {
        return zoom;
    }

    /**
     * Renders the fractal
     *
     */
    public void renderMandelbrot() {
        long startTime = System.nanoTime();

        for (int y = 0; y < HEIGHT; y++) {
            final int yFinal = y;
            executor.submit(() -> {
                for (int x = 0; x < WIDTH; x++) {
                    double c_real = (x - WIDTH / 2) / zoom + xOffset;
                    double c_imag = (yFinal - HEIGHT / 2) / zoom + yOffset;
                    int color = Renderer.mandelbrot(c_real, c_imag, zoom); // Use Renderer class
                    mandelbrotImage.setRGB(x, yFinal, new Color(color, color, color).getRGB());
                }
            });
        }

        // Lambda for multi-threaded processing
        executor.submit(() -> {
            try {
                Thread.sleep(50);
                repaint();
                long endTime = System.nanoTime();
                System.out.println("Render time: " + (endTime - startTime) / 1_000_000 + " ms");
                updateStatusBar();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void updateStatusBar() {
        SwingUtilities.invokeLater(() -> statusBar.setText("Zoom: " + zoom));
    }

    public void zoom(double factor) {
        zoom *= factor;
        panSpeed = 20 / zoom;
        renderMandelbrot();
    }

    public void resetView() {
        xOffset = -0.5;
        yOffset = 0;
        zoom = 300;
        panSpeed = 20 / zoom;
        renderMandelbrot();
    }

    /**
     * Renders out an actual image file for locally storing screenshots
     *
     */
    public void saveImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            
            // Ensure the file has the right extension
            if (!filePath.endsWith(".jpg") && !filePath.endsWith(".jpeg") && !filePath.endsWith(".png")) {
                filePath += ".png"; // Default to PNG format
            }

            try {
                ImageIO.write(mandelbrotImage, filePath.endsWith(".png") ? "png" : "jpeg", new File(filePath));
                System.out.println("Image saved to: " + filePath);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void toggleAutoZoom() {
        autoZooming = !autoZooming; 
        if (autoZooming) {
            startAutoZoom();
        } else {
            stopAutoZoom();
        }
    }

    private void startAutoZoom() {
        autoZoomTimer = new Timer(100, e -> zoom(1.01)); 
        autoZoomTimer.start();
    }

    private void stopAutoZoom() {
        if (autoZoomTimer != null) {
            autoZoomTimer.stop();
        }
    }

    // Keyboard input listeners
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) zoom(1.1);
        if (key == KeyEvent.VK_S) zoom(1 / 1.1);
        if (key == KeyEvent.VK_UP) yOffset -= panSpeed;
        if (key == KeyEvent.VK_DOWN) yOffset += panSpeed;
        if (key == KeyEvent.VK_LEFT) xOffset -= panSpeed;
        if (key == KeyEvent.VK_RIGHT) xOffset += panSpeed;
        renderMandelbrot();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(mandelbrotImage, 0, 0, null);
    }

    // Keyboard continuous press listeners
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}
