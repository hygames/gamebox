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
import co.hygames.gamebox.module.settings.ModulesSettings;
import co.hygames.gamebox.utilities.FileUtility;
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
    private Set<String> hasUpdateAvailable = new HashSet<>();

    public ModulesManager(GameBox gameBox) {
        this.gameBox = gameBox;
        connectToCloud();
        prepareFiles();
        loadModuleSettings();
        collectLocalModules();
        collectLocalModuleUpdates();
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
        } catch (CloudException e) {
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
        List<File> jars = FileUtility.getAllJars(modulesDir);
        for (File jar : jars) {
            try {
                LocalModule localModule = LocalModule.fromFile(jar);
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
