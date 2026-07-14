package Networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/// # 2. Неблокирующий ввод-вывод (NIO)
/// Демонстрация сервера, который может обслуживать множество клиентов ОДНИМ потоком.
@SuppressWarnings("ALL")
public class NioSocketLecture2 {
    public static void main(String[] args) throws IOException {
        // 1. Создаем Selector (мультиплексор)
        Selector selector = Selector.open();

        // 2. Создаем канал для сервера
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8081));

        // ВАЖНО: Переводим в неблокирующий режим!
        serverSocketChannel.configureBlocking(false);

        // 3. Регистрируем канал в селекторе и говорим, что нас интересуют подключения (OP_ACCEPT)
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("NIO Сервер запущен на порту 8081");

        // 4. Основной Event Loop (Цикл событий)
        while (true) {
            // Блокируется, пока не появится хотя бы одно событие
            selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();

                if (key.isAcceptable()) {
                    // Кто-то хочет подключиться
                    handleAccept(key, selector);
                } else if (key.isReadable()) {
                    // Кто-то прислал данные
                    handleRead(key);
                }

                // Обязательно удаляем обработанное событие
                iter.remove();
            }
        }
    }

    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        // Принимаем соединение
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false); // Делаем клиента неблокирующим

        // Регистрируем клиента в том же селекторе, теперь ждем от него данные (OP_READ)
        clientChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("Подключен новый клиент: " + clientChannel.getRemoteAddress());
    }

    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            System.out.println("Клиент отключился");
            clientChannel.close();
            return;
        }

        buffer.flip(); // Переключаем буфер из режима записи в режим чтения
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        System.out.println("Получено: " + new String(data).trim());

        // Эхо-ответ
        ByteBuffer response = ByteBuffer.wrap(("NIO ЭХО: " + new String(data)).getBytes());
        clientChannel.write(response);
    }
}