package DataStructures;

import java.util.*;
/// # Java Collections Framework и утилитарные классы: Полный гид
/// В этом классе объединены возможности `java.util.Collections` и `java.util.Arrays`.
///
/// ### Основные концепции:
/// 1. **Механизмы доступа**: От классических итераторов до `Spliterator` для параллелизма.
/// 2. **Инструментарий Arrays**: Манипуляции с массивами, глубокое сравнение и мост к Streams.
/// 3. **Алгоритмы Collections**: Сортировка, поиск, перемешивание и мутация.
/// 4. **Фабрики и Обертки**: Создание безопасных, неизменяемых и проверяемых коллекций.
@SuppressWarnings("ALL")
public class CollectionsAndArraysLecture3 {

    public static void main(String[] args) {
        demonstrateIteratorsAndFailFast();
        demonstrateSpliterators();
        demonstrateArraysUtility();
        demonstrateDeepArrayUtils();
        demonstrateUtilityAlgorithms();
        demonstrateAdvancedCollectionsUtils();
        demonstrateImmutableFactories();
        demonstrateCheckedCollections();
        demonstrateSynchronizedWrappers();
    }

    /// ## 1. Итераторы и поведение Fail-Fast
    /// `Iterator` — базовый способ обхода. `ListIterator` — только для списков, позволяет ходить в обе стороны.
    ///
    /// **Методы и сложности:**
    /// - `iterator.next()` / `hasNext()`: **O(1)**.
    /// - `iterator.remove()`: **O(N)** для ArrayList (сдвиг), **O(1)** для LinkedList.
    /// - `listIterator.add(e)` / `set(e)`: **O(1)** для текущей позиции.
    /// - `Fail-Fast`: выбрасывает `ConcurrentModificationException` при структурном изменении коллекции в обход итератора.
    public static void demonstrateIteratorsAndFailFast() {
        List<String> list = new ArrayList<>(List.of("A", "B", "C", "D"));

        // Использование Iterator для безопасного удаления
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String val = it.next();
            if (val.equals("B")) it.remove();
        }

        // ListIterator: навигация и модификация
        ListIterator<String> listIt = list.listIterator();
        listIt.next(); // Прошли "A"
        listIt.set("Modified_A"); // Заменили "A" на "Modified_A"
        listIt.add("New_After_A"); // Вставили элемент между 1-м и 2-м

        // Демонстрация Fail-Fast
        try {
            for (String s : list) {
                if (s.equals("C")) list.remove(s); // Прямое изменение коллекции
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("Ловушка Fail-Fast сработала: прямая модификация запрещена.");
        }
    }

    /// ## 2. Spliterator (Java 8)
    /// Предназначен для разделения источника данных на части для параллельной обработки.
    ///
    /// **Методы:**
    /// - `trySplit()`: **O(N)** (обычно делит пополам). Пытается отщепить часть элементов в новый Spliterator.
    /// - `tryAdvance(action)`: **O(1)**. Аналог `hasNext` + `next`.
    /// - `estimateSize()`: **O(1)**. Возвращает примерное количество оставшихся элементов.
    public static void demonstrateSpliterators() {
        List<Integer> data = List.of(1, 2, 3, 4, 5, 6);
        Spliterator<Integer> split1 = data.spliterator();

        // Попытка разделить данные для параллельной обработки
        Spliterator<Integer> split2 = split1.trySplit();

        System.out.println("Оценка размера split1: " + split1.estimateSize()); // 3
        if (split2 != null) {
            System.out.println("Оценка размера split2: " + split2.estimateSize()); // 3
            split2.forEachRemaining(n -> System.out.println("Split2 обрабатывает: " + n));
        }
        split1.tryAdvance(n -> System.out.println("Split1 обработал один элемент: " + n));
    }

    /// ## 3. Класс Arrays: Утилиты массивов
    /// Предоставляет алгоритмы для «сырых» массивов примитивов и объектов.
    ///
    /// **Методы и сложности:**
    /// - `Arrays.sort(arr)`: **O(N log N)**. (Dual-Pivot Quicksort для примитивов).
    /// - `Arrays.binarySearch(arr, key)`: **O(log N)**. Только для отсортированных массивов!
    /// - `Arrays.fill(arr, val)`: **O(N)**.
    /// - `Arrays.mismatch(arr1, arr2)`: **O(N)**. Возвращает индекс первого несовпадения.
    /// - `Arrays.stream(arr)`: **O(1)**. Создает поток.
    public static void demonstrateArraysUtility() {
        int[] arr = {10, 5, 20, 15};

        Arrays.sort(arr); // {5, 10, 15, 20}
        int idx = Arrays.binarySearch(arr, 15); // вернет 2

        int[] other = {5, 10, 99, 20};
        int firstDiff = Arrays.mismatch(arr, other); // вернет 2 (индекс, где 15 != 99)

        Arrays.fill(other, 0, 2, -1); // {-1, -1, 99, 20}

        long count = Arrays.stream(arr).filter(n -> n > 10).count(); // Stream API мост
    }

