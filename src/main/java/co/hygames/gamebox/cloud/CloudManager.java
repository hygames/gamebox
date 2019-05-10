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

import co.hygames.gamebox.GameBox;
import co.hygames.gamebox.cloud.data.ModuleData;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Niklas Eicker
 */
public class CloudManager {
    private static final String API_BASE_URL = "https://api.hygames.co/";
    private static final Gson GSON = new Gson();

    private GameBox gameBox;
    private Map<String, ModuleData> cloudContent = new HashMap<>();

    public CloudManager(GameBox gameBox) {
        this.gameBox = gameBox;
    }

    public void updateCloudContent() {
        cloudContent.clear();

    }
}
