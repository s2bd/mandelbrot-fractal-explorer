/**
 * The calculations behind each iteration of the fractal
 *
 * @author Dewan Mukto
 * @version 2024-10-07 1.0
 */
public class Renderer {
    public static int mandelbrot(double c_real, double c_imag, double zoom) {
        double z_real = 0, z_imag = 0;
        int iterations = 0;
        int maxIterations = (int) (500 + Math.log(zoom) * 10);

        while (z_real * z_real + z_imag * z_imag < 4 && iterations < maxIterations) {
            double temp_real = z_real * z_real - z_imag * z_imag + c_real;
            z_imag = 2 * z_real * z_imag + c_imag;
            z_real = temp_real;
            iterations++;
        }

        return iterations == maxIterations ? 0 : (int) (255 * (iterations / (double) maxIterations));
    }
}
