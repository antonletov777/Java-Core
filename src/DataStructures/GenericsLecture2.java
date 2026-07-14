package DataStructures;

import java.util.ArrayList;
import java.util.List;
/// # Generics (Дженерики): Полное руководство
/// Дженерики обеспечивают строгую проверку типов на этапе компиляции, устраняя необходимость ручного кастования (`ClassCastException` предотвращается до запуска программы).
///
/// ### 1. Type Erasure (Стирание типов)
/// Дженерики существуют **только на этапе компиляции**.
/// В байткоде информация о типах (`<T>`) удаляется. Типы заменяются на их границы (обычно `Object`), а компилятор автоматически вставляет инструкции `CHECKCAST` там, где происходит чтение.
/// Для сохранения полиморфизма в наследниках компилятор также генерирует невидимые *bridge-методы*.
///
/// ### 2. Правило PECS (Producer Extends, Consumer Super)
/// - **Producer (`? extends T`)**: Если коллекция только поставляет данные — она ковариантна. Из нее можно читать, но нельзя писать.
/// - **Consumer (`? super T`)**: Если коллекция только принимает данные — она контрвариантна. В нее можно писать, но чтение возвращает только `Object`.
@SuppressWarnings("ALL")
public class GenericsLecture2 {

    public static void main(String[] args) {
        demonstrateRawTypes();
        demonstrateInvarianceVsCovariance();
        demonstratePECS();
        demonstrateCustomGenericClass();
        demonstrateMultipleBounds();
        demonstrateHeapPollutionAndSafeVarargs(new ArrayList<String>(), new ArrayList<String>());
        demonstrateRestrictions();
    }

    /// ## 1. Сырые типы (Raw Types)
    /// Использование дженериков без указания типа (`List` вместо `List<String>`).
    /// Оставлены в Java исключительно для обратной совместимости с кодом до Java 5.
    /// **Использовать категорически запрещено**, так как они отключают проверку типов компилятором.
    public static void demonstrateRawTypes() {
        List rawList = new ArrayList<String>(); // Потеряли типизацию
        rawList.add(8); // Компилятор промолчит
        rawList.add("String");

        // При попытке чтения мы получим ClassCastException в рантайме,
        // так как ожидаем String, а там оказался Integer.
    }

    /// ## 2. Инвариантность vs Ковариантность
    /// Дженерики **инвариантны**: `List<String>` никак не связан с `List<Object>`. Это защищает от ошибок типизации.
    /// Массивы **ковариантны**: `String[]` является подтипом `Object[]`, что оставляет дыру для `ArrayStoreException` в рантайме.
    public static void demonstrateInvarianceVsCovariance() {
        // Ковариантность массивов (Опасно!)
        String[] stringArray = {"Java", "Spring"};
        Object[] objectArray = stringArray;
        try {
            objectArray[0] = 42; // Успешно компилируется, но падает в рантайме
        } catch (ArrayStoreException e) {
            System.out.println("Ошибка массивов предотвращена: " + e.getMessage());
        }

        // Инвариантность дженериков (Безопасно)
        List<String> stringList = new ArrayList<>();
        // List<Object> objectList = stringList; // ОШИБКА КОМПИЛЯЦИИ: несовместимые типы
    }

    /// ## 3. Маски (Wildcards) и правило PECS
    /// Классический пример из JDK — метод `Collections.copy(dest, src)`.
    /// `dest` принимает данные (Consumer -> `super`), `src` отдает (Producer -> `extends`).
    public static void demonstratePECS() {
        List<Integer> integers = new ArrayList<>(List.of(1, 2, 3));
        List<Number> numbers = new ArrayList<>();

        // ? extends Number (Ковариантность) - Коллекция выступает как Producer
        List<? extends Number> producer = integers;
        Number num = producer.get(0); // Читать безопасно
        // producer.add(4); // ОШИБКА: компилятор не знает конкретный тип списка

        // ? super Integer (Контрвариантность) - Коллекция выступает как Consumer
        List<? super Integer> consumer = numbers;
        consumer.add(42); // Писать безопасно, Integer является Number
        Object obj = consumer.get(0); // Читать можно только как Object
    }

    /// ## 4. Собственные Generic-классы
    /// Класс параметризуется типом `T` при создании объекта.
    static class Container<T> {
        private T item;

        public void setItem(T item) { this.item = item; }
        public T getItem() { return item; }
    }

    public static void demonstrateCustomGenericClass() {
        Container<String> stringContainer = new Container<>();
        stringContainer.setItem("Backend");
        System.out.println("Контейнер содержит: " + stringContainer.getItem());
    }

    /// ## 5. Множественные ограничения (Multiple Bounds)
    /// Тип может иметь одну границу-класс и сколько угодно границ-интерфейсов.
    /// Класс всегда должен идти первым в списке: `<T extends ClassA & InterfaceB & InterfaceC>`.
    public static <T extends Number & Comparable<T>> void processNumber(T number) {
        System.out.println("Число " + number + " реализует и Number, и Comparable.");
    }

    public static void demonstrateMultipleBounds() {
        processNumber(10); // Успешно, Integer расширяет Number и реализует Comparable
        // processNumber(new AtomicInteger(10)); // ОШИБКА: AtomicInteger расширяет Number, но не реализует Comparable
    }

    /// ## 6. Загрязнение кучи (Heap Pollution) и @SafeVarargs
    /// Смешивание varargs (`...`) и дженериков опасно, так как varargs под капотом — это массив,
    /// а массивы ковариантны и не сохраняют информацию о дженериках в рантайме.
    /// Аннотация @SafeVarargs говорит компилятору: "Я обещаю не писать в этот массив элементы другого типа".
    @SafeVarargs
    public static <T> void demonstrateHeapPollutionAndSafeVarargs(List<T>... lists) {
        // Если бы не @SafeVarargs, компилятор выдал бы "Possible heap pollution from parameterized vararg type"
        for (List<T> list : lists) {
            System.out.println("Обработка списка размером: " + list.size());
        }
    }

    /// ## 7. Жесткие ограничения дженериков в Java
    /// Из-за обратной совместимости и стирания типов в Java есть ряд запретов.
    public static void demonstrateRestrictions() {
        // 1. Нельзя использовать примитивы
        // List<int> ints = new ArrayList<>(); // ОШИБКА. Нужно List<Integer>

        // 2. Нельзя создать экземпляр T или массив T
        // T obj = new T(); // ОШИБКА
        // T[] array = new T[10]; // ОШИБКА

        // 3. Нельзя использовать instanceof с параметризованными типами
        List<String> strings = new ArrayList<>();
        // if (strings instanceof List<String>) {} // ОШИБКА

        // Разрешено только с маской без границ:
        if (strings instanceof List<?>) {
            System.out.println("Проверка через List<?> допустима");
        }
    }
}