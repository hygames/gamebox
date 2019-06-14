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
import co.hygames.gamebox.exceptions.module.ModuleDependencyException;
import co.hygames.gamebox.exceptions.module.ModuleVersionException;
import co.hygames.gamebox.module.cloud.CloudManager;
import co.hygames.gamebox.exceptions.module.GameBoxCloudException;
import co.hygames.gamebox.module.data.LocalModuleData;
import co.hygames.gamebox.module.local.LocalModule;
import co.hygames.gamebox.module.settings.ModulesSettings;
import co.hygames.gamebox.utilities.FileUtility;
import co.hygames.gamebox.utilities.ModuleUtility;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.text.ParseException;
import java.util.*;

/**
 * @author Niklas Eicker
 */
public class ModulesManager {
    private GameBox gameBox;
    private CloudManager cloudManager;
    private File modulesDir;
    private File modulesFile;
    private ModulesSettings modulesSettings;
    private Map<String, LocalModule> localModules = new HashMap<>();
    private Map<String, GameBoxModule> loadedModules = new HashMap<>();
    private Set<String> hasUpdateAvailable = new HashSet<>();

    public ModulesManager(GameBox gameBox) {
        this.gameBox = gameBox;
        connectToCloud();
        prepareFiles();
        loadModuleSettings();
        collectLocalModules();
        checkDependencies();
        //collectLocalModuleUpdates();
        loadLocalModules();
    }

    private void checkDependencies() {
        try {
            ModuleUtility.checkDependencies(this.localModules);
        } catch (ModuleDependencyException e) {
            e.printStackTrace();
            // ToDo: info about version range? Link to docs
        }
    }

    private void loadLocalModules() {
        Map<String, LocalModule> modulesToLoad = localModules;
        // GameBox doesn't need to be loaded
        modulesToLoad.remove(GameBox.moduleId);
        List<LocalModule> sortedModules = ModuleUtility.sortModulesByDependencies(modulesToLoad.values());
        for (LocalModule localModule : sortedModules) {
            gameBox.getLogger().fine("Loading module '" + localModule.getName() + "'...");
            if (loadedModules.containsKey(localModule.getModuleId())) {
                gameBox.getLogger().fine("    already loaded! Skipping...");
                continue;
            }
            loadModule(localModule);
        }
    }

