package co.hygames.gamebox.language;

import co.hygames.gamebox.GameBox;

import java.util.List;

/**
 * @author Niklas Eicker
 */
public class ModuleLanguage implements MessageSource {
    protected GameBox gameBox;
    protected String moduleID;

    public ModuleLanguage(GameBox gameBox, String moduleID) {

    }

    @Override
    public String getMessage(String key) {
        return null;
    }

    @Override
    public List<String> getMessageList(String key) {
        return null;
    }
}
