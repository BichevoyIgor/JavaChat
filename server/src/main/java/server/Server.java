package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ServerSocket server;
    private static Socket socket;
    private static final int PORT = 8188;

    public static void main(String[] args) {

        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен");
            socket = server.accept();
            System.out.println("Установлено соединеие с клиентом: " + socket.getLocalSocketAddress());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

//чтение с клавиатуры и отправка клиенту
            Thread thread = new Thread(() -> {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    try {
                        out.write(reader.readLine() + "\n");
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();

            while (true){
                String str = input.readLine();
                if (str.equals("/exit")){
                    System.out.println("Соединение с клиентом завершено");
                    out.write("/exit" + "\n");
                    out.flush();
                    break;
                }else {
                    System.out.println(socket.getLocalSocketAddress() + ": " + str);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
