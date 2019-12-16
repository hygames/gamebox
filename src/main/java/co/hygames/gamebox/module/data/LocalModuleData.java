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

package co.hygames.gamebox.module.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocalModuleData implements ModuleInfo, Serializable {
    private String id;
    private List<String> authors = new ArrayList<>();
    private String name;
    private String description;
    private String sourceUrl;
    private String version;
    private List<DependencyData> dependencies = new ArrayList<>();

    private final static long serialVersionUID = 8241484990221433533L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalModuleData withId(String id) {
        this.id = id;
        return this;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public LocalModuleData withAuthors(List<String> authors) {
        this.authors = authors;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalModuleData withName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalModuleData withDescription(String description) {
        this.description = description;
        return this;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public LocalModuleData withSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalModuleData withVersion(String version) {
        this.version = version;
        return this;
    }

    public List<DependencyData> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<DependencyData> dependencies) {
        this.dependencies = dependencies;
    }

    public LocalModuleData withDependencies(List<DependencyData> dependencies) {
        this.dependencies = dependencies;
        return this;
    }
}
