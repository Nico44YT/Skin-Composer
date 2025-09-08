package nazario.skin_composer.skin;

public enum SkinType {
    SLIM("./models/player_slim.obj"),
    DEFAULT("./models/player.obj");

    private final String modelPath;
    SkinType(String modelPath) {
        this.modelPath = modelPath;
    }

    public String getModelPath() {
        return this.modelPath;
    }
}