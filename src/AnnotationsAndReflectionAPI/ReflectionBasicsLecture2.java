package AnnotationsAndReflectionAPI;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/// # 2. Основы Reflection API
/// Демонстрация исследования класса и нарушения инкапсуляции (доступ к private).
@SuppressWarnings("ALL")
public class ReflectionBasicsLecture2 {

    public static void main(String[] args) throws Exception {
        // Создаем обычный объект
        CustomAnnotationsLecture1.UserEntity user =
                new CustomAnnotationsLecture1.UserEntity(1L, "Alice", 25);

        // 1. Получаем объект Class
        Class<?> clazz = user.getClass();
        System.out.println("Анализируем класс: " + clazz.getSimpleName());

        System.out.println("\n--- Поля класса ---");
        // getDeclaredFields() возвращает ВСЕ поля, включая private
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            System.out.println("Найдено поле: " + field.getType().getSimpleName() + " " + field.getName());
        }

        System.out.println("\n--- Чтение и изменение private поля ---");
        // Берем конкретное поле по имени
        Field usernameField = clazz.getDeclaredField("username");

        // МАГИЯ РЕФЛЕКСИИ: Отключаем проверку доступа Java!
        usernameField.setAccessible(true);

        // Читаем значение из конкретного объекта
        String currentName = (String) usernameField.get(user);
        System.out.println("Текущее имя: " + currentName);

        // Изменяем значение (даже если нет сеттера!)
        usernameField.set(user, "Alice_Hacked");
        System.out.println("Новое имя после рефлексии: " + usernameField.get(user));

        System.out.println("\n--- Вызов private метода ---");
        Method secretMethod = clazz.getDeclaredMethod("secretMethod");
        secretMethod.setAccessible(true);
        // Вызываем метод на объекте user. Второй аргумент null, так как метод не принимает параметров.
        secretMethod.invoke(user);
    }
}