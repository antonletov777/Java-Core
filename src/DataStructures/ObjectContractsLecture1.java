package DataStructures;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/// # Superclass Object: Контракты и базовые методы
/// Все классы в Java неявно наследуют класс `Object`.
///
/// ### Контракт equals(Object obj)
/// Переопределенный метод обязан соблюдать 5 математических правил:
/// 1. **Рефлексивность**: `x.equals(x)` всегда `true`.
/// 2. **Симметричность**: `x.equals(y)` вернет `true` тогда и только тогда, когда `y.equals(x)` вернет `true`.
/// 3. **Транзитивность**: если `x.equals(y)` и `y.equals(z)` равны `true`, то `x.equals(z)` тоже `true`.
/// 4. **Консистентность**: многократный вызов без изменения полей всегда возвращает одинаковый результат.
/// 5. **Сравнение с null**: `x.equals(null)` всегда `false`.
///
/// ### Контракт hashCode()
/// 1. **Внутренняя консистентность**: многократный вызов в рамках одного запуска приложения возвращает одно и то же число.
/// 2. **Связь с equals (Главное правило)**: Если `x.equals(y) == true`, то их `hashCode` **обязаны** совпадать.
/// 3. **Коллизии**: Если `x.equals(y) == false`, их `hashCode` **могут** совпадать. Однако хорошая хэш-функция стремится минимизировать коллизии для сохранения производительности хэш-таблиц (O(1)).
@SuppressWarnings("ALL")
public class ObjectContractsLecture1 {

    public static void main(String[] args) {
        demonstrateValidContract();
        demonstrateBrokenContract();
        demonstrateOtherMethods();
    }

    /// Демонстрация правильной работы хэш-коллекций при соблюдении контракта.
    public static void demonstrateValidContract() {
        System.out.println("--- Правильный контракт ---");
        Set<User> set = new HashSet<>();
        User user1 = new User(1, "Alice");
        User user2 = new User(1, "Alice"); // Логический дубликат

        set.add(user1);

        // HashSet сначала проверяет hashCode. Они равны.
        // Затем проверяет equals. Он возвращает true. Объект считается тем же самым.
        boolean added = set.add(user2);

        System.out.println("Дубликат добавлен? " + added); // false
        System.out.println("Размер множества: " + set.size()); // 1
        System.out.println("Содержит user2? " + set.contains(user2)); // true
    }

    /// Демонстрация фатальных ошибок (утечки памяти, потеря объектов), если переопределить только equals.
    public static void demonstrateBrokenContract() {
        System.out.println("\n--- Нарушенный контракт ---");
        Set<BadUser> set = new HashSet<>();
        BadUser badUser1 = new BadUser(1, "Bob");
        BadUser badUser2 = new BadUser(1, "Bob"); // Логический дубликат

        set.add(badUser1);

        // HashSet вычисляет hashCode для badUser2. Так как hashCode не переопределен,
        // берется identity hash code (адрес в памяти). Он отличается от badUser1!
        // HashSet кладет объект в ДРУГУЮ корзину. Метод equals даже не вызывается.
        boolean added = set.add(badUser2);

        System.out.println("Дубликат добавлен? " + added); // true (Ошибка бизнес-логики!)
        System.out.println("Размер множества: " + set.size()); // 2

        // Мы "потеряли" объект для поиска по новому инстансу с теми же данными
        System.out.println("Содержит ли сет new BadUser(1, 'Bob')? " + set.contains(new BadUser(1, "Bob"))); // false
    }

    /// Демонстрация других важных методов класса Object.
    public static void demonstrateOtherMethods() {
        System.out.println("\n--- Другие методы Object ---");
        User user = new User(2, "Charlie");

        // getClass() - возвращает объект Class, содержащий метаданные времени выполнения.
        Class<?> clazz = user.getClass();
        System.out.println("Имя класса: " + clazz.getName());

        // toString() - по умолчанию возвращает ИмяКласса@ХэшКодВШестнадцатеричнойСистеме.
        // У нас он переопределен для красивого вывода логов.
        System.out.println("toString: " + user.toString());
    }

    /// Идеально реализованный класс: переопределены equals, hashCode и toString.
    static class User {
        private final int id;
        private final String name;

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true; // Проверка по ссылке (оптимизация)
            if (obj == null || getClass() != obj.getClass()) return false; // Защита от null и сравнения разных классов
            User user = (User) obj;
            return id == user.id && Objects.equals(name, user.name); // Сравнение значимых полей
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name); // Генерация хэша на основе тех же полей, что и в equals
        }

        @Override
        public String toString() {
            return "User{id=" + id + ", name='" + name + "'}";
        }
    }

    /// Класс с ошибкой: equals переопределен, а hashCode оставлен стандартным (от Object).
    static class BadUser {
        private final int id;
        private final String name;

        public BadUser(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BadUser badUser = (BadUser) obj;
            return id == badUser.id && Objects.equals(name, badUser.name);
        }
        // ОШИБКА: Забыли переопределить hashCode!
    }
}