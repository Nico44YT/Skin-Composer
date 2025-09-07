package nazario.skin_composer;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.File;

public class SkinPart {

    protected Image image;
    public int offsetX;
    public int offsetY;
    public double hueShift;
    public double saturationFactor;
    public double brightnessFactor;
    public double opacityFactor;

    public SkinPart(File file) {
        this(new Image(file.getAbsolutePath()), 0, 0, 1, 1, 1, 1);
    }

    public SkinPart(Image image, int offsetX, int offsetY, double colorHue, double saturationFactor, double brightnessFactor, double opacityFactor) {
        this.image = image;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.hueShift = colorHue;
        this.saturationFactor = saturationFactor;
        this.brightnessFactor = brightnessFactor;
        this.opacityFactor = opacityFactor;
    }

    public Image createImage() {
        int width = (int)image.getWidth();
        int height = (int)image.getHeight();

        WritableImage newImage = new WritableImage(width, height);

        PixelReader reader = image.getPixelReader();
        PixelWriter writer = newImage.getPixelWriter();
        for(int x = 0;x<width;x++) {
            for(int y = 0;y<height;y++) {
                Color color = reader.getColor(x, y);
                int newX = Math.max(0, Math.min(x + offsetX, width - 1));
                int newY = Math.max(0, Math.min(y + offsetY, height - 1));
                writer.setColor(newX, newY, color.deriveColor(hueShift, saturationFactor, brightnessFactor, opacityFactor));
            }
        }

        return newImage;
    }
}
