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

import java.io.File;
import java.util.logging.Logger;

/**
 * @author Niklas Eicker
 */
public class GameBox {
    private static GameBox instance;

    public void onEnable() {
        instance = this;
        // ToDo: load local modules and update info about cloud modules
    }

    public static GameBox getInstance() {
        return instance;
    }

    public File getDataFolder() {
        // ToDo
        return null;
    }

    public Logger getLogger() {
        // ToDo: logger from Hytale?
        return null;
    }
}
