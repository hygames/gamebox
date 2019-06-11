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
import co.hygames.gamebox.utilities.versioning.SemanticVersion;

import java.io.File;
import java.text.ParseException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Niklas Eicker
 */
public class GameBox {
    public static final String moduleId = "gamebox";
    private static GameBox instance;
    private ModulesManager modulesManager;
    private File dataFolder;
    private Logger logger;
    private SemanticVersion version;

    public static void main(String args[]) {
        new GameBox().onEnable();
    }

    public void onEnable() {
        instance = this;
        try {
            setUp();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setUp() throws ParseException {
        version = new SemanticVersion(this.getClass().getPackage().getImplementationVersion());
        createLogger();
        dataFolder = new File("/home/nikl/Desktop/gamebox"); // testing ToDo: remove
        this.modulesManager = new ModulesManager(this);
        modulesManager.installModule("test-module");
        // ToDo: load local modules and update info about cloud modules
    }

    private void createLogger() {
        logger = Logger.getLogger("GameBox");
        Handler systemOut = new ConsoleHandler();
        systemOut.setLevel( Level.ALL );
        logger.setUseParentHandlers(false);
        logger.addHandler( systemOut );
        logger.setLevel(Level.FINEST); // Debugging... ToDo: remove
    }

    public static GameBox getInstance() {
        return instance;
    }

    public File getDataFolder() {
        // ToDo
        return dataFolder;
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
        // ToDo: move to responsible langauge class
        return null;
    }

    public SemanticVersion getVersion() {
        return version;
    }
}
