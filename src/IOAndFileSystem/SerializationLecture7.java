package IOAndFileSystem;

import java.io.*;

/// # 8. Сериализация: Классика и Реальность
/// Устаревшая нативная сериализация Java (содержит риск RCE уязвимостей).
/// В реальном бэкенде вместо этого используют Jackson (JSON) или Protobuf.
@SuppressWarnings("ALL")
public class SerializationLecture7 {

    public static void main(String[] args) {
        AppConfig config = new AppConfig("jdbc:postgresql://localhost:5432/db", "root123");

        serializeConfig(config, "config.ser");
        AppConfig restored = deserializeConfig("config.ser");

        System.out.println("Восстановленный DB URL: " + restored.getDbUrl());
        // Пароль будет null из-за модификатора transient
        System.out.println("Восстановленный Пароль: " + restored.getDbPassword());
    }

    /// Маркерный интерфейс. Без него выбросится NotSerializableException.
    static class AppConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L; // Контроль версий класса

        private String dbUrl;

        // transient исключает поле из бинарного потока (для безопасности)
        private transient String dbPassword;

        public AppConfig(String dbUrl, String dbPassword) {
            this.dbUrl = dbUrl;
            this.dbPassword = dbPassword;
        }

        public String getDbUrl() { return dbUrl; }
        public String getDbPassword() { return dbPassword; }
    }

    public static void serializeConfig(AppConfig config, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AppConfig deserializeConfig(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            // Опасно: Выполнение cast и readObject на данных от пользователя может вызвать RCE (выполнение кода)
            return (AppConfig) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}