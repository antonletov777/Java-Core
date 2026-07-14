package Multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/// # 8. Эволюция Java 21: Virtual Threads и Structured Concurrency
/// Демонстрация главной революции бэкенда Java (Project Loom).
@SuppressWarnings("ALL")
public class VirtualThreadsAndSpringLecture8 {

    public static void main(String[] args) throws Exception {
        demonstrateVirtualThreadsThroughput();
        // demonstrateSpringAsyncStructure(); // Псевдокод структуры Spring
    }

    /// Пропускная способность (Throughput) виртуальных потоков.
    /// Виртуальные потоки не привязаны 1:1 к потокам ОС.
    /// Когда виртуальный поток засыпает (Thread.sleep) или ждет сеть, он освобождает поток ОС (unmount),
    /// позволяя другому виртуальному потоку работать на этом же ядре.
    public static void demonstrateVirtualThreadsThroughput() {
        System.out.println("--- Создание 10 000 Виртуальных Потоков ---");

        long start = System.currentTimeMillis();

        // Создаем пул, который под капотом использует миллионы легковесных виртуальных потоков
        // В отличие от CachedThreadPool, это не обвалит JVM ошибкой OutOfMemoryError!
        try (ExecutorService virtualPool = Executors.newVirtualThreadPerTaskExecutor()) {

            // Закидываем 10 000 задач. Каждая задача спит 1 секунду.
            IntStream.range(0, 10_000).forEach(i -> {
                virtualPool.submit(() -> {
                    try {
                        // Блокирующий I/O вызов. В этот момент поток ОС освобождается для других!
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                });
            });
            // Пул автоматически дождется завершения всех задач благодаря try-with-resources
        }

        // Вся работа займет чуть больше 1 секунды, потому что все 10000 задач ждали
        // "одновременно", не блокируя потоки операционной системы!
        System.out.println("Время выполнения 10 000 блокирующих задач: " + (System.currentTimeMillis() - start) + "ms");
    }

    /// Иллюстрация архитектуры @Async в Spring Boot
    public static void demonstrateSpringAsyncStructure() {
        /*
        В Spring Framework ручное управление пулами (ExecutorService) заменяется декларативным подходом.

        1. Конфигурация:
        @Configuration
        @EnableAsync // Включаем механизм
        public class AsyncConfig {
            @Bean(name = "taskExecutor")
            public Executor taskExecutor() {
                // В Spring Boot 3.2+ можно вернуть пул виртуальных потоков:
                return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
            }
        }

        2. Сервис:
        @Service
        public class ReportService {

            // Spring оборачивает этот метод в прокси. Вызов мгновенно возвращает управление,
            // а тело метода улетает в taskExecutor.
            @Async("taskExecutor")
            public CompletableFuture<String> generateHeavyReport() {
                // ... долгий запрос к БД ...
                return CompletableFuture.completedFuture("Report Data");
            }
        }
        */
    }
}