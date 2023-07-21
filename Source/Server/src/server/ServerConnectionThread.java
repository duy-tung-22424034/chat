
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author tungpd
 */
public class ServerConnectionThread implements Runnable {

    ServerSocket socketServer;
    MainForm mainForm;
    boolean keepRunning = true;

    public ServerConnectionThread(int port, MainForm mainForm) {
        mainForm.appendMessage("[Server]: Starting server in port " + port);
        try {
            this.mainForm = mainForm;
            socketServer = new ServerSocket(port);
            mainForm.appendMessage("[Server]: Server started.!");
        } catch (IOException e) {
            mainForm.appendMessage("[IOException]: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (keepRunning) {
                Socket socket = socketServer.accept();
                /** SOcket thread **/
                new Thread(new SocketHandlingThread(socket, mainForm)).start();
            }
        } catch (IOException e) {
            mainForm.appendMessage("[ServerThreadIOException]: " + e.getMessage());
        }
    }

    public void stop() {
        try {
            socketServer.close();
            keepRunning = false;
            System.out.println("Server is now closed..!");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
