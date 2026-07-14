package AnnotationsAndReflectionAPI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// # 1. Создание кастомных аннотаций
/// Здесь мы определяем аннотации, которые позже используем для маппинга класса в базу данных.
@SuppressWarnings("ALL")
public class CustomAnnotationsLecture1 {

    // Аннотация для класса (указывает имя таблицы)
    @Retention(RetentionPolicy.RUNTIME) // Доступна через рефлексию!
    @Target(ElementType.TYPE)           // Можно вешать только на классы
    public @interface Table {
        String name() default "";       // Если не указано, будем брать имя класса
    }

    // Аннотация для полей (указывает имя колонки в БД)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)          // Можно вешать только на поля
    public @interface Column {
        String name() default "";
        boolean isPrimaryKey() default false;
    }

    // Пример использования (Доменная модель)
    @Table(name = "users")
    public static class UserEntity {

        @Column(name = "user_id", isPrimaryKey = true)
        private Long id;

        @Column(name = "user_name")
        private String username;

        @Column // Имя колонки не указано, процессор должен будет использовать имя поля 'age'
        private int age;

        // Конструктор для удобства
        public UserEntity(Long id, String username, int age) {
            this.id = id;
            this.username = username;
            this.age = age;
        }

        // Приватный метод (мы доберемся до него через рефлексию во втором классе)
        private void secretMethod() {
            System.out.println("Это секретный метод пользователя " + username);
        }
    }

    public static void main(String[] args) {
        System.out.println("Аннотации созданы! Они пассивны и сами по себе ничего не делают.");
        System.out.println("Смотри классы Lecture 2 и Lecture 3, чтобы заставить их работать.");
    }
}