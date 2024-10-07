import java.awt.Color;

/**
 * The calculations behind each iteration of the fractal
 *
 * @author Dewan Mukto
 * @version 2024-10-07 1.4
 */
public class Renderer {

    private static Settings settings;  // Store settings to check the mode

    public static void setSettings(Settings settings) {
        Renderer.settings = settings;  // Set settings instance
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
}
