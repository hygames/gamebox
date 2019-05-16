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

package co.hygames.gamebox.cloud.data;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Niklas Eicker
 */
public class ModuleData implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("author")
    @Expose
    private String author;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("sourceUrl")
    @Expose
    private String sourceUrl;

    @SerializedName("latestVersion")
    @Expose
    private String latestVersion;

    @SerializedName("lastUpdateAt")
    @Expose
    private Long lastUpdateAt;

    @SerializedName("versions")
    @Expose
    private List<VersionData> versions = null;

    private final static long serialVersionUID = 4719087577866667965L;

    public ModuleData() {
    }

    public ModuleData(String id, String author, String name, String description, List<VersionData> versions) {
        this.id = id;
        this.author = author;
        this.name = name;
        this.description = description;
        this.versions = versions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ModuleData withId(String id) {
        this.id = id;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ModuleData withAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModuleData withName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ModuleData withDescription(String description) {
        this.description = description;
        return this;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public ModuleData withSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
        return this;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public ModuleData withLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
        return this;
    }

    public Long getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Long lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public ModuleData withLastUpdateAt(Long lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
        return this;
    }

    public List<VersionData> getVersions() {
        return versions;
    }

    public void setVersions(List<VersionData> versions) {
        this.versions = versions;
    }

    public ModuleData withVersions(List<VersionData> versions) {
        this.versions = versions;
        return this;
    }
}
