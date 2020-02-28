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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package co.hygames.gamebox;

import co.hygames.gamebox.exceptions.module.InvalidModuleException;
import co.hygames.gamebox.module.local.LocalModule;
import co.hygames.gamebox.module.local.LocalModuleData;
import co.hygames.gamebox.module.local.VersionedModule;
import co.hygames.gamebox.utilities.FileUtility;
import co.hygames.gamebox.utilities.GameBoxYmlBuilder;
import co.hygames.gamebox.utilities.ModuleUtility;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GameBoxSettings {
    public String LANGUAGE_FILE;
    private GameBox instance;
    private static VersionedModule gameBoxModuleInfo;

    public GameBoxSettings(GameBox instance) {
        this.instance = instance;
        this.load();
    }

    private void load() {
        this.loadModuleInfo();
    }

    private void loadModuleInfo() {
        try {
            InputStream moduleYml = FileUtility.getResource("module.yml");
            LocalModuleData moduleData = GameBoxYmlBuilder.buildLocalModuleDataYml().loadAs(new InputStreamReader(moduleYml), LocalModuleData.class);
            ModuleUtility.validateLocalModuleData(moduleData);
            ModuleUtility.fillDefaults(moduleData);
            gameBoxModuleInfo = new LocalModule(moduleData);
        } catch (IOException | InvalidModuleException e) {
            e.printStackTrace();
        }
    }

    public static VersionedModule getGameBoxModuleInfo() {
        return gameBoxModuleInfo;
    }
}
