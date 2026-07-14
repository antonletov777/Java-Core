package Networking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/// # 1. Классический (Блокирующий) Socket API
/// Демонстрация простого TCP Эхо-сервера и Клиента.
@SuppressWarnings("all")
public class BlockingSocketLecture1 {
    public static void main(String[] args) throws InterruptedException {
        // Запускаем сервер в отдельном потоке, чтобы он не заблокировал main
        new Thread(BlockingSocketLecture1::startServer).start();

        Thread.sleep(1000); // Ждем секунду, чтобы сервер успел подняться

        // Запускаем клиента
        startClient();
    }

    public static void startServer() {
        // ServerSocket "слушает" порт 8080
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("[Сервер] Запущен на порту 8080. Ожидание подключений...");

            while (true) {
                // ВНИМАНИЕ: Это блокирующий вызов!
                // Поток останавливается здесь, пока не подключится клиент.
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Сервер] Клиент подключился: " + clientSocket.getInetAddress());

                // В классическом I/O на каждого клиента создают новый поток
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (Exception ignored) {
        }
    }

    private static void handleClient(Socket socket) {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String inputLine;
            // Блокирующее чтение: ждем, пока клиент что-то пришлет
            while ((inputLine = in.readLine()) != null) {
                System.out.println("[Сервер] Получено: " + inputLine);
                out.println("ЭХО: " + inputLine); // Отправляем обратно
                if ("exit".equalsIgnoreCase(inputLine)) break;
            }
        } catch (Exception ignored) {
        }
    }

    public static void startClient() {
        // Клиент "звонит" на localhost:8080
        try (Socket socket = new Socket("localhost", 8080); PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            System.out.println("[Клиент] Отправляю сообщение...");
            out.println("Привет, Java Сервер!");

            // Читаем ответ
            String response = in.readLine();
            System.out.println("[Клиент] Ответ от сервера: " + response);

            out.println("exit");
        } catch (Exception ignored) {
        }
    }
}