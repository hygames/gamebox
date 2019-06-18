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

package co.hygames.gamebox.language;

import co.hygames.gamebox.GameBox;
import co.hygames.gamebox.exceptions.language.LanguageException;
import co.hygames.gamebox.language.messages.Message;

import java.util.List;
import java.util.Map;

public class GameBoxLanguage extends Language {

    public GameBoxLanguage(GameBox gameBox) {
        super(gameBox.getLanguageDir());
    }

    @Override
    protected void loadMessages() {
        for (Messages message : Messages.values()) {
            try {
                loadMessage(message.getKey());
            } catch (LanguageException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void loadLists() {
        for (Lists list : Lists.values()) {
            try {
                loadList(list.getKey());
            } catch (LanguageException e) {
                e.printStackTrace();
            }
        }
    }

    public enum Messages implements Message<String> {
        ;

        static Language language;
        String key;

        Messages(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }

        @Override
        public String get() {
            return language.getMessage(key).get();
        }

        @Override
        public String resolve(Map<String, String> context) {
            return language.getMessage(key).resolve(context);
        }
    }

    public enum Lists implements Message<List<String>> {
        ;

        static Language language;
        String key;

        Lists(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }

        @Override
        public List<String> get() {
            return language.getMessageList(key).get();
        }

        @Override
        public List<String> resolve(Map<String, String> context) {
            return language.getMessageList(key).resolve(context);
        }
    }
}
