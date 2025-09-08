package nazario.skin_composer.dialog;

import nazario.skin_composer.SkinComposer;
import nazario.skin_composer.skin.SkinPart;

import javax.swing.*;

public class SkinPartTweakDialog extends JDialog {
    private JPanel contentPane;
    private JButton applyButton;
    private JButton resetButton;
    private JButton cancelButton;

    private JSpinner xOffsetSpinner;
    private JSpinner yOffsetSpinner;
    private JCheckBox flipHorizontallyCheckBox;
    private JCheckBox flipVerticallyCheckBox;
    private JSlider hueSlider;
    private JSpinner hueSpinner;
    private JSlider brightnessSlider;
    private JSpinner brightnessSpinner;
    private JSlider saturationSlider;
    private JSpinner saturationSpinner;
    private JSlider opacitySlider;
    private JSpinner opacitySpinner;

    private final SkinComposer composer;
    private final SkinPart skinPart;
    private final SkinPart prevSkinPart;

    public SkinPartTweakDialog(SkinComposer composer, SkinPart skinPart) {
        this.composer = composer;
        this.skinPart = skinPart;
        this.prevSkinPart = skinPart.copy();

        this.setContentPane(contentPane);
        this.setModal(true);
        this.getRootPane().setDefaultButton(applyButton);

        this.pack();

        this.applyButton.addActionListener(e -> {
            this.dispose();
        });

        this.cancelButton.addActionListener(e -> {
            this.setFromPart(this.prevSkinPart);
            this.updateSkinPart();

            this.dispose();
        });

        this.resetButton.addActionListener(e -> this.reset());

        this.setFromPart(this.skinPart);

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

        this.brightnessSpinner.addChangeListener(event -> {
            this.brightnessSlider.setValue((int)this.brightnessSpinner.getValue());
            updateSkinPart();
        });
        this.brightnessSlider.addChangeListener(event -> {
            this.brightnessSpinner.setValue(this.brightnessSlider.getValue());
            updateSkinPart();
        });

        this.saturationSpinner.addChangeListener(event -> {
            this.saturationSlider.setValue((int)this.saturationSpinner.getValue());
            updateSkinPart();
        });
        this.saturationSlider.addChangeListener(event -> {
            this.saturationSpinner.setValue(this.saturationSlider.getValue());
            updateSkinPart();
        });

        this.opacitySpinner.addChangeListener(event -> {
            this.opacitySlider.setValue((int)this.opacitySpinner.getValue());
            updateSkinPart();
        });
        this.opacitySlider.addChangeListener(event -> {
            this.opacitySpinner.setValue(this.opacitySlider.getValue());
            updateSkinPart();
        });
    }

    protected void updateSkinPart() {
        skinPart.flip(this.flipHorizontallyCheckBox.isSelected(), this.flipVerticallyCheckBox.isSelected());
        skinPart.setOffset((Integer) this.xOffsetSpinner.getValue(), (Integer) this.yOffsetSpinner.getValue() * -1);

        skinPart.setProperties((int)this.hueSpinner.getValue()/100d, (int)this.saturationSpinner.getValue()/100d, (int)this.brightnessSpinner.getValue()/100d, (int)this.opacitySpinner.getValue()/100d);

        composer.updateSkin();
    }

    protected void setFromPart(SkinPart part) {
        this.xOffsetSpinner.setValue((int)part.getOffsetX());
        this.yOffsetSpinner.setValue((int)part.getOffsetY() * -1);

        this.flipHorizontallyCheckBox.setSelected(part.isFlipHorizontal());
        this.flipVerticallyCheckBox.setSelected(part.isFlipVertical());

        this.hueSpinner.setValue((int) ((part.getHueSift()*100d) / 360d));
        this.hueSlider.setValue((int)this.hueSpinner.getValue());
        this.brightnessSpinner.setValue((int)(part.getBrightnessFactor()*100d));
        this.brightnessSlider.setValue((int)this.brightnessSpinner.getValue());
        this.saturationSpinner.setValue((int)(part.getSaturationFactor()*100d));
        this.saturationSlider.setValue((int)this.saturationSpinner.getValue());
        this.opacitySpinner.setValue((int)(part.getOpacityFactor()*100d));
        this.opacitySlider.setValue((int)this.opacitySpinner.getValue());
    }

    protected void reset() {
        this.xOffsetSpinner.setValue(0);
        this.yOffsetSpinner.setValue(0);

        this.flipHorizontallyCheckBox.setSelected(false);
        this.flipVerticallyCheckBox.setSelected(false);

        this.hueSpinner.setValue(0);
        this.hueSlider.setValue(0);
        this.brightnessSpinner.setValue(100);
        this.brightnessSlider.setValue(100);
        this.saturationSpinner.setValue(100);
        this.saturationSlider.setValue(100);
        this.opacitySpinner.setValue(100);
        this.opacitySlider.setValue(100);
    }
}
