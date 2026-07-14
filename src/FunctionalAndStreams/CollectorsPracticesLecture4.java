package FunctionalAndStreams;

import java.util.*;
import java.util.stream.Collectors;

/// # 4. Продвинутая сборка (Collectors)
/// Исчерпывающая демонстрация всех востребованных коллекторов на доменной модели.
@SuppressWarnings("ALL")
public class CollectorsPracticesLecture4 {

    record Item(String name, String category, double price) {}

    public static void main(String[] args) {
        List<Item> items = List.of(
                new Item("Laptop", "Electronics", 1200),
                new Item("Phone", "Electronics", 800),
                new Item("Book", "Education", 50),
                new Item("Pen", "Education", 5)
        );

        // 1. toCollection (Явное указание реализации)
        LinkedList<Item> linkedList = items.stream()
                .collect(Collectors.toCollection(LinkedList::new));

        // 2. joining (Склейка строк с разделителем, префиксом и суффиксом)
        String catalog = items.stream()
                .map(Item::name)
                .collect(Collectors.joining(", ", "Catalog: [", "]"));
        System.out.println(catalog);

        // 3. toMap (С разрешением коллизий!)
        // Если попытаться сделать ключ 'category', будет IllegalStateException (дубли).
        // Поэтому применяем mergeFunction: суммируем цены в одной категории.
        Map<String, Double> categoryValue = items.stream()
                .collect(Collectors.toMap(
                        Item::category,
                        Item::price,
                        Double::sum // Разрешение коллизий
                ));
        System.out.println("Сумма по категориям (toMap): " + categoryValue);

        // 4. partitioningBy (Разделение строго на две группы - true/false)
        Map<Boolean, List<Item>> expensiveItems = items.stream()
                .collect(Collectors.partitioningBy(item -> item.price > 100));
        System.out.println("Дорогие (>100): " + expensiveItems.get(true).size() + " шт.");

        // 5. groupingBy (Каскадная группировка)
        // Группируем по категории, но значения мапим в Set из названий (а не в списки объектов)
        Map<String, Set<String>> namesByCategory = items.stream()
                .collect(Collectors.groupingBy(
                        Item::category,
                        Collectors.mapping(Item::name, Collectors.toSet())
                ));
        System.out.println("Имена по категориям (groupingBy): " + namesByCategory);

        // 6. summarizingDouble (Вся статистика разом)
        DoubleSummaryStatistics stats = items.stream()
                .collect(Collectors.summarizingDouble(Item::price));
        System.out.printf("Статистика цен: Мин=%.1f, Макс=%.1f, Среднее=%.1f%n",
                stats.getMin(), stats.getMax(), stats.getAverage());

        // 7. teeing (Java 12+) - Параллельно пускаем стрим в два коллектора
        // Находим одновременно самую дорогую и самую дешевую вещь, сводим в Map
        Map<String, String> minMax = items.stream().collect(Collectors.teeing(
                Collectors.maxBy(Comparator.comparingDouble(Item::price)),
                Collectors.minBy(Comparator.comparingDouble(Item::price)),
                (max, min) -> Map.of("Max", max.get().name(), "Min", min.get().name())
        ));
        System.out.println("Teeing (Min & Max): " + minMax);
    }
}