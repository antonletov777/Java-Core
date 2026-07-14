# Исчерпывающее руководство: Функциональное программирование и Stream API в Java



---

## 1. Функциональные интерфейсы, Компараторы и Optional
*Соответствующий класс: `FunctionalInterfacesLecture1.java`*

Функциональное программирование базируется на передаче поведения как аргумента и отказе от null-ссылок.

### 1.1. Базовые функциональные интерфейсы (@FunctionalInterface)
Интерфейс с ровно одним абстрактным методом (SAM).
* **`Predicate<T>`**: `T -> boolean`. (Условие, фильтрация).
* **`Function<T, R>`**: `T -> R`. (Преобразование, маппинг).
* **`Consumer<T>`**: `T -> void`. (Потребитель, изменение состояния).
* **`Supplier<T>`**: `() -> T`. (Поставщик, ленивая инициализация).
* **`UnaryOperator<T>`**: `T -> T`. (Маппинг в тот же тип).
* **`BiFunction<T, U, R>`**, **`BiConsumer<T, U>`** и т.д. — работают с двумя аргументами.
* **`Runnable`** / **`Callable<V>`** — выполнение задач (без аргументов).

### 1.2. Компараторы (Comparator)
Мощнейший функциональный интерфейс для сортировки. В Java 8 получил множество `default` и `static` методов.
* `Comparator.comparing(Function)` — базовая сортировка по ключу.
* `.thenComparing()` — цепочная (вторичная) сортировка.
* `.reversed()` — обратный порядок.

### 1.3. Ссылки на методы (Method References)
Делают код декларативным (`::`).
* Статические: `Integer::parseInt`
* Конкретного объекта: `System.out::println`
* Произвольного объекта: `String::toUpperCase`
* Конструкторы: `ArrayList::new`

### 1.4. Монада Optional<T>
Заменяет проверки `if (obj != null)`. Поддерживает `map()`, `flatMap()`, `filter()`, `ifPresent()`, `orElseGet()`, `orElseThrow()`.
* **Важно (Java 9):** `Optional.stream()` позволяет бесшовно интегрировать Optional в Stream API через `flatMap`, автоматически отсеивая пустые значения.

### 1.5. Проблема Checked Exceptions в Лямбдах
Лямбды не объявляют `throws Exception`. Если метод бросает проверяемое исключение, используется паттерн Wrapper — вспомогательный метод, оборачивающий вызов в `try-catch` и бросающий `RuntimeException`.

---

## 2. Создание Stream API
*Соответствующий класс: `StreamCreationLecture2.java`*

**Stream** — абстракция для декларативной обработки данных. Не хранит данные, иммутабелен, ленив (ждет терминальной операции), одноразов.

### 2.1. Все способы создания Stream
* Коллекции и массивы: `list.stream()`, `Arrays.stream(arr)`.
* Перечисление: `Stream.of("A", "B")`.
* Безопасность от null (Java 9): `Stream.ofNullable(obj)`.
* Паттерн Builder: `Stream.builder().add("1").build()`.
* Бесконечные генераторы: `Stream.generate(Supplier)`, `Stream.iterate(seed, UnaryOperator)`.
* Строки и Регулярки: `"String".chars()`, `Pattern.compile(",").splitAsStream("a,b")`.
* Файлы (I/O): `Files.lines(Path)`, `Files.walk(Path)`.
* Примитивные: `IntStream.range()`, `new Random().ints()`.
* Объединение: `Stream.concat(s1, s2)`.

---

## 3. Конвейер Stream API: Операции
*Соответствующий класс: `StreamOperationsLecture3.java`*

### 3.1. Промежуточные операции (Intermediate)
* **Stateless (Базовые):** Не требуют памяти для других элементов.
  * `filter(Predicate)` — фильтрация.
  * `map(Function)` — преобразование 1-в-1.
  * `flatMap(Function)` — расплющивание 1-ко-многим (например, из списка списков в список).
  * `peek(Consumer)` — промежуточное действие (побочный эффект).
* **Stateful (Хранящие состояние):** Накапливают данные в памяти. Опасны для бесконечных потоков.
  * `distinct()` — уникальные элементы.
  * `sorted()` / `sorted(Comparator)` — сортировка.
* **Срезы потока:**
  * До Java 9: `limit(n)` (оставить N), `skip(n)` (пропустить N).
  * **Java 9+:** `takeWhile(Predicate)` (брать, пока истинно), `dropWhile(Predicate)` (отбрасывать, пока истинно).

### 3.2. Терминальные операции (Terminal)
* **Базовые:** `forEach()`, `count()`, `min()`, `max()`.
* **Сборка:** `toList()`, `collect()`.
* **Прерыватели (Short-circuiting):** `anyMatch()`, `allMatch()`, `noneMatch()`, `findFirst()`, `findAny()`.
* **Свертка (Reduce):**
  * 1 аргумент: `reduce(BinaryOperator)` -> `Optional`.
  * 2 аргумента: `reduce(Identity, Accumulator)` -> `Значение`.
  * 3 аргумента: `reduce(Identity, Accumulator, Combiner)` -> Критически важно для распараллеливания, где Combiner объединяет результаты разных потоков.

---

## 4. Продвинутая сборка (Collectors)
*Соответствующий класс: `CollectorsPracticesLecture4.java`*

Класс `Collectors` упаковывает результаты потока.
* В коллекции: `toList()`, `toSet()`, `toCollection(LinkedList::new)`.
* В Map: `toMap(keyMapper, valueMapper, mergeFunction)` — `mergeFunction` обязательна при дубликатах ключей.
* Группировка: `groupingBy()` — создание словаря по критерию.
* Разделение: `partitioningBy(Predicate)` — разбиение строго на две группы (true/false).
* Статистика: `summarizingDouble()` — `sum, min, max, avg, count` за один проход.
* Строки: `joining(delimiter, prefix, suffix)`.
* **Java 12+ `teeing`** — пропуск потока через два разных коллектора одновременно и объединение их результатов.

---

## 5. Параллельные стримы (Parallel Streams)
*Соответствующий класс: `ParallelStreamsLecture5.java`*

Метод `.parallelStream()` запускает многопоточную обработку.
* **Под капотом:** Используется `ForkJoinPool.commonPool()`.
* **Особенности:** Порядок обработки нарушается. `Stateful` операции (`sorted`, `limit`) работают медленнее и потребляют много памяти.
* **Когда использовать:** CPU-Intensive задачи на огромных массивах данных (сотни тысяч элементов).
* **Главная опасность (Блокировка):** Вызов блокирующих I/O операций (HTTP-запросы, Thread.sleep, запросы в БД) внутри `parallelStream` заблокирует весь общий пул потоков JVM, обрушив производительность всего приложения.