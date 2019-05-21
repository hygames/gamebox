/*
 * GameBox
 * Copyright (C) 2019  Niklas Eicker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
    private File languageFolder;
    private File moduleFolder;
    protected ModuleLanguage moduleLanguage;

    GameBoxModule() {}

    public File getModuleFolder() {
        if (moduleFolder != null) return moduleFolder;
        moduleFolder = new File(gameBox.getModulesManager().getModulesDir(), identifier);
        if (!moduleFolder.isDirectory()) moduleFolder.mkdirs();
        return moduleFolder;
    }

    public File getLanguageFolder() {
        if (languageFolder != null) return languageFolder;
        languageFolder = new File(gameBox.getLanguageDir(), identifier);
        if (!languageFolder.isDirectory()) languageFolder.mkdirs();
        return languageFolder;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    void setGameBox(GameBox gameBox) throws IllegalAccessException {
        if (this.gameBox != null) throw new IllegalAccessException("Cannot change the GameBox instance in a module");
        this.gameBox = gameBox;
    }

    void setIdentifier(String moduleId) throws IllegalAccessException {
        if (this.identifier != null) throw new IllegalAccessException("Cannot change the identifier in a module");
        this.identifier = moduleId;
    }
}
