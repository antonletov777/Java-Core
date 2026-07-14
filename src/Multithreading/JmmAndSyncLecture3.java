package Multithreading;

import java.util.concurrent.atomic.AtomicInteger;

/// # 3. Java Memory Model (JMM) и Примитивы синхронизации
/// Иллюстрация проблемы видимости, работы volatile, мониторов (synchronized) и Атомиков.
@SuppressWarnings("ALL")
public class JmmAndSyncLecture3 {

    // volatile заставляет все потоки читать переменную ТОЛЬКО ИЗ ГЛАВНОЙ ПАМЯТИ, минуя кэши L1/L2
    private static volatile boolean keepRunning = true;

    // Общая переменная для демонстрации synchronized
    private static int counter = 0;

    // Атомарная переменная (работает через аппаратную инструкцию CAS)
    private static AtomicInteger atomicCounter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        demonstrateVisibilityIssue();
        demonstrateSynchronized();
        demonstrateAtomics();
    }

    /// Проблема Видимости (Visibility)
    public static void demonstrateVisibilityIssue() throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            long count = 0;
            // Если бы keepRunning не была volatile, этот поток мог бы закэшировать ее
            // в регистре процессора и цикл стал бы БЕСКОНЕЧНЫМ, несмотря на изменение в main!
            while (keepRunning) {
                count++;
            }
            System.out.println("Фоновый поток успешно увидел изменение флага!");
        });

        backgroundThread.start();
        Thread.sleep(500);

        keepRunning = false; // Меняем флаг из потока Main
        backgroundThread.join();
    }

    /// Использование монитора объекта (synchronized) для взаимного исключения.
    public static void demonstrateSynchronized() throws InterruptedException {
        // Мы используем сам объект класса (его Class объект) как монитор-замок
        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                // Только один поток в мире может одновременно находиться в этом блоке
                synchronized (JmmAndSyncLecture3.class) {
                    counter++; // Это действие (read -> increment -> write) не атомарно, поэтому нужен лок
                }
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start(); t2.start();
        t1.join(); t2.join();

        System.out.println("Synchronized counter (ожидаем 20000): " + counter);
    }

    /// Неблокирующая синхронизация (CAS) — Самый быстрый способ работы со счетчиками.
    public static void demonstrateAtomics() throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                // incrementAndGet выполняется аппаратно за одну неразрывную операцию.
                // Никакие мониторы не захватываются!
                atomicCounter.incrementAndGet();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start(); t2.start();
        t1.join(); t2.join();

        System.out.println("Atomic counter (ожидаем 20000): " + atomicCounter.get());
    }
}