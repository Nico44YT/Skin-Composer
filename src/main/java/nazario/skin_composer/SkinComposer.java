package nazario.skin_composer;

import javafx.scene.control.Skin;
import nazario.skin_composer.components.PickedSkinEntryComponent;
import nazario.skin_composer.components.SkinPartViewerComponent;
import nazario.skin_composer.components.SkinViewerComponent;
import nazario.skin_composer.util.FileHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SkinComposer extends JFrame {
    private JPanel mainPanel;
    private JPanel selectedParts;
    private JPanel availableParts;
    private JPanel skinView;
    private JTabbedPane partsTabbedPane;
    private JButton exportButton;
    private JButton saveButton;
    private JButton exportAsAseprite;
    private JButton loadButton;
    public JPanel selectedPartsPanel;
    private JScrollPane scrollPanePickedParts;

    protected SkinViewerComponent skinViewerComponent;

    public List<SkinPartListEntry> PART_ENTRIES;
    private SkinType skinType;

    public CompoundSkinImage currentSkin;

    public SkinComposer(String titleName) {
        super(titleName);
        this.setContentPane(this.mainPanel);
        this.setMinimumSize(new Dimension(512 + 256, 512));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        this.skinViewerComponent = new SkinViewerComponent(this, (int)this.getMinimumSize().getWidth()/3, (int)this.getMinimumSize().getHeight());

        this.getContentPane().setLayout(new GridLayout(1, 3));
        this.selectedParts.setMinimumSize(new Dimension((int)this.getMinimumSize().getWidth()/3, (int)this.getMinimumSize().getHeight()));
        this.getContentPane().add(this.selectedParts);

        this.skinView.setMinimumSize(new Dimension((int)this.getMinimumSize().getWidth()/3, (int)this.getMinimumSize().getHeight()));
        this.getContentPane().add(this.skinView);

        this.availableParts.setMinimumSize(new Dimension((int)this.getMinimumSize().getWidth()/3, (int)this.getMinimumSize().getHeight()));
        this.getContentPane().add(this.availableParts);

        this.skinView.setLayout(new BorderLayout());
        this.skinView.add(skinViewerComponent, BorderLayout.CENTER);

        this.skinType = SkinType.DEFAULT;
        this.PART_ENTRIES = new ArrayList<>();

        this.scrollPanePickedParts.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.currentSkin = new CompoundSkinImage(new ArrayList<>());

        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                if(currentSkin != null) {
                    updateSkinViewer();
                }
            }
            //region
            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
            //endregion
        });


        this.exportButton.addActionListener(this::action$exportSkin);
    }

    public SkinType getSkinType() {
        if(this.skinView == null) return SkinType.DEFAULT;
        return this.skinType;
    }

    public void updateTabbedPane() {
        this.PART_ENTRIES.forEach(entry -> {
            File[] parts = entry.files();
            JPanel holder = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4)); // left aligned, 4px gaps
            holder.setPreferredSize(new Dimension(300, parts.length * 70));

            for (File file : parts) {
                SkinPartViewerComponent skinPartComponent = getPartViewerComponent(file);

                holder.add(skinPartComponent);
            }

            JScrollPane scrollPane = new JScrollPane(holder, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            this.partsTabbedPane.add(entry.name(), scrollPane);
        });
    }

    private SkinPartViewerComponent getPartViewerComponent(File file) {
        SkinPartViewerComponent skinPartComponent = new SkinPartViewerComponent(new SkinPart(file), this, 96, 96);

        skinPartComponent.addActionListener(lis -> {
            currentSkin.images.add(new SkinPart(file));

            updateSkinViewer();
        });

        skinPartComponent.setMinimumSize(new Dimension(96, 96));
        skinPartComponent.setPreferredSize(new Dimension(96, 96));
        skinPartComponent.setSize(new Dimension(96, 96));
        return skinPartComponent;
    }

    List<PickedSkinEntryComponent> PICKED_PARTS = new ArrayList<>();

    public void smartUpdateSkinViewer() {
        this.selectedPartsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        for (int i = 0; i < PICKED_PARTS.size(); i++) {
            PickedSkinEntryComponent part = PICKED_PARTS.get(i);

            boolean containsInCompoundSkin = this.currentSkin.images.contains(part.getSkinPart());
            if(!containsInCompoundSkin) {
                this.selectedPartsPanel.remove(part);
            } else {
                boolean containedInList = part.getParent() == this.selectedPartsPanel;
                if(!containedInList) {
                    this.selectedPartsPanel.add(new PickedSkinEntryComponent(this, part.getSkinPart(), i, this.getWidth()/3 - 35));
                }
            }

        }

        this.skinViewerComponent.updateTexture(this.currentSkin.toImage());

        repaint();
    }

    public void updateSkinViewer() {
        //this.selectedPartsPanel.removeAll();
        //this.selectedPartsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//
        //this.selectedPartsPanel.setPreferredSize(new Dimension(this.getWidth()/3, this.currentSkin.images.size() * 70));
//
        //for (int i = 0; i < this.currentSkin.images.size(); i++) {
        //    this.selectedPartsPanel.add(new PickedSkinEntryComponent(this, this.currentSkin.images.get(i), i, this.getWidth()/3 - 35));
        //}
//
        //this.skinViewerComponent.updateTexture(this.currentSkin.toImage());
//
//
        //repaint();

        smartUpdateSkinViewer();
    }

    private void action$exportSkin(ActionEvent event) {
        File saveLocation = FileHandler.saveFileChooser("Save Skin");

        saveLocation.mkdirs();

        try {
            BufferedImage bi = this.currentSkin.asBufferedImage();
            ImageIO.write(bi, "png", saveLocation);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
