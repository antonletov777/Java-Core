package ModernJava;

/// # 2. Моделирование данных и Pattern Matching (Records, Sealed Classes, instanceof)
public class DataModelingLecture2 {

    // Sealed interface (Java 15) - строго задаем, кто может нас реализовать
    public sealed interface Shape permits Circle, Rectangle {}

    // Records (Java 16) - идеальны для DTO и хранения данных
    public record Circle(double radius) implements Shape {}
    public record Rectangle(double length, double width) implements Shape {}

    // Вложенный Record для демонстрации деструктуризации
    public record GPSPoint(double lat, double lng) {}
    public record Location(String name, GPSPoint coordinates) {}

    public static void main(String[] args) {
        Shape myCircle = new Circle(5.0);
        Shape myRect = new Rectangle(10.0, 4.0);

        System.out.println("Площадь круга: " + calculateArea(myCircle));
        System.out.println("Площадь прямоугольника: " + calculateArea(myRect));

        Location loc = new Location("Офис", new GPSPoint(55.75, 37.61));
        demonstrateRecordPattern(loc);
    }

    /// Pattern Matching для switch (Java 19+)
    public static double calculateArea(Shape shape) {
        // Компилятор знает, что Shape может быть ТОЛЬКО Circle или Rectangle.
        // Блок default не нужен!
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Rectangle r -> r.length() * r.width();
        };
    }

    /// Record Patterns - Деструктуризация (Java 19+)
    public static void demonstrateRecordPattern(Object obj) {
        System.out.println("\n--- Деструктуризация Records ---");

        // Мы сразу "разбираем" объект Location на переменные name, lat и lng
        if (obj instanceof Location(String name, GPSPoint(double lat, double lng))) {
            System.out.printf("Локация: %s. Координаты: [%f, %f]%n", name, lat, lng);
        }
    }
}