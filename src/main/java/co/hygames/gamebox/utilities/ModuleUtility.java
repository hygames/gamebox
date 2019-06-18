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
import co.hygames.gamebox.exceptions.module.ModuleDependencyException;
import co.hygames.gamebox.module.data.DependencyData;
import co.hygames.gamebox.module.data.LocalModuleData;
import co.hygames.gamebox.module.local.LocalModule;
import co.hygames.gamebox.utilities.versioning.SemanticVersion;
import co.hygames.gamebox.utilities.versioning.VersionRangeUtility;

import java.text.ParseException;
import java.util.*;

public class ModuleUtility {

    public static void validateLocalModuleData(LocalModuleData localModuleData) throws InvalidModuleException {
        if (localModuleData.getId() == null || localModuleData.getId().replaceAll("\\s","").isEmpty()) {
            throw new InvalidModuleException("No valid module id found");
        }
        try {
            new SemanticVersion(localModuleData.getVersion());
        } catch (ParseException e) {
            throw new InvalidModuleException("No valid semantic version found", e);
        }
    }

    public static void fillDefaults(LocalModuleData localModuleData) {
        if (localModuleData.getName() == null || localModuleData.getName().replaceAll("\\s","").isEmpty()) {
            localModuleData.setName(localModuleData.getId());
        }
    }

    public static List<LocalModule> sortModulesByDependencies(Collection<LocalModule> modules) {
        List<LocalModule> sortedModules = new ArrayList<>(modules);
        sortedModules.sort((localModule1, localModule2) -> {
            List<DependencyData> dependencyData1 = localModule1.getVersionData().getDependencies();
            List<DependencyData> dependencyData2 = localModule2.getVersionData().getDependencies();
            if (dependencyData1.isEmpty() && dependencyData2.isEmpty()) {
                return 0;
            }
            if (dependencyData1.isEmpty()) {
                return 1;
            }
            if (dependencyData2.isEmpty()) {
                return -1;
            }
            if (dependencyData1.stream().anyMatch(dep -> dep.getId().equals(localModule2.getModuleId()))) {
                return -1;
            }
            if (dependencyData2.stream().anyMatch(dep -> dep.getId().equals(localModule1.getModuleId()))) {
                return 1;
            }
            return 0;
        });
        return sortedModules;
    }

    public static void checkDependencies(Map<String, LocalModule> localModules) throws ModuleDependencyException {
        List<String> log = new ArrayList<>();
        boolean foundIssue = true;
        while (foundIssue && !localModules.isEmpty()) {
            foundIssue = false;
            Iterator<LocalModule> modules = localModules.values().iterator();
            while (modules.hasNext()) {
                LocalModule currentModule = modules.next();
                for (DependencyData dependencyData : currentModule.getVersionData().getDependencies()) {
                    LocalModule dependency = localModules.get(dependencyData.getId());
                    if (dependency == null) {
                        if (dependencyData.isSoftDependency()) {
                            continue;
                        }
                        log.add("The dependency '" + dependencyData.getId()
                                + "' is missing for the module '" + currentModule.getModuleId() + "'");
                        log.add("   " + currentModule.getModuleId() + " asks for a version in the range '"
                                + dependencyData.getVersionConstrain() + "'" );
                        foundIssue = true;
                        modules.remove();
                        break;
                    }
                    if (dependencyData.getVersionConstrain() == null || dependencyData.getVersionConstrain().isEmpty()) continue;
                    try {
                        if (!VersionRangeUtility.isInVersionRange(dependency.getVersion(), dependencyData.getVersionConstrain())) {
                            if (dependencyData.isSoftDependency()) {
                                continue;
                            }
                            log.add("'" + currentModule.getModuleId() + "' asks for '"
                                    + dependency.getModuleId() + "' with the version constrain '"
                                    + dependencyData.getVersionConstrain() + "'");
                            log.add("   The installed version is '" + dependency.getVersion().toString() + "'" );
                            foundIssue = true;
                            modules.remove();
                            break;
                        }
                    } catch (ParseException e) {
                        // can be ignored, since the version ranges are parsed before
                    }
                }
            }
        }
        if (!log.isEmpty()) throw new ModuleDependencyException(String.join("\n", log));
    }
}
