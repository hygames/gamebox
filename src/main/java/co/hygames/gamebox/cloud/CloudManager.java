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

package co.hygames.gamebox.cloud;

import co.hygames.gamebox.GameBox;
import co.hygames.gamebox.cloud.data.CloudModuleData;
import co.hygames.gamebox.database.Callback;
import co.hygames.gamebox.exceptions.module.ModuleCloudException;
import co.hygames.gamebox.module.LocalModule;
import co.hygames.gamebox.utilities.versioning.SemanticVersion;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niklas Eicker
 */
public class CloudManager {
    private static final String API_BASE_URL = "https://api.hygames.co/gamebox/";
    private static final Gson GSON = new Gson();

    private GameBox gameBox;
    private Map<String, CloudModuleData> cloudContent = new HashMap<>();
    private Map<String, Callback<LocalModule>> downlodingModules = new HashMap<>();

    public CloudManager(GameBox gameBox) {
        this.gameBox = gameBox;
    }

    public void updateCloudContent() throws ModuleCloudException {
        cloudContent.clear();
        try {
            CloudModuleData[] modulesData = GSON.fromJson(new InputStreamReader(new URL(API_BASE_URL + "modules").openStream()), CloudModuleData[].class);
            for (CloudModuleData moduleData : modulesData) {
                cloudContent.put(String.valueOf(moduleData.getId()), moduleData);
            }
        } catch (IOException e) {
            throw new ModuleCloudException(e);
        }
    }

    public void updateCloudModule(String moduleId) throws ModuleCloudException {
        try {
            CloudModuleData moduleData = GSON.fromJson(new InputStreamReader(new URL(API_BASE_URL + "modules/" + moduleId).openStream()), CloudModuleData.class);
            cloudContent.put(String.valueOf(moduleData.getId()), moduleData);
        } catch (IOException e) {
            throw new ModuleCloudException(e);
        }
    }

    public CloudModuleData getModuleData(String moduleID) {
        return cloudContent.get(moduleID);
    }

    private boolean hasUpdate(LocalModule localModule) throws ParseException {
        CloudModuleData cloudModule = cloudContent.get(localModule.getModuleId());
        if (cloudModule == null) {
            // might be local module
            return false;
        }
        SemanticVersion localVersion = new SemanticVersion(localModule.getVersionData().getVersion());
        SemanticVersion newestCloudVersion = new SemanticVersion(cloudModule.getLatestVersion());
        return newestCloudVersion.isUpdateFor(localVersion);
    }
}
