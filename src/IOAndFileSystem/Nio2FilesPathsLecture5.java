package IOAndFileSystem;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Stream;

/// # 5. Современная работа с файлами (NIO.2)
/// Использование Path, Files и Stream API для безопасной и элегантной манипуляции ФС.
public class Nio2FilesPathsLecture5 {

    public static void main(String[] args) throws IOException {
        demonstratePathOperations();
        demonstrateFilesApi();
        demonstrateLargeFileReading();
        demonstrateFileTreeWalking();
    }

    /// Работа с интерфейсом Path
    public static void demonstratePathOperations() {
        Path path = Path.of("logs", "..", "configs", "app.properties");
        System.out.println("Сырой путь: " + path);

        Path cleanPath = path.normalize(); // Убирает '..' -> configs/app.properties
        System.out.println("Нормализованный: " + cleanPath.toAbsolutePath());
    }

    /// Утилиты Files для быстрых операций (только для малых файлов!)
    public static void demonstrateFilesApi() throws IOException {
        Path tempFile = Files.createTempFile("app_log", ".txt");

        // Запись и чтение в одну строку (удобно для небольших JSON/XML)
        Files.writeString(tempFile, "Memory: 512MB");

        List<String> lines = Files.readAllLines(tempFile); // Грузит весь файл в кучу (Heap)
        System.out.println("Первая строка: " + lines.get(0));
    }

    /// Правильное чтение гигантских файлов (Lazy Evaluation)
    public static void demonstrateLargeFileReading() throws IOException {
        Path logFile = Path.of("report.txt");
        if (!Files.exists(logFile)) return;

        // Stream лениво подтягивает строки из файла. Не вызывает OutOfMemoryError.
        // Обязательно в try-with-resources для закрытия underlying дескриптора файла!
        try (Stream<String> lineStream = Files.lines(logFile)) {
            lineStream
                    .filter(line -> line.contains("200"))
                    .limit(5)
                    .forEach(System.out::println);
        }
    }

    /// Рекурсивный обход папок через паттерн Visitor (защита от StackOverflow)
    public static void demonstrateFileTreeWalking() throws IOException {
        Path startDir = Path.of(".");

        Files.walkFileTree(startDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.toString().endsWith(".properties")) {
                    System.out.println("Найден конфиг: " + file.getFileName());
                }
                return FileVisitResult.CONTINUE;
            }

            @Override // Обработка прав доступа (чтобы не упасть на папке 'System Volume Information')
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.SKIP_SUBTREE;
            }
        });
    }
}