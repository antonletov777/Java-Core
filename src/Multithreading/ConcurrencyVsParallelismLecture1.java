package Multithreading;

import java.util.stream.IntStream;

/// # 1. Концепции: Конкурентность, Параллелизм и Асинхронность
/// Класс демонстрирует базовую разницу между последовательным,
/// параллельным (многоядерным) и конкурентным (многопоточным на уровне планировщика) выполнением.
@SuppressWarnings("ALL")
public class ConcurrencyVsParallelismLecture1 {

    public static void main(String[] args) {
        demonstrateSequential();
        demonstrateTrueParallelism();
        demonstrateConcurrency();
    }

    /// Последовательное выполнение: Один поток (Main) делает всё по очереди.
    public static void demonstrateSequential() {
        System.out.println("--- Последовательное выполнение ---");
        long start = System.currentTimeMillis();
        // Вся работа выполняется строго в потоке main
        IntStream.range(0, 5).forEach(i -> doHeavyWork(i, "Sequential"));
        System.out.println("Время: " + (System.currentTimeMillis() - start) + "ms\n");
    }

    /// Истинный параллелизм (на уровне железа).
    /// Используется ForkJoinPool для распределения задач по ядрам процессора.
    public static void demonstrateTrueParallelism() {
        System.out.println("--- Истинный Параллелизм (Parallel Stream) ---");
        long start = System.currentTimeMillis();

        // parallel() делит задачу на подзадачи и раскидывает по пулу потоков ОС (по ядрам).
        // Если у процессора 8 ядер, все 5 задач выполнятся одновременно!
        IntStream.range(0, 5)
                .parallel()
                .forEach(i -> doHeavyWork(i, "Parallel"));

        System.out.println("Время: " + (System.currentTimeMillis() - start) + "ms\n");
    }

    /// Конкурентность (Многозадачность на уровне потоков).
    /// Мы создаем 100 потоков. Даже если у нас всего 4 ядра, ОС будет переключать контекст,
    /// создавая иллюзию одновременности для всех 100 задач.
    public static void demonstrateConcurrency() {
        System.out.println("--- Конкурентность (Context Switching) ---");
        long start = System.currentTimeMillis();

        // Запускаем 10 ручных потоков одновременно
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            threads[i] = new Thread(() -> doHeavyWork(taskId, "Concurrent"));
            threads[i].start(); // Запуск в фоне
        }

        // Ждем завершения всех потоков (Thread.join)
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException e) {}
        }

        System.out.println("Время: " + (System.currentTimeMillis() - start) + "ms\n");
    }

    // Имитация тяжелой процессорной или сетевой задачи (0.5 секунды)
    private static void doHeavyWork(int id, String type) {
        try {
            Thread.sleep(500);
            System.out.printf("[%s] Задача %d выполнена потоком %s%n",
                    type, id, Thread.currentThread().getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}