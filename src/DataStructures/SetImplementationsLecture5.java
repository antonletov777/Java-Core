package DataStructures;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/// # Реализации Set: Множества
/// `Set` гарантирует уникальность элементов. Все стандартные реализации под капотом используют соответствующие `Map`.
@SuppressWarnings("ALL")
public class SetImplementationsLecture5 {

    public static void main(String[] args) {
        demonstrateHashSet();
        demonstrateLinkedHashSet();
        demonstrateTreeSet();
    }

    /// ## HashSet
    /// Базируется на `HashMap`. Элементы не упорядочены. Лучший выбор для обеспечения уникальности.
    ///
    /// **Временная сложность:**
    /// - `add(E e)`: **O(1)** (в худшем случае O(N) или O(log N) при коллизиях).
    /// - `remove(Object o)`: **O(1)**.
    /// - `contains(Object o)`: **O(1)**.
    /// - `size()`, `isEmpty()`: **O(1)**.
    /// - Теоретико-множественные операции (`addAll`, `retainAll`, `removeAll`): **O(N)**.
    public static void demonstrateHashSet() {
        Set<String> hashSet = new HashSet<>();

        // Базовые операции
        hashSet.add("A"); // O(1)
        hashSet.add("B"); // O(1)
        hashSet.add("A"); // Игнорируется, O(1)

        boolean hasA = hashSet.contains("A"); // O(1), true
        int size = hashSet.size(); // O(1), 2
        boolean isEmpty = hashSet.isEmpty(); // O(1), false

        hashSet.remove("B"); // O(1)

        // Массовые операции (Теория множеств)
        Set<String> otherSet = Set.of("A", "C", "D");
        hashSet.addAll(otherSet); // Объединение (Union), O(N)
        hashSet.retainAll(Set.of("A", "C")); // Пересечение (Intersection), оставляет только общие элементы, O(N)
        hashSet.removeAll(Set.of("C")); // Разность (Difference), удаляет указанные элементы, O(N)

        hashSet.clear(); // Очистка множества, O(N)
    }

    /// ## LinkedHashSet
    /// Базируется на `LinkedHashMap`. Сохраняет порядок добавления элементов благодаря дополнительному двусвязному списку.
    ///
    /// **Временная сложность:** аналогична `HashSet` (**O(1)** для `add`, `remove`, `contains`), но требует чуть больше памяти.
    public static void demonstrateLinkedHashSet() {
        Set<String> linkedHashSet = new LinkedHashSet<>();

        linkedHashSet.add("First"); // O(1)
        linkedHashSet.add("Second"); // O(1)
        linkedHashSet.add("Third"); // O(1)

        boolean hasSecond = linkedHashSet.contains("Second"); // O(1)
        linkedHashSet.remove("Second"); // O(1)

        // Порядок итерации гарантированно: "First", затем "Third"
        for (String item : linkedHashSet) {
            System.out.println("Элемент LinkedHashSet: " + item);
        }
    }

    /// ## TreeSet
    /// Базируется на `TreeMap` (Красно-черное дерево). Элементы автоматически сортируются (Natural Ordering или по `Comparator`).
    ///
    /// **Временная сложность:**
    /// - `add(E e)`, `remove(Object o)`, `contains(Object o)`: **O(log N)**.
    /// - `first()`, `last()`: **O(log N)**.
    /// - `lower(E e)` (<), `floor(E e)` (<=): **O(log N)**.
    /// - `higher(E e)` (>), `ceiling(E e)` (>=): **O(log N)**.
    /// - `subSet`, `headSet`, `tailSet`: **O(log N)**.
    public static void demonstrateTreeSet() {
        TreeSet<Integer> treeSet = new TreeSet<>();

        treeSet.add(10); // O(log N)
        treeSet.add(5); // O(log N)
        treeSet.add(20); // O(log N)
        treeSet.add(15); // O(log N)

        boolean hasTen = treeSet.contains(10); // O(log N), true
        treeSet.remove(15); // O(log N)

        // Навигация
        Integer min = treeSet.first(); // O(log N), 5
        Integer max = treeSet.last(); // O(log N), 20

        Integer strictlyLess = treeSet.lower(10); // O(log N), 5
        Integer lessOrEqual = treeSet.floor(10); // O(log N), 10
        Integer strictlyGreater = treeSet.higher(10); // O(log N), 20
        Integer greaterOrEqual = treeSet.ceiling(11); // O(log N), 20

        // Подмножества (Views)
        Set<Integer> headSet = treeSet.headSet(10); // Все элементы < 10
        Set<Integer> tailSet = treeSet.tailSet(10); // Все элементы >= 10
        Set<Integer> subSet = treeSet.subSet(5, 20); // Элементы в диапазоне [5, 20)
    }
}