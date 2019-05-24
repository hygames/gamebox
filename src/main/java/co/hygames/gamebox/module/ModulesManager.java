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
import co.hygames.gamebox.exceptions.module.InvalidModuleException;
import co.hygames.gamebox.module.cloud.CloudManager;
import co.hygames.gamebox.exceptions.module.ModuleCloudException;
import co.hygames.gamebox.module.local.LocalModule;
import co.hygames.gamebox.utilities.FileUtility;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * @author Niklas Eicker
 */
public class ModulesManager {
    private GameBox gameBox;
    private CloudManager cloudManager;
    private File modulesDir;
    private File modulesSettings;
    private Map<String, LocalModule> localModules = new HashMap<>();
    private Set<String> hasUpdateAvailable = new HashSet<>();

    public ModulesManager(GameBox gameBox) {
        this.gameBox = gameBox;
        connectToCloud();
        loadFiles();
        collectLocalModules();
        collectLocalModuleUpdates();
    }

    private void collectLocalModuleUpdates() {
        hasUpdateAvailable.clear();
        for (String moduleId : localModules.keySet()) {
            try {
                if (cloudManager.hasUpdate(localModules.get(moduleId))) {
                    hasUpdateAvailable.add(moduleId);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectToCloud() {
        this.cloudManager = new CloudManager(gameBox);
        try {
            cloudManager.updateCloudContent();
        } catch (ModuleCloudException e) {
            gameBox.getLogger().severe("Error while attempting to load cloud content");
            e.printStackTrace();
        }
    }

    private void loadFiles() {
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
    }

    private void collectLocalModules() {
        List<File> jars = FileUtility.getAllJars(modulesDir);
        for (File jar : jars) {
            try {
                LocalModule localModule = LocalModule.fromFile(jar);
                List<Class<?>> clazzes = FileUtility.getClassesFromJar(jar, GameBoxModule.class);
                if (clazzes.size() < 1) throw new InvalidModuleException("No class extending GameBoxModule was found in '" + localModule.getName() + "'");
                if (clazzes.size() > 1) throw new InvalidModuleException("More then one class extending GameBoxModule was found in '" + localModule.getName() + "'");
                localModules.put(localModule.getModuleId(), localModule);
            } catch (InvalidModuleException e) {
                gameBox.getLogger().severe("Error while loading module from the jar '" + jar.getName() + "'");
                e.printStackTrace();
                gameBox.getLogger().severe("Skipping...");
            }
        }
    }

    private void registerAllModules() {
        List<File> jars = FileUtility.getAllJars(modulesDir);

        // ToDo: read version, dependencies into module settings and save defaults in module settings file
    }

    public File getModulesDir() {
        return this.modulesDir;
    }
}
