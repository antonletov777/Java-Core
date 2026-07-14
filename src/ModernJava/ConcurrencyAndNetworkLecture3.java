package ModernJava;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executors;

/// # 3. Сеть и Многопоточность (HTTP Client & Virtual Threads)
public class ConcurrencyAndNetworkLecture3 {

    public static void main(String[] args) {
        System.out.println("Запускаем 10_000 запросов через Виртуальные потоки...");

        long start = System.currentTimeMillis();

        // Создаем Executor, который для каждой задачи создает новый Виртуальный поток (Java 21)
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 0; i < 10_000; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    // Выполняем сетевой запрос (блокирующая I/O операция)
                    makeHttpRequest(taskId);
                });
            }

        } // Здесь главный поток ждет завершения всех виртуальных потоков благодаря try-with-resources

        long end = System.currentTimeMillis();
        System.out.println("Все 10_000 виртуальных потоков завершили работу за " + (end - start) + " мс.");
    }

    /// Использование современного HttpClient (Java 11)
    private static void makeHttpRequest(int taskId) {
        try {
            // Новый удобный API для HTTP-запросов
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("[https://httpbin.org/delay/1](https://httpbin.org/delay/1)")) // Сервер ответит через 1 секунду
                    .GET()
                    .build();

            // Этот вызов заблокирует ВИРТУАЛЬНЫЙ поток на 1 секунду.
            // Но поток ОС (платформенный поток) будет немедленно освобожден для других задач!
            client.send(request, HttpResponse.BodyHandlers.discarding());

            if (taskId % 2000 == 0) {
                System.out.println("Задача " + taskId + " выполнена потоком: " + Thread.currentThread());
            }

        } catch (Exception e) {
            // Игнорируем ошибки сети для чистоты консоли
        }
    }
}