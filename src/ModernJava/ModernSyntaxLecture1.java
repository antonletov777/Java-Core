package ModernJava;

import java.time.DayOfWeek;
import java.util.List;

/// # 1. Современный синтаксис (var, Text Blocks, Switch Expressions)
public class ModernSyntaxLecture1 {

    public static void main(String[] args) {
        demonstrateVar();
        demonstrateTextBlocks();
        demonstrateSwitchExpression(DayOfWeek.FRIDAY);
    }

    /// Демонстрация вывода типов (Java 10)
    public static void demonstrateVar() {
        System.out.println("--- 1. var ---");

        // Раньше: List<String> list = List.of("Java");
        // Теперь компилятор сам понимает, что это List<String>
        var list = List.of("Java 8", "Java 17", "Java 21");

        for (var item : list) {
            System.out.println("Элемент: " + item);
        }
    }

    /// Демонстрация текстовых блоков (Java 13)
    public static void demonstrateTextBlocks() {
        System.out.println("\n--- 2. Текстовые блоки ---");

        // Не нужны конкатенации (+) и экранирование кавычек
        String json = """
                {
                    "language": "Java",
                    "version": 21,
                    "isAwesome": true
                }
                """;
        System.out.println(json);
    }

    /// Демонстрация выражений Switch (Java 12)
    public static void demonstrateSwitchExpression(DayOfWeek day) {
        System.out.println("\n--- 3. Switch Expressions ---");

        // Switch теперь возвращает значение и не требует break!
        int wordLength = switch (day) {
            case MONDAY, FRIDAY, SUNDAY -> 6;
            case TUESDAY                -> 7;
            case THURSDAY, SATURDAY     -> 8;
            case WEDNESDAY              -> 9;
        };

        System.out.println("Длина слова для " + day + " = " + wordLength);
    }
}