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
    protected ModuleLanguage moduleLanguage;

    public GameBoxModule(GameBox gameBox, String identifier) {
        this.gameBox = gameBox;
        this.identifier = identifier;
    }

    public abstract File getModuleFolder();

    public abstract File getLanguageFolder();

    public String getIdentifier() {
        return this.identifier;
    }
}
