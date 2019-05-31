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

import co.hygames.gamebox.module.GameBoxModule;
import co.hygames.gamebox.module.data.*;
import co.hygames.gamebox.exceptions.module.InvalidModuleException;
import co.hygames.gamebox.exceptions.module.ModuleVersionException;
import co.hygames.gamebox.utilities.FileUtility;
import co.hygames.gamebox.utilities.ModuleUtility;
import co.hygames.gamebox.utilities.versioning.SemanticVersion;
import com.google.gson.Gson;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;
import java.util.jar.JarFile;

/**
 * @author Niklas Eicker
 */
public class LocalModule implements VersionedModule {
    private static final Gson GSON = new Gson();
    private static final Yaml YAML;
    static {
        Constructor constructor = new Constructor(LocalModuleData.class);
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        YAML = new Yaml(constructor, representer);
    }

    private String moduleId;
    private String name;
    private String description;
    private List<String> authors;
    private VersionData versionData;
    private SemanticVersion version;
    private File moduleJar;

    private LocalModule(String id, VersionData version) {
        this.moduleId = id;
        this.versionData = version;
    }

    public static LocalModule fromLocalModuleData(LocalModuleData moduleData) {
        LocalModule instance = new LocalModule(moduleData.getId(), new VersionData().withVersion(moduleData.getVersion()).withDependencies(moduleData.getDependencies()));
        return instance.fillInfo(moduleData);
    }

    public static LocalModule fromCloudModuleData(CloudModuleData moduleData) throws ModuleVersionException {
        return fromCloudModuleData(moduleData, moduleData.getLatestVersion());
    }

    public static LocalModule fromCloudModuleData(CloudModuleData moduleData, String version) throws ModuleVersionException {
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
        return instance.fillInfo(moduleData);
    }

    private LocalModule fillInfo(ModuleData moduleData) {
        this.setName(moduleData.getName());
        this.setAuthors(moduleData.getAuthors());
        this.setDescription(moduleData.getDescription());
        try {
            this.setVersion(new SemanticVersion(this.getVersionData().getVersion()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static LocalModule fromFile(File file) throws InvalidModuleException {
        JarFile jarFile;
        LocalModule localModule = null;
        try {
            jarFile = new JarFile(file);
            InputStream moduleFile = jarFile.getInputStream(jarFile
                    .stream()
                    .filter(e -> e.getName().equals("module.yml"))
                    .findFirst()
                    .orElseThrow(() -> new InvalidModuleException("No 'module.yml' found for " + file.getName())));
            LocalModuleData moduleData = YAML.loadAs(new InputStreamReader(moduleFile), LocalModuleData.class);
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

    public void setModuleJar(File moduleJar) throws InvalidModuleException {
        List<Class<?>> clazzes = FileUtility.getClassesFromJar(moduleJar, GameBoxModule.class);
        if (clazzes.size() < 1) throw new InvalidModuleException("No class extending GameBoxModule was found in '" + getName() + "'");
        if (clazzes.size() > 1) throw new InvalidModuleException("More then one class extending GameBoxModule was found in '" + getName() + "'");
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

    public SemanticVersion getVersion() {
        return this.version;
    }

    public void setVersion(SemanticVersion version) {
        this.version = version;
    }

    public boolean sameIdAndVersion(LocalModule localModule) {
        return this.moduleId.equals(localModule.getModuleId()) && this.getVersion().equals(localModule.getVersion());
    }
}
