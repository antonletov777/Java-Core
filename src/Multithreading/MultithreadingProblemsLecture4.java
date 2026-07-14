package Multithreading;

/// # 4. Проблемы многопоточного программирования
/// Исчерпывающая демонстрация Race Condition и взаимной блокировки (Deadlock).
@SuppressWarnings("ALL")
public class MultithreadingProblemsLecture4 {

    // Ресурсы для Deadlock
    private static final Object RESOURCE_1 = new Object();
    private static final Object RESOURCE_2 = new Object();

    // Ресурс для Race Condition
    private static int unsafeBalance = 0;

    public static void main(String[] args) throws InterruptedException {
        demonstrateRaceCondition();
        System.out.println("Сейчас будет запущен Deadlock. Программа зависнет навсегда!");
        Thread.sleep(2000);
        demonstrateDeadlock();
    }

    /// 1. Состояние гонки (Race Condition).
    /// Возникает, когда результат зависит от непредсказуемого расписания ОС (Scheduler).
    public static void demonstrateRaceCondition() throws InterruptedException {
        Runnable depositTask = () -> {
            for (int i = 0; i < 100_000; i++) {
                // Операция unsafeBalance++ не является атомарной!
                // Она состоит из 3 шагов: 1. Читать из памяти. 2. Увеличить в регистре. 3. Записать в память.
                // Два потока могут прочитать одно и то же старое значение (например, 100),
                // оба увеличат до 101 и запишут 101. Одно обновление будет потеряно!
                unsafeBalance++;
            }
        };

        Thread t1 = new Thread(depositTask);
        Thread t2 = new Thread(depositTask);
        t1.start(); t2.start();
        t1.join(); t2.join();

        // Из-за Race Condition результат почти всегда будет меньше 200_000
        System.out.println("Итоговый баланс (Race Condition): " + unsafeBalance);
    }

    /// 2. Взаимная блокировка (Deadlock).
    /// Классическая проблема циклических зависимостей локов.
    public static void demonstrateDeadlock() {
        Thread threadA = new Thread(() -> {
            // Поток А захватывает Ресурс 1
            synchronized (RESOURCE_1) {
                System.out.println("Поток А: Захватил Resource 1. Жду Resource 2...");
                try { Thread.sleep(100); } catch (Exception e) {}

                // Пытается захватить Ресурс 2, но он уже у Потока Б
                synchronized (RESOURCE_2) {
                    System.out.println("Поток А: Захватил оба!");
                }
            }
        });

        Thread threadB = new Thread(() -> {
            // Поток Б захватывает Ресурс 2
            synchronized (RESOURCE_2) {
                System.out.println("Поток Б: Захватил Resource 2. Жду Resource 1...");
                try { Thread.sleep(100); } catch (Exception e) {}

                // Пытается захватить Ресурс 1, но он уже у Потока А
                synchronized (RESOURCE_1) {
                    System.out.println("Поток Б: Захватил оба!");
                }
            }
        });

        threadA.start();
        threadB.start();
        // ПРОГРАММА ЗАВИСНЕТ ЗДЕСЬ! Состояния потоков будут BLOCKED.
    }
}