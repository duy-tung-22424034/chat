
package client;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author tungpd
 */
public class MainForm extends javax.swing.JFrame {
    
    String username;
    String host;
    int port;
    Socket socket;
    DataOutputStream dos;
    public boolean attachmentOpen = false;
    private boolean isConnected = false;
    private String mydownloadfolder = "D:\\";
    private ArrayList<String> onlineList = new ArrayList<String>();
    
    /**
     * Creates new form MainForm
     */
    public MainForm() {
        initComponents();
    }

    public ArrayList<String> getOnlineList() {
        return onlineList;
    }

    public void addOnlineList(Vector onlineList) {
        this.onlineList.clear();
        Iterator iterator = onlineList.iterator();
        while (iterator.hasNext()) {
            String temp = iterator.next().toString();
            if (!temp.equalsIgnoreCase(this.username)) {
                this.onlineList.add(temp);
            }
        }
    }
    
    public void initFrame(String username, String host, int port){
        this.username = username;
        this.host = host;
        this.port = port;
        setTitle("Login as " + username);
        /** Connect **/
        connect();
    }
    
    public void connect(){
        appendMessage(" Connecting...", "Status", Color.DARK_GRAY, Color.LIGHT_GRAY);
        try {
            socket = new Socket(host, port);
            dos = new DataOutputStream(socket.getOutputStream());
            /** Send our username **/
            dos.writeUTF("CMD_JOIN "+ username);
            appendMessage(" Connected", "Status", Color.DARK_GRAY, Color.GREEN);
        
            
            /** Start Client Thread **/
            new Thread(new ClientThread(socket, this)).start();
            jButtonSend.setEnabled(true);
            // were now connected
            isConnected = true;
            
        }
        catch(IOException e) {
            isConnected = false;
            JOptionPane.showMessageDialog(this, "Unable to Connect to Server, please try again later.!","Connection Failed",JOptionPane.ERROR_MESSAGE);
            appendMessage("[IOException]: "+ e.getMessage(), "Error", Color.RED, Color.RED);
        }
    }
    
    /*
        is Connected
    */
    public boolean isConnected(){
        return this.isConnected;
    }
    
    /*
        System Message
    */
    public void appendMessage(String msg, String header, Color headerColor, Color contentColor){
        jTextPaneChat.setEditable(true);
        getMsgHeader(header, headerColor);
        getMsgContent(msg, contentColor);
        jTextPaneChat.setEditable(false);
    }
    
    /*
        My Message
    */
    public void appendMyMessage(String msg, String header){
        jTextPaneChat.setEditable(true);
        getMsgHeader(header, Color.RED);
        getMsgContent(msg, Color.DARK_GRAY);
        jTextPaneChat.setEditable(false);
    }
    
    /*
        Message Header
    */
    public void getMsgHeader(String header, Color color){
        int len = jTextPaneChat.getDocument().getLength();
        jTextPaneChat.setCaretPosition(len);
        jTextPaneChat.setCharacterAttributes(MessageStyle.styleMessageContent(color, "Impact", 13), false);
        jTextPaneChat.replaceSelection(header+":");
    }
    /*
        Message Content
    */
    public void getMsgContent(String msg, Color color){
        int len = jTextPaneChat.getDocument().getLength();
        jTextPaneChat.setCaretPosition(len);
        jTextPaneChat.setCharacterAttributes(MessageStyle.styleMessageContent(color, "Arial", 12), false);
        jTextPaneChat.replaceSelection(msg +"\n\n");
    }
    
    public void appendOnlineList(Vector list){
        //  showOnLineList(list);  -  Original Method()
        sampleOnlineList(list);  // - Sample Method()
    }
    
