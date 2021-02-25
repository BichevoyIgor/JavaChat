package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/exit")) {
                            System.out.println("Клиент отключен " + socket.getRemoteSocketAddress());
                            out.writeUTF("/exit");
                            break;
                        }
                        if (str.startsWith("/auth")) {
                            String[] token = str.split("\\s");
                            if (token.length < 3) continue;
                            String newNick = server.getAuthService().getNicknameAndPassword(token[1], token[2]);

                            if (newNick != null) {
                                nickname = newNick;
                                sendMessage("/authok " + nickname);
                                server.subscribe(this);
                                break;
                            } else {
                                sendMessage("Не верный логиин или пароль");
                                continue;
                            }
                        }
                    }
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/exit")) {
                            System.out.println("Клиент отключен " + socket.getRemoteSocketAddress());
                            out.writeUTF("/exit");
                            break;
                        }
                        server.broadcastMSG(this, str);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);
                    try {
                        in.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String str) {
        try {
            out.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }
}
