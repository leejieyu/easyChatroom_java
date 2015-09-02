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
             //创建服务器端套接字
            serverSocket = new ServerSocket(12345);
            System.out.println("*****服务器已开启*****");
            while(true) //开启监听循环
            {             
               //监听并接收客户端的连接、此方法在连接传入之间一直阻塞
              s = serverSocket.accept();// 一旦客户端应用程序申请建立一个Socket，accept方法返回一个对应的服务器端Socket对象
              new TalkToClientThread(s); //创建一个新的线程与client通信
            }   
        }catch(IOException ex){ //接收I/O流建立错误?
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
