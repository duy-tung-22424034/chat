package client;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author tungpd
 */
public class ClientThread implements Runnable {

    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    MainForm mainForm;
    StringTokenizer stringTokenizer;
    protected DecimalFormat df = new DecimalFormat("##,#00");

    public ClientThread(Socket socket, MainForm mainForm) {
        this.mainForm = mainForm;
        this.socket = socket;
        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            mainForm.appendMessage("[IOException]: " + e.getMessage(), "Error", Color.RED, Color.RED);
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String data = dis.readUTF();
                stringTokenizer = new StringTokenizer(data);
                /** Get Message CMD **/
                String command = stringTokenizer.nextToken();
                switch (command) {
                    case "CMD_MESSAGE":
                        SoundEffect.MessageReceive.play(); // Play Audio clip
                        String message = "";
                        String frm = stringTokenizer.nextToken();
                        while (stringTokenizer.hasMoreTokens()) {
                            message = message + " " + stringTokenizer.nextToken();
                        }
                        mainForm.appendMessage(message, frm, Color.ORANGE, Color.BLUE);
                        break;

                    case "CMD_ONLINE":
                        Vector onlineUsers = new Vector();
                        while (stringTokenizer.hasMoreTokens()) {
                            String onlineUser = stringTokenizer.nextToken();
                            if (!onlineUser.equalsIgnoreCase(mainForm.username)) {
                                onlineUsers.add(onlineUser);
                            }
                        }
                        mainForm.appendOnlineList(onlineUsers);
                        mainForm.addOnlineList(onlineUsers);
                        break;

                    // This will inform the client that there's a file receive, Accept or Reject the
                    // file
                    case "CMD_FILE_XD": // Format: CMD_FILE_XD [sender] [receiver] [filename]
                        String sender = stringTokenizer.nextToken();
                        String receiver = stringTokenizer.nextToken();
                        String fname = stringTokenizer.nextToken();
                        int confirm = JOptionPane.showConfirmDialog(mainForm,
                                "From: " + sender + "\nFilename: " + fname + "\nwould you like to Accept.?");
                        // SoundEffect.FileSharing.play(); // Play Audio
                        if (confirm == 0) { // client accepted the request, then inform the sender to send the file now
                            /* Select were to save this file */
                            mainForm.openFolder();
                            try {
                                dos = new DataOutputStream(socket.getOutputStream());
                                // Format: CMD_SEND_FILE_ACCEPT [ToSender] [Message]
                                String format = "CMD_SEND_FILE_ACCEPT " + sender + " accepted";
                                dos.writeUTF(format);

                                /*
                                 * this will create a filesharing socket to handle incoming file and this socket
                                 * will automatically closed when it's done.
                                 */
                                Socket fSoc = new Socket(mainForm.getMyHost(), mainForm.getMyPort());
                                DataOutputStream fdos = new DataOutputStream(fSoc.getOutputStream());
                                fdos.writeUTF("CMD_SHARINGSOCKET " + mainForm.getMyUsername());
                                /* Run Thread for this */
                                new Thread(new FileReceivingThread(fSoc, mainForm)).start();
                            } catch (IOException e) {
                                System.out.println("[CMD_FILE_XD]: " + e.getMessage());
                            }
                        } else { // client rejected the request, then send back result to sender
                            try {
                                dos = new DataOutputStream(socket.getOutputStream());
                                // Format: CMD_SEND_FILE_ERROR [ToSender] [Message]
                                String format = "CMD_SEND_FILE_ERROR " + sender
                                        + " Client rejected your request or connection was lost.!";
                                dos.writeUTF(format);
                            } catch (IOException e) {
                                System.out.println("[CMD_FILE_XD]: " + e.getMessage());
                            }
                        }
                        break;

                    default:
                        mainForm.appendMessage("[CMDException]: Unknown Command " + command, "CMDException", Color.RED,
                                Color.RED);
                        break;
                }
            }
        } catch (IOException e) {
            mainForm.appendMessage(" Server Connection was lost, please try again later.!", "Error", Color.RED,
                    Color.RED);
        }
    }
}
