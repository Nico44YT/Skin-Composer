package nazario.skin_composer.components;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import nazario.skin_composer.SkinComposer;
import nazario.skin_composer.SkinType;
import nazario.skin_composer.util.FileHandler;
import org.fxyz3d.importers.Model3D;
import org.fxyz3d.importers.obj.ObjImporter;

import javax.swing.*;
import java.awt.*;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.net.URL;

public class SkinViewerComponent extends JPanel {
    protected final SkinComposer parent;
    protected final JFXPanel jfxPanel;
    protected PerspectiveCamera camera;
    protected Model3D model;

    public SkinViewerComponent(SkinComposer parent, int width, int height) {
        super(new BorderLayout());
        this.setSize(width, height);

        this.parent = parent;

        this.jfxPanel = new JFXPanel();
        add(this.jfxPanel, BorderLayout.CENTER);

        Platform.runLater(this::initFx);
    }

    private void initFx() {
        this.model = loadModel(this.parent.getSkinType());

        Group world = new Group(this.model.getRoot());
        Scene scene = new Scene(world, this.getWidth(), this.getHeight(), true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);

        this.camera = new PerspectiveCamera(true);
        this.camera.setNearClip(0.1f);
        this.camera.setFarClip(100f);
        this.camera.setTranslateZ(-5f);
        world.getChildren().add(this.camera);
        scene.setCamera(this.camera);

        this.model.getRoot().getTransforms().add(new Rotate(180, Rotate.X_AXIS));
        this.model.getRoot().getTransforms().add(new Rotate(180, Rotate.Y_AXIS));

        jfxPanel.setScene(scene);

        setupInputListeners(this.model.getRoot(), jfxPanel);
    }

    public void updateTexture(File file) {
        this.setTexture(new javafx.scene.image.Image(file.getAbsolutePath(), 1024, 1024, false, false, false));
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

    private static double anchorX, anchorY;
    private static double anchorAngleX = 0;
    private static double anchorAngleY = 0;
    private static final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private static final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    protected void setupInputListeners(Node model, JFXPanel fxPanel) {
        model.getTransforms().addAll(rotateX, rotateY);

        fxPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                double dx = e.getX() - anchorX;
                double dy = e.getY() - anchorY;

                rotateY.setAngle((anchorAngleY + dx * 0.5) % 360); // sensitivity
                rotateX.setAngle(Math.max(Math.min(anchorAngleX - dy * 0.5, 90), -90));
            }
        });

        fxPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                anchorX = e.getX();
                anchorY = e.getY();
                anchorAngleX = rotateX.getAngle();
                anchorAngleY = rotateY.getAngle();
                fxPanel.requestFocusInWindow(); // for key input if needed

                jfxPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                jfxPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        fxPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double amount = e.getPreciseWheelRotation();

                camera.setFieldOfView(Math.min(Math.max(camera.getFieldOfView() + amount, 1d), 100d));
            }
        });
    }
}
