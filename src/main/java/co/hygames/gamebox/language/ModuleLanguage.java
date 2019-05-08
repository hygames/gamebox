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

package co.hygames.gamebox.language;

import co.hygames.gamebox.GameBox;

import java.util.List;

/**
 * @author Niklas Eicker
 */
public class ModuleLanguage implements MessageSource {
    protected GameBox gameBox;
    protected String moduleID;

    public ModuleLanguage(GameBox gameBox, String moduleID) {

    }

    @Override
    public String getMessage(String key) {
        return null;
    }

    @Override
    public List<String> getMessageList(String key) {
        return null;
    }
}
