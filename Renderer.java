import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.concurrent.*;

/**
 * The calculations behind each iteration of the fractal
 *
 * @author Dewan Mukto
 * @version 2024-10-08 1.4
 */
public class Renderer {

    private static Settings settings;  // Store settings to check the mode
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // Thread pool for rendering

    public static void setSettings(Settings settings) {
        Renderer.settings = settings;  // Set settings instance
    }

    public static BufferedImage renderMandelbrot(double zoom, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        double minRe = -2.0, maxRe = 1.0;
        double minIm = -1.5, maxIm = 1.5;

        // Create a list of futures to hold the results of each row computation
        Future<?>[] futures = new Future[height];
        for (int y = 0; y < height; y++) {
            final int row = y; // Final variable for lambda expression
            futures[y] = executor.submit(() -> {
                for (int x = 0; x < width; x++) {
                    double cRe = minRe + (maxRe - minRe) * x / width;
                    double cIm = minIm + (maxIm - minIm) * row / height;

                    Color color = mandelbrot(cRe, cIm, zoom);
                    image.setRGB(x, row, color.getRGB());
                }
            });
        }

        // Wait for all tasks to complete
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return image;
    }

    public static Color mandelbrot(double c_real, double c_imag, double zoom) {
        double z_real = 0, z_imag = 0;
        int iterations = 0;
        int maxIterations;

        // Check the rendering mode from the settings
        if (settings.getMode() == Settings.Mode.SCALED) {
            // Scaled Mode: Dynamically calculate maxIterations
            int X = 100;
            if (zoom > 1) {
                int zoomPower = (int) Math.log10(zoom);  // Get the power of 10 for zoom
                X += zoomPower * 100;  // Increase X based on zoom power
            }
            maxIterations = (int) (X + Math.log1p(zoom));
        } else {
            // Fast Mode: Fixed maxIterations
            maxIterations = (int) (100 + Math.log1p(zoom));
        }

        while (z_real * z_real + z_imag * z_imag < 4 && iterations < maxIterations) {
            double temp_real = z_real * z_real - z_imag * z_imag + c_real;
            z_imag = 2 * z_real * z_imag + c_imag;
            z_real = temp_real;
            iterations++;
        }

        // Return a color depending on the number of iterations
        if (iterations == maxIterations) {
            return Color.BLACK;  // Points inside the Mandelbrot set
        } else {
            float hue = 0.7f + (float) iterations / maxIterations;  // Set hue between 0.7 and 1.0
            return Color.getHSBColor(hue, 0.8f, 1.0f);  // Full saturation and brightness
        }
    }

    public static void shutdownExecutor() {
        executor.shutdown();  // Cleanly shut down the thread pool when rendering is complete
    }
}
