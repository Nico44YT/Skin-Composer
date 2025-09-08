package nazario.skin_composer;

import nazario.skin_composer.skin.*;
import nazario.skin_composer.skin.viewer.MovableSkinViewerComponent;
import nazario.skin_composer.skin.viewer.SkinPartViewerComponent;
import nazario.skin_composer.skin.viewer.SkinViewerComponent;
import org.controlsfx.control.spreadsheet.Grid;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SkinComposer extends JFrame {
    private JPanel mainPanel;
    private JPanel skinViewPanel;
    private JTabbedPane availablePartsTabs;
    private JPanel pickedPartsPanel;
    private JPanel firstHalf;
    private JScrollPane pickedPartsScrollPane;
    private JButton button1;

    protected SkinViewerComponent skinViewer;

    protected final List<AddedSkinPartComponent> ADDED_PARTS;
    protected final List<AvailableSkinParts> AVAILABLE_SKIN_PARTS;

    public SkinComposer(String windowTitle) {
        super(windowTitle);
        this.setMinimumSize(new Dimension(1024 + 512, 512 + 256));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        this.setContentPane(this.mainPanel);

        this.skinViewPanel.setPreferredSize(new Dimension(this.getWidth()/3, this.getHeight()));


        this.skinViewer = new MovableSkinViewerComponent(this, new Skin(),this.getWidth()/3, this.getHeight());
        this.AVAILABLE_SKIN_PARTS = new ArrayList<>();
        this.ADDED_PARTS = new ArrayList<>();

        this.skinViewPanel.setLayout(new BorderLayout());
        this.skinViewPanel.add(skinViewer, BorderLayout.CENTER);

        this.firstHalf.setLayout(new GridLayout(1, 2));
        this.pickedPartsPanel.setLayout(new BoxLayout(this.pickedPartsPanel, BoxLayout.Y_AXIS));
        this.pickedPartsScrollPane.setPreferredSize(new Dimension(96*2 + 70, this.getHeight()));
        this.pickedPartsScrollPane.setMaximumSize(new Dimension(96*2 + 70, this.getHeight()));

        this.mainPanel.setLayout(new GridLayout(1, 3));
        this.mainPanel.add(this.firstHalf);
        this.mainPanel.add(this.skinViewPanel);
        this.mainPanel.add(this.availablePartsTabs);
    }

    public List<AvailableSkinParts> getAvailableSkinParts() {
        return this.AVAILABLE_SKIN_PARTS;
    }

    public void updateAvailableSkinPartsTabs() {
        AVAILABLE_SKIN_PARTS.forEach(group -> {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
            panel.setPreferredSize(new Dimension(300, group.imageFiles().length * 70));

            for(File file : group.imageFiles()) {
                int size = 96;
                SkinPartViewerComponent partViewer = new SkinPartViewerComponent(this, new SkinPart(file), size, size);
                partViewer.setMinimumSize(new Dimension(size, size));
                partViewer.setPreferredSize(new Dimension(size, size));

                partViewer.addActionListener(lis -> {
                    this.skinViewer.getSkin().getSkinParts().add(new SkinPart(file));
                    updateSkin();
                    updatePickedList();
                });

                panel.add(partViewer);
            }

            JScrollPane scrollPane = new JScrollPane(panel);
            availablePartsTabs.add(group.name(), scrollPane);
        });

        repaint();
    }

    public void updateSkin() {
        this.skinViewer.updateTexture(this.skinViewer.getSkin().toImage());
    }

    private void updatePickedList() {
        Skin skin = this.getSkinViewer().getSkin();
        List<SkinPart> parts = skin.getSkinParts();

        // Ensure ADDED_PARTS list has the correct size
        while (this.ADDED_PARTS.size() < parts.size()) {
            this.ADDED_PARTS.add(null);
        }

        for (int i = 0; i < parts.size(); i++) {
            SkinPart part = parts.get(i);

            AddedSkinPartComponent existing = this.ADDED_PARTS.get(i);
            if (existing == null) {
                // No component yet → create new
                this.ADDED_PARTS.set(i, new AddedSkinPartComponent(this, part, i));
            } else {
                // Component exists → update only the skin part if it changed
                if (!existing.getSkinPart().equals(part)) {
                    existing.setSkinPart(part); // <- you need a setter in AddedSkinPartComponent
                }
            }
        }

        // Rebuild the panel only once
        this.pickedPartsPanel.removeAll();
        for (int i = 0; i < this.ADDED_PARTS.size(); i++) {
            AddedSkinPartComponent component = this.ADDED_PARTS.get(i);
            if (component != null) {
                this.pickedPartsPanel.add(component);
                component.setMinimumSize(new Dimension(96*2, 96));
                component.setPreferredSize(new Dimension(96*2, 96));
                component.updateButtons(this, i);
            }
        }

        this.pickedPartsPanel.revalidate();
        this.pickedPartsPanel.repaint();
    }

    public SkinType getSkinType() {
        return SkinType.DEFAULT;
    }

    public SkinViewerComponent getSkinViewer() {
        return this.skinViewer;
    }

    public List<AddedSkinPartComponent> getAddedPartComponents() {
        return this.ADDED_PARTS;
    }

    public void updateAddedParts() {
        this.updatePickedList();
    }
}
