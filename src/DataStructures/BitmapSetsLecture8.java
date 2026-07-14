package DataStructures;

import java.util.BitSet;
import java.util.EnumSet;

/// # Bitmap-based structures: Битовые векторы и множества перечислений
/// Структуры, использующие побитовые операции над `long` для достижения максимальной производительности.
@SuppressWarnings("ALL")
public class BitmapSetsLecture8 {

    enum Status { NEW, IN_PROGRESS, COMPLETED, FAILED }

    public static void main(String[] args) {
        demonstrateEnumSet();
        demonstrateBitSet();
    }

    /// ## EnumSet
    /// Внутри использует битовую маску (`RegularEnumSet` для < 64 элементов хранит состояние в одном `long`).
    ///
    /// **Временная сложность:**
    /// - `add`, `remove`, `contains`: **O(1)** — выполняются за одну побитовую инструкцию процессора.
    /// - `EnumSet.of`, `EnumSet.allOf`: **O(1)**.
    public static void demonstrateEnumSet() {
        EnumSet<Status> activeStatuses = EnumSet.of(Status.NEW, Status.IN_PROGRESS);

        activeStatuses.add(Status.COMPLETED); // O(1)
        boolean isNew = activeStatuses.contains(Status.NEW); // O(1)

        EnumSet<Status> all = EnumSet.allOf(Status.class); // Создает сет со всеми элементами
    }

    /// ## BitSet
    /// Динамически расширяемый массив битов (под капотом `long[]`). Используется для флагов, фильтров Блума, плотных целочисленных множеств.
    ///
    /// **Временная сложность:**
    /// - `set(int bitIndex)`, `get(int bitIndex)`, `clear(int bitIndex)`: **O(1)**.
    /// - Побитовые логические операции (`and`, `or`, `xor` с другим BitSet): **O(N)**, где N — длина массива `long`.
    public static void demonstrateBitSet() {
        BitSet bitSet = new BitSet();

        bitSet.set(10); // Устанавливает 10-й бит в true (O(1))
        bitSet.set(20);

        boolean bit10 = bitSet.get(10); // true (O(1))
        bitSet.clear(20); // сбрасывает бит в false (O(1))

        BitSet other = new BitSet();
        other.set(10);

        bitSet.and(other); // Логическое И над двумя битовыми векторами
    }
}