package nazario.skin_composer.skin;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import nazario.skin_composer.util.FileHandler;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Skin {
    public static final Supplier<Skin> DEFAULT = () -> new Skin(new SkinPart(FileHandler.getResourceAsFile("./textures/default_wide.png")));
    protected final List<SkinPart> SKIN_PARTS;

    public Skin(SkinPart... skinPart) {
        this.SKIN_PARTS = new ArrayList<>(List.of(skinPart));
    }

    public List<SkinPart> getSkinParts() {
        return this.SKIN_PARTS;
    }

    public BufferedImage toBufferedImage() {
        Image image = toImage();
        int width = (int)image.getWidth();
        int height = (int)image.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = image.getPixelReader().getColor(x, y);
                int a = (int) (color.getOpacity() * 255);
                int r = (int) (color.getRed() * 255);
                int g = (int) (color.getGreen() * 255);
                int b = (int) (color.getBlue() * 255);
                int argb = (a << 24) | (r << 16) | (g << 8) | b;
                bufferedImage.setRGB(x, y, argb);
            }
        }

        return bufferedImage;
    }

    public Image toImage() {
        int width = 64;
        int height = 64;
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter writer = writableImage.getPixelWriter();

        // Preload images once
        List<Image> loadedImages = new ArrayList<>();
        for (SkinPart skinPart : this.getSkinParts()) {
            loadedImages.add(skinPart.createImage());
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color finalColor = Color.TRANSPARENT;

                // Go through each image (bottom → top)
                for (Image img : loadedImages) {
                    Color color = img.getPixelReader().getColor(x, y);

                    // Alpha blend with what’s already there
                    double alpha = color.getOpacity();
                    if (alpha > 0) {
                        finalColor = blend(finalColor, color);
                    }
                }

                writer.setColor(x, y, finalColor);
            }
        }

        return writableImage;
    }

    private Color blend(Color background, Color foreground) {
        double alpha = foreground.getOpacity();
        double invAlpha = 1.0 - alpha;

        double r = foreground.getRed() * alpha + background.getRed() * invAlpha;
        double g = foreground.getGreen() * alpha + background.getGreen() * invAlpha;
        double b = foreground.getBlue() * alpha + background.getBlue() * invAlpha;
        double a = alpha + background.getOpacity() * invAlpha;

        return new Color(r, g, b, a);
    }
}
