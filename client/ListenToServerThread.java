/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom.client;
import java.net.*;
import java.io.*;
/**
 *
 * @author ljy
 */
public class ListenToServerThread extends Thread{
    private BufferedReader server_message ;
    public ListenToServerThread(BufferedReader s){
          server_message =s;
          start();
    }
    public void run(){
      String str=null;
      try{ 
           while(true)    //一直侦听服务器发来的消息，并打印到屏幕上
           {
              str =  server_message.readLine();   //如果抛出错误则表示与服务器连接异常，认为服务器掉线
              if(str.equals("break")){   //表示服务器已经收到客户的下线消息，退出循环
                  break;
              }
              else{
                  System.out.println(str);
              }
           }
       }catch(IOException ex){
          //ex.printStackTrace();
          System.out.println("与服务器断开连接，请输入/quit退出");
       }
       //finally{}
    }
}
