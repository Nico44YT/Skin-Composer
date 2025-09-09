package nazario.skin_composer.skin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import nazario.skin_composer.util.FileHandler;

import java.io.File;
import java.util.function.Consumer;

public class SkinPart {

    protected Image image;

    // * Properties * //
    protected int offsetX;
    protected int offsetY;

    protected boolean flipHorizontal;
    protected boolean flipVertical;

    /**
     * From 0 to 360
     */
    protected double hueSift;
    protected double saturationFactor;
    protected double brightnessFactor;
    protected double opacityFactor;

    public SkinPart(File file) {
        this(new Image(file.getAbsolutePath()), 0, 0, false, false, 1, 1, 1, 1);
    }

    public SkinPart(Image image, int offsetX, int offsetY, boolean flipHorizontal, boolean flipVertical, double hueShift, double saturationFactor, double brightnessFactor, double opacityFactory) {
        this.image = image;

        this.offsetX = offsetX;
        this.offsetY = offsetY;

        this.flipHorizontal = flipHorizontal;
        this.flipVertical = flipVertical;

        this.hueSift = hueShift;
        this.saturationFactor = saturationFactor;
        this.brightnessFactor = brightnessFactor;
        this.opacityFactor = opacityFactory;
    }

    public SkinPart addOffset(int offsetX, int offsetY) {
        this.offsetX += offsetX;
        this.offsetY += offsetY;

        return this;
    }

    public SkinPart setOffset(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        return this;
    }

    public SkinPart flip(boolean horizontally, boolean vertically) {
        this.flipHorizontal = horizontally;
        this.flipVertical = vertically;

        return this;
    }

    public SkinPart setProperties(double hueShift, double saturationFactor, double brightnessFactor, double opacityFactor) {
        this.hueSift = hueShift*360d;
        this.saturationFactor = saturationFactor;
        this.brightnessFactor = brightnessFactor;
        this.opacityFactor = opacityFactor;

        return this;
    }

    public Image createImage() {
        int width = (int)image.getWidth();
        int height = (int)image.getHeight();

        WritableImage newImage = new WritableImage(width, height);

        PixelReader reader = image.getPixelReader();
        PixelWriter writer = newImage.getPixelWriter();

        writePixels(width, height, reader, writer);

        return newImage;
    }

    public void writePixels(final int width, final int height, PixelReader reader, PixelWriter writer) {
        constructLoop(this.flipHorizontal ? width : 0, this.flipHorizontal ? 0 : width, this.flipHorizontal ? -1 : 1, (x) -> {
            constructLoop(this.flipVertical ? height : 0, this.flipVertical ? 0 : height, this.flipVertical ? -1 : 1, (y) -> {
                Color color = reader.getColor(x, y);
                int newX = Math.max(0, Math.min(x + offsetX, width - 1));
                int newY = Math.max(0, Math.min(y + offsetY, height - 1));
                writer.setColor(newX, newY, color.deriveColor(hueSift, saturationFactor, brightnessFactor, opacityFactor));
            });
        });
    }

    private void constructLoop(int start, int end, int change, Consumer<Integer> index) {
        if(change > 0) {
            for(int i = start;i<end;i += change) index.accept(i);
            return;
        }

        for(int i = end-1;i>start;i += change) index.accept(i);
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public boolean isFlipHorizontal() {
        return flipHorizontal;
    }

    public boolean isFlipVertical() {
        return flipVertical;
    }

    public double getHueSift() {
        return hueSift;
    }

    public double getSaturationFactor() {
        return saturationFactor;
    }

    public double getBrightnessFactor() {
        return brightnessFactor;
    }

    public double getOpacityFactor() {
        return opacityFactor;
    }

    public SkinPart copy() {
        return new SkinPart(this.image, offsetX, offsetY, flipHorizontal, flipVertical, hueSift, saturationFactor, brightnessFactor, opacityFactor);
    }

    public JsonObject toJson() {
        String relativePath = FileHandler.toRelativePath(this.image.getUrl());

        JsonObject parentObject = new JsonObject();

        parentObject.addProperty("image", relativePath);
        parentObject.add("offset", integersToArray(offsetX, offsetY));
        parentObject.add("flip", integersToArray(this.flipHorizontal ? 1 : 0, this.flipVertical ? 1 : 0));
        parentObject.addProperty("hue", this.hueSift);
        parentObject.addProperty("saturation", this.saturationFactor);
        parentObject.addProperty("brightness", this.brightnessFactor);
        parentObject.addProperty("opacity", this.opacityFactor);

        return parentObject;
    }

    public static SkinPart fromJson(JsonObject jsonObject) {
        JsonArray offsetArray = jsonObject.get("offset").getAsJsonArray();
        JsonArray flipArray = jsonObject.get("flip").getAsJsonArray();

        return new SkinPart(
                new Image(new File(FileHandler.skinComposerData, jsonObject.get("image").getAsString()).getAbsolutePath()),
                offsetArray.get(0).getAsInt(),
                offsetArray.get(1).getAsInt(),
                flipArray.get(0).getAsInt() == 1,
                flipArray.get(1).getAsInt() == 1,
                jsonObject.get("hue").getAsDouble(),
                jsonObject.get("saturation").getAsDouble(),
                jsonObject.get("brightness").getAsDouble(),
                jsonObject.get("opacity").getAsDouble()
                );
    }

    protected static JsonArray integersToArray(int... numbers) {
        JsonArray array = new JsonArray();
        for (int number : numbers) {
            array.add(number);
        }
        return array;
    }
}
