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

package co.hygames.gamebox.module;

import co.hygames.gamebox.cloud.data.VersionData;

public class ModuleSettings {
    private String id;
    private VersionData versionData;
    private boolean enabled;

    public ModuleSettings(String id, VersionData versionData, boolean enabled) {
        this.id = id;
        this.versionData = versionData;
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public VersionData getVersionData() {
        return versionData;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        // ToDo: disable or enable the module here or separate?
    }
}
