package nazario.skin_composer.skin;

import nazario.skin_composer.Icons;
import nazario.skin_composer.SkinComposer;
import nazario.skin_composer.dialog.SkinPartTweakDialog;
import nazario.skin_composer.skin.viewer.SkinPartViewerComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AddedSkinPartComponent extends JPanel {
    protected static final int size = 110;

    protected SkinPartViewerComponent partViewer;
    protected SkinPart skinPart;
    protected int index;

    protected JButton moveUpButton;
    protected JButton moveDownButton;
    protected JButton removeButton;
    protected JButton openTweakDialogButton;

    public AddedSkinPartComponent(SkinComposer composer, SkinPart skinPart, int index) {
        this.setLayout(new BorderLayout());

        this.partViewer = new SkinPartViewerComponent(composer, skinPart, size, size);
        this.partViewer.setMinimumSize(new Dimension(size, size));
        this.partViewer.setPreferredSize(new Dimension(size, size));

        this.setBackground(this.getBackground().darker());
        this.setOpaque(true);

        this.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setMaximumSize(new Dimension(size*2 + 35, size + 5));

        this.skinPart = skinPart;
        this.index = index;

        this.moveUpButton = new JButton();
        this.moveDownButton = new JButton();
        this.removeButton = new JButton();
        this.openTweakDialogButton = new JButton();

        this.moveUpButton.setIcon(Icons.UP);
        this.moveDownButton.setIcon(Icons.DOWN);
        this.removeButton.setIcon(Icons.REMOVE);
        this.openTweakDialogButton.setIcon(Icons.TWEAK);

        this.moveUpButton.setPreferredSize(new Dimension(90, size/4));
        this.moveDownButton.setPreferredSize(new Dimension(90, size/4));
        this.removeButton.setPreferredSize(new Dimension(90, size/4));
        this.openTweakDialogButton.setPreferredSize(new Dimension(90, size/4));

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

        this.setSkinPart(this.skinPart);

        updateButtons(composer, newIndex);
        composer.updateAddedParts();
        composer.updateSkin();
    }

    public void updateButtons(SkinComposer composer, int index) {
        this.moveUpButton.setEnabled(index > 0);
        this.moveDownButton.setEnabled(composer.getSkinViewer().getSkin().getSkinParts().size()-1 > index);
    }

    public SkinPart getSkinPart() {
        if(!this.partViewer.getSkinPart().equals(this.skinPart)) this.partViewer.setTexture(this.skinPart.createImage());
        return this.skinPart;
    }

    public void setSkinPart(SkinPart skinPart) {
        this.skinPart = skinPart;
        this.partViewer.setTexture(skinPart.createImage());
    }
}
