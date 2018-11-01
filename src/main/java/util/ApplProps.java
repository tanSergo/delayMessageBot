package util;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

// Получаем данные с файла src/main/resources/config/.properties

public class ApplProps {
    private static Map<String, String> props = new HashMap<>();

    public static String get(String key) {
        return props.get(key);
    }

    public static void set(String key, String value) {
        props.put(key, value);
        System.out.println("  -- Значение переменной '" + key + "' = " + value + " записано в config");
    }

    public static void getProps(String nameFile) {
        Properties prop = new Properties();

        try {
            InputStreamReader fileInput = new InputStreamReader(new FileInputStream(new File("src/main/resources/config/" + nameFile)), "UTF-8");
            prop.load(fileInput);
        } catch (FileNotFoundException e) {
            System.out.println("Не найден файл 'src/main/resources/config/" + nameFile + "'");
        } catch (IOException e) {
            System.out.println("Не получилось загрузить файл 'src/main/resources/config/" + nameFile + "'");
        }
        Enumeration keyValues = prop.keys();
        System.out.println("\n============ TestConfig =============\n");
        while (keyValues.hasMoreElements()) {
            String key = (String) keyValues.nextElement();
            String value = prop.getProperty(key);

            props.put(key.trim(), value.trim());
            System.out.println("   " + key.trim() + " = " + value.trim());
        }
        System.out.println("=====================================");
        System.out.println("Файл 'src/main/resources/config/" + nameFile + "' пуст. Просьба заполнить файл.");
    }
}