    private void loadModuleSettings() {
        //Yaml yaml = new Yaml(new Constructor(ModulesSettings.class));
        Constructor constructor = new Constructor(ModulesSettings.class);
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(constructor, representer);
        try {
            this.modulesSettings = yaml.loadAs(new FileInputStream(modulesFile), ModulesSettings.class);
            // prevent NPE for empty modules file
            modulesSettings.setModules(modulesSettings.getModules() == null ? new HashMap<>() : modulesSettings.getModules());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void connectToCloud() {
        this.cloudManager = new CloudManager(gameBox);
        try {
            cloudManager.updateCloudContent();
        } catch (GameBoxCloudException e) {
            gameBox.getLogger().severe("Error while attempting to load cloud content");
            e.printStackTrace();
        }
    }

    private void prepareFiles() {
        modulesDir = new File(gameBox.getDataFolder(), "modules");
        if (modulesDir.mkdirs()) {
            gameBox.getLogger().info("Created Modules Directory");
        }
        modulesFile = new File(modulesDir, "modules.yml");
        if (!modulesFile.isFile()) {
            try {
                FileUtility.copyResource("modules/modules.yml", modulesFile);
                gameBox.getLogger().info("Copied default 'modules.yml' file");
            } catch (IOException e) {
                gameBox.getLogger().warning("Error while attempting to create a new module settings file:");
                e.printStackTrace();
            }
        }
    }

    private void collectLocalModules() {
        // ToDo: check the module settings! Ignore disabled modules
        List<File> jars = FileUtility.getAllJars(modulesDir);
        for (File jar : jars) {
            try {
                LocalModule localModule = LocalModule.fromJar(jar);
                localModules.put(localModule.getModuleId(), localModule);
            } catch (InvalidModuleException e) {
                gameBox.getLogger().severe("Error while loading module from the jar '" + jar.getName() + "'");
                e.printStackTrace();
                gameBox.getLogger().severe("Skipping...");
            }
        }
        // add GameBox as a local module
        localModules.put(GameBox.moduleId, LocalModule.fromLocalModuleData(new LocalModuleData()
                .withId(GameBox.moduleId)
                .withVersion(getClass().getPackage().getImplementationVersion())
                .withName(getClass().getPackage().getImplementationTitle())
        ));
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
        } catch (ModuleVersionException | GameBoxCloudException e) {
            e.printStackTrace();
            return;
        }
    }

    public void installModule(String moduleId, String version) {
        try {
            LocalModule localModule = LocalModule.fromCloudModuleData(cloudManager.getModuleData(moduleId), version);
            installModule(localModule);
        } catch (ModuleVersionException | GameBoxCloudException e) {
            e.printStackTrace();
        }
    }

    public void installModule(LocalModule localModule) {
        cloudManager.downloadModule(localModule, new Callback<LocalModule>() {
            @Override
            public void success(LocalModule result) {
                gameBox.getLogger().info("Download complete. Loading the module...");
                localModules.put(result.getModuleId(), result);
                addModuleToSettings(result.getModuleId());
                // ToDo: should be careful here with dependencies... check for any and if a reload is needed do it automatically, or ask the source of the installation for an OK
                loadModule(result);
            }

            @Override
            public void fail(LocalModule defaultResult, Exception exception) {
                gameBox.getLogger().severe("Error while downloading module '" + defaultResult.getName() + "' version " + defaultResult.getVersion().toString());
                if (exception != null) exception.printStackTrace();
            }
        });
    }

    private void loadModule(LocalModule localModule) {
        GameBoxModule instance;
        try {
            gameBox.getLogger().info("    instantiating");
            instance = (GameBoxModule) FileUtility.getClassesFromJar(localModule.getModuleJar(), GameBoxModule.class).get(0).newInstance();
            gameBox.getLogger().info("    done.");
        } catch (InstantiationException | IllegalAccessException e) {
            gameBox.getLogger().warning("Failed to instantiate module '" + localModule.getName() + "' from the jar '" + localModule.getModuleJar().getName() + "'");
            e.printStackTrace();
            unloadModule(localModule);
            return;
        }
        instance.setGameBox(gameBox);
        instance.setModuleData(localModule);
        try {
            instance.onEnable();
        } catch (Exception e) { // catch all and skip module if there is an exception in onEnable
            gameBox.getLogger().severe("Exception while enabling " + localModule.getName() + " @" + localModule.getVersion().toString() + ":");
            e.printStackTrace();
            unloadModule(localModule);
            return;
        }
        loadedModules.put(localModule.getModuleId(), instance);
    }

    private void unloadModule(LocalModule localModule) {
        // ToDo: unload parent modules first!
        GameBoxModule instance = loadedModules.get(localModule.getModuleId());
        if (instance != null) {
            try {
                instance.onDisable();
            } catch (Exception e) {
                gameBox.getLogger().severe("Exception while disabling " + localModule.getName() + " @" + localModule.getVersion().toString() + ":");
                e.printStackTrace();
            }
        }
    }

    private void addModuleToSettings(String moduleId) {
        Map<String, ModulesSettings.ModuleSettings> currentSettings = modulesSettings.getModules();
        currentSettings.putIfAbsent(moduleId, new ModulesSettings.ModuleSettings());
        modulesSettings.setModules(currentSettings);
        dumpModuleSettings();
    }

    private void removeModuleFromSettings(String moduleId) {
        Map<String, ModulesSettings.ModuleSettings> currentSettings = modulesSettings.getModules();
        currentSettings.remove(moduleId);
        modulesSettings.setModules(currentSettings);
        dumpModuleSettings();
    }

    private void updateModuleSettings(String moduleId, ModulesSettings.ModuleSettings settings) {
        Map<String, ModulesSettings.ModuleSettings> currentSettings = modulesSettings.getModules();
        currentSettings.put(moduleId, settings);
        modulesSettings.setModules(currentSettings);
        dumpModuleSettings();
    }

    private void dumpModuleSettings() {
        Constructor constructor = new Constructor(ModulesSettings.class);
        Yaml yaml = new Yaml(constructor);
        try {
            yaml.dump(modulesSettings, new FileWriter(modulesFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the instance of a module by its ID
     * @param moduleID the module to get
     * @return module instance or null
     */
    public GameBoxModule getModuleInstance(String moduleID) {
        return loadedModules.get(moduleID);
    }
}
