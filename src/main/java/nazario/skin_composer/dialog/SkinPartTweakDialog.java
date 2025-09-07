package nazario.skin_composer.dialog;

import nazario.skin_composer.SkinComposer;
import nazario.skin_composer.SkinPart;

import javax.swing.*;
import java.awt.event.*;

public class SkinPartTweakDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JSlider colorShift;
    private JSpinner xOffset;
    private JSpinner yOffset;
    private JSlider brightnessFactorSlider;
    private JSlider opacifyFactorSlider;

    protected SkinPart tweakedPart;

    public SkinPartTweakDialog(SkinComposer skinComposer, SkinPart skinPart) {
        this.setContentPane(contentPane);
        this.setModal(true);
        this.getRootPane().setDefaultButton(buttonOK);

        this.tweakedPart = skinPart;

        buttonOK.addActionListener(lis -> onOK());
        buttonCancel.addActionListener(lis -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        colorShift.addChangeListener(lis -> {
            double value = colorShift.getValue();
            this.tweakedPart.hueShift = value;

            skinComposer.updateSkinViewer();
        });

        xOffset.addChangeListener(lis -> {
            this.tweakedPart.offsetX = (int)xOffset.getValue();

            skinComposer.updateSkinViewer();
        });

        yOffset.addChangeListener(lis -> {
            this.tweakedPart.offsetY = (int)yOffset.getValue()*-1;

            skinComposer.updateSkinViewer();
        });

        opacifyFactorSlider.addChangeListener(lis -> {
            this.tweakedPart.opacityFactor = opacifyFactorSlider.getValue()/100d;

            skinComposer.updateSkinViewer();
        });

        this.pack();
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