    /*
        Append online list
    */
    public void showOnLineList(Vector list){
        try {
            txtpaneOnlineStatus.setEditable(true);
            txtpaneOnlineStatus.setContentType("text/html");
            StringBuilder sb = new StringBuilder();
            Iterator it = list.iterator();
            sb.append("<html><table>");
            while(it.hasNext()){
                Object e = it.next();
                URL url = getImageFile();
                Icon icon = new ImageIcon(this.getClass().getResource("/images/online.png"));
                //sb.append("<tr><td><img src='").append(url).append("'></td><td>").append(e).append("</td></tr>");
                sb.append("<tr><td><b>></b></td><td>").append(e).append("</td></tr>");
                System.out.println("Online: "+ e);
            }
            sb.append("</table></body></html>");
            txtpaneOnlineStatus.removeAll();
            txtpaneOnlineStatus.setText(sb.toString());
            txtpaneOnlineStatus.setEditable(false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    /*
      ************************************  Show Online Sample  *********************************************
    */
    private void sampleOnlineList(Vector list){
        txtpaneOnlineStatus.setEditable(true);
        txtpaneOnlineStatus.removeAll();
        txtpaneOnlineStatus.setText("");
        Iterator i = list.iterator();
        while(i.hasNext()){
            Object e = i.next();
            /*  Show Online Username   */
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            panel.setBackground(Color.white);
            
            Icon icon = new ImageIcon(this.getClass().getResource("/images/online.png"));
            JLabel label = new JLabel(icon);
            label.setText(" "+ e);
            panel.add(label);
            int len = txtpaneOnlineStatus.getDocument().getLength();
            txtpaneOnlineStatus.setCaretPosition(len);
            txtpaneOnlineStatus.insertComponent(panel);
            /*  Append Next Line   */
            sampleAppend();
        }
        txtpaneOnlineStatus.setEditable(false);
    }
    private void sampleAppend(){
        int len = txtpaneOnlineStatus.getDocument().getLength();
        txtpaneOnlineStatus.setCaretPosition(len);
        txtpaneOnlineStatus.replaceSelection("\n");
    }
    /*
      ************************************  Show Online Sample  *********************************************
    */
    
    
    
    
    /*
        Get image file path
    */
    public URL getImageFile(){
        URL url = this.getClass().getResource("/images/online.png");
        return url;
    }
    
    
    /*
        Set myTitle
    */
    public void setMyTitle(String s){
        setTitle(s);
    }
    
    /*
        Get My Download Folder
    */
    public String getMyDownloadFolder(){
        return this.mydownloadfolder;
    }
    
    /*
        Get Host
    */
    public String getMyHost(){
        return this.host;
    }
    
    /*
        Get Port
    */
    public int getMyPort(){
        return this.port;
    }
    
    /*
        Get My Username
    */
    public String getMyUsername(){
        return this.username;
    }
    
    /*
        Update Attachment 
    */
    public void updateAttachment(boolean b){
        this.attachmentOpen = b;
    }
    
    /*
        This will open a file chooser
    */
    public void openFolder(){
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int open = chooser.showDialog(this, "Browse Folder");
        if(open == chooser.APPROVE_OPTION){
            mydownloadfolder = chooser.getSelectedFile().toString()+"\\";
        } else {
            mydownloadfolder = "D:\\";
        }
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chooser = new javax.swing.JFileChooser();
        jTextFieldMessage = new javax.swing.JTextField();
        jButtonSend = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPaneChat = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtpaneOnlineStatus = new javax.swing.JTextPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFileSharing = new javax.swing.JMenu();
        sendFileMenu = new javax.swing.JMenuItem();
        jMenuItemDownload = new javax.swing.JMenuItem();
        jMenuAccount = new javax.swing.JMenu();
        LogoutMenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(145, 53, 53));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTextFieldMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldMessageActionPerformed(evt);
            }
        });

        jButtonSend.setBackground(new java.awt.Color(102, 102, 102));
        jButtonSend.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jButtonSend.setForeground(new java.awt.Color(255, 255, 255));
        jButtonSend.setText("Send Message");
        jButtonSend.setEnabled(false);
        jButtonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(jTextPaneChat);

        txtpaneOnlineStatus.setFont(new java.awt.Font("Tahoma", 1, 9)); // NOI18N
        txtpaneOnlineStatus.setForeground(new java.awt.Color(120, 14, 3));
        txtpaneOnlineStatus.setAutoscrolls(false);
        txtpaneOnlineStatus.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane3.setViewportView(txtpaneOnlineStatus);

