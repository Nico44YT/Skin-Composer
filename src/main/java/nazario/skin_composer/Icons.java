package nazario.skin_composer;

import nazario.skin_composer.util.FileHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public interface Icons {
    ImageIcon UP = Icons.createIcon("up");
    ImageIcon DOWN = Icons.createIcon("down");
    ImageIcon REMOVE = Icons.createIcon("remove");
    ImageIcon TWEAK = Icons.createIcon("tweak");


    static ImageIcon createIcon(String name) {
        try{
            Image image = ImageIO.read(FileHandler.getResourceAsFile("./icons/" + name + ".png"));
            return new ImageIcon(image.getScaledInstance(16, 16, 16));
        }catch (Exception e) {
            return null;
        }
    }
}
