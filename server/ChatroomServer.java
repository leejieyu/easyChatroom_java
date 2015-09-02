/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom.server;
import java.io.*;
import java.net.*;

/**
 *
 * @author ljy
 */
public class ChatroomServer {

    /**
     * @param args the command line arguments
     */
  
    public static void main(String[] args) {
        ServerSocket serverSocket = null; //
        Socket s = null;
        try {
             //�������������׽���
            serverSocket = new ServerSocket(12345);
            System.out.println("*****�������ѿ���*****");
            while(true) //��������ѭ��
            {             
               //���������տͻ��˵����ӡ��˷��������Ӵ���֮��һֱ����
              s = serverSocket.accept();// һ���ͻ���Ӧ�ó������뽨��һ��Socket��accept��������һ����Ӧ�ķ�������Socket����
              new TalkToClientThread(s); //����һ���µ��߳���clientͨ��
            }   
        }catch(IOException ex){ //����I/O����������?
           ex.printStackTrace();
        }finally{
           if(null != s){
                try{ s.close();}
                catch(IOException ex){ ex.printStackTrace();}
           }
           if(null != serverSocket){
                try{ serverSocket.close();}
                catch(IOException ex) {ex.printStackTrace();}           
           }
        }     
    }   
}
