package nazario.skin_composer.components;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import nazario.skin_composer.SkinComposer;
import nazario.skin_composer.SkinPart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class SkinPartViewerComponent extends SkinViewerComponent {

    protected ActionListener actionListener = null;

    public SkinPartViewerComponent(SkinPart skinPart, SkinComposer parent, int width, int height) {
        super(parent, width, height);

        Platform.runLater(() -> {
            if (this.model != null) {
                this.updateTexture(skinPart.createImage());
            }
        });
    }

    public void addActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    protected void setupInputListeners(Node model, JFXPanel fxPanel) {
        JPanel panel = this;

        fxPanel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if(actionListener != null) actionListener.actionPerformed(new ActionEvent(e.getComponent(), e.getID(), e.getButton()+""));
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                jfxPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                jfxPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }
}
