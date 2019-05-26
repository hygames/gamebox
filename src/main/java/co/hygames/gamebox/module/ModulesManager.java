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
import co.hygames.gamebox.database.Callback;
import co.hygames.gamebox.exceptions.module.InvalidModuleException;
import co.hygames.gamebox.exceptions.module.ModuleVersionException;
import co.hygames.gamebox.module.cloud.CloudManager;
import co.hygames.gamebox.exceptions.module.CloudException;
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
        prepareFiles();
        collectLocalModules();
        collectLocalModuleUpdates();
    }

    private void connectToCloud() {
        this.cloudManager = new CloudManager(gameBox);
        try {
            cloudManager.updateCloudContent();
        } catch (CloudException e) {
            gameBox.getLogger().severe("Error while attempting to load cloud content");
            e.printStackTrace();
        }
    }

    private void prepareFiles() {
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
            } catch (IOException e) {
                gameBox.getLogger().warning("Error while attempting to create a new module settings file:");
                e.printStackTrace();
            }
        }
    }

    private void collectLocalModules() {
        List<File> jars = new ArrayList<>();
        for (File moduleFolder : modulesDir.listFiles()) {
            if (!moduleFolder.isDirectory()) continue;
            List<File> moduleJars = FileUtility.getAllJars(moduleFolder);
            if (moduleJars.size() > 1) {
                gameBox.getLogger().warning("There seems to be more then one jar in " + moduleFolder.getName());
                gameBox.getLogger().warning("    Attempting to load " + moduleJars.get(0).getName());
                gameBox.getLogger().warning("    Please remove any jars you do not need");
                jars.add(moduleJars.get(0));
                continue;
            }
            jars.addAll(moduleJars);
        }
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

    private void registerAllLocalModules() {
        List<File> jars = FileUtility.getAllJars(modulesDir);

        // ToDo: read version, dependencies into module settings and save defaults in module settings file
    }

    public File getModulesDir() {
        return this.modulesDir;
    }

    public void installModule(String moduleId) {
        gameBox.getLogger().fine("Install module '" + moduleId +"'...");
        try {
            LocalModule localModule = LocalModule.fromCloudModuleData(cloudManager.getModuleData(moduleId));
            if (localModules.containsKey(moduleId) && localModules.get(moduleId).sameIdAndVersion(localModule)) {
                // module already installed!
                gameBox.getLogger().fine("Attempted to install already installed module '" + localModule.getModuleId() +"' @" + localModule.getVersion().toString());
                return;
            }
            installModule(localModule);
        } catch (ModuleVersionException | CloudException e) {
            e.printStackTrace();
            return;
        }
    }

    public void installModule(String moduleId, String version) {
        try {
            LocalModule localModule = LocalModule.fromCloudModuleData(cloudManager.getModuleData(moduleId), version);
            installModule(localModule);
        } catch (ModuleVersionException | CloudException e) {
            e.printStackTrace();
        }
    }

    public void installModule(LocalModule localModule) {
        cloudManager.downloadModule(localModule, new Callback<LocalModule>() {
            @Override
            public void success(LocalModule result) {
                gameBox.getLogger().info("Download complete");
            }

            @Override
            public void fail(LocalModule defaultResult, Exception exception) {
                gameBox.getLogger().severe("Error while downloading module '" + defaultResult.getName() + "' version " + defaultResult.getVersion().toString());
                if (exception != null) exception.printStackTrace();
            }
        });
    }
}
