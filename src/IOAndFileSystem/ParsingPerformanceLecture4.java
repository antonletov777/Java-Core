package IOAndFileSystem;

import java.io.*;
import java.util.Scanner;
import java.util.StringTokenizer;

/// # 2. Чтение данных: Удобство vs Производительность
/// Сравнение медленного Scanner и паттерна Fast I/O, стандарта для высоконагруженных систем.
@SuppressWarnings("ALL")
public class ParsingPerformanceLecture4 {

    public static void main(String[] args) throws IOException {
        // demonstrateScanner(); // Удобно, но медленно
        demonstrateFastIO(); // Быстро и оптимально по памяти
    }

    /// Scanner: Парсит регулярками, крошечный буфер 1 КБ, много блокировок (synchronized).
    /// Сложность парсинга высока из-за поиска разделителей.
    public static void demonstrateScanner() {
        String data = "100 200 300";
        Scanner scanner = new Scanner(data);

        while (scanner.hasNextInt()) {
            System.out.println("Считано Scanner'ом: " + scanner.nextInt());
        }
    }

    /// Паттерн Fast I/O: Идеален для олимпиадного программирования и парсинга логов.
    /// Читает огромными блоками через BufferedReader, разбивает через легковесный StringTokenizer.
    public static void demonstrateFastIO() {
        // В реальном коде сюда передается System.in или FileInputStream
        FastScanner fastScanner = new FastScanner(System.in);

        System.out.print("Введите два числа через пробел (FastScanner): ");
        int a = fastScanner.nextInt();
        int b = fastScanner.nextInt();
        System.out.println("Сумма: " + (a + b));
    }

    /// Классическая реализация FastScanner
    static class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        public FastScanner(InputStream is) {
            br = new BufferedReader(new InputStreamReader(is));
        }

        String next() {
            while (st == null || !st.hasMoreElements()) {
                try {
                    String line = br.readLine();
                    if (line == null) return null; // Конец потока
                    st = new StringTokenizer(line); // Не использует регулярки (в отличие от split)
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }

        long nextLong() {
            return Long.parseLong(next());
        }

        double nextDouble() {
            return Double.parseDouble(next());
        }

        String nextLine() {
            String str = "";
            try {
                str = br.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return str;
        }
    }
}