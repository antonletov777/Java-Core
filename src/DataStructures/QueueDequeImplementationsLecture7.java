package DataStructures;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.Queue;

/// # Реализации Queue и Deque: Очереди
/// `Queue` — очередь (FIFO). `Deque` — двусторонняя очередь (LIFO/FIFO), которая является современной заменой устаревшему классу `Stack`.
///
/// ### Особенности методов Queue
/// Интерфейс предоставляет два набора методов для одних и тех же операций:
/// 1. **Выбрасывают исключения:** `add(e)`, `remove()`, `element()`.
/// 2. **Возвращают false/null:** `offer(e)`, `poll()`, `peek()`. Рекомендуется использовать именно их.
@SuppressWarnings("ALL")
public class QueueDequeImplementationsLecture7 {

    public static void main(String[] args) {
        demonstratePriorityQueue();
        demonstrateArrayDeque();
    }

    /// ## PriorityQueue
    /// Очередь с приоритетами, основанная на структуре "Min-Heap" (Двоичная куча).
    /// Элементы извлекаются в порядке их приоритета (Comparable / Comparator).
    /// **ВАЖНО:** Итератор по `PriorityQueue` не гарантирует обход элементов в отсортированном порядке! Порядок гарантирован только при извлечении через `poll()`.
    ///
    /// **Временная сложность:**
    /// - `offer(E e)` / `add(E e)`: **O(log N)** — добавление в конец кучи и просеивание вверх.
    /// - `poll()` (извлечь минимум): **O(log N)** — извлечение корня, перенос последнего элемента в корень и просеивание вниз.
    /// - `peek()` (посмотреть минимум): **O(1)** — корень дерева.
    /// - `remove(Object o)`: **O(N)** — линейный поиск элемента и перестроение кучи.
    public static void demonstratePriorityQueue() {
        Queue<Integer> pq = new PriorityQueue<>();

        // Добавление элементов
        pq.offer(10); // O(log N)
        pq.add(1); // O(log N), аналогично offer, но выбросит IllegalStateException при превышении емкости
        pq.offer(5); // O(log N)

        // Просмотр без извлечения
        Integer minVal = pq.peek(); // O(1), вернет 1

        // Удаление конкретного объекта
        boolean isRemoved = pq.remove(10); // O(N), удалит 10 и перестроит кучу

        // Извлечение элементов (гарантируется порядок приоритета)
        Integer firstPoll = pq.poll(); // O(log N), вернет 1
        Integer secondPoll = pq.poll(); // O(log N), вернет 5
    }

    /// ## ArrayDeque
    /// Двусторонняя очередь на базе кольцевого (циклического) массива.
    /// Быстрее `LinkedList` при работе с обоими концами. Не допускает хранение `null`.
    ///
    /// **Временная сложность:**
    /// - `addFirst(E e)`, `addLast(E e)`, `offerFirst`, `offerLast`: **O(1)** амортизированное.
    /// - `pollFirst()`, `pollLast()`, `removeFirst`, `removeLast`: **O(1)**.
    /// - `peekFirst()`, `peekLast()`, `getFirst`, `getLast`: **O(1)**.
    public static void demonstrateArrayDeque() {
        Deque<String> deque = new ArrayDeque<>();

        // 1. Использование как Deque (Двусторонняя очередь)
        deque.addFirst("Front"); // O(1)
        deque.addLast("Back"); // O(1)
        deque.offerFirst("New Front"); // O(1), безопасный вариант addFirst

        String first = deque.peekFirst(); // O(1), "New Front"
        String last = deque.peekLast(); // O(1), "Back"

        deque.pollFirst(); // O(1), извлечет "New Front"
        deque.pollLast(); // O(1), извлечет "Back"

        deque.clear();

        // 2. Использование как Стек (LIFO) - лучшая альтернатива java.util.Stack
        deque.push("Task 1"); // O(1), под капотом вызывает addFirst
        deque.push("Task 2");
        String topTask = deque.pop(); // O(1), вернет "Task 2" (под капотом removeFirst)

        deque.clear();

        // 3. Использование как классическая Очередь (FIFO)
        deque.offer("Job 1"); // O(1), под капотом вызывает offerLast
        deque.offer("Job 2");
        String nextJob = deque.poll(); // O(1), вернет "Job 1" (под капотом pollFirst)
    }
}