        jMenuFileSharing.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sharing.png"))); // NOI18N
        jMenuFileSharing.setText("File Sharing");
        jMenuFileSharing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuFileSharingActionPerformed(evt);
            }
        });

        sendFileMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sendfile.png"))); // NOI18N
        sendFileMenu.setText("Send File");
        sendFileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendFileMenuActionPerformed(evt);
            }
        });
        jMenuFileSharing.add(sendFileMenu);

        jMenuItemDownload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/folder.png"))); // NOI18N
        jMenuItemDownload.setText("Downloads");
        jMenuItemDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDownloadActionPerformed(evt);
            }
        });
        jMenuFileSharing.add(jMenuItemDownload);

        jMenuBar1.add(jMenuFileSharing);

        jMenuAccount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/process.png"))); // NOI18N
        jMenuAccount.setText("My Account");

        LogoutMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/loggoff.png"))); // NOI18N
        LogoutMenu.setText("Logout");
        LogoutMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogoutMenuActionPerformed(evt);
            }
        });
        jMenuAccount.add(LogoutMenu);

        jMenuBar1.add(jMenuAccount);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSend))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonSend))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendActionPerformed
        // TODO add your handling code here:
        try {
            String content = username+" "+ jTextFieldMessage.getText();
            dos.writeUTF("CMD_CHATALL "+ content);
            appendMyMessage(" "+jTextFieldMessage.getText(), username);
            jTextFieldMessage.setText("");
        } catch (IOException e) {
            appendMessage(" Unable to Send Message now, Server is not available at this time please try again later or Restart this Application.!", "Error", Color.RED, Color.RED);
        }
    }//GEN-LAST:event_jButtonSendActionPerformed

    private void jMenuFileSharingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuFileSharingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuFileSharingActionPerformed

    private void sendFileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendFileMenuActionPerformed
        // TODO add your handling code here:
        if(!attachmentOpen){
            SendFile s = new SendFile();
            s.setSendToList(onlineList);
            if(s.prepare(username, host, port, this)){
                s.setLocationRelativeTo(null);
                s.setVisible(true);
                attachmentOpen = true;
            } else {
                JOptionPane.showMessageDialog(this, "Unable to stablish File Sharing at this moment, please try again later.!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_sendFileMenuActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(this, "Close this Application.?");
        if(confirm == 0){
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            this.dispose();
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    private void jTextFieldMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldMessageActionPerformed
        // TODO add your handling code here:
        try {
            String content = username+" "+ evt.getActionCommand();
            dos.writeUTF("CMD_CHATALL "+ content);
            appendMyMessage(" "+evt.getActionCommand(),username);
            jTextFieldMessage.setText("");
        } catch (IOException e) {
            appendMessage(" Unable to Send Message now, Server is not available at this time please try again later or Restart this Application.!", "Error", Color.RED, Color.RED);
        }
    }//GEN-LAST:event_jTextFieldMessageActionPerformed

    private void jMenuItemDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDownloadActionPerformed
        // TODO add your handling code here:
        try {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int browse = chooser.showOpenDialog(this);
            if(browse == chooser.APPROVE_OPTION){
                this.mydownloadfolder = chooser.getSelectedFile().toString() +"\\";
            }
        } catch (HeadlessException e) {
        }
    }//GEN-LAST:event_jMenuItemDownloadActionPerformed

    private void LogoutMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogoutMenuActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(null, "Logout your Account.?");
        if(confirm == 0){
            try {
                socket.close();
                setVisible(false);
                /** Login Form **/
                new LoginForm().setVisible(true);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }//GEN-LAST:event_LogoutMenuActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem LogoutMenu;
    private javax.swing.JFileChooser chooser;
    private javax.swing.JButton jButtonSend;
    private javax.swing.JMenu jMenuAccount;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuFileSharing;
    private javax.swing.JMenuItem jMenuItemDownload;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextFieldMessage;
    private javax.swing.JTextPane jTextPaneChat;
    private javax.swing.JMenuItem sendFileMenu;
    private javax.swing.JTextPane txtpaneOnlineStatus;
    // End of variables declaration//GEN-END:variables
}
