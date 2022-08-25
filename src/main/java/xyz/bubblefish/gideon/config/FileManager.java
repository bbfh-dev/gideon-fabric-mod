package xyz.bubblefish.gideon.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public static String CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(Path.of("gideon-integration")).toString();


    public static String getConfigPath(String path) {
        return FabricLoader.getInstance().getConfigDir().resolve(Path.of("gideon-integration", path)).toString();
    }


    public static void writeToConfig(String path, List<String> lines) {
        writeToFile(getConfigPath(path), lines);
    }


    public static List<String> readConfig(String path) {
        return readFile(getConfigPath(path));
    }


    public static void writeToFile(String path, List<String> lines) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                new File(CONFIG_DIR).mkdirs();
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(path);
            for (String line : lines) fileWriter.write(line + System.getProperty("line.separator"));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException ignored) {}
    }


    public static List<String> readFile(String path) {
        List<String> output = new ArrayList<>();
        String line;

        try {
            File file = new File(path);
            if (!file.exists()) {
                new File(CONFIG_DIR).mkdirs();
                file.createNewFile();
            }

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                output.add(line);
            }

            fileReader.close();
            bufferedReader.close();

        } catch (IOException ignored) {}

        return output;
    }
}
