package co.hygames.gamebox;

import java.io.File;
import java.util.logging.Logger;

/**
 * @author Niklas Eicker
 */
public class GameBox {
    private static GameBox instance;

    public void onEnable() {
        instance = this;
        // ToDo: load local modules and update info about cloud modules
    }

    public static GameBox getInstance() {
        return instance;
    }

    public File getDataFolder() {
        // ToDo
        return null;
    }

    public Logger getLogger() {
        // ToDo: logger from Hytale?
        return null;
    }
}
