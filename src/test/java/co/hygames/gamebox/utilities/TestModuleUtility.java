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

import co.hygames.gamebox.GameBox;
import co.hygames.gamebox.exceptions.module.ModuleDependencyException;
import co.hygames.gamebox.module.data.LocalModuleData;
import co.hygames.gamebox.module.local.LocalModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestModuleUtility {
    private static Map<String, LocalModule> modules = new HashMap<>();
    private static final Yaml YAML;
    static {
        Constructor constructor = new Constructor(LocalModuleData.class);
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        YAML = new Yaml(constructor, representer);
    }

    @BeforeAll
    public static void prepare() throws FileNotFoundException {
        for (int i = 1; i < 6; i++) {
            LocalModuleData data = YAML.loadAs(new FileReader(new File("src/test/resources/test_local_module_" + i + ".yml")), LocalModuleData.class);
            modules.put(data.getId(), LocalModule.fromLocalModuleData(data));
        }
        // add GameBox as a local module
        modules.put(GameBox.moduleId, LocalModule.fromLocalModuleData(new LocalModuleData()
                .withId(GameBox.moduleId)
                .withVersion("1.0.1")
        ));
    }

    @Test
    @DisplayName("Check dependent modules")
    public void checkDependencies() {
        Map<String, LocalModule> modules = new HashMap<>(getModules());
        try {
            ModuleUtility.checkDependencies(modules);
        } catch (ModuleDependencyException e) {
            e.printStackTrace();
        }
        assertEquals(getModules().size(), modules.size());
    }

    private Map<String, LocalModule> getModules() {
        return Collections.unmodifiableMap(modules);
    }
}
