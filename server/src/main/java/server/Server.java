package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private ServerSocket server;
    private Socket socket;
    private final int PORT = 8188;
    private DataInputStream in;
    private DataOutputStream out;
    private List<ClientHandler> clientHandlerList;
    private AuthService authService;

    public Server() {
        clientHandlerList = new CopyOnWriteArrayList<>();
        authService = new SimpleAuthService();
        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен");
            while (true) {
                socket = server.accept();
                System.out.println("Подключился клиент " + socket.getRemoteSocketAddress());
                new ClientHandler(this, socket);
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

    public void broadcastMSG(ClientHandler sender, String msg){
        String message = String.format("%s: %s", sender.getNickname(),msg);
        for (ClientHandler client: clientHandlerList){
            client.sendMessage(message);
        }
    }

    public void subscribe(ClientHandler clientHandler){
        clientHandlerList.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler){
        clientHandlerList.remove(clientHandler);
    }

    public AuthService getAuthService() {
        return authService;
    }
}
