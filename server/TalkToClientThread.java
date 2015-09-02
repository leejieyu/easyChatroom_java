/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom.server;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.Iterator;
/**
 *
 * @author ljy
 */
//用户类，用于存储
/*
class Clients{

   private String name;
   private PrintWriter cout;
   Clients(String n,PrintWriter out){name=n;cout =out;}
}*/

public class TalkToClientThread extends Thread{
    final private Socket socket;   //final 不会再改变。
    final private PrintWriter out;   //输入输出采用字符流的形式来  
    final private BufferedReader in;
//    final private Timer heartBeatTimer;
//    final private TimerTask heartBeatTask;
    private boolean isconnect = true;//用于记录与client的连接状态，
    private boolean islogin = false;//记录client的登录状态
    private String user_name = null;
    static ConcurrentHashMap<String,PrintWriter> clientgroup = new ConcurrentHashMap<>(); //用于存储用户名及其输出流,static 对象的使用。
    //构建函数
    public TalkToClientThread(Socket s) throws IOException{
          socket = s; 
          out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
          in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 /*         heartBeatTimer = new Timer();
          heartBeatTask = new TimerTask(){
              @Override
              public void run(){
                   try{socket.sendUrgentData(0xff);System.out.println("debug");}
                   catch(IOException ex){isconnect = false;}
              }
          };*/
          start();
    }
   
