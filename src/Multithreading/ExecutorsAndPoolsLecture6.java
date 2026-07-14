package Multithreading;

import java.util.concurrent.*;

/// # 6. Thread Pools: Executors и ScheduledExecutorService
/// Как переиспользовать потоки, не создавая накладных расходов ОС (без new Thread()).
@SuppressWarnings("ALL")
public class ExecutorsAndPoolsLecture6 {

    public static void main(String[] args) throws Exception {
        demonstrateFixedThreadPool();
        demonstrateScheduledThreadPool();
    }

    /// Фиксированный пул потоков - классика бэкенда.
    /// Создает строго N потоков. Если задач больше, они копятся во внутренней очереди.
    public static void demonstrateFixedThreadPool() throws ExecutionException, InterruptedException {
        System.out.println("--- Fixed Thread Pool ---");

        // Пул из 2 потоков
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Callable в отличие от Runnable может возвращать результат и кидать Exception
        Callable<String> task = () -> {
            Thread.sleep(500);
            return "Отчет сгенерирован потоком " + Thread.currentThread().getName();
        };

        // Метод submit ставит задачу в очередь пула и мгновенно возвращает Future (обещание)
        Future<String> future1 = executor.submit(task);
        Future<String> future2 = executor.submit(task);
        Future<String> future3 = executor.submit(task); // Будет ждать в очереди, т.к. потоков всего 2

        System.out.println("Main: Делаю свои дела, пока генерируются отчеты...");

        // БЛОКИРУЮЩИЙ ВЫЗОВ! future.get() "заморозит" текущий поток, пока результат не будет готов.
        System.out.println(future1.get());
        System.out.println(future2.get());
        System.out.println(future3.get());

        // ВАЖНО: Пул потоков нужно обязательно глушить, иначе программа (JVM) никогда не завершится!
        executor.shutdown();
    }

    /// Пул для отложенных и периодических задач (Фоновые чистильщики, рассылки)
    public static void demonstrateScheduledThreadPool() throws InterruptedException {
        System.out.println("\n--- Scheduled Executor ---");
        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

        Runnable periodicTask = () -> System.out.println("Heartbeat (ping) отправлен БД...");

        // Выполнять каждые 500 миллисекунд с начальной задержкой 0
        ScheduledFuture<?> handler = scheduledExecutor.scheduleAtFixedRate(
                periodicTask, 0, 500, TimeUnit.MILLISECONDS
        );

        Thread.sleep(2000); // Даем поработать 2 секунды
        handler.cancel(true); // Отменяем задачу
        scheduledExecutor.shutdown();
    }
}