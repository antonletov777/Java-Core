package AnnotationsAndReflectionAPI;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/// # 3. Метапрограммирование (ORM Генератор)
/// Связываем аннотации и рефлексию: пишем код, который генерирует SQL-запросы
/// на основе аннотаций любого переданного объекта.
@SuppressWarnings("ALL")
public class AnnotationProcessorLecture3 {

    public static void main(String[] args) throws IllegalAccessException {
        CustomAnnotationsLecture1.UserEntity user =
                new CustomAnnotationsLecture1.UserEntity(100L, "Bob", 30);

        // Передаем объект в наш "фреймворк"
        String sql = generateInsertSql(user);
        System.out.println("\nСгенерированный SQL-запрос:");
        System.out.println(sql);
    }

    /// Универсальный метод. Ничего не знает о классе UserEntity напрямую!
    /// Анализирует объект только через рефлексию и аннотации.
    public static String generateInsertSql(Object obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();

        // 1. Проверяем наличие аннотации @Table
        if (!clazz.isAnnotationPresent(CustomAnnotationsLecture1.Table.class)) {
            throw new IllegalArgumentException("Объект не аннотирован @Table");
        }

        // 2. Получаем имя таблицы
        CustomAnnotationsLecture1.Table tableAnn = clazz.getAnnotation(CustomAnnotationsLecture1.Table.class);
        String tableName = tableAnn.name().isEmpty() ? clazz.getSimpleName().toLowerCase() : tableAnn.name();

        // Подготавливаем списки для колонок и значений
        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();

        // 3. Перебираем все поля
        for (Field field : clazz.getDeclaredFields()) {
            // Обрабатываем только поля с аннотацией @Column
            if (field.isAnnotationPresent(CustomAnnotationsLecture1.Column.class)) {
                CustomAnnotationsLecture1.Column columnAnn = field.getAnnotation(CustomAnnotationsLecture1.Column.class);

                // Определяем имя колонки (из аннотации или из имени поля)
                String columnName = columnAnn.name().isEmpty() ? field.getName() : columnAnn.name();
                columns.add(columnName);

                // Достаем значение поля из переданного объекта
                field.setAccessible(true); // Разрешаем доступ к private
                Object value = field.get(obj); // Читаем значение

                // Форматируем значение для SQL (строки берем в кавычки)
                if (value instanceof String) {
                    values.add("'" + value + "'");
                } else {
                    values.add(String.valueOf(value));
                }
            }
        }

        // 4. Склеиваем итоговый SQL запрос
        return String.format("INSERT INTO %s (%s) VALUES (%s);",
                tableName,
                String.join(", ", columns),
                String.join(", ", values)
        );
    }
}