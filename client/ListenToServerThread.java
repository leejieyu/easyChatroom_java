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
           while(true)    //һֱ������������������Ϣ������ӡ����Ļ��
           {
              str =  server_message.readLine();   //����׳��������ʾ������������쳣����Ϊ����������
              if(str.equals("break")){   //��ʾ�������Ѿ��յ��ͻ���������Ϣ���˳�ѭ��
                  break;
              }
              else{
                  System.out.println(str);
              }
           }
       }catch(IOException ex){
          //ex.printStackTrace();
          System.out.println("��������Ͽ����ӣ�������/quit�˳�");
       }
       //finally{}
    }
}
