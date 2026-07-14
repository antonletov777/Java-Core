package Multithreading;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/// # 5. Продвинутая синхронизация (java.util.concurrent.locks)
/// Разбор современных инструментов (Locks, Semaphores, Latches), которые пришли на замену wait/notify.
@SuppressWarnings("ALL")
public class AdvancedLocksLecture5 {

    // ReentrantLock заменяет 'synchronized', предлагая больше контроля (например, tryLock)
    private static final ReentrantLock lock = new ReentrantLock();

    // Semaphore ограничивает максимальное число потоков, владеющих ресурсом
    private static final Semaphore dbConnectionsPool = new Semaphore(2); // Только 2 одновременных коннекта

    // CountDownLatch позволяет главному потоку дождаться завершения работы группы рабочих потоков
    private static final CountDownLatch latch = new CountDownLatch(3);

    public static void main(String[] args) throws InterruptedException {
        demonstrateTryLockToAvoidDeadlock();
        demonstrateSemaphore();
        demonstrateCountDownLatch();
    }

    /// Решение проблемы Deadlock с помощью tryLock()
    public static void demonstrateTryLockToAvoidDeadlock() {
        Runnable smartTask = () -> {
            // Пытаемся захватить замок. В отличие от synchronized, если замок занят,
            // поток не уходит в BLOCKED навсегда, а получает false и идет дальше!
            if (lock.tryLock()) {
                try {
                    System.out.println(Thread.currentThread().getName() + " захватил ReentrantLock");
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
                finally {
                    lock.unlock(); // ВАЖНО: Разблокировка всегда строго в finally!
                }
            } else {
                System.out.println(Thread.currentThread().getName() + " не смог получить лок. Избежали зависания!");
            }
        };

        new Thread(smartTask, "SmartThread-1").start();
        new Thread(smartTask, "SmartThread-2").start();
    }

    /// Демонстрация Семафора (Ограничение нагрузки)
    public static void demonstrateSemaphore() throws InterruptedException {
        System.out.println("\n--- Semaphore ---");
        Runnable dbTask = () -> {
            try {
                // Если семафор пуст (доступно 0), поток засыпает до появления свободных пермитов
                dbConnectionsPool.acquire();
                System.out.println(Thread.currentThread().getName() + " работает с БД...");
                Thread.sleep(500); // Имитация долгого запроса
            } catch (InterruptedException e) {}
            finally {
                // Обязательно возвращаем "билет" (пермит) обратно в пул
                System.out.println(Thread.currentThread().getName() + " освободил соединение.");
                dbConnectionsPool.release();
            }
        };

        // Запускаем 5 потоков, но одновременно в БД пройдут только 2! (благодаря семафору 2)
        for (int i = 0; i < 5; i++) new Thread(dbTask, "DB-Worker-" + i).start();
        Thread.sleep(2000);
    }

    /// Защелка (Синхронизация по количеству событий)
    public static void demonstrateCountDownLatch() throws InterruptedException {
        System.out.println("\n--- CountDownLatch ---");
        Runnable workerTask = () -> {
            System.out.println(Thread.currentThread().getName() + " завершил часть работы.");
            // Уменьшаем счетчик защелки. Когда он дойдет до 0, все ожидающие потоки разбудятся.
            latch.countDown();
        };

        for (int i = 0; i < 3; i++) new Thread(workerTask, "Microservice-" + i).start();

        System.out.println("Main: Жду, пока поднимутся все 3 микросервиса...");
        // Блокируем Main поток, пока счетчик не станет равен 0
        latch.await();
        System.out.println("Main: Все микросервисы готовы, запускаем систему!");
    }
}