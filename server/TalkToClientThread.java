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
//�û��࣬���ڴ洢
/*
class Clients{

   private String name;
   private PrintWriter cout;
   Clients(String n,PrintWriter out){name=n;cout =out;}
}*/

public class TalkToClientThread extends Thread{
    final private Socket socket;   //final �����ٸı䡣
    final private PrintWriter out;   //������������ַ�������ʽ��  
    final private BufferedReader in;
//    final private Timer heartBeatTimer;
//    final private TimerTask heartBeatTask;
    private boolean isconnect = true;//���ڼ�¼��client������״̬��
    private boolean islogin = false;//��¼client�ĵ�¼״̬
    private String user_name = null;
    static ConcurrentHashMap<String,PrintWriter> clientgroup = new ConcurrentHashMap<>(); //���ڴ洢�û������������,static �����ʹ�á�
    //��������
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
   
    //����㲥��Ϣ  strtomeΪ��㲥�����߷�������Ϣ��strtootherΪ�������˹㲥����Ϣ
    public void BroadcastMsg(String strTome,String strToother){
           //����hushmap�ķ���     
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
    //����ִ����Ϣ
    public void ExecuteMsg(String str){
        //"/login"�����ж��Ƿ��ǵ�¼��Ϣ����ƹ㲥
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
            String[] strgroup = str.split(" "); //�������ո�ָ������һ��
            int length = strgroup.length;
            if(1 == length){
                clientgroup.get(user_name).println("������˽�Ķ���");
            }
            else if (2 == length){
                clientgroup.get(user_name).println("������˽������");
            }
            else{
                 if(clientgroup.containsKey(strgroup[1])){
                        if(strgroup[1].equals(user_name)){//����Ƕ��Լ�˵��
                           clientgroup.get(user_name).println("Stop talking to yourself!");
                        }
                        else{
                            String msg = strgroup[2];
                            for(int i = 3;i< length;i++){
                                msg = msg+" "+strgroup[i];
                            }
                           clientgroup.get(strgroup[1]).println(user_name+"����˵:"+msg);
                           clientgroup.get(user_name).println("���"+strgroup[1]+"˵:"+msg);   
                        }
                 }
                 else{//����û�������
                        clientgroup.get(user_name).println(strgroup[1]+" is not online");
                          }
            }
  /*          else{
                clientgroup.get(user_name).println("�Բ������������ݲ�֧�ֿո�");
            }*/
                
     /*       if(4 == str.length()){
                clientgroup.get(user_name).println("������˽�Ķ���");
            }
            else{
              //  String[] strgroup = new String[3];
                String[] strgroup = str.split(" "); //�������ո�ָ������һ��
                if(clientgroup.containsKey(strgroup[1])){
                   if(strgroup[1].equals(user_name)){//����Ƕ��Լ�˵��
                      clientgroup.get(user_name).println("Stop talking to yourself!");
                   }
                   else{ //�������
                       System.out.print(strgroup.length);
                       if(3==strgroup.length){
                      clientgroup.get(strgroup[1]).println(user_name+"����˵:"+strgroup[2]);
                      clientgroup.get(user_name).println("���"+strgroup[1]+"˵:"+strgroup[2]);
                       }
                       else if(2 == strgroup.length){
                           clientgroup.get(user_name).println("��������������");
                       }
                       else{
                          clientgroup.get(user_name).println("�Բ������������ݲ�֧�ֿո�");
                       }
                       }
                   }
                
                else//���˵��������
                {
                    clientgroup.get(user_name).println(strgroup[1]+" is not online");
                }
            }*/
        }
        else{ //����ȷ�������ʽ
              clientgroup.get(user_name).println("error:����ȷ�������ʽ");
        }
        
    }
    //����Ԥ����Ϣ
    public void PreinstallMsg(String str){
        //hi usr_name �㲥���� 
        String sentence = null;
        if(str.startsWith("//hi")){
            if(4 == str.length()){
                sentence = user_name+"���Ҵ��к�����Hi����Һã�������~��";
                BroadcastMsg(sentence,sentence);
            }
            else{
                String targetname = str.substring(5);
                sentence = user_name+"��"+targetname+"���к�����Hi����ð�~��";
                BroadcastMsg(sentence,sentence);               
            }
        }
        else{
           clientgroup.get(user_name).println("�Բ������޸�Ԥ������");
        }    
    }
    public void run(){
           //�û����ӵ�½�׶�
        try{
           out.println("Login in:");  //����login in ��ʾ�����������ӣ�����ʾ��¼
           //sout.writeUTF("**********");
           //�û���¼�׶Σ��費Ҫ����ʶ��login��Ϣ�ء���������
           String client_msg = null;       
     //      heartBeatTimer.schedule(heartBeatTask,2,2500);//��������
           while(true){
                client_msg = in.readLine();
            //ʶ����Ϣ����
                //��¼��Ϣ,�˳����������� ���������
                if(client_msg.startsWith("/login ")){
                     user_name = client_msg.substring(7);
                     if(clientgroup.containsKey(user_name))
                     {
                        out.println("exist");//����û���¼֮��/login ���ܻ���ִ���
                     }
                     else
                     {
                        clientgroup.put(user_name, out);//�������û�
                        BroadcastMsg("You have logined",user_name.concat(" has logined"));//��¼�ɹ����������㲥
                        System.out.println(user_name+"������");
                        islogin=true;  //���µ�¼״̬
                     }                    
                 }
                else if(client_msg.equals("/quit")){
                    clientgroup.remove(user_name);
                    BroadcastMsg(null,user_name+" has quit");
                    System.out.println(user_name+"������");
                    out.println("break");
                    islogin = false;
                    break;
                }
                else if(client_msg.startsWith("//")){//Ԥ����Ϣ
                   PreinstallMsg(client_msg);
                   System.out.println(user_name+"����һ��Ԥ����Ϣ");
                }
                else if(client_msg.startsWith("/")){ //ִ����Ϣ         
                   ExecuteMsg(client_msg);
                   System.out.println(user_name+"������һ��ִ����Ϣ");
                }
                else{                        //�㲥��Ϣ             
                  BroadcastMsg("��˵:"+client_msg,user_name+"˵:"+client_msg);
                  System.out.println(user_name+"������һ���㲥��Ϣ");
                }
           }
          // System.out.println(user_name+"�ѵ���");
  /*         if(!isconnect){  //����Ƿ������˳�
               clientgroup.remove(user_name);
               BroadcastMsg(null,user_name+" �ѵ���");
               System.out.println(user_name+"�ѵ���");
              // out.println("break");
           }*/
          
           in.close();
           out.close();
           socket.close();
        }catch(IOException ex){ //��client���������ߣ����׳�IOException�����ڴ˴������û����������������û��㲥������Ϣ��
           // ex.printStackTrace(); 
           if(true == islogin ){
                clientgroup.remove(user_name);
                BroadcastMsg(null,user_name+"�ѵ���");
                System.out.println(user_name+"�ѵ���");
            }
            try{     //������Դ
                  in.close();
                  out.close();
                  socket.close();
            }
            catch(IOException error){error.printStackTrace();}
        }
    }
}
