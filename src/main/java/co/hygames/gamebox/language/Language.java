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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class Language implements MessageSource {
    protected static Yaml yaml = new Yaml();
    protected String defaultLanguage = "lang_en.yml";
    protected Map<String, Message<String>> messages = new HashMap<>();
    protected Map<String, Message<List<String>>> lists = new HashMap<>();

    public Language() {}

    protected void setup(File languageFolder, String languageFile) throws LanguageException {
        setup(languageFolder, languageFolder, languageFile, defaultLanguage);
    }

    protected void setup(File languageFolder, File jarFile, String languageFile) throws LanguageException {
        setup(languageFolder, jarFile, languageFile, defaultLanguage);
    }

    protected void setup(File languageFolder, File jarFile, String languageFile, String defaultFile) throws LanguageException {
        if (jarFile.getName().endsWith(".jar")) {
            loadMessagesFromJar(jarFile, defaultFile);
        } else {
            loadMessagesFromFolder(jarFile, defaultFile);
        }
        loadMessagesFromFolder(languageFolder, languageFile);
    }

    private void loadMessagesFromJar(File jar) throws LanguageException {
        loadMessagesFromJar(jar, this.defaultLanguage);
    }

    private void loadMessagesFromJar(File jar, String language) throws LanguageException {
        Map messageMap = getMessageMapFromJar(jar, language);
        readLanguageMap(messageMap, this.messages, this.lists);
    }

    private Map getMessageMapFromJar(File jar, String languageFile) throws LanguageException {
        try {
            JarFile jarFile = new JarFile(jar);
            JarEntry entry = jarFile.getJarEntry("language/" + languageFile);
            if (entry == null) {
                throw new LanguageException("Language file '" + languageFile + "' not found in jar " + jarFile.getName());
            }
            return yaml.load(jarFile.getInputStream(entry));
        } catch (IOException e) {
            throw new LanguageException("Exception while loading messages from " + languageFile + " in the jar " + jar.getName());
        }
    }

    private void loadMessagesFromFolder(File folder) throws LanguageException {
        loadMessagesFromJar(folder, defaultLanguage);
    }

    private void loadMessagesFromFolder(File folder, String language) throws LanguageException {
        File languageFile = new File(folder, language);
        try {
            Map messageMap = yaml.load(new FileInputStream(languageFile));
            readLanguageMap(messageMap, this.messages, this.lists);
        } catch (FileNotFoundException e) {
            throw new LanguageException("Failed to find the language file '" + languageFile + "'", e);
        }
    }

    private Map getMessageMapFromDirectory(File directory, String language) throws LanguageException {
        try {
            File languageFile = new File(directory, language);
            return yaml.load(new FileInputStream(languageFile));
        } catch (IOException e) {
            throw new LanguageException("Exception while loading messages from " + language + " in the directory " + directory.getName());
        }
    }

    private void readLanguageMap(Map messageMap, Map<String, Message<String>> messages, Map<String, Message<List<String>>> lists) {
        readLanguageMap(messageMap, "", messages, lists);
    }

    private void readLanguageMap(Map messageMap, String path, Map<String, Message<String>> messages, Map<String, Message<List<String>>> lists) {
        for (Object key : messageMap.keySet()) {
            Object entry = messageMap.get(key);
            String currentPath = path.isEmpty()?key.toString():path + "." + key.toString();
            if (entry instanceof Map) readLanguageMap((Map) entry, currentPath, messages, lists);
            else if (entry instanceof List) lists.put(currentPath, new SimpleMessageList((List<String>) entry, currentPath));
            else if (entry instanceof String) messages.put(currentPath, new SimpleMessage((String) entry, currentPath));
            else {
                GameBox.getInstance().getLogger().warning("Unexpected entry in language file...");
            }
        }
    }

    public Map<String, Message> collectMissingMessages(File defaultDirectory, String defaultFile, File languageDirectory, String languageFile) throws LanguageException {
        Map defaultMessagesMap, messagesMap;
        // load everything to maps
        if (defaultDirectory.getAbsolutePath().endsWith(".jar")) {
            defaultMessagesMap = getMessageMapFromJar(defaultDirectory, defaultFile);
        } else {
            defaultMessagesMap = getMessageMapFromDirectory(defaultDirectory, defaultFile);
        }
        if (languageDirectory.getAbsolutePath().endsWith(".jar")) {
            messagesMap = getMessageMapFromJar(languageDirectory, languageFile);
        } else {
            messagesMap = getMessageMapFromDirectory(languageDirectory, languageFile);
        }

        // collect all keys and corresponding messages
        Map<String, Message<String>> defaultMessages = new HashMap<>();
        Map<String, Message<List<String>>> defaultLists = new HashMap<>();
        readLanguageMap(defaultMessagesMap, defaultMessages, defaultLists);
        Map<String, Message<String>> messages = new HashMap<>();
        Map<String, Message<List<String>>> lists = new HashMap<>();
        readLanguageMap(messagesMap, messages, lists);

        // ignore keys that only show up in the configured language
        // collect keys that show up in the default file but not in the configured one
        Map<String, Message> toReturn = new HashMap<>();
        for (String key : defaultMessages.keySet()) {
            if (!messages.containsKey(key)) {
                toReturn.put(key, defaultMessages.get(key));
            }
        }
        for (String key : defaultLists.keySet()) {
            if (!lists.containsKey(key)) {
                toReturn.put(key, defaultLists.get(key));
            }
        }
        return toReturn;
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
