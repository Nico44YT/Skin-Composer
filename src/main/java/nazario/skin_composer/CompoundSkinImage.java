package nazario.skin_composer;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CompoundSkinImage {

    public List<SkinPart> images;

    public CompoundSkinImage(List<File> images) {
        this.images = new ArrayList<>(images.stream().map(SkinPart::new).toList());
    }

    public BufferedImage asBufferedImage() {
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
        for (SkinPart skinPart : this.images) {
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

    public void remove(int index) {
        this.images.remove(index);
    }

    public int size() {
        return this.images.size();
    }
}
