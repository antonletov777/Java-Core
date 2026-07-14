package IOAndFileSystem;

import java.io.*;

/// # 4. Буферизация и Запись данных
/// Уменьшение системных вызовов (System Calls) к ядру ОС за счет промежуточной памяти.
@SuppressWarnings("ALL")
public class BufferedIOLecture2 {

    public static void main(String[] args) {
        demonstrateBufferedIO();
        demonstratePrintWriter();
    }

    /// Сравнение побайтового копирования через буфер.
    public static void demonstrateBufferedIO() {
        // BufferedInputStream накапливает байты из ОС огромными кусками (по умолчанию 8 КБ)
        try (FileInputStream fis = new FileInputStream("text.txt");
             BufferedInputStream bis = new BufferedInputStream(fis);

             FileOutputStream fos = new FileOutputStream("copy.txt");
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            int data;
            // Даже если мы вызываем read() по 1 байту, системный вызов к диску
            // происходит только 1 раз на каждые 8192 итерации цикла!
            while ((data = bis.read()) != -1) {
                bos.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /// PrintWriter - самый удобный инструмент для записи текстовых данных, логов и отчетов.
    public static void demonstratePrintWriter() {
        try (FileWriter fw = new FileWriter("report.txt");
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) { // PrintWriter скрывает IOException внутри

            out.println("Отчет сервера:");
            out.printf("Статус: %s, Код: %d%n", "ОК", 200);

            if (out.checkError()) { // Ошибки ввода-вывода нужно проверять вручную
                System.err.println("Произошла ошибка при записи.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}