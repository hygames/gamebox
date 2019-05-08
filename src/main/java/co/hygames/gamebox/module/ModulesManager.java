package co.hygames.gamebox.module;

import co.hygames.gamebox.GameBox;

import java.io.File;

/**
 * @author Niklas Eicker
 */
public class ModulesManager {
    private GameBox gameBox;
    private File modulesDir;
    private File gamesDir;

    public ModulesManager(GameBox gameBox) {
        this.gameBox = gameBox;
        modulesDir = new File(gameBox.getDataFolder(), "modules");
        gamesDir = new File(gameBox.getDataFolder(), "games");
        final boolean newModulesDir = modulesDir.mkdirs();
        final boolean newGamesDir = gamesDir.mkdirs();
        if (newModulesDir) {
            gameBox.getLogger().info("Created Modules Directory");
        }
        if (newGamesDir) {
            gameBox.getLogger().info("Created Games Directory");
        }
    }
}
