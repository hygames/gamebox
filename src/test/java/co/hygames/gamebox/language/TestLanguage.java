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

import co.hygames.gamebox.exceptions.language.LanguageException;
import co.hygames.gamebox.exceptions.language.MissingListException;
import co.hygames.gamebox.exceptions.language.MissingMessageException;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TestLanguage {
    @Test
    @DisplayName("successfully loads default file with all messages")
    public void loadDefaultFile() {
        try {
            Language language = new Language("default.yml", "default.yml");
            assertEquals(language.messages.size(), 4);
            assertEquals(language.lists.size(), 2);
        } catch (LanguageException e) {
            fail("Failed to load the language", e);
        }
    }

    @Test
    @DisplayName("loads the correct messages and lists")
    public void loadDefaultFileCorrectly() {
        try {
            Language language = new Language("default.yml", "default.yml");
            assertEquals("Test name", language.getMessage("name").get());
            assertEquals("Hi", language.getMessage("singleString").get());
            assertEquals("Queen", language.getMessage("anotherOneBitesTheDust").get());
            assertEquals("Hello", language.getMessage("section.hello").get());
            assertEquals(Arrays.asList("first entry", "#2"), language.getMessageList("list").get());
            assertEquals(Arrays.asList("Hey", "There"), language.getMessageList("section.secondList").get());
        } catch (LanguageException e) {
            fail("Failed to load the language", e);
        }
    }

    @Test
    @DisplayName("defaults can be overwritten by the language file")
    public void defaultsCanBeOverwritten() {
        try {
            // keys overwritten in lang_de_DE
            Language language = new Language("lang_de_DE.yml", "default.yml");
            assertEquals("Deutscher name", language.getMessage("name").get());
            assertEquals("Dies ist ein Test", language.getMessage("singleString").get());
            assertEquals("Coole band", language.getMessage("anotherOneBitesTheDust").get());
            assertEquals(Arrays.asList("einziger Eintrag"), language.getMessageList("list").get());

            // not overwritten values should still be the default
            assertEquals(Arrays.asList("Hey", "There"), language.getMessageList("section.secondList").get());
            assertEquals("Hello", language.getMessage("section.hello").get());
        } catch (LanguageException e) {
            fail("Failed to load the language", e);
        }
    }
    @Test
    @DisplayName("throws missing message/list exception when trying to get a non-existing message/list")
    public void failsForNotExistingMessagesAndLists() {
        try {
            Language language = new Language("lang_de_DE.yml", "default.yml");
            assertThrows(MissingMessageException.class, () -> language.getMessage("doesNotExist"));
            assertThrows(MissingListException.class, () -> language.getMessageList("doesNotExist"));
        } catch (LanguageException e) {
            fail("Failed to load the language", e);
        }
    }

    private class Language extends co.hygames.gamebox.language.Language {
        public Language(String language, String defaultLanguage) throws LanguageException {
            File source = new File("src/test/resources/language");
            this.setup(source, source, language, defaultLanguage);
        }
    }
}
