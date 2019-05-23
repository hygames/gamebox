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

package co.hygames.gamebox.utilities;

import co.hygames.gamebox.exceptions.module.InvalidModuleException;
import co.hygames.gamebox.module.data.LocalModuleData;
import co.hygames.gamebox.utilities.versioning.SemanticVersion;

import java.text.ParseException;

public class ModuleUtility {
    public static void validateLocalModuleData(LocalModuleData localModuleData) throws InvalidModuleException {
        if (localModuleData.getId() == null || localModuleData.getId().replaceAll("\\s","").isEmpty()) {
            throw new InvalidModuleException("No valid module id found");
        }
        try {
            new SemanticVersion(localModuleData.getVersion());
        } catch (ParseException e) {
            throw new InvalidModuleException("No valid version found", e);
        }
    }

    public static void fillDefaults(LocalModuleData localModuleData) {
        if (localModuleData.getName() == null || localModuleData.getName().replaceAll("\\s","").isEmpty()) {
            localModuleData.setName(localModuleData.getId());
        }
    }
}
