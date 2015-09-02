/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom.client;
import java.io.*;
import java.net.*;
/**
 *
 * @author ljy
 */
public class ChatroomClient {

    /**
     * @param args the command line arguments
     */

    //登陆函数，接收client的用户名输入并检错，向服务器发出用户名，true成功false失败
    public static boolean Login(PrintWriter out,BufferedReader in){
        System.out.println("Login in:");
        String str = null;
        try{ 
            BufferedReader cin = new BufferedReader(new InputStreamReader(System.in)); //用于用户的键盘输入      
            boolean inputstatus = false; //记录输入是否正确
            do{                     //输入检错
            str = cin.readLine();
            inputstatus = str.startsWith("/login ");//检查格式
            if(!inputstatus){ System.out.println("Invalid command");}
            }while(!inputstatus);//直到格式输入正确才跳出循环
            out.println(str);    //将输入发送给服务器
            str=in.readLine();   //接收服务器的确认信息
           // cin.close();             //这句话引起了错误  
      }catch(IOException ex){ex.printStackTrace();}
      finally{
              if(str.equals("exist")) //服务器返回用户已存在
             { 
                System.out.println("Name exist, please choose anthoer name.");              
                return false;
              }
             else{System.out.println(str);return true;}  //登录成功
      }
    }
    public static void main(String[] args) {
        Socket c_socket = null;      //声明客户端的socket对象
        try{
           System.out.println("*****连接服务器中*****");
           c_socket = new Socket("127.0.0.1",12345);///初始化，对应的是UnknownHostException 错误
          // c_socket.setKeepAlive(true); 
          
           //封装socket的输入输出流，对应的错误是IOException,PrintWriter 不会抛出IOException（）
           PrintWriter out = new PrintWriter(       //c_socket.getOutputStream 是为当前Socket的对象创建输出流（往服务器方向的流）
                         new OutputStreamWriter(c_socket.getOutputStream()),true);
           BufferedReader in = new BufferedReader(  //c_socket.getInputStream是为当前Socket对象创建输入流，用来接收服务器的消息
                            new InputStreamReader(c_socket.getInputStream())); 
           //定义用于接收用户键盘输入的输入流
           BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
           //连接阶段，等待服务器的信息
           String str = in.readLine();
           if (str.equals("Login in:"))  //如果服务器返回正确的login信息
           {  //登录阶段
             // boolean loginstatus; //用于记录登录的状态
                  boolean loginstatus;
                  do{ loginstatus = Login(out,in);}while(loginstatus == false);//一直到登录成功才跳出循环
           }
           else
           {
               System.out.println("Login Error!");
           }   
           //进入聊天室
           //创建进程开始侦听服务器的消息
           ListenToServerThread thread = new ListenToServerThread(in);
           //while 循环用于用户的输入
     
           while(true){
              str=sin.readLine();
              if(str.startsWith("/login ")){
                  System.out.println("You have already logined in!"); 
              }
              else{
                  out.println(str);
                  if(str.equals("/quit")){break;}} //检查是否是退出命令
           }
           while(thread.isAlive()){   //保证线程已关闭再退出
           }
           in.close();   //释放资源
           out.close();
           sin.close();         
       }catch (UnknownHostException ex){    //接收socket连接的错误
          ex.printStackTrace();
       }catch(IOException ex){    //接收IO流的错误（看Socket的输入、输出流创建是否有错误）
          ex.printStackTrace();
       }finally{   //finally一般是做善后工作。释放资源
             if(null != c_socket){    //如果socket不空则关闭连接
                 try{ c_socket.close();}
                 catch(IOException ex){ ex.printStackTrace();}           
             }
       }
    }   
}
