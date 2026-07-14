package DataStructures;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/// # Реализации List: Динамические массивы и Связные списки
/// `List` — упорядоченная коллекция, допускающая дубликаты и обеспечивающая доступ по индексу.
@SuppressWarnings("ALL")
public class ListImplementationsLecture4 {

    public static void main(String[] args) {
        demonstrateArrayList();
        demonstrateLinkedList();
    }

    /// ## ArrayList
    /// Реализован на базе динамического массива. Емкость по умолчанию — 10. При заполнении увеличивается в 1.5 раза.
    /// В 99% случаев используется именно он из-за высокой скорости работы с процессорным кэшем (локальность данных).
    ///
    /// **Временная сложность основных методов:**
    /// - `add(E e)` (в конец): **O(1)** амортизированное. При расширении массива — **O(N)**.
    /// - `add(int index, E e)` (в середину): **O(N)** — требует сдвига элементов вправо.
    /// - `get(int index)`, `set(int index, E e)`: **O(1)** — прямой доступ к памяти.
    /// - `remove(int index)` / `remove(Object o)`: **O(N)** — поиск и сдвиг элементов влево.
    /// - `indexOf(Object o)` / `contains(Object o)`: **O(N)**.
    /// - `subList(from, to)`: **O(1)** — возвращает View.
    public static void demonstrateArrayList() {
        // Оптимизация: если знаем примерное количество элементов, задаем емкость сразу (избегаем реаллокаций O(N))
        ArrayList<String> arrayList = new ArrayList<>(100);

        // Базовые операции
        arrayList.add("Java"); // O(1)
        arrayList.add(0, "Spring"); // O(N)

        String val = arrayList.get(1); // O(1)
        arrayList.set(1, "Java Core"); // O(1)

        // Поиск
        boolean hasSpring = arrayList.contains("Spring"); // O(N)
        int index = arrayList.indexOf("Java Core"); // O(N), вернет 1

        arrayList.remove("Spring"); // O(N)

        // Управление памятью (специфичные методы ArrayList)
        arrayList.ensureCapacity(200); // Гарантирует вместимость без расширения массива (O(N) единоразово)
        arrayList.trimToSize(); // Урезает внутренний массив до фактического размера size() для экономии памяти (O(N))

        // Представления (Views)
        // ВАЖНО: subList не копирует данные! Изменение subList изменит оригинальный arrayList.
        List<String> subList = arrayList.subList(0, 1); // O(1)
    }

    /// ## LinkedList
    /// Реализован как двусвязный список. Каждый узел хранит ссылку на предыдущий и следующий элементы.
    /// Также реализует интерфейс `Deque` (может работать как очередь и стек).
    /// В современной разработке используется редко из-за промахов кэша процессора и оверхеда памяти на узлы.
    ///
    /// **Временная сложность основных методов:**
    /// - `add(E e)` (в конец), `addFirst`, `addLast`: **O(1)**.
    /// - `get(int index)`, `set(int index, E e)`: **O(N)** — последовательный перебор с начала или конца (что ближе).
    /// - `remove(int index)`: **O(N)** для поиска + **O(1)** для перевешивания ссылок.
    /// - Вставка/Удаление через `ListIterator`: **O(1)** — единственное реальное преимущество структуры.
    public static void demonstrateLinkedList() {
        LinkedList<String> linkedList = new LinkedList<>();

        // Базовые операции и методы интерфейса Deque
        linkedList.add("Node 1"); // O(1)
        linkedList.addFirst("Node 0"); // O(1)
        linkedList.addLast("Node 2"); // O(1)

        // Доступ по индексу (Медленно!)
        String val = linkedList.get(1); // O(N) - обход по ссылкам

        linkedList.removeFirst(); // O(1)
        linkedList.removeLast(); // O(1)

        linkedList.add("Node 3");
        linkedList.add("Node 4");

        // Демонстрация главной фичи: модификация за O(1) в процессе обхода
        var iterator = linkedList.listIterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item.equals("Node 3")) {
                iterator.remove(); // O(1) - просто перевешивает ссылки соседних узлов
                iterator.add("Inserted Node"); // O(1) - вставка прямо на текущую позицию
            }
        }
    }
}