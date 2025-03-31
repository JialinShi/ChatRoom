import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个类的职责是：
 * 启动服务器，在端口 12345 上监听客户端连接
 * 每来一个客户端，就创建一个 ClientHandler 线程处理它
 * 管理所有在线用户，把消息广播给所有人
 * */
public class Server {

    //保存所有客户端输出流，用于广播
    /**
     * 客户端发给服务器的数据是字符串，但我们需要一个流stream来读写这些字符串，
     * PrintWriter是Java提供的输出流类，用于向文本输出目的地写入数据
     * 在服务端，每个链接进来的客户端，会 "PrintWriter writer = new PrintWriter(socket.getOutputStream(),true"
     * */
    private static final List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("Chat server is running on port 12345...");
        ServerSocket serverSocket = new ServerSocket(12345);

        //一直监听，只要程序运行着，就一直等待新的客户端连接
        while(true){
            Socket clientSocket = serverSocket.accept(); // 阻塞等待，直到某个客户端链接进来
            System.out.println("New client connected: "+ clientSocket);

            //创建新线程处理这个客户端
            ClientHandler handler = new ClientHandler(clientSocket, clientWriters);
            Thread thread = new Thread(handler);
            thread.start();
        }

    }

}
