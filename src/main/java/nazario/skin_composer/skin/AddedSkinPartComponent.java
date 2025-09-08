package nazario.skin_composer.skin;

import nazario.skin_composer.SkinComposer;
import nazario.skin_composer.dialog.SkinPartTweakDialog;
import nazario.skin_composer.skin.viewer.SkinPartViewerComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AddedSkinPartComponent extends JPanel {

    protected SkinPartViewerComponent partViewer;
    protected SkinPart skinPart;
    protected int index;

    protected JButton moveUpButton;
    protected JButton moveDownButton;
    protected JButton removeButton;
    protected JButton openTweakDialogButton;

    public AddedSkinPartComponent(SkinComposer composer, SkinPart skinPart, int index) {
        this.setLayout(new BorderLayout());

        this.partViewer = new SkinPartViewerComponent(composer, skinPart, 96, 96);
        this.partViewer.setMinimumSize(new Dimension(96, 96));
        this.partViewer.setPreferredSize(new Dimension(96, 96));

        this.setBackground(this.getBackground().darker());
        this.setOpaque(true);

        this.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setMaximumSize(new Dimension(96*2 + 35, 96));

        this.skinPart = skinPart;
        this.index = index;

        this.moveUpButton = new JButton("Up");
        this.moveDownButton = new JButton("Down");
        this.removeButton = new JButton("Remove");
        this.openTweakDialogButton = new JButton("Tweak");

        this.moveUpButton.addActionListener(event -> move(composer, index, -1));
        this.moveDownButton.addActionListener(event -> move(composer, index, 1));
        this.removeButton.addActionListener(event -> {
            composer.getSkinViewer().getSkin().getSkinParts().remove(skinPart);
            composer.getAddedPartComponents().remove(this);

            composer.updateSkin();
            composer.updateAddedParts();
        });
        this.openTweakDialogButton.addActionListener(event -> {
            SkinPartTweakDialog tweakDialog = new SkinPartTweakDialog(composer, skinPart);
            tweakDialog.setLocationRelativeTo(this.openTweakDialogButton);
            tweakDialog.setResizable(false);
            tweakDialog.setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
        buttonPanel.add(moveUpButton);
        buttonPanel.add(moveDownButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(openTweakDialogButton);

        this.add(this.partViewer, BorderLayout.WEST);
        this.add(buttonPanel, BorderLayout.EAST);
    }

    public void move(SkinComposer composer, int currentIndex, int change) {
        List<SkinPart> parts = composer.getSkinViewer().getSkin().getSkinParts();
        int newIndex = Math.max(Math.min(currentIndex + change, parts.size()-1), 0);

        parts.remove(this.skinPart);
        parts.add(newIndex, this.skinPart);

        updateButtons(composer, newIndex);
        composer.updateAddedParts();
        composer.updateSkin();
    }

    public void updateButtons(SkinComposer composer, int index) {
        this.moveUpButton.setEnabled(index > 0);
        this.moveDownButton.setEnabled(composer.getSkinViewer().getSkin().getSkinParts().size()-1 > index);
    }

    public SkinPart getSkinPart() {
        return this.skinPart;
    }

    public void setSkinPart(SkinPart skinPart) {
        this.skinPart = skinPart;
        this.partViewer.setTexture(skinPart.createImage());
    }
}
