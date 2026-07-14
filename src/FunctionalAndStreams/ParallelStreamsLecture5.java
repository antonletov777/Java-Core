package FunctionalAndStreams;

import java.util.List;
import java.util.stream.IntStream;

/// # 5. Параллельные стримы (Parallel Streams)
/// Демонстрация принципов работы, нарушения порядка и главной опасности (блокировок).
@SuppressWarnings("ALL")
public class ParallelStreamsLecture5 {

    public static void main(String[] args) {
        demonstrateUnorderedExecution();
        demonstrateStatefulOverhead();
        demonstrateForkJoinPoolCaveat();
    }

    /// 1. Нарушение порядка обработки
    public static void demonstrateUnorderedExecution() {
        System.out.println("--- 1. Нарушение порядка (.parallelStream()) ---");
        List<Integer> data = IntStream.range(1, 10).boxed().toList();

        System.out.print("Обычный: ");
        data.stream().forEach(n -> System.out.print(n + " "));

        System.out.print("\nПараллельный: ");
        // Порядок абсолютно случаен, так как элементы раскидываются по пулу потоков
        data.parallelStream().forEach(n -> System.out.print(n + " "));
        System.out.println("\n");
    }

    /// 2. Оверхед Stateful-операций в параллельной среде
    public static void demonstrateStatefulOverhead() {
        System.out.println("--- 2. Оверхед Stateful операций ---");
        // parallel() + limit()/sorted() = плохая идея.
        // Потокам приходится синхронизироваться между собой, чтобы гарантировать
        // сохранение первых N элементов исходного порядка, что убивает всю параллельность.
        long count = IntStream.range(0, 100_000)
                .parallel()
                .filter(n -> n % 2 == 0)
                .limit(10) // Stateful операция тормозит параллельный стрим
                .count();
        System.out.println("Обработано (limit): " + count);
    }

    /// 3. ВАЖНО: Опасность блокировок (I/O операции)
    public static void demonstrateForkJoinPoolCaveat() {
        System.out.println("\n--- 3. Опасность блокировок пула ---");

        // По умолчанию размер ForkJoinPool.commonPool() = (Количество ядер CPU) - 1.
        // Если мы запустим внутри parallelStream() код, ожидающий ответа БД (Thread.sleep),
        // пул мгновенно забьется, и все остальные parallelStream в JVM зависнут.

        System.out.println("Размер Common Pool: " + java.util.concurrent.ForkJoinPool.commonPool().getPoolSize());

        long start = System.currentTimeMillis();
        List<Integer> tasks = IntStream.range(0, 4).boxed().toList();

        tasks.parallelStream().forEach(task -> {
            try {
                // ИМИТАЦИЯ ПОХОДА В БАЗУ ДАННЫХ (БЛОКИРУЮЩИЙ ВЫЗОВ). ТАК ДЕЛАТЬ НЕЛЬЗЯ!
                Thread.sleep(500);
                System.out.println("Задача " + task + " завершена потоком " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        System.out.println("Заняло " + (System.currentTimeMillis() - start) + " мс.");
        System.out.println("Если задач больше, чем потоков в пуле, время резко увеличится.");
    }
}