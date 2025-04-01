import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**🎯 客户端要做的事：
 连接服务器（IP + 端口）
 创建两个线程：
 主线程：持续监听用户输入，发送消息
 接收线程：不停读取服务器广播的消息，显示在终端
 使用 BufferedReader + PrintWriter 读写数据*/

public class Client {

    public static void main(String[] args) {
        try{
            Socket socket = new Socket("localhost", 12345); //连接本地服务器
            System.out.println("Connected to chat server.");

            //创建reader/writer
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in)); //用户输入
            BufferedReader reader = new BufferedReader((new InputStreamReader((socket.getInputStream())))); //读取
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true); //写数据发给服务器

            //创建接受线程：不断从服务器读消息
            Thread receiveThread = new Thread(() ->{
                try {
                    String msgFromServer;
                    while((msgFromServer = reader.readLine()) != null){
                        System.out.println(msgFromServer);
                    }
                }catch(IOException e){
                    System.out.println("Connection closed by server.");
                }
            });
            receiveThread.start();

            //主线程： 读取用户输入并发送
            String input;
            while((input = userInput.readLine()) != null ){
                writer.println(input); // 这是写数据进socket ， 发送给服务器
            }
            socket.close(); //用户输入结束后退出

        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
