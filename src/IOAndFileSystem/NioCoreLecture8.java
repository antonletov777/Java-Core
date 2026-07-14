package IOAndFileSystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/// # 6. Производительный I/O: Java NIO
/// Неблокирующий подход, Direct Buffers и каналы. Фундамент HighLoad.
@SuppressWarnings("ALL")
public class NioCoreLecture8 {

    public static void main(String[] args) throws IOException {
        demonstrateDirectBuffers();
        demonstrateRandomAccessAndChannels();
    }

    /// Heap vs Direct Buffers
    public static void demonstrateDirectBuffers() {
        // Heap Buffer: живет в JVM. Быстро создается, но копируется ОС при I/O.
        ByteBuffer heapBuf = ByteBuffer.allocate(1024);

        // Direct Buffer: живет вне JVM (в памяти ОС). Аллокация дорогая,
        // но I/O операции происходят без дублирования (Zero-Copy). Идеально для сети.
        ByteBuffer directBuf = ByteBuffer.allocateDirect(1024);

        directBuf.put("Zero Copy Data".getBytes());
        directBuf.flip(); // Переводим буфер из режима ЗАПИСИ в режим ЧТЕНИЯ

        while (directBuf.hasRemaining()) {
            System.out.print((char) directBuf.get());
        }
        System.out.println();
    }

    /// Многопоточное скачивание или случайный доступ (RandomAccess)
    public static void demonstrateRandomAccessAndChannels() throws IOException {
        Path file = Path.of("nio_test.bin");

        // Открываем канал напрямую к файлу. Поддерживает конкурентный доступ к разным сегментам.
        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {

            ByteBuffer buffer = ByteBuffer.allocate(32);
            buffer.put("Hello NIO".getBytes());
            buffer.flip();

            // Пишем в начало
            channel.write(buffer);

            // Прыгаем на 100-й байт (создавая "дыру" в файле - sparse file)
            channel.position(100);
            buffer.rewind(); // Сброс позиции в буфере для повторного чтения
            channel.write(buffer);
        }
    }
}