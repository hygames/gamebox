package co.hygames.gamebox.module;

import co.hygames.gamebox.GameBox;
import co.hygames.gamebox.language.ModuleLanguage;

import java.io.File;

/**
 * @author Niklas Eicker
 */
public abstract class GameBoxModule {
    protected String identifier;
    protected GameBox gameBox;
    protected ModuleLanguage moduleLanguage;

    public GameBoxModule(GameBox gameBox, String identifier) {
        this.gameBox = gameBox;
        this.identifier = identifier;
    }

    public abstract File getModuleFolder();

    public abstract File getLanguageFolder();
}
