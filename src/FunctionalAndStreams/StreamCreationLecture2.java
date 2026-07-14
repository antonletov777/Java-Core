package FunctionalAndStreams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/// # 2. Создание Stream API
/// Исчерпывающая демонстрация всех актуальных способов получения потока.
@SuppressWarnings("ALL")
public class StreamCreationLecture2 {

    public static void main(String[] args) throws IOException {
        demonstrateStandardCreation();
        demonstrateInfiniteStreams();
        demonstratePrimitivesAndStrings();
        demonstrateAdvancedCreation();
    }

    /// 1. Стандартные коллекции, массивы, элементы и Builder
    public static void demonstrateStandardCreation() {
        System.out.println("--- 1. Стандартные стримы ---");
        Stream<String> fromList = List.of("A", "B").stream();
        Stream<String> fromArray = Arrays.stream(new String[]{"C", "D"});
        Stream<String> fromElements = Stream.of("E", "F");

        // Stream.builder() удобен, когда стрим собирается динамически в цикле
        Stream.Builder<String> builder = Stream.builder();
        builder.add("G").add("H");
        Stream<String> fromBuilder = builder.build();

        // Безопасность от Null (Java 9)
        Stream<String> nullSafe = Stream.ofNullable(null); // Вернет Stream.empty()
    }

    /// 2. Бесконечные генераторы
    public static void demonstrateInfiniteStreams() {
        System.out.println("\n--- 2. Бесконечные стримы (требуют limit!) ---");
        // iterate: предыдущее значение -> новое значение
        System.out.print("Iterate: ");
        Stream.iterate(2, n -> n * 2).limit(4).forEach(n -> System.out.print(n + " ")); // 2 4 8 16

        // generate: фабрика объектов
        System.out.print("\nGenerate: ");
        Stream.generate(() -> "X").limit(3).forEach(s -> System.out.print(s + " ")); // X X X
    }

    /// 3. Примитивы, Строки и Регулярки
    public static void demonstratePrimitivesAndStrings() {
        System.out.println("\n\n--- 3. Примитивы и Строки ---");
        System.out.print("IntStream.rangeClosed: ");
        IntStream.rangeClosed(1, 5).forEach(n -> System.out.print(n + " "));

        System.out.print("\nRandom ints: ");
        new Random().ints(3, 1, 100).forEach(n -> System.out.print(n + " "));

        // Разбор строки на символы (возвращает IntStream с кодами ASCII)
        System.out.print("\nString.chars(): ");
        "Java".chars().forEach(c -> System.out.print((char) c + " "));

        // Регулярные выражения (разбиение строки сразу в Stream)
        System.out.print("\nPattern.splitAsStream: ");
        Pattern.compile(",\\s*").splitAsStream("apple, banana, orange")
                .forEach(s -> System.out.print(s + " | "));
    }

    /// 4. Файлы и Конкатенация
    public static void demonstrateAdvancedCreation() throws IOException {
        System.out.println("\n\n--- 4. Конкатенация и Файлы ---");
        Stream<String> s1 = Stream.of("1", "2");
        Stream<String> s2 = Stream.of("3", "4");
        Stream<String> combined = Stream.concat(s1, s2);

        // Files.lines лениво читает файл, не переполняя ОЗУ
        Path dummyPath = Path.of("dummy.txt");
        if (!Files.exists(dummyPath)) Files.writeString(dummyPath, "Line1\nLine2");

        try (Stream<String> lines = Files.lines(dummyPath)) {
            System.out.println("Прочитано из файла: " + lines.count() + " строк.");
        }
        Files.delete(dummyPath);
    }
}