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

import co.hygames.gamebox.cloud.json.DependencyData;
import co.hygames.gamebox.cloud.json.ModuleData;
import co.hygames.gamebox.cloud.json.VersionData;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Niklas Eicker
 */
public class TestCloudModuleFromJson {
    private static File testCloudModuleFile;
    private static ModuleData testCloudModule;

    @BeforeAll
    public static void prepare() {
        testCloudModuleFile = new File("src/test/resources/test_cloud_module.json");
        manuallyBuildTestModule();
    }

    private static void manuallyBuildTestModule() {
        DependencyData dependency = new DependencyData().withName("gamebox").withVersionRange("~> 1.0");
        List<DependencyData> dependencies = new ArrayList<>();
        dependencies.add(dependency);
        List<VersionData> versions = new ArrayList<>();
        versions.add(new VersionData()
                .withVersion("1.0.0")
                .withDependencies(dependencies)
                .withReleaseNotes(Arrays.asList(
                        "Changes:",
                        "One change",
                        "A very long change that most probably is over a line long and thus should show what happens if the release notes are written in one long line. This is a second sentence which is also quite long and utterly useless."
                        )
                )
        );
        versions.add(new VersionData()
                .withVersion("1.1.0")
                .withDependencies(dependencies)
                .withReleaseNotes(Arrays.asList(
                        "Some updates..."
                        )
                )
        );
        testCloudModule = new ModuleData()
                .withId(1)
                .withAuthor("Nikl")
                .withName("Test module")
                .withDescription("This module is only for test purposes")
                .withVersions(versions)
        ;
    }

    @Test
    public void parseTestCloudModule() throws FileNotFoundException {
        Gson gson = new Gson();
        ModuleData fileModule = gson.fromJson(new FileReader(testCloudModuleFile), ModuleData.class);
        assertEquals(fileModule.getId(), testCloudModule.getId(),"Not the same id");
        assertEquals(fileModule.getAuthor(), testCloudModule.getAuthor(),"Not the same author");
        assertEquals(fileModule.getName(), testCloudModule.getName(),"Not the same name");
        assertEquals(fileModule.getDescription(), testCloudModule.getDescription(),"Not the same description");
        assertEquals(fileModule.getVersions().size(), testCloudModule.getVersions().size(),"Not the same number of versions");
        Iterator<VersionData> itManualModule = testCloudModule.getVersions().iterator();
        Iterator<VersionData> itFileModule = fileModule.getVersions().iterator();

        while (itManualModule.hasNext() && itFileModule.hasNext()) {
            VersionData version1 = itManualModule.next();
            VersionData version2 = itFileModule.next();
            assertEquals(version1.getVersion(), version2.getVersion(),"Versions: Not the same version");
            assertIterableEquals(version1.getReleaseNotes(), version2.getReleaseNotes(), "Versions: Not the same release notes");
            assertEquals(version1.getDependencies().size(), version2.getDependencies().size(),"Versions: Not the same number of dependencies");

            Iterator<DependencyData> itManualDependency = version1.getDependencies().iterator();
            Iterator<DependencyData> itFileDependency = version2.getDependencies().iterator();
            while (itManualDependency.hasNext() && itFileDependency.hasNext()) {
                DependencyData dependency1 = itManualDependency.next();
                DependencyData dependency2 = itFileDependency.next();
                assertEquals(dependency1.getName(), dependency2.getName(),"Dependencies: Not the same name");
                assertEquals(dependency1.getVersionRange(), dependency2.getVersionRange(),"Dependencies: Not the same version range");
            }
        }
    }
}
