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

package co.hygames.gamebox.language.messages;

import java.util.Map;

public class SimpleMessage implements Message<String> {
    private String message;
    private String key;

    public SimpleMessage(String message, String key) {
        this.message = message;
        this.key = key;
    }

    @Override
    public String get() {
        return this.message;
    }

    @Override
    public String resolve(Map<String, String> context) {
        String toReturn = this.message;
        for (String placeholder : context.keySet()) {
            toReturn = toReturn.replaceAll(placeholder, context.get(placeholder));
        }
        return toReturn;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
