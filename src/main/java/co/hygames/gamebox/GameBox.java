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

package co.hygames.gamebox;

import co.hygames.gamebox.module.ModulesManager;
import co.hygames.gamebox.module.data.VersionData;
import co.hygames.gamebox.module.data.VersionedModule;
import co.hygames.gamebox.utilities.FileUtility;
import co.hygames.gamebox.utilities.versioning.SemanticVersion;

import java.io.File;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Niklas Eicker
 */
public class GameBox {
    public static VersionedModule versionInfo;
    static {
        try {
            final String ID = "gamebox";
            final SemanticVersion VERSION = new SemanticVersion("1.0.0");
            final String NAME = "GameBox";
            final String DESCRIPTION = "GameBox is a collection of inventory games";
            final String SOURCE = "https://github.com/hygames-team/gamebox";
            final List AUTHORS = Arrays.asList("Niklas Eicker");
            final List DEPENDENCIES = Collections.EMPTY_LIST;
            final VersionData versionData = new VersionData().withVersion(VERSION.toString()).withDependencies(DEPENDENCIES);
            // This instance is simply for easy dependency checking of other modules. By handling GameBox just like
            // any other module dependency, it is made much easier for module authors to depend on a specific GameBox version
            versionInfo = new VersionedModule() {
                @Override
                public VersionData getVersionData() {
                    return versionData;
                }

                @Override
                public SemanticVersion getVersion() {
                    return VERSION;
                }

                @Override
                public String getId() {
                    return ID;
                }

                @Override
                public List<String> getAuthors() {
                    return AUTHORS;
                }

                @Override
                public String getName() {
                    return NAME;
                }

                @Override
                public String getDescription() {
                    return DESCRIPTION;
                }

                @Override
                public String getSourceUrl() {
                    return SOURCE;
                }
            };
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private static GameBox instance;
    private ModulesManager modulesManager;
    private File dataFolder;
    private File languageFolder;
    private Logger logger;
    private SemanticVersion version;

    public static void main(String args[]) {
        new GameBox().onEnable();
    }

    public void onEnable() {
        instance = this;
        // ToDo: testing! remove!
        this.dataFolder = new File("/home/nikl/Desktop/gamebox");
        FileUtility.copyDefaultLanguageFiles();
        try {
            setUp();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setUp() throws ParseException {
        this.version = new SemanticVersion(this.getClass().getPackage().getImplementationVersion());
        createLogger();
        this.modulesManager = new ModulesManager(this);
        //modulesManager.installModule("test-module");
        // ToDo: load local modules and update info about cloud modules
    }

    private void createLogger() {
        logger = Logger.getLogger("GameBox");
        Handler systemOut = new ConsoleHandler();
        systemOut.setLevel( Level.ALL );
        logger.setUseParentHandlers(false);
        logger.addHandler( systemOut );
        logger.setLevel(Level.FINEST); // ToDo: Debugging... remove
    }

    public static GameBox getInstance() {
        return instance;
    }

    public File getDataFolder() {
        return this.dataFolder;
    }

    public Logger getLogger() {
        // ToDo: logger from Hytale?
        return logger;
    }

    public static void debug(String message) {
        System.out.println("GB debug: " + message);
    }

    public ModulesManager getModulesManager() {
        return modulesManager;
    }

    public File getLanguageDir() {
        if (languageFolder != null) return languageFolder;
        return languageFolder = new File(getDataFolder(), "language");
    }

    public SemanticVersion getVersion() {
        return version;
    }
}
