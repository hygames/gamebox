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
import co.hygames.gamebox.utilities.FileUtility;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niklas Eicker
 */
public class ModulesManager {
    private GameBox gameBox;
    private File modulesDir;
    private File modulesSettings;
    private Map<String, LocalModule> localModules = new HashMap<>();

    public ModulesManager(GameBox gameBox) {
        this.gameBox = gameBox;
        modulesDir = new File(gameBox.getDataFolder(), "modules");
        final boolean newModulesDir = modulesDir.mkdirs();
        if (newModulesDir) {
            gameBox.getLogger().info("Created Modules Directory");
        }
        modulesSettings = new File(modulesDir, "modules.yml");
        final boolean needNewSettingsFile = !modulesSettings.isFile();
        if (needNewSettingsFile) {
            try {
                modulesSettings.createNewFile();
                gameBox.getLogger().info("Created a new module settings file");
                registerAllModules();
            } catch (IOException e) {
                gameBox.getLogger().warning("Error while attempting to create a new module settings file:");
                e.printStackTrace();
            }
        }
        collectLocalModules();
    }

    private void collectLocalModules() {
        List<File> jars = FileUtility.getAllJars(modulesDir);

    }

    private void registerAllModules() {
        List<File> jars = FileUtility.getAllJars(modulesDir);

        // ToDo: read version, dependencies into module settings and save defaults in module settings file
    }

    public File getModulesDir() {
        return this.modulesDir;
    }
}
