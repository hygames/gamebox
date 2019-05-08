package co.hygames.gamebox.utilities;

import co.hygames.gamebox.GameBox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * @author Niklas Eicker
 */
public class FileUtility {

    /**
     * Copy all default language files to the language folder.
     *
     * This method looks for .yml files in the language folder inside the jar
     * and checks whether they are already present in the language folder.
     * If not they are copied.
     */
    public static void copyDefaultLanguageFiles() {
        URL main = GameBox.class.getResource("GameBox.class");
        try {
            JarURLConnection connection = (JarURLConnection) main.openConnection();
            JarFile jar = new JarFile(URLDecoder.decode(connection.getJarFileURL().getFile(), "UTF-8"));
            GameBox gameBox = GameBox.getInstance();
            for (Enumeration list = jar.entries(); list.hasMoreElements(); ) {
                JarEntry entry = (JarEntry) list.nextElement();
                if (entry.getName().split("/")[0].equals("language")) {
                    String[] pathParts = entry.getName().split("/");
                    if (pathParts.length < 2 || !entry.getName().endsWith(".yml") || !entry.getName().endsWith(".yaml")) {
                        continue;
                    }
                    File file = new File(gameBox.getDataFolder().toString() + File.separatorChar + entry.getName());
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        streamToFile(jar.getInputStream(entry), file);
                    }
                }
            }
            jar.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Collect all classes of the given type in the provided subfolder of the GameBox folder
     *
     * @param subFolder to check for classes
     * @param type classes checked for
     * @return list of found classes
     */
    public static List<Class<?>> getClasses(String subFolder, Class<?> type) {
        return getClasses(subFolder, null, type);
    }

    /**
     * Collect all classes of type `type` in the jar file with the provided name
     * in the given subfolder of the GameBox folder
     *
     * @param subFolder to check for classes
     * @param fileName look for jar with specific name
     * @param type classes checked for
     * @return list of found classes
     */
    public static List<Class<?>> getClasses(String subFolder, String fileName, Class<?> type) {
        List<Class<?>> list = new ArrayList<>();
        try {
            File folder = new File(GameBox.getInstance().getDataFolder(), subFolder);
            if (!folder.exists()) {
                return list;
            }
            FilenameFilter fileNameFilter = (dir, name) -> {
                if (fileName != null) {
                    return name.endsWith(".jar") && name.replace(".jar", "")
                            .equalsIgnoreCase(fileName.replace(".jar", ""));
                }
                return name.endsWith(".jar");
            };
            File[] jars = folder.listFiles(fileNameFilter);
            if (jars == null) {
                return list;
            }
            for (File jar : jars) {
                list = gather(jar.toURI().toURL(), list, type);
            }
            return list;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Class<?>> gather(URL jar, List<Class<?>> list, Class<?> clazz) {
        if (list == null) {
            list = new ArrayList<>();
        }
        try (
                URLClassLoader classLoader = new URLClassLoader(new URL[]{jar}, clazz.getClassLoader());
                JarInputStream jarInputStream = new JarInputStream(jar.openStream())
        ) {
            while (true) {
                JarEntry jarEntry = jarInputStream.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }
                String name = jarEntry.getName();
                if (name == null || name.isEmpty()) {
                    continue;
                }
                if (name.endsWith(".class")) {
                    name = name.replace("/", ".");
                    String className = name.substring(0, name.lastIndexOf(".class"));
                    Class<?> jarEntryClass = classLoader.loadClass(className);
                    if (clazz.isAssignableFrom(jarEntryClass)) {
                        list.add(jarEntryClass);
                    }
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static void streamToFile(InputStream initialStream, File targetFile) throws IOException {
        byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
    }
}
