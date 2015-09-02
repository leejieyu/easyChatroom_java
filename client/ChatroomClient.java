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

    //��½����������client���û������벢���������������û�����true�ɹ�falseʧ��
    public static boolean Login(PrintWriter out,BufferedReader in){
        System.out.println("Login in:");
        String str = null;
        try{ 
            BufferedReader cin = new BufferedReader(new InputStreamReader(System.in)); //�����û��ļ�������      
            boolean inputstatus = false; //��¼�����Ƿ���ȷ
            do{                     //������
            str = cin.readLine();
            inputstatus = str.startsWith("/login ");//����ʽ
            if(!inputstatus){ System.out.println("Invalid command");}
            }while(!inputstatus);//ֱ����ʽ������ȷ������ѭ��
            out.println(str);    //�����뷢�͸�������
            str=in.readLine();   //���շ�������ȷ����Ϣ
           // cin.close();             //��仰�����˴���  
      }catch(IOException ex){ex.printStackTrace();}
      finally{
              if(str.equals("exist")) //�����������û��Ѵ���
             { 
                System.out.println("Name exist, please choose anthoer name.");              
                return false;
              }
             else{System.out.println(str);return true;}  //��¼�ɹ�
      }
    }
    public static void main(String[] args) {
        Socket c_socket = null;      //�����ͻ��˵�socket����
        try{
           System.out.println("*****���ӷ�������*****");
           c_socket = new Socket("127.0.0.1",12345);///��ʼ������Ӧ����UnknownHostException ����
          // c_socket.setKeepAlive(true); 
          
           //��װsocket���������������Ӧ�Ĵ�����IOException,PrintWriter �����׳�IOException����
           PrintWriter out = new PrintWriter(       //c_socket.getOutputStream ��Ϊ��ǰSocket�Ķ��󴴽�������������������������
                         new OutputStreamWriter(c_socket.getOutputStream()),true);
           BufferedReader in = new BufferedReader(  //c_socket.getInputStream��Ϊ��ǰSocket���󴴽����������������շ���������Ϣ
                            new InputStreamReader(c_socket.getInputStream())); 
           //�������ڽ����û����������������
           BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
           //���ӽ׶Σ��ȴ�����������Ϣ
           String str = in.readLine();
           if (str.equals("Login in:"))  //���������������ȷ��login��Ϣ
           {  //��¼�׶�
             // boolean loginstatus; //���ڼ�¼��¼��״̬
                  boolean loginstatus;
                  do{ loginstatus = Login(out,in);}while(loginstatus == false);//һֱ����¼�ɹ�������ѭ��
           }
           else
           {
               System.out.println("Login Error!");
           }   
           //����������
           //�������̿�ʼ��������������Ϣ
           ListenToServerThread thread = new ListenToServerThread(in);
           //while ѭ�������û�������
     
           while(true){
              str=sin.readLine();
              if(str.startsWith("/login ")){
                  System.out.println("You have already logined in!"); 
              }
              else{
                  out.println(str);
                  if(str.equals("/quit")){break;}} //����Ƿ����˳�����
           }
           while(thread.isAlive()){   //��֤�߳��ѹر����˳�
           }
           in.close();   //�ͷ���Դ
           out.close();
           sin.close();         
       }catch (UnknownHostException ex){    //����socket���ӵĴ���
          ex.printStackTrace();
       }catch(IOException ex){    //����IO���Ĵ��󣨿�Socket�����롢����������Ƿ��д���
          ex.printStackTrace();
       }finally{   //finallyһ�������ƺ������ͷ���Դ
             if(null != c_socket){    //���socket������ر�����
                 try{ c_socket.close();}
                 catch(IOException ex){ ex.printStackTrace();}           
             }
       }
    }   
}
