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
import co.hygames.gamebox.exceptions.language.MissingMessageException;
import co.hygames.gamebox.language.messages.Message;
import co.hygames.gamebox.language.messages.MessageSource;
import co.hygames.gamebox.language.messages.SimpleMessage;
import co.hygames.gamebox.language.messages.SimpleMessageList;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Language implements MessageSource {
    protected static Yaml yaml = new Yaml();
    protected String defaultLanguage = "lang_en.yml";
    protected File defaultLanguageFile;
    protected File languageFile;
    protected Map<String, Message<String>> messages = new HashMap<>();
    protected Map<String, Message<List<String>>> lists = new HashMap<>();

    private Map defaultMessageMap;
    private Map messageMap;

    public Language(File languageDir) {
        // load default file from the given folder
        // ToDo
        try {
            setup(languageDir, "lang_en.yml");
        } catch (LanguageException e) {
            e.printStackTrace();
        }
    }

    protected void setup(File languageFolder, String languageFile) throws LanguageException {
        // load default language
        try {
            this.defaultLanguageFile = new File(languageFolder, this.defaultLanguage);
            URL url = Thread.currentThread().getContextClassLoader().getResource("language/" + this.defaultLanguage);
            if (url == null) throw new IOException("Resource '" + "language/" + this.defaultLanguage + "' not found");
            URLConnection connection = url.openConnection();
            this.defaultMessageMap = yaml.load(connection.getInputStream());
        } catch (IOException exception) {
            throw new LanguageException("Failed to load default language file", exception);
        }
        this.languageFile = new File(languageFolder, languageFile);
        try {
            this.messageMap = yaml.load(new FileInputStream(this.languageFile));
        } catch (FileNotFoundException e) {
            throw new LanguageException("Failed to find the language file '" + languageFile + "'", e);
        }
    }

    protected abstract void loadMessages();

    protected abstract void loadLists();

    protected void loadMessage(String key) throws LanguageException {
        Object message = messageMap.get(key);
        if (!(message instanceof String)) {
            throw new LanguageException("Did not find a String for the key '" + key + "'");
        }
        this.messages.put(key, new SimpleMessage((String) message));
    }

    protected void loadList(String key) throws LanguageException {
        Object message = messageMap.get(key);
        if (!(message instanceof List)) {
            throw new LanguageException("Did not find a List for the key '" + key + "'");
        }
        this.lists.put(key, new SimpleMessageList((List<String>) message));
    }

    @Override
    public Message<String> getMessage(String key) {
        Message<String> message = messages.get(key);
        if (message != null) return message;
        throw new MissingMessageException("Unknown Message key '" + key + "'");
    }

    @Override
    public Message<List<String>> getMessageList(String key) {
        return lists.get(key);
    }
}
