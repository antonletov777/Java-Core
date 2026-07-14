package DataStructures;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/// # Реализации Map: Словари (Ассоциативные массивы)
/// Хранят пары "ключ-значение". Ключи уникальны. Не наследуют интерфейс `Collection`.
@SuppressWarnings("ALL")
public class MapImplementationsLecture6 {

    public static void main(String[] args) {
        demonstrateHashMap();
        demonstrateLinkedHashMap();
        demonstrateTreeMap();
    }

    /// ## HashMap
    /// Хэш-таблица. Ключи не упорядочены. Разрешает коллизии методом цепочек (списки или красно-черные деревья).
    ///
    /// **Временная сложность:**
    /// - `put(K key, V value)`: **O(1)** в среднем.
    /// - `get(Object key)`: **O(1)**.
    /// - `remove(Object key)`: **O(1)**.
    /// - `containsKey(Object key)`: **O(1)** — вычисляется хэш и проверяется корзина.
    /// - `containsValue(Object value)`: **O(N)** — требует полного перебора всех корзин.
    /// - `putIfAbsent(K key, V value)`: **O(1)**.
    /// - `getOrDefault(Object key, V defaultValue)`: **O(1)**.
    public static void demonstrateHashMap() {
        Map<String, Integer> hashMap = new HashMap<>();

        // Базовые операции
        hashMap.put("One", 1); // O(1)
        hashMap.put("Two", 2); // O(1)
        hashMap.putIfAbsent("Two", 22); // O(1), значение не обновится

        Integer val = hashMap.get("One"); // O(1)
        Integer defaultVal = hashMap.getOrDefault("Three", 3); // O(1), вернет 3

        // Проверки наличия
        boolean hasKey = hashMap.containsKey("One"); // O(1), true
        boolean hasVal = hashMap.containsValue(2); // O(N), true

        hashMap.remove("Two"); // O(1)

        // Получение представлений коллекции (Views)
        var keys = hashMap.keySet(); // Set ключей
        var values = hashMap.values(); // Collection значений
        var entries = hashMap.entrySet(); // Set пар "ключ-значение"
    }

    /// ## LinkedHashMap
    /// Хэш-таблица с двусвязным списком. Сохраняет порядок вставки или порядок доступа (LRU).
    ///
    /// **Временная сложность:** **O(1)** для базовых операций. `containsValue` также работает за **O(N)**,
    /// но перебор идет по связному списку, что быстрее, чем прыжки по памяти в `HashMap`.
    public static void demonstrateLinkedHashMap() {
        // Третий параметр true включает порядок доступа (access-order) для LRU кэшей
        Map<String, String> lruMap = new LinkedHashMap<>(16, 0.75f, true);

        lruMap.put("A", "Alpha"); // O(1)
        lruMap.put("B", "Beta");
        lruMap.put("C", "Gamma");

        boolean hasVal = lruMap.containsValue("Beta"); // O(N)

        // Обращение к элементу меняет его позицию (перемещает в конец списка доступа)
        lruMap.get("A"); // O(1)

        // Порядок вывода будет: B, C, A
        System.out.println("Порядок LRU: " + lruMap.keySet());
    }

    /// ## TreeMap
    /// На базе красно-черного дерева. Ключи всегда отсортированы (Natural Ordering или Comparator).
    ///
    /// **Временная сложность:**
    /// - `put(K key, V value)`: **O(log N)**.
    /// - `get(Object key)`: **O(log N)**.
    /// - `containsKey(Object key)`: **O(log N)**.
    /// - `containsValue(Object value)`: **O(N)** — поиск по значениям не оптимизирован деревом.
    /// - Методы навигации (`firstEntry`, `lastEntry`, `subMap`): **O(log N)**.
    public static void demonstrateTreeMap() {
        TreeMap<Integer, String> treeMap = new TreeMap<>();

        treeMap.put(100, "Max"); // O(log N)
        treeMap.put(1, "Min"); // O(log N)
        treeMap.put(50, "Mid"); // O(log N)

        String val = treeMap.get(50); // O(log N)
        boolean hasKey = treeMap.containsKey(100); // O(log N)
        boolean hasVal = treeMap.containsValue("Min"); // O(N)

        // Навигация по дереву (NavigableMap)
        Map.Entry<Integer, String> first = treeMap.firstEntry(); // (1, "Min")
        Map.Entry<Integer, String> last = treeMap.lastEntry(); // (100, "Max")

        // Получение подмножеств (Views)
        Map<Integer, String> headMap = treeMap.headMap(50); // Все элементы ДО ключа 50
        Map<Integer, String> tailMap = treeMap.tailMap(50); // Все элементы ОТ ключа 50 и выше
        Map<Integer, String> subMap = treeMap.subMap(10, 90); // Элементы в диапазоне [10, 90)
    }
}