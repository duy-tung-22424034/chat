/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitorInputStream;

/**
 *
 * @author tungpd
 */
public class FileReceivingThread implements Runnable {

    protected Socket socket;
    protected DataInputStream dis;
    protected DataOutputStream dos;
    protected MainForm mainForm;
    protected StringTokenizer stringTokenizer;
    protected DecimalFormat df = new DecimalFormat("##,#00");
    private final int BUFFER_SIZE = 100;

    public FileReceivingThread(Socket soc, MainForm m) {
        this.socket = soc;
        this.mainForm = m;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("[ReceivingFileThread]: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String data = dis.readUTF();
                stringTokenizer = new StringTokenizer(data);
                String command = stringTokenizer.nextToken();

                switch (command) {

                    // This will handle the recieving of a file in background process from other
                    // user
                    case "CMD_SENDFILE":
                        String consignee = null;
                        try {
                            String filename = stringTokenizer.nextToken();
                            int filesize = Integer.parseInt(stringTokenizer.nextToken());
                            consignee = stringTokenizer.nextToken(); // Get the Sender Username
                            mainForm.setMyTitle("Downloading File....");
                            System.out.println("Downloading File....");
                            System.out.println("From: " + consignee);
                            String path = mainForm.getMyDownloadFolder() + filename;
                            /* Creat Stream */
                            FileOutputStream fos = new FileOutputStream(path);
                            InputStream input = socket.getInputStream();
                            /* Monitor Progress */
                            ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(mainForm,
                                    "Downloading file please wait...", input);
                            /* Buffer */
                            BufferedInputStream bis = new BufferedInputStream(pmis);
                            /** Create a temporary file **/
                            byte[] buffer = new byte[BUFFER_SIZE];
                            int count, percent = 0;
                            while ((count = bis.read(buffer)) != -1) {
                                percent = percent + count;
                                int p = (percent / filesize);
                                mainForm.setMyTitle("Downloading File  " + p + "%");
                                fos.write(buffer, 0, count);
                            }
                            fos.flush();
                            fos.close();
                            mainForm.setMyTitle("you are logged in as: " + mainForm.getMyUsername());
                            JOptionPane.showMessageDialog(null, "File has been downloaded to \n'" + path + "'");
                            System.out.println("File was saved: " + path);
                        } catch (IOException e) {
                            /*
                             * Send back an error message to sender Format: CMD_SENDFILERESPONSE [username]
                             * [Message]
                             */
                            DataOutputStream eDos = new DataOutputStream(socket.getOutputStream());
                            eDos.writeUTF("CMD_SENDFILERESPONSE " + consignee
                                    + " Connection was lost, please try again later.!");

                            System.out.println(e.getMessage());
                            mainForm.setMyTitle("you are logged in as: " + mainForm.getMyUsername());
                            JOptionPane.showMessageDialog(mainForm, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                            socket.close();
                        }
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("[ReceivingFileThread]: " + e.getMessage());
        }
    }
}
