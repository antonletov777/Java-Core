package FunctionalAndStreams;

import java.util.*;
import java.util.function.*;

/// # 1. Функциональные интерфейсы, Компараторы и Optional
@SuppressWarnings("ALL")
public class FunctionalInterfacesLecture1 {

    public static void main(String[] args) {
        demonstrateBasicInterfaces();
        demonstrateComparators();
        demonstrateMethodReferences();
        demonstrateOptional();
        demonstrateExceptionHandling();
    }

    /// 1. Все базовые функциональные интерфейсы
    public static void demonstrateBasicInterfaces() {
        System.out.println("--- 1. Базовые интерфейсы ---");
        Predicate<String> isNotEmpty = s -> s != null && !s.isEmpty();
        Function<String, Integer> toLength = String::length;
        Consumer<Integer> printer = n -> System.out.println("Длина: " + n);
        Supplier<String> defaultString = () -> "Default";
        UnaryOperator<String> toUpper = String::toUpperCase;

        BiFunction<String, String, Integer> totalLength = (s1, s2) -> s1.length() + s2.length();
        BiConsumer<String, Integer> printKeyVal = (k, v) -> System.out.println(k + ": " + v);

        if (isNotEmpty.test("Java")) {
            String str = toUpper.apply("Java");
            printer.accept(toLength.apply(str));
        }
    }

    /// 2. Использование Comparator как функционального интерфейса
    public static void demonstrateComparators() {
        System.out.println("\n--- 2. Компараторы ---");
        record User(String name, int age) {}
        List<User> users = Arrays.asList(
                new User("Alice", 30),
                new User("Bob", 25),
                new User("Charlie", 25)
        );

        // Цепочное сравнение: сначала по возрасту, затем по имени в обратном порядке
        users.sort(Comparator.comparingInt(User::age)
                .thenComparing(User::name, Comparator.reverseOrder()));

        users.forEach(u -> System.out.println(u.name() + " (" + u.age() + ")"));
    }

    /// 3. Все 4 вида Method References
    public static void demonstrateMethodReferences() {
        System.out.println("\n--- 3. Ссылки на методы ---");
        // 1. Статический
        Function<String, Double> parseDouble = Double::parseDouble;
        // 2. Метод конкретного объекта
        String prefix = "User: ";
        Function<String, String> addPrefix = prefix::concat;
        // 3. Метод произвольного объекта
        Function<String, String> trim = String::trim;
        // 4. Конструктор
        Supplier<List<String>> listFactory = ArrayList::new;
    }

    /// 4. Optional и его интеграция со Stream API (Java 9)
    public static void demonstrateOptional() {
        System.out.println("\n--- 4. Optional.stream() ---");
        List<Integer> ids = List.of(1, 2, 3);

        // Получаем List<String> только существующих юзеров, откидывая Optional.empty()
        List<String> validUsers = ids.stream()
                .map(FunctionalInterfacesLecture1::findUserOptional)
                .flatMap(Optional::stream) // Ключевая фича Java 9!
                .toList();

        System.out.println("Найденные юзеры: " + validUsers);
    }

    /// 5. Паттерн обертки для Checked Exceptions
    public static void demonstrateExceptionHandling() {
        List<String> files = List.of("file1.txt", "file2.txt");
        // file -> Files.readString(Path.of(file)) бросит IOException.
        // Имитируем через обертку wrap():
        files.stream()
                .map(wrap(FunctionalInterfacesLecture1::readDataThrowingException))
                .forEach(System.out::println);
    }

    // --- Вспомогательные методы ---
    private static Optional<String> findUserOptional(int id) {
        return id % 2 != 0 ? Optional.of("User_" + id) : Optional.empty();
    }

    private static String readDataThrowingException(String path) throws Exception {
        return "Data from " + path; // Имитация throws Exception
    }

    private static <T, R> Function<T, R> wrap(CheckedFunction<T, R> function) {
        return t -> {
            try { return function.apply(t); }
            catch (Exception e) { throw new RuntimeException(e); }
        };
    }

    @FunctionalInterface
    interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }
}