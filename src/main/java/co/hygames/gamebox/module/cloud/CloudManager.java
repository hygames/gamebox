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

package co.hygames.gamebox.module.cloud;

import co.hygames.gamebox.GameBox;
import co.hygames.gamebox.database.Callback;
import co.hygames.gamebox.exceptions.module.GameBoxCloudException;
import co.hygames.gamebox.exceptions.module.InvalidModuleException;
import co.hygames.gamebox.module.data.CloudModuleData;
import co.hygames.gamebox.module.data.ModuleInfo;
import co.hygames.gamebox.module.local.LocalModule;
import co.hygames.gamebox.utilities.versioning.SemanticVersion;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niklas Eicker
 */
public class CloudManager {
    private static final String API_BASE_URL = "https://api.hygames.co/gamebox/";
    //private static final String API_BASE_URL = "http://127.0.0.1:4000/gamebox/";
    private static final Gson GSON = new Gson();

    private GameBox gameBox;
    private Map<String, CloudModuleData> cloudContent = new HashMap<>();
    private Map<String, Thread> downloadingModules = new HashMap<>();

    public CloudManager(GameBox gameBox) {
        this.gameBox = gameBox;
    }

    public void updateCloudContent() throws GameBoxCloudException {
        try {
            CloudModuleData[] modulesData = GSON.fromJson(new InputStreamReader(new URL(API_BASE_URL + "modules").openStream()), CloudModuleData[].class);
            cloudContent.clear();
            for (CloudModuleData moduleData : modulesData) {
                cloudContent.put(moduleData.getId(), moduleData);
                gameBox.getLogger().info("got moduledata for id:'" + moduleData.getId() + "'");
            }
        } catch (UnknownHostException e) {
            throw new GameBoxCloudException("Connection problem to the cloud. Please make sure that you are connected to the internet.", e);
        } catch (IOException e) {
            throw new GameBoxCloudException(e);
        }
    }

    public void updateCloudModule(String moduleId) throws GameBoxCloudException {
        try {
            CloudModuleData moduleData = GSON.fromJson(new InputStreamReader(new URL(API_BASE_URL + "modules/" + moduleId).openStream()), CloudModuleData.class);
            cloudContent.put(String.valueOf(moduleData.getId()), moduleData);
        } catch (IOException e) {
            throw new GameBoxCloudException(e);
        }
    }

    public CloudModuleData getModuleData(String moduleID) throws GameBoxCloudException {
        CloudModuleData cloudModuleData = cloudContent.get(moduleID);
        if (cloudModuleData == null) throw new GameBoxCloudException("No moduledata found for ID '" + moduleID + "'");
        return cloudModuleData;
    }

    public boolean hasUpdate(LocalModule localModule) throws ParseException {
        CloudModuleData cloudModule = cloudContent.get(localModule.getId());
        if (cloudModule == null) {
            // might be local module
            return false;
        }
        SemanticVersion localVersion = new SemanticVersion(localModule.getVersionData().getVersion());
        SemanticVersion newestCloudVersion = new SemanticVersion(cloudModule.getLatestVersion());
        return newestCloudVersion.isUpdateFor(localVersion);
    }

    public void downloadModule(CloudModuleData cloudModule, String version, Callback<ModuleInfo> callback) {
        final String fileName = cloudModule.getId() + "@" + version + ".jar";
        try {
            final File outputFile = new File(gameBox.getModulesManager().getModulesDir(), fileName);
            if (outputFile.isFile()) {
                gameBox.getLogger().info("Module " + cloudModule.getName() + " @" + version + " already exists...");
                gameBox.getLogger().info("   skipping download of '" + fileName + "'");
                try {
                    LocalModule localModule = LocalModule.fromJar(outputFile);
                    callback.success(localModule);
                } catch (InvalidModuleException e) {
                    callback.fail(null, e);
                }
                return;
            }
            final URL fileUrl = new URL(API_BASE_URL + "assets/modules/" + fileName);

            // download
            downloadingModules.put(fileName, new Thread(() -> {
                try (BufferedInputStream in = new BufferedInputStream(fileUrl.openStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                    LocalModule localModule = LocalModule.fromJar(outputFile);
                    callback.success(localModule);
                } catch (IOException | InvalidModuleException exception) {
                    callback.fail(null, exception);
                } finally {
                    downloadingModules.remove(fileName);
                }
            }));
            downloadingModules.get(fileName).start();
        } catch (MalformedURLException e) {
            callback.fail(null, e);
        }
    }

    public boolean isDownloading() {
        return !downloadingModules.isEmpty();
    }
}
