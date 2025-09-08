package nazario.skin_composer;

import com.formdev.flatlaf.FlatDarkLaf;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nazario.skin_composer.skin.AvailableSkinParts;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;

public class Launcher {
    public static String[] DEFAULT_PARTS = new String[]{"Base","Head","Body","Legs","Feet","Other"};

    public static void main(String[] args) {
        SkinComposer composer = new SkinComposer("Skin Composer");

        try{
            UIManager.setLookAndFeel(FlatDarkLaf.class.getName());
            SwingUtilities.updateComponentTreeUI(composer);
        }catch (Exception e) {}

        composer.setVisible(true);

        try{
            File skinComposerData = new File(System.getProperty("user.home"), "\\AppData\\Local\\Nazario\\Skin Composer\\Parts");
            loadAllParts(composer, skinComposerData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected static void loadAllParts(SkinComposer composer, File partsFolder) throws IOException {
        composer.getAvailableSkinParts().clear();

        File partsConfig = new File(partsFolder, "parts.json");

        //Default Parts Config doesn't exist, create one
        if(!partsFolder.exists()) createDefaultParts(partsFolder);

        JsonArray jsonArray;

        try{
            Gson gson = new Gson();

            jsonArray = gson.fromJson(new FileReader(partsConfig), JsonArray.class);
        }catch (Exception e) {
            jsonArray = new JsonArray();
        }

        jsonArray.forEach(entry -> {
            JsonObject jsonObject = entry.getAsJsonObject();

            String id = jsonObject.get("id").getAsString();
            loadParts(composer, jsonObject, new File(partsFolder, id));
        });

        SwingUtilities.invokeLater(composer::updateAvailableSkinPartsTabs);
    }

    protected static void loadParts(SkinComposer composer, JsonObject object, File partsFolder) {
        File[] files = Arrays.stream(partsFolder.listFiles()).filter(file -> file.getName().endsWith(".png")).toArray(File[]::new);

        partsFolder.mkdirs();

        composer.getAvailableSkinParts().add(new AvailableSkinParts(
                object.get("name").getAsString(),
                object.get("id").getAsString(),
                object.get("icon").getAsString(),
                files
        ));
    }

    protected static void createDefaultParts(File partsFolder) throws IOException {
        File partsConfig = new File(partsFolder, "parts.json");

        JsonArray parentJson = new JsonArray();

        for(String defaultPart : DEFAULT_PARTS) {
            File file = new File(partsFolder, defaultPart.toLowerCase());
            file.mkdirs();

            JsonObject partJsonConfigEntry = new JsonObject();

            partJsonConfigEntry.addProperty("id", defaultPart.toLowerCase());
            partJsonConfigEntry.addProperty("name", defaultPart);
            partJsonConfigEntry.addProperty("icons", "none");
            parentJson.add(partJsonConfigEntry);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(partsConfig)) {
            gson.toJson(parentJson, writer);
        }
    }
}
