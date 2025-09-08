package nazario.skin_composer.dialog;

import nazario.skin_composer.SkinComposer;
import nazario.skin_composer.skin.SkinPart;

import javax.swing.*;

public class SkinPartTweakDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JSpinner xOffsetSpinner;
    private JSpinner yOffsetSpinner;
    private JCheckBox flipHorizontallyCheckBox;
    private JCheckBox flipVerticallyCheckBox;
    private JSlider hueSlider;
    private JSpinner hueSpinner;

    private final SkinComposer composer;
    private final SkinPart skinPart;

    public SkinPartTweakDialog(SkinComposer composer, SkinPart skinPart) {
        this.composer = composer;
        this.skinPart = skinPart;

        this.setContentPane(contentPane);
        this.setModal(true);
        this.getRootPane().setDefaultButton(buttonOK);

        this.pack();

        this.flipHorizontallyCheckBox.addChangeListener(event -> updateSkinPart());
        this.flipVerticallyCheckBox.addChangeListener(event -> updateSkinPart());
        this.xOffsetSpinner.addChangeListener(event -> updateSkinPart());
        this.yOffsetSpinner.addChangeListener(event -> updateSkinPart());

        this.hueSpinner.addChangeListener(event -> {
            this.hueSlider.setValue((int)this.hueSpinner.getValue());
            updateSkinPart();
        });
        this.hueSlider.addChangeListener(event -> {
            this.hueSpinner.setValue(this.hueSlider.getValue());
            updateSkinPart();
        });
    }

    protected void updateSkinPart() {
        skinPart.flip(this.flipHorizontallyCheckBox.isSelected(), this.flipVerticallyCheckBox.isSelected());
        skinPart.setOffset((Integer) this.xOffsetSpinner.getValue(), (Integer) this.yOffsetSpinner.getValue() * -1);

        skinPart.setProperties(this.hueSlider.getValue()/100d, 1, 1, 1);

        composer.updateSkin();
    }
}
