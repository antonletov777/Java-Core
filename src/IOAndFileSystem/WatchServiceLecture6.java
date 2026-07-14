package IOAndFileSystem;

import java.io.IOException;
import java.nio.file.*;

/// # 7. Мониторинг изменений (WatchService)
/// Асинхронное отслеживание изменений на уровне ОС без вечных циклов с Thread.sleep().
@SuppressWarnings("ALL")
public class WatchServiceLecture6 {

    public static void main(String[] args) throws IOException {
        // demonstrateWatchService(); // Раскомментировать для запуска мониторинга
    }

    public static void demonstrateWatchService() throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path directory = Path.of(".");

        // Регистрация директории на события: Создание, Изменение, Удаление
        directory.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);

        System.out.println("Ожидание изменений в " + directory.toAbsolutePath());

        while (true) {
            WatchKey key;
            try {
                // Блокирует поток до появления события (не тратит процессор!)
                key = watchService.take();
            } catch (InterruptedException e) {
                break;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) continue; // Потерянные события

                WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                System.out.printf("Событие [%s] с файлом: %s%n", kind.name(), pathEvent.context());
            }

            // Обязательный сброс ключа. Вернет false, если папку удалили.
            if (!key.reset()) {
                System.out.println("Директория недоступна. Выход.");
                break;
            }
        }
    }
}