    /// ## 4. Глубокие операции с массивами
    /// Обычные `equals()` и `toString()` для массивов выводят только адрес/хеш-код.
    /// Методы `deep...` рекурсивно заходят во вложенные массивы.
    public static void demonstrateDeepArrayUtils() {
        Object[] nested1 = { new String[]{"A", "B"}, "C" };
        Object[] nested2 = { new String[]{"A", "B"}, "C" };

        System.out.println("Обычный toString: " + nested1.toString()); // [Ljava.lang.Object;@...
        System.out.println("Глубокий toString: " + Arrays.deepToString(nested1)); // [[A, B], C]

        System.out.println("Обычный equals: " + Arrays.equals(nested1, nested2)); // false (разные ссылки вложенных массивов)
        System.out.println("Глубокий equals: " + Arrays.deepEquals(nested1, nested2)); // true (содержимое идентично)
    }

    /// ## 5. Базовые алгоритмы Collections
    /// Статические методы для манипуляции коллекциями любого типа.
    ///
    /// **Сложности:**
    /// - `Collections.sort(list)`: **O(N log N)**.
    /// - `Collections.binarySearch(list, key)`: **O(log N)** для RandomAccess (ArrayList), **O(N)** для LinkedList.
    /// - `Collections.reverse(list)` / `shuffle(list)`: **O(N)**.
    /// - `Collections.addAll(col, ...)`: **O(M)**, где M — количество добавляемых элементов. Работает быстрее `list.addAll(Arrays.asList)`.
    public static void demonstrateUtilityAlgorithms() {
        List<Integer> nums = new ArrayList<>();
        Collections.addAll(nums, 3, 1, 4, 1, 5); // Быстрое наполнение

        Collections.sort(nums); // {1, 1, 3, 4, 5}
        int pos = Collections.binarySearch(nums, 3); // 2

        Collections.shuffle(nums); // Случайный порядок
        Collections.reverse(nums); // Разворот

        Integer max = Collections.max(nums); // O(N)
        Integer min = Collections.min(nums); // O(N)
    }

    /// ## 6. Продвинутые методы Collections
    /// **Методы и сложности:**
    /// - `Collections.frequency(col, obj)`: **O(N)**. Подсчет вхождений.
    /// - `Collections.disjoint(col1, col2)`: **O(N)** (или O(N log N)). `true`, если нет общих элементов.
    /// - `Collections.replaceAll(list, old, new)`: **O(N)**.
    /// - `Collections.nCopies(n, obj)`: **O(1)** по памяти. Создает неизменяемый «виртуальный» список.
    public static void demonstrateAdvancedCollectionsUtils() {
        List<String> list = new ArrayList<>(List.of("X", "Y", "X", "Z"));

        int countX = Collections.frequency(list, "X"); // 2
        Collections.replaceAll(list, "X", "W"); // {W, Y, W, Z}

        boolean noCommons = Collections.disjoint(list, List.of("A", "B")); // true

        // Создание списка из 100 элементов "N", занимающего память как 1 элемент
        List<String> virtual = Collections.nCopies(100, "N");

        Collections.swap(list, 0, 3); // O(1) для ArrayList. Поменяли W и Z местами.
    }

    /// ## 7. Фабрики Immutable и суррогаты
    /// Создание коллекций, которые нельзя изменить.
    ///
    /// **Методы:**
    /// - `List.of(...)` / `Set.of(...)`: **O(N)**. Создают настоящие иммутабельные коллекции (Java 9+).
    /// - `List.copyOf(col)`: **O(N)**. Делает защитную копию.
    /// - `Collections.unmodifiableList(list)`: **O(1)**. Обертка (View).
    /// - `Collections.emptyList()`: **O(1)**. Возвращает константный пустой объект.
    public static void demonstrateImmutableFactories() {
        List<String> realImmutable = List.of("One", "Two");
        // realImmutable.add("Three"); // Выбросит UnsupportedOperationException

        Map<String, Integer> map = Map.of("K1", 1, "K2", 2); // До 10 элементов
        Map<String, Integer> largeMap = Map.ofEntries(Map.entry("A", 1)); // Для > 10

        List<String> mutable = new ArrayList<>(List.of("A"));
        List<String> view = Collections.unmodifiableList(mutable);

        mutable.add("B");
        System.out.println("View видит изменения оригинала: " + view); // [A, B]
        // view.add("C"); // Ошибка
    }

    /// ## 8. Checked Collections (Проверяемые коллекции)
    /// Динамическая проверка типов в рантайме. Нужна для защиты от **Heap Pollution** при работе с Legacy кодом, использующим `Raw Types`.
    public static void demonstrateCheckedCollections() {
        List rawList = new ArrayList<String>();
        // Оборачиваем в checkedList, фиксируя тип String.class
        List<String> checked = Collections.checkedList(rawList, String.class);

        checked.add("Valid");

        try {
            List legacyRef = checked;
            legacyRef.add(100); // Exception вылетит ЗДЕСЬ, а не при попытке достать элемент.
        } catch (ClassCastException e) {
            System.out.println("Загрязнение кучи (Heap Pollution) остановлено динамически.");
        }
    }

    /// ## 9. Синхронизированные обертки (Legacy)
    /// Делают методы коллекции потокобезопасными (через `synchronized` по мьютексу).
    /// **Важно:** Итерация по таким коллекциям НЕ является потокобезопасной автоматически!
    public static void demonstrateSynchronizedWrappers() {
        List<String> syncList = Collections.synchronizedList(new ArrayList<>());

        syncList.add("Safe Add"); // Потокобезопасно

        // Правильный обход синхронизированной коллекции
        synchronized (syncList) {
            Iterator<String> it = syncList.iterator();
            while (it.hasNext()) {
                System.out.println("Внутри мьютекса: " + it.next());
            }
        }
    }
}