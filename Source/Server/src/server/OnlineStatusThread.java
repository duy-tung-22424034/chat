/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author tungpd
 */
public class OnlineStatusThread implements Runnable {
    
    MainForm mainForm;
    
    public OnlineStatusThread(MainForm mainForm){
        this.mainForm = mainForm;
    }

    @Override
    public void run() {
        try {
            while(!Thread.interrupted()){
                String message = "";
                for(int x=0; x < mainForm.getClients().size(); x++){
                    message = message+" "+ mainForm.getClients().elementAt(x);
                }
                
                for(int x=0; x < mainForm.getSockets().size(); x++){
                    Socket tsoc = (Socket) mainForm.getSockets().elementAt(x);
                    DataOutputStream dos = new DataOutputStream(tsoc.getOutputStream());
                    /** CMD_ONLINE [user1] [user2] [user3] **/
                    if(message.length() > 0){
                        dos.writeUTF("CMD_ONLINE "+ message);
                    }
                }
                
                Thread.sleep(1900);
            }
        } catch(InterruptedException e){
            mainForm.appendMessage("[InterruptedException]: "+ e.getMessage());
        } catch (IOException e) {
            mainForm.appendMessage("[IOException]: "+ e.getMessage());
        }
    }
    
    
}
