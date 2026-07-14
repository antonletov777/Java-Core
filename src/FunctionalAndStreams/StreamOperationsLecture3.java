package FunctionalAndStreams;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/// # 3. Конвейер Stream API: Операции
/// Строгое разделение: Базовые, Продвинутые, Терминальные, Срезы, Reduce.
@SuppressWarnings("ALL")
public class StreamOperationsLecture3 {

    public static void main(String[] args) {
        demonstrateIntermediateBasic();
        demonstrateIntermediateStateful();
        demonstrateSlicing();
        demonstrateTerminalBasicAndShortCircuiting();
        demonstrateReduce();
    }

    /// 1. Промежуточные (Stateless / Без состояния)
    public static void demonstrateIntermediateBasic() {
        System.out.println("--- 1. Базовые промежуточные ---");
        List<List<String>> nested = List.of(List.of("A", "B"), List.of("C"));

        nested.stream()
                .flatMap(List::stream)                 // Разглаживаем список списков
                .filter(s -> !s.equals("B"))           // Фильтруем
                .map(String::toLowerCase)              // Трансформируем
                .peek(s -> System.out.print(s + " "))  // Логируем без изменения потока
                .toList();                             // a c
        System.out.println();
    }

    /// 2. Промежуточные (Stateful / С состоянием)
    public static void demonstrateIntermediateStateful() {
        System.out.println("\n--- 2. Хранящие состояние (Stateful) ---");
        Stream.of(3, 1, 4, 1, 5, 9, 3)
                .distinct() // Копит элементы под капотом
                .sorted()   // Ждет окончания потока для сортировки
                .forEach(n -> System.out.print(n + " ")); // 1 3 4 5 9
        System.out.println();
    }

    /// 3. Срезы потока (Java 9)
    public static void demonstrateSlicing() {
        System.out.println("\n--- 3. Срезы потока ---");
        List<Integer> list = List.of(1, 2, 3, 4, 1, 2);

        System.out.println("limit(3): " + list.stream().limit(3).toList()); // [1, 2, 3]
        System.out.println("skip(3): " + list.stream().skip(3).toList());   // [4, 1, 2]

        // takeWhile прерывается на первом false
        System.out.println("takeWhile(< 4): " + list.stream().takeWhile(n -> n < 4).toList()); // [1, 2, 3]
        // dropWhile отбрасывает до первого false, затем берет всё
        System.out.println("dropWhile(< 4): " + list.stream().dropWhile(n -> n < 4).toList()); // [4, 1, 2]
    }

    /// 4. Базовые Терминальные и Прерыватели
    public static void demonstrateTerminalBasicAndShortCircuiting() {
        System.out.println("\n--- 4. Терминальные и Прерыватели ---");
        List<Integer> nums = List.of(5, 10, 15, 20);

        System.out.println("count: " + nums.stream().count());
        System.out.println("max: " + nums.stream().max(Integer::compareTo).get());

        // Прерыватели (останавливают стрим досрочно)
        System.out.println("allMatch(> 0): " + nums.stream().allMatch(n -> n > 0)); // true
        System.out.println("anyMatch(> 15): " + nums.stream().anyMatch(n -> n > 15)); // true
        System.out.println("noneMatch(< 0): " + nums.stream().noneMatch(n -> n < 0)); // true

        Optional<Integer> first = nums.stream().filter(n -> n > 10).findFirst();
        System.out.println("findFirst(> 10): " + first.orElse(-1)); // 15
    }

    /// 5. Все формы Reduce (Свертка)
    public static void demonstrateReduce() {
        System.out.println("\n--- 5. Reduce ---");
        List<Integer> nums = List.of(1, 2, 3, 4);

        // 1 аргумент: (Accumulator) -> Optional
        Optional<Integer> sum1 = nums.stream().reduce(Integer::sum);

        // 2 аргумента: (Identity, Accumulator) -> Значение
        int sum2 = nums.stream().reduce(0, Integer::sum);

        // 3 аргумента: (Identity, Accumulator, Combiner)
        // Критически важно для многопоточности и случаев, когда тип источника не равен типу результата
        List<String> words = List.of("A", "BB", "CCC");
        int totalLength = words.stream().reduce(
                0,
                (currentLen, word) -> currentLen + word.length(), // Accumulator: Integer + String
                Integer::sum                                      // Combiner: Integer + Integer
        );
        System.out.println("Суммарная длина строк: " + totalLength);
    }
}