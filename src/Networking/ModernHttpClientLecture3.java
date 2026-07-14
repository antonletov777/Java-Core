package Networking;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/// # 3. Современный HTTP-клиент (java.net.http)
/// Демонстрация асинхронного вызова с использованием HTTP/2
public class ModernHttpClientLecture3 {

    public static void main(String[] args) {
        System.out.println("Отправляем асинхронный HTTP запрос...");

        // 1. Создание и настройка клиента (Builder)
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2) // Предпочитаем HTTP/2
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // 2. Формирование запроса (Builder)
        HttpRequest request = HttpRequest.newBuilder()
                // Публичный сервис для тестирования (ответит вашим IP)
                .uri(URI.create("[https://httpbin.org/get](https://httpbin.org/get)"))
                .GET()
                .build();

        // 3. Асинхронная отправка запроса
        // Возвращает CompletableFuture, не блокируя основной поток
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body) // Достаем тело ответа (String)
                .thenAccept(body -> {
                    System.out.println("\n--- Ответ от сервера ---");
                    System.out.println(body);
                })
                .exceptionally(e -> {
                    System.err.println("Произошла ошибка: " + e.getMessage());
                    return null;
                })
                .join(); // Блокируем main поток, чтобы программа не завершилась до получения ответа

        System.out.println("Работа приложения завершена.");
    }
}