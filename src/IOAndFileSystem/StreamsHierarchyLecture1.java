package IOAndFileSystem;

import java.io.*;
import java.nio.charset.StandardCharsets;

/// # 3. Иерархия потоков: Байтовые и Символьные (Streams)
/// Архитектура java.io построена на паттерне Decorator (Декоратор).
/// В этом классе исчерпывающе продемонстрирована работа с источниками данных
/// (файлы, память, межпотоковое взаимодействие) и их обертками.
@SuppressWarnings("ALL")
public class StreamsHierarchyLecture1 {

    public static void main(String[] args) throws Exception {
        System.out.println("--- Байтовые потоки ---");
        demonstrateByteStreamSources();
        demonstrateByteStreamDecorators();

        System.out.println("\n--- Символьные потоки ---");
        demonstrateCharStreamSources();
        demonstrateCharStreamDecorators();

        System.out.println("\n--- Классы-мосты ---");
        demonstrateBridgeStreams();

        System.out.println("\n--- Межпотоковое взаимодействие (Piped) ---");
        demonstratePipedStreams();
    }

    /// 3.1. БАЙТОВЫЕ ПОТОКИ: Источники (Sources)
    /// Работают с сырыми бинарными данными (картинки, архивы, байт-код).
    public static void demonstrateByteStreamSources() {
        // 1. Источник - Память (ByteArray)
        // Часто используется в тестах для мокирования I/O
        byte[] inMemoryData = { 0x48, 0x65, 0x6C, 0x6C, 0x6F }; // "Hello"
        try (ByteArrayInputStream bais = new ByteArrayInputStream(inMemoryData);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int b;
            while ((b = bais.read()) != -1) {
                baos.write(Character.toUpperCase(b)); // Трансформируем в ОЗУ
            }
            System.out.println("Из ByteArray: " + baos.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. Источник - Файловая система (Files)
        try (FileOutputStream fos = new FileOutputStream("data.bin");
             FileInputStream fis = new FileInputStream("data.bin")) {
            fos.write(inMemoryData);
            byte[] buffer = new byte[10];
            int read = fis.read(buffer);
            System.out.println("Из FileStream прочитано байт: " + read);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /// 3.2. БАЙТОВЫЕ ПОТОКИ: Декораторы (Wrappers)
    /// Добавляют новое поведение базовым потокам (буферизация, примитивы).
    public static void demonstrateByteStreamDecorators() {
        try (FileOutputStream fos = new FileOutputStream("primitives.bin");
             // Оборачиваем базовый файловый поток в буфер и в Data-декоратор
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             DataOutputStream dos = new DataOutputStream(bos)) {

            // DataOutputStream позволяет писать примитивы напрямую в бинарном виде
            dos.writeInt(42);
            dos.writeDouble(3.14);
            dos.writeBoolean(true);
            dos.flush(); // Обязательно выталкиваем данные из BufferedOutputStream
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Читаем обратно строго в том же порядке!
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream("primitives.bin")))) {

            int i = dis.readInt();
            double d = dis.readDouble();
            boolean b = dis.readBoolean();
            System.out.printf("Прочитаны примитивы: %d, %.2f, %b%n", i, d, b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /// 3.3. СИМВОЛЬНЫЕ ПОТОКИ: Источники (Sources)
    /// Оптимизированы для работы с текстом (char, 16-bit).
    public static void demonstrateCharStreamSources() {
        // Источник - Память (String/CharArray).
        // StringWriter очень полезен при работе с шаблонизаторами (Velocity, FreeMarker)
        try (StringWriter sw = new StringWriter()) {
            sw.write("Сборка ");
            sw.write("строки ");
            sw.write("в памяти");

            // Извлекаем результат (под капотом работает StringBuffer)
            String result = sw.toString();

            // Читаем строку как поток
            try (StringReader sr = new StringReader(result)) {
                System.out.println("Прочитано из StringReader: " + (char)sr.read()); // Читает первый символ 'С'
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /// 3.4. СИМВОЛЬНЫЕ ПОТОКИ: Декораторы (Wrappers)
    public static void demonstrateCharStreamDecorators() {
        // PrintWriter - ультимативный декоратор для вывода текста.
        // Имеет методы printf, println и подавляет (глотает) IOException.
        try (FileWriter fw = new FileWriter("logs.txt");
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {

            pw.println("--- Системный Лог ---");
            pw.printf("Ошибка в модуле %s: код %d%n", "AuthService", 500);

            if (pw.checkError()) { // Из-за подавления ошибок их нужно проверять так
                System.err.println("Ошибка при записи лога!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /// 3.5. КЛАССЫ-МОСТЫ (Bridges)
    /// Транслируют сырые байты в символы с учетом кодировки.
    public static void demonstrateBridgeStreams() {
        // Допустим, мы получаем сырой InputStream (как System.in или ответ от веб-сервера)
        InputStream rawNetworkStream = new ByteArrayInputStream("Привет, мост!".getBytes(StandardCharsets.UTF_8));

        // Надеваем мост (с явным указанием кодировки) и декоратор (для метода readLine)
        try (InputStreamReader isr = new InputStreamReader(rawNetworkStream, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            String line = br.readLine();
            System.out.println("Прочитано через мост: " + line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /// 3.6. МЕЖПОТОКОВОЕ ВЗАИМОДЕЙСТВИЕ (Piped Streams)
    /// Передача данных напрямую между Thread-ами без использования файлов на диске.
    public static void demonstratePipedStreams() throws Exception {
        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos); // Связываем потоки

        // Поток-Писатель
        Thread writerThread = new Thread(() -> {
            try {
                pos.write("Данные из другого потока".getBytes(StandardCharsets.UTF_8));
                pos.close(); // Обязательно закрываем, чтобы Reader понял, что это конец (EOF)
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Поток-Читатель
        Thread readerThread = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(pis, StandardCharsets.UTF_8))) {
                System.out.println("Получено через Piped: " + br.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        writerThread.start();
        readerThread.start();
        writerThread.join();
        readerThread.join();
    }
}