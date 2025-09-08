package nazario.skin_composer.skin.viewer;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import nazario.skin_composer.SkinComposer;
import nazario.skin_composer.skin.Skin;

import java.awt.*;
import java.awt.event.*;

public class MovableSkinViewerComponent extends SkinViewerComponent {
    public MovableSkinViewerComponent(SkinComposer composer, Skin skin, int width, int height) {
        super(composer, skin, width, height);
    }

    //Rotation
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;

    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    private final Translate translate = new Translate(0, 0, 0);

    private boolean isMousePrimaryDown = false;
    private boolean isMouseSecondaryDown = false;

    @Override
    protected void initFx() {
        super.initFx();

        this.setupInputListeners(this.model.getRoot(), jfxPanel);
    }

    protected void setupInputListeners(Node model, JFXPanel fxPanel) {
        model.getTransforms().addAll(translate, rotateX, rotateY);

        fxPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(isMousePrimaryDown) {
                    double dx = e.getX() - anchorX;
                    double dy = e.getY() - anchorY;

                    rotateY.setAngle((anchorAngleY + dx * 0.5) % 360); // sensitivity
                    rotateX.setAngle(Math.max(Math.min(anchorAngleX - dy * 0.5, 90), -90));
                    return;
                }

                if (isMouseSecondaryDown) {
                    double dx = (e.getX() - anchorX) * -1;
                    double dy = (e.getY() - anchorY) * -1;

                    double max = getWidth()/1000d;

                    translate.setX(Math.max(Math.min(translate.getX() + dx/200f, max), -max));
                    translate.setY(Math.max(Math.min(translate.getY() + dy/200f, max), -max));

                    anchorX = e.getX();
                    anchorY = e.getY();
                }
            }
        });

        fxPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isMousePrimaryDown = e.getButton() == MouseEvent.BUTTON1;
                isMouseSecondaryDown = e.getButton() == MouseEvent.BUTTON3;

                if(e.getButton() == MouseEvent.BUTTON2) {
                    translate.setX(0);
                    translate.setY(0);

                    rotateX.setAngle(0);
                    rotateY.setAngle(0);
                }

                if(isMousePrimaryDown || isMouseSecondaryDown) {
                    anchorX = e.getX();
                    anchorY = e.getY();
                    anchorAngleX = rotateX.getAngle();
                    anchorAngleY = rotateY.getAngle();
                    fxPanel.requestFocusInWindow(); // for key input if needed

                    jfxPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    return;
                }
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
