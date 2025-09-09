package nazario.skin_composer.util;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class FileHandler {
    public static final FileChooser.ExtensionFilter JSON_FILTER = new FileChooser.ExtensionFilter("Json Files", ".json");
    public static final File skinComposerData = new File(System.getProperty("user.home"), "\\AppData\\Local\\Nazario\\Skin Composer\\Parts");

    public static String toRelativePath(String url) {
        return url.replace(skinComposerData.getAbsolutePath(), "");
    }

    public static File getResourceAsFile(String name) {
        return new File(ClassLoader.getSystemClassLoader().getResource(name).getPath());
    }

    public static InputStream getResourceAsStream(String name) {
        try(InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(name)){
            return in;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //region// * File Chooser * //
    private static boolean isFXInitialized = false;

    private static void initFX() {
        if (!isFXInitialized) {
            new JFXPanel(); // Initializes the JavaFX runtime
            isFXInitialized = true;
        }
    }

    public static File saveFileChooser(String title) {
        initFX();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<File> result = new AtomicReference<>();

        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(title);

            result.set(fileChooser.showSaveDialog(new FakeStage()));

            latch.countDown();
        });

        try{
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result.get();
    }

    public static File openFileChooser(String title, FileChooser.ExtensionFilter extension) {
        initFX();

        // Create a CountDownLatch to wait for the JavaFX task to complete
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<File> result = new AtomicReference<>();

        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(title);
            if(extension != null) fileChooser.getExtensionFilters().add(extension);

            // Show the FileChooser dialog
            result.set(fileChooser.showOpenDialog(new FakeStage()));

            // Release the latch to unblock the waiting thread
            latch.countDown();
        });

        try {
            // Wait for the JavaFX task to complete
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result.get();
    }

    public static File openDirectoryChooser(String title, String initialDirectory) {
        initFX();

        // Create a CountDownLatch to wait for the JavaFX task to complete
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<File> result = new AtomicReference<>();

        Platform.runLater(() -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            if(initialDirectory != null && new File(initialDirectory).exists()) directoryChooser.setInitialDirectory(new File(initialDirectory));
            directoryChooser.setTitle(title);

            result.set(directoryChooser.showDialog(new FakeStage()));

            // Release the latch to unblock the waiting thread
            latch.countDown();
        });

        try {
            // Wait for the JavaFX task to complete
            latch.await();
        } catch (InterruptedException e) {

        }
        return result.get();
    }

    // A simple fake Stage to use with the FileChooser
    private static class FakeStage extends Window {
        public FakeStage() {
            super();
        }
    }
    //endregion
}