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

package co.hygames.gamebox.module.local;

import co.hygames.gamebox.module.data.CloudModuleData;
import co.hygames.gamebox.module.data.VersionData;
import co.hygames.gamebox.exceptions.module.InvalidModuleException;
import co.hygames.gamebox.exceptions.module.ModuleVersionException;
import co.hygames.gamebox.module.data.LocalModuleData;
import co.hygames.gamebox.module.data.VersionedModule;
import co.hygames.gamebox.utilities.ModuleUtility;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.jar.JarFile;

/**
 * @author Niklas Eicker
 */
public class LocalModule implements VersionedModule {
    private static final Gson GSON = new Gson();

    private String moduleId;
    private String name;
    private String description;
    private List<String> authors;
    private VersionData versionData;
    private File moduleJar;

    private LocalModule(String id, VersionData version) {
        this.moduleId = id;
        this.versionData = version;
    }

    private static LocalModule fromLocalModuleData(LocalModuleData moduleData) {
        LocalModule instance = new LocalModule(moduleData.getId(), new VersionData().withVersion(moduleData.getVersion()).withDependencies(moduleData.getDependencies()));
        instance.setName(moduleData.getName());
        instance.setAuthors(moduleData.getAuthors());
        instance.setDescription(moduleData.getDescription());
        return instance;
    }

    private static LocalModule fromCloudModuleData(CloudModuleData moduleData) throws ModuleVersionException {
        return fromCloudModuleData(moduleData, moduleData.getLatestVersion());
    }

    private static LocalModule fromCloudModuleData(CloudModuleData moduleData, String version) throws ModuleVersionException {
        VersionData matchingVersion = null;
        for (VersionData versionData : moduleData.getVersions()) {
            if (versionData.getVersion().equals(version)) {
                matchingVersion = versionData;
                break;
            }
        }
        if (matchingVersion == null) {
            throw new ModuleVersionException("Version '" + version + "' cannot be found");
        }
        LocalModule instance =  new LocalModule(moduleData.getId(), matchingVersion);
        instance.setName(moduleData.getName());
        instance.setAuthors(moduleData.getAuthors());
        instance.setDescription(moduleData.getDescription());
        return instance;
    }

    public static LocalModule fromFile(File file) throws InvalidModuleException {
        JarFile jarFile;
        LocalModule localModule = null;
        try {
            jarFile = new JarFile(file);
            InputStream moduleJson = jarFile.getInputStream(jarFile
                    .stream()
                    .filter(e -> e.getName().equals("module.json"))
                    .findFirst()
                    .orElseThrow(() -> new InvalidModuleException("No 'module.json' found for " + file.getName())));
            LocalModuleData moduleData = GSON.fromJson(new InputStreamReader(moduleJson), LocalModuleData.class);
            ModuleUtility.validateLocalModuleData(moduleData);
            ModuleUtility.fillDefaults(moduleData);
            jarFile.close();
            localModule = fromLocalModuleData(moduleData);
            localModule.setModuleJar(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localModule;
    }

    @Override
    public String getModuleId() {
        return this.moduleId;
    }

    @Override
    public VersionData getVersionData() {
        return this.versionData;
    }

    public File getModuleJar() {
        return moduleJar;
    }

    public void setModuleJar(File moduleJar) {
        this.moduleJar = moduleJar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
