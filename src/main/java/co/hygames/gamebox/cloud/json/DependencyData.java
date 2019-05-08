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

package co.hygames.gamebox.cloud.json;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Niklas Eicker
 */
public class DependencyData implements Serializable {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("versionRange")
    @Expose
    private String versionRange;

    private final static long serialVersionUID = 3080774369300795773L;

    public DependencyData() {
    }

    public DependencyData(String name, String versionRange) {
        super();
        this.name = name;
        this.versionRange = versionRange;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DependencyData withName(String name) {
        this.name = name;
        return this;
    }

    public String getVersionRange() {
        return versionRange;
    }

    public void setVersionRange(String versionRange) {
        this.versionRange = versionRange;
    }

    public DependencyData withVersionRange(String versionRange) {
        this.versionRange = versionRange;
        return this;
    }
}
