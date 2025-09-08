package nazario.skin_composer.skin.viewer;

import com.google.errorprone.annotations.DoNotCall;
import javafx.embed.swing.JFXPanel;
import nazario.skin_composer.SkinComposer;
import nazario.skin_composer.skin.Skin;
import nazario.skin_composer.skin.SkinPart;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SkinPartViewerComponent extends SkinViewerComponent {

    protected ActionListener actionListener = null;
    protected SkinPart skinPart;

    public SkinPartViewerComponent(SkinComposer composer, SkinPart skinPart, int width, int height) {
        super(composer, null, width, height);

        this.skinPart = skinPart;
    }

    @Override
    protected void initFx() {
        super.initFx();

        this.updateTexture(skinPart.createImage());
        this.setupInputListeners(jfxPanel);
    }

    @DoNotCall
    public Skin getSkin() {
        return null;
    }

    public SkinPart getSkinPart() {
        return this.skinPart;
    }

    public void addActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    protected void setupInputListeners(JFXPanel fxPanel) {
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
                if(actionListener != null) jfxPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(actionListener != null) jfxPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }
}
