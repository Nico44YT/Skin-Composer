package nazario.skin_composer.skin.viewer;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import nazario.skin_composer.SkinComposer;
import nazario.skin_composer.skin.Skin;
import nazario.skin_composer.skin.SkinType;
import nazario.skin_composer.util.FileHandler;
import org.fxyz3d.importers.Model3D;
import org.fxyz3d.importers.obj.ObjImporter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class SkinViewerComponent extends JPanel {
    protected final SkinComposer composer;
    protected final JFXPanel jfxPanel;

    protected Skin skin;
    protected PerspectiveCamera camera;
    protected Model3D model;

    public SkinViewerComponent(SkinComposer composer, Skin skin, int width, int height) {
        super(new BorderLayout());
        this.setSize(width, height);

        this.composer = composer;
        this.skin = skin;

        this.jfxPanel = new JFXPanel();
        this.add(this.jfxPanel, BorderLayout.CENTER);

        Platform.runLater(this::initFx);
    }

    protected void initFx() {
        this.model = loadModel(this.composer.getSkinType());

        Group world = new Group(this.model.getRoot());
        Scene scene = new Scene(world, this.getWidth(), this.getHeight(), true, SceneAntialiasing.BALANCED);
        scene.setFill(javafx.scene.paint.Color.BLACK);

        this.camera = new PerspectiveCamera(true);
        this.camera.setNearClip(0.1f);
        this.camera.setFarClip(100f);
        this.camera.setTranslateZ(-5f);
        world.getChildren().add(this.camera);
        scene.setCamera(this.camera);

        this.model.getRoot().getTransforms().add(new Rotate(180, Rotate.X_AXIS));
        this.model.getRoot().getTransforms().add(new Rotate(180, Rotate.Y_AXIS));

        jfxPanel.setScene(scene);

        if(this.getSkin() != null) this.updateTexture(this.getSkin().toImage());
    }

    public void updateTexture(File file) {
        this.setTexture(new Image(file.getAbsolutePath(), 1024, 1024, false, false, false));
    }

    public void updateTexture(Image image) {
        int targetWidth = 1024;
        int targetHeight = 1024;

        WritableImage scaled = new WritableImage(targetWidth, targetHeight);
        PixelWriter writer = scaled.getPixelWriter();
        PixelReader reader = image.getPixelReader();

        double scaleX = image.getWidth() / targetWidth;
        double scaleY = image.getHeight() / targetHeight;

        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                // Nearest-neighbor scaling
                int srcX = (int) (x * scaleX);
                int srcY = (int) (y * scaleY);
                writer.setColor(x, y, reader.getColor(srcX, srcY));
            }
        }

        this.setTexture(scaled);
    }

    public void setTexture(Image image) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(image);
        material.setDiffuseColor(Color.WHITE.deriveColor(1, 1, 1, 1.0));
        applyMaterialRecursively(this.model.getRoot(), material);
    }

    protected Model3D loadModel(SkinType skinType) {
        try{
            File file = FileHandler.getResourceAsFile(skinType.getModelPath());
            URL url = file.getCanonicalFile().toURL();
            ObjImporter importer = new ObjImporter();
            return importer.load(url);

        }catch (Exception e) {
            System.out.println("My life is horrible and I hate everything");
            e.printStackTrace();
            return null;
        }
    }

    private void applyMaterialRecursively(Parent parent, PhongMaterial material) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            if (child instanceof Shape3D shape) {
                shape.setMaterial(material);
            } else if (child instanceof Parent p) {
                applyMaterialRecursively(p, material);
            }
        }
    }

    public Skin getSkin() {
        return this.skin;
    }
}
