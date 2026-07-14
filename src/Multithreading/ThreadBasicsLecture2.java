package Multithreading;

/// # 2. Основы: Жизненный цикл потоков (Thread, Runnable)
/// Демонстрация создания, состояний, демонов и безопасной остановки потоков.
@SuppressWarnings("ALL")
public class ThreadBasicsLecture2 {

    public static void main(String[] args) throws InterruptedException {
        demonstrateThreadCreation();
        demonstrateLifecycleStates();
        demonstrateDaemonThread();
        demonstrateSafeInterruption();
    }

    /// Современный способ через интерфейс Runnable (Композиция лучше наследования)
    public static void demonstrateThreadCreation() {
        // Runnable - это функциональный интерфейс (поведение)
        Runnable task = () -> System.out.println("Поток работает: " + Thread.currentThread().getName());

        // Thread - это рабочий (исполнитель)
        Thread worker = new Thread(task, "My-Backend-Thread");

        // ОШИБКА НОВИЧКОВ: Вызов worker.run() выполнит код в потоке Main!
        // ПРАВИЛЬНО: Вызов worker.start() запрашивает у ОС создание нового потока
        worker.start();
    }

    /// Демонстрация изменения состояний (NEW -> RUNNABLE -> TIMED_WAITING -> TERMINATED)
    public static void demonstrateLifecycleStates() throws InterruptedException {
        Thread thread = new Thread(() -> {
            try { Thread.sleep(500); } // Переводит поток в TIMED_WAITING
            catch (InterruptedException e) { /* IGNORED */ }
        });

        System.out.println("Статус после создания: " + thread.getState()); // NEW
        thread.start();
        System.out.println("Статус после start(): " + thread.getState()); // RUNNABLE

        Thread.sleep(100); // Даем потоку запуститься и уснуть
        System.out.println("Статус во время sleep: " + thread.getState()); // TIMED_WAITING

        thread.join(); // Ждем его полного завершения
        System.out.println("Статус после завершения: " + thread.getState()); // TERMINATED
    }

    /// Демонстрация потока-демона (Служебный поток)
    public static void demonstrateDaemonThread() {
        Thread daemon = new Thread(() -> {
            while (true) {
                try { Thread.sleep(1000); System.out.println("Daemon пишет логи..."); }
                catch (InterruptedException e) {}
            }
        });

        // ВАЖНО: setDaemon нужно вызывать ДО старта!
        // JVM убьет этот бесконечный цикл автоматически, как только завершится поток main
        daemon.setDaemon(true);
        daemon.start();
    }

    /// Кооперативная (безопасная) остановка потока.
    /// Метод stop() удален (Deprecated), так как может сломать ресурсы и локи.
    public static void demonstrateSafeInterruption() throws InterruptedException {
        Thread worker = new Thread(() -> {
            // Поток должен САМ периодически проверять свой флаг прерывания
            while (!Thread.currentThread().isInterrupted()) {
                // Делаем полезную работу...
            }
            // Флаг поднят! Очищаем ресурсы (закрываем файлы/соединения) и мягко выходим.
            System.out.println("Получен сигнал Interrupted. Мягкое завершение потока.");
        });

        worker.start();
        Thread.sleep(100); // Даем поработать

        // Посылаем сигнал (поднимаем флаг)
        worker.interrupt();
    }
}