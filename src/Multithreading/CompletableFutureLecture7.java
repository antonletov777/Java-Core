package Multithreading;

import java.util.concurrent.CompletableFuture;

/// # 7. Асинхронное программирование: CompletableFuture
/// Решает главную проблему Future.get() (блокировку) за счет построения асинхронных пайплайнов (цепочек callback'ов).
@SuppressWarnings("ALL")
public class CompletableFutureLecture7 {

    public static void main(String[] args) throws Exception {
        demonstrateAsyncPipeline();
        demonstrateCombine();
        demonstrateErrorHandling();
        Thread.sleep(2000); // Ждем завершения асинхронных тасок, так как они работают в Daemon-пуле (ForkJoinPool)
    }

    /// Построение цепочки независимых шагов (Pipeline)
    public static void demonstrateAsyncPipeline() {
        System.out.println("--- Async Pipeline (thenApply / thenAccept) ---");

        // Запуск асинхронной задачи, возвращающей данные (работает в ForkJoinPool.commonPool)
        CompletableFuture.supplyAsync(() -> {
                    System.out.println("1. Идем в БД за юзером... " + Thread.currentThread().getName());
                    sleep(500);
                    return "User(David)";
                })
                // Трансформация результата (Аналог map в Stream API). Срабатывает автоматически, когда готов Шаг 1.
                .thenApply(user -> {
                    System.out.println("2. Формируем email для " + user);
                    return user + " -> Email Body";
                })
                // Потребление финального результата (Аналог forEach).
                .thenAccept(email -> {
                    System.out.println("3. Отправлено: " + email);
                });

        System.out.println("Main: Продолжаю работу без блокировок!");
    }

    /// Комбинация двух параллельных асинхронных задач в один результат
    public static void demonstrateCombine() {
        System.out.println("\n--- Комбинация (thenCombine) ---");

        CompletableFuture<Integer> priceFuture = CompletableFuture.supplyAsync(() -> {
            sleep(500); return 100; // Узнаем цену (500мс)
        });

        CompletableFuture<Double> discountFuture = CompletableFuture.supplyAsync(() -> {
            sleep(300); return 0.2; // Узнаем скидку (300мс)
        });

        // Как только ОБЕ фьючи будут готовы, выполнится функция-комбинатор.
        // Общее время выполнения = MAX(500, 300) = 500мс (вместо 800мс при последовательном вызове!)
        priceFuture.thenCombine(discountFuture, (price, discount) -> price * (1 - discount))
                .thenAccept(finalPrice -> System.out.println("Итоговая цена: " + finalPrice));
    }

    /// Изящная обработка исключений (вместо try-catch блоков)
    public static void demonstrateErrorHandling() {
        System.out.println("\n--- Обработка ошибок (exceptionally) ---");

        CompletableFuture.supplyAsync(() -> {
                    if (true) throw new RuntimeException("БД недоступна!");
                    return "Секретные данные";
                })
                // Если произошла ошибка, возвращаем дефолтное (fallback) значение, чтобы не прерывать пайплайн
                .exceptionally(ex -> {
                    System.out.println("Перехватили ошибку: " + ex.getMessage());
                    return "Дефолтные данные из кэша";
                })
                .thenAccept(data -> System.out.println("Результат после ошибки: " + data));
    }

    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (Exception e) {}
    }
}