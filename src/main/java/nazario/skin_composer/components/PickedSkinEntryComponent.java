package nazario.skin_composer.components;

import nazario.skin_composer.SkinComposer;
import nazario.skin_composer.SkinPart;
import nazario.skin_composer.dialog.SkinPartTweakDialog;

import javax.swing.*;
import java.awt.*;
public class PickedSkinEntryComponent extends JPanel {

    public JPanel buttonsPanel;
    public SkinPartViewerComponent partViewerComponent;

    public JButton moveUpButton;
    public JButton moveDownButton;
    public JButton removeButton;
    public JButton changePropertiesButton;

    protected SkinPart skinPart;
    protected int index;

    public PickedSkinEntryComponent(SkinComposer composer, SkinPart skinPart, int index, int width) {
        this.index = index;
        this.skinPart = skinPart;

        this.setLayout(new GridLayout(1, 2));

        this.partViewerComponent = new SkinPartViewerComponent(skinPart, composer, 96, 96);
        this.buttonsPanel = new JPanel(new GridLayout(4, 1));

        this.moveUpButton = new JButton("Up");
        this.moveDownButton = new JButton("Down");
        this.removeButton = new JButton("Remove");
        this.changePropertiesButton = new JButton("Tweak");

        this.buttonsPanel.add(this.moveUpButton);
        this.buttonsPanel.add(this.moveDownButton);
        this.buttonsPanel.add(this.removeButton);
        this.buttonsPanel.add(this.changePropertiesButton);

        this.moveUpButton.addActionListener(lis -> {
            move(composer, index, -1);
        });

        this.moveDownButton.addActionListener(lis -> {
            move(composer, index, 1);
        });

        this.removeButton.addActionListener(lis -> {
            composer.currentSkin.remove(index);
            composer.selectedPartsPanel.remove(this);

            update(composer);
        });

        this.changePropertiesButton.addActionListener(lis -> {
            SkinPartTweakDialog dialog = new SkinPartTweakDialog(composer, skinPart);

            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        this.add(partViewerComponent);
        this.add(buttonsPanel);

        this.setMinimumSize(new Dimension(width, 96));
        this.setPreferredSize(new Dimension(width, 96));
        this.setSize(new Dimension(width, 96));

        updateButtons(composer, index);
    }

    public void updateButtons(SkinComposer composer, int index) {
        this.moveUpButton.setEnabled(index > 0);
        this.moveDownButton.setEnabled(composer.currentSkin.size()-1 > index);
    }

    public void update(SkinComposer composer) {
        composer.updateSkinViewer();
        composer.repaint();
    }

    public void move(SkinComposer composer, int currentIndex, int change) {
        int newIndex = Math.max(Math.min(currentIndex + change, composer.currentSkin.size()-1), 0);

        composer.currentSkin.images.remove(this.skinPart);
        composer.currentSkin.images.add(newIndex, this.skinPart);

        update(composer);
        updateButtons(composer, newIndex);
    }

    public SkinPart getSkinPart() {
        return this.skinPart;
    }
}
