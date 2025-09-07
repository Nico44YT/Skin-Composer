package nazario.skin_composer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.Arrays;

public class Launcher {
    public static String[] DEFAULT_PARTS = new String[]{"Base","Head","Body","Legs","Feet","Other"};

    public static void main(String[] args) {
        SkinComposer composer = new SkinComposer("Skin Composer");

        composer.setVisible(true);

        try{
            File skinComposerData = new File(System.getProperty("user.home"), "\\AppData\\Local\\Nazario\\Skin Composer\\Parts");
            loadAllParts(composer, skinComposerData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected static void loadAllParts(SkinComposer composer, File partsFolder) throws IOException {
        composer.PART_ENTRIES.clear();

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

        composer.updateTabbedPane();
    }

    protected static void loadParts(SkinComposer composer, JsonObject object, File partsFolder) {
        File[] files = Arrays.stream(partsFolder.listFiles()).filter(file -> file.getName().endsWith(".png")).toArray(File[]::new);

        partsFolder.mkdirs();

        composer.PART_ENTRIES.add(new SkinPartListEntry(
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
            partJsonConfigEntry.addProperty("icon", "none");
            parentJson.add(partJsonConfigEntry);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(partsConfig)) {
            gson.toJson(parentJson, writer);
        }
    }
}
