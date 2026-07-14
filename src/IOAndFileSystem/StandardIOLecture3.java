package IOAndFileSystem;

import java.io.Console;
import java.io.IOException;
import java.util.Arrays;

/// # 1. Базовый Ввод/Вывод: Стандартные потоки и Консоль
/// Демонстрация работы со стандартными потоками ОС и безопасного ввода паролей.
@SuppressWarnings("ALL")
public class StandardIOLecture3 {

    public static void main(String[] args) throws IOException {
        demonstrateStandardStreams();
        demonstrateSecureConsole();
    }

    /// Работа с System.in, System.out и System.err
    public static void demonstrateStandardStreams() throws IOException {
        System.out.println("Стандартный поток вывода (stdout)");
        System.err.println("Стандартный поток ошибок (stderr) - часто не буферизуется и выводится сразу");

        System.out.print("Введите один символ и нажмите Enter: ");
        // Читает ровно один байт (0-255). Медленно, если использовать в цикле без буфера.
        int inputByte = System.in.read();
        System.out.println("Вы ввели ASCII-код: " + inputByte);

        // Очистка буфера от символов переноса строки
        while (System.in.available() > 0) System.in.read();
    }

    /// Безопасное чтение пароля (работает только при запуске из настоящего терминала ОС,
    /// в IDE console() обычно возвращает null).
    public static void demonstrateSecureConsole() {
        Console console = System.console();
        if (console == null) {
            System.err.println("Консоль недоступна. Запустите программу из терминала.");
            return;
        }

        // Пароль читается в char[], а не в String, чтобы не попасть в String Pool
        char[] password = console.readPassword("Введите секретный пароль: ");
        console.printf("Пароль принят (длина: %d)%n", password.length);

        // Практика безопасного бэкенда: немедленно затираем пароль в памяти
        Arrays.fill(password, '0');
    }
}