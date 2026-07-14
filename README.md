# Java Core: Учебные материалы и справочник

[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/)

Данный репозиторий представляет собой структурированную базу знаний и набор практических примеров по фундаментальным темам платформы Java (Java SE). Проект разбит на логические модули-лекции, охватывающие работу со структурами данных, потоками ввода-вывода, функциональным программированием и многопоточностью.

## 🗂 Структура проекта

Кодовая база разделена на 7 основных разделов, расположенных в директории `src/`:

### 1. 🧱 DataStructures (Структуры данных)
Детальный разбор Java Collections Framework и принципов ООП:
* `ObjectContractsLecture1`: Контракты методов `equals()`, `hashCode()`, `toString()`.
* `GenericsLecture2`: Обобщения (Generics), PECS, сырые типы (Raw types).
* `CollectionsAndArraysLecture3`: Работа с массивами и базовыми утилитами коллекций `Collections` и `Arrays`.
* `ListImplementationsLecture4`, `SetImplementationsLecture5`, `MapImplementationsLecture6`, `QueueDequeImplementationsLecture7`: Внутреннее устройство, вычислительная сложность и нюансы использования основных структур данных (`ArrayList`, `LinkedList`, `HashMap`, `TreeSet` и др.).
* `BitmapSetsLecture8`: Особенности использования битовых множеств (`BitSet`).

### 2. 🔀 FunctionalAndStreams (Функциональное программирование)
Глубокое погружение в функциональные интерфейсы и Stream API:
* `FunctionalInterfacesLecture1`: Встроенные и пользовательские функциональные интерфейсы (`Predicate`, `Function`, `Consumer`, `Supplier`).
* `StreamCreationLecture2` & `StreamOperationsLecture3`: Способы создания стримов, промежуточные (intermediate) и терминальные (terminal) операции.
* `CollectorsPracticesLecture4`: Практика работы с классом `Collectors` (группировка, партиционирование, агрегация данных).
* `ParallelStreamsLecture5`: Использование параллельных стримов, принцип работы ForkJoinPool и потенциальные проблемы.

### 3. 💾 IOAndFileSystem (Ввод-вывод и файловая система)
Классический `java.io` и современный `java.nio`:
* `StreamsHierarchyLecture1`, `StandardIOLecture3`: Иерархия байтовых (`InputStream`/`OutputStream`) и символьных (`Reader`/`Writer`) потоков.
* `BufferedIOLecture2`: Буферизированный ввод-вывод для оптимизации производительности.
* `SerializationLecture7`: Механизмы встроенной сериализации и десериализации объектов.
* `NioCoreLecture8` & `Nio2FilesPathsLecture5`: Неблокирующий I/O, каналы (Channels), буферы (Buffers), селекторы, работа с путями через `Path` и `Files`.
* `WatchServiceLecture6`: Отслеживание изменений (создание, удаление, модификация) в файловой системе.
* `ParsingPerformanceLecture4`: Анализ производительности различных подходов к парсингу файлов.
* `DeveloperNotesIOLecture9`: Иллюстрация полезных фичей `FastScanner` и `Files API`.

### 4. ⚙️ Multithreading (Многопоточность)
Основы и продвинутые концепции конкурентного программирования:
* `ConcurrencyVsParallelismLecture1` & `ThreadBasicsLecture2`: Базовые понятия, жизненный цикл `Thread`, создание и прерывание потоков.
* `JmmAndSyncLecture3`: Модель памяти Java (JMM), видимость переменных, ключевые слова `volatile` и `synchronized`, правила happens-before.
* `MultithreadingProblemsLecture4`: Проблемы конкурентного доступа — Race conditions, Deadlocks, Livelocks и Starvation.
* `AdvancedLocksLecture5`: Использование пакета `java.util.concurrent.locks` (`ReentrantLock`, `ReadWriteLock`, `Condition`).
* `ExecutorsAndPoolsLecture6`: Пулы потоков (Thread Pools) и интерфейс `ExecutorService`.
* `CompletableFutureLecture7`: Асинхронное программирование, комбинирование и обработка ошибок в цепочках задач.
* `VirtualThreadsAndSpringLecture8`: Работа с легковесными виртуальными потоками (Project Loom) и их интеграция.

### 5. 🔍 AnnotationsAndReflectionAPI (Аннотации и Reflection API)
Метапрограммирование, анализ и модификация кода:
* `CustomAnnotationsLecture1`: Создание и использование пользовательских аннотаций, политики удержания (@Retention).
* `ReflectionBasicsLecture2`: Основы Reflection API (анализ классов, полей, методов и создание экземпляров во время выполнения).
* `AnnotationProcessorLecture3`: Обработка аннотаций на этапе компиляции (Annotation Processing).

### 6. ✨ ModernJava (Современная Java)
Нововведения последних версий языка Java:
* `ModernSyntaxLecture1`: Обновленный синтаксис (var, switch-выражения, текстовые блоки).
* `DataModelingLecture2`: Современное моделирование данных (Records для неизменяемых DTO, Sealed-классы).
* `ConcurrencyAndNetworkLecture3`: Краткий обзор новых возможностей в многопоточности (Project Loom) и работе с сетью.

### 7. 🌐 Networking (Сеть)
Разработка сетевых приложений на Java:
* `BlockingSocketLecture1`: Традиционный подход с использованием блокирующих сокетов (`java.net.Socket`, `ServerSocket`).
* `NioSocketLecture2`: Неблокирующий сетевой ввод-вывод (`java.nio.channels.SocketChannel`, селекторы).
* `ModernHttpClientLecture3`: Использование современного HTTP-клиента (`java.net.http.HttpClient`) для асинхронных запросов.

## 🚀 Как использовать

Репозиторий представляет собой набор независимых Java-классов. Каждый класс-лекция содержит теоретические комментарии и исполняемый метод `main` для демонстрации концепций в консоли.

1. Склонируйте репозиторий на локальную машину.
2. Откройте директорию `Java-Core` в вашей IDE (IntelliJ IDEA, Eclipse и др.).
3. Изучайте код лекций последовательно или выборочно, запуская соответствующие методы `main`.

## 🛠 Стек технологий

* **Java:** Рекомендуется **JDK 21+**, так как проект включает примеры работы с современными фичами языка (Virtual Threads из Java 21, Records, Pattern Matching).
* **Среда разработки:** Проект содержит стандартные файлы конфигурации IntelliJ IDEA (`.iml`), но может быть открыт в любой IDE с поддержкой Java.