    //处理广播消息  strtome为向广播发出者发出的消息，strtoother为向其他人广播的消息
    public void BroadcastMsg(String strTome,String strToother){
           //遍历hushmap的方法     
        Set<String> keys = clientgroup.keySet();
        for(Iterator<String> it = keys.iterator();it.hasNext();){
           String key = it.next();
           if(key.equals(user_name)){
               if(null!=strTome){
               clientgroup.get(key).println(strTome);
               }
           }
           else
           {
               clientgroup.get(key).println(strToother);
           }
        }     
    }
    //处理执行消息
    public void ExecuteMsg(String str){
        //"/login"首先判断是否是登录信息，设计广播
        if(str.startsWith("/who")){
                Set<String> namegroup =clientgroup.keySet();
                for(Iterator<String>it = namegroup.iterator();it.hasNext();){
                   String username = it.next();
                   clientgroup.get(user_name).println(username);                 
                }
                String size= Integer.toString(clientgroup.size());
                clientgroup.get(user_name).println("Total online user:"+size );
        }
        else if(str.startsWith("/to "))
        {
            String[] strgroup = str.split(" "); //将话按空格分割开来，第一个
            int length = strgroup.length;
            if(1 == length){
                clientgroup.get(user_name).println("请输入私聊对象");
            }
            else if (2 == length){
                clientgroup.get(user_name).println("请输入私聊内容");
            }
            else{
                 if(clientgroup.containsKey(strgroup[1])){
                        if(strgroup[1].equals(user_name)){//如果是对自己说话
                           clientgroup.get(user_name).println("Stop talking to yourself!");
                        }
                        else{
                            String msg = strgroup[2];
                            for(int i = 3;i< length;i++){
                                msg = msg+" "+strgroup[i];
                            }
                           clientgroup.get(strgroup[1]).println(user_name+"对你说:"+msg);
                           clientgroup.get(user_name).println("你对"+strgroup[1]+"说:"+msg);   
                        }
                 }
                 else{//如果用户不存在
                        clientgroup.get(user_name).println(strgroup[1]+" is not online");
                          }
            }
  /*          else{
                clientgroup.get(user_name).println("对不起，聊天内容暂不支持空格");
            }*/
                
     /*       if(4 == str.length()){
                clientgroup.get(user_name).println("请输入私聊对象");
            }
            else{
              //  String[] strgroup = new String[3];
                String[] strgroup = str.split(" "); //将话按空格分割开来，第一个
                if(clientgroup.containsKey(strgroup[1])){
                   if(strgroup[1].equals(user_name)){//如果是对自己说话
                      clientgroup.get(user_name).println("Stop talking to yourself!");
                   }
                   else{ //正常情况
                       System.out.print(strgroup.length);
                       if(3==strgroup.length){
                      clientgroup.get(strgroup[1]).println(user_name+"对你说:"+strgroup[2]);
                      clientgroup.get(user_name).println("你对"+strgroup[1]+"说:"+strgroup[2]);
                       }
                       else if(2 == strgroup.length){
                           clientgroup.get(user_name).println("请输入聊天内容");
                       }
                       else{
                          clientgroup.get(user_name).println("对不起，聊天内容暂不支持空格");
                       }
                       }
                   }
                
                else//如果说话对象不在
                {
                    clientgroup.get(user_name).println(strgroup[1]+" is not online");
                }
            }*/
        }
        else{ //不正确的命令格式
              clientgroup.get(user_name).println("error:不正确的命令格式");
        }
        
    }
    //处理预设消息
    public void PreinstallMsg(String str){
        //hi usr_name 广播命令 
        String sentence = null;
        if(str.startsWith("//hi")){
            if(4 == str.length()){
                sentence = user_name+"向大家打招呼，“Hi，大家好！我来咯~”";
                BroadcastMsg(sentence,sentence);
            }
            else{
                String targetname = str.substring(5);
                sentence = user_name+"向"+targetname+"打招呼：“Hi，你好啊~”";
                BroadcastMsg(sentence,sentence);               
            }
        }
        else{
           clientgroup.get(user_name).println("对不起，暂无该预设命令");
        }    
    }
    public void run(){
           //用户连接登陆阶段
        try{
           out.println("Login in:");  //发出login in 表示服务器已连接，并提示登录
           //sout.writeUTF("**********");
           //用户登录阶段，需不要独立识别login消息呢。。。。？
           String client_msg = null;       
     //      heartBeatTimer.schedule(heartBeatTask,2,2500);//开启心跳
           while(true){
                client_msg = in.readLine();
            //识别消息类型
                //登录消息,退出具有特殊性 单独提出来
                if(client_msg.startsWith("/login ")){
                     user_name = client_msg.substring(7);
                     if(clientgroup.containsKey(user_name))
                     {
                        out.println("exist");//如果用户登录之后还/login 可能会出现错误
                     }
                     else
                     {
                        clientgroup.put(user_name, out);//加入新用户
                        BroadcastMsg("You have logined",user_name.concat(" has logined"));//登录成功，向其他广播
                        System.out.println(user_name+"已上线");
                        islogin=true;  //更新登录状态
                     }                    
                 }
                else if(client_msg.equals("/quit")){
                    clientgroup.remove(user_name);
                    BroadcastMsg(null,user_name+" has quit");
                    System.out.println(user_name+"已下线");
                    out.println("break");
                    islogin = false;
                    break;
                }
                else if(client_msg.startsWith("//")){//预设信息
                   PreinstallMsg(client_msg);
                   System.out.println(user_name+"发布一条预设消息");
                }
                else if(client_msg.startsWith("/")){ //执行信息         
                   ExecuteMsg(client_msg);
                   System.out.println(user_name+"发布了一条执行消息");
                }
                else{                        //广播信息             
                  BroadcastMsg("你说:"+client_msg,user_name+"说:"+client_msg);
                  System.out.println(user_name+"发布了一条广播消息");
                }
           }
          // System.out.println(user_name+"已掉线");
  /*         if(!isconnect){  //如果是非正常退出
               clientgroup.remove(user_name);
               BroadcastMsg(null,user_name+" 已掉线");
               System.out.println(user_name+"已掉线");
              // out.println("break");
           }*/
          
           in.close();
           out.close();
           socket.close();
        }catch(IOException ex){ //当client非正常离线，会抛出IOException错误，在此处更新用户名单，并向其他用户广播离线信息。
           // ex.printStackTrace(); 
           if(true == islogin ){
                clientgroup.remove(user_name);
                BroadcastMsg(null,user_name+"已掉线");
                System.out.println(user_name+"已掉线");
            }
            try{     //清理资源
                  in.close();
                  out.close();
                  socket.close();
            }
            catch(IOException error){error.printStackTrace();}
        }
    }
}
