import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**🎯 这个类的职责：
 * 每个客户端连接进来，服务器就创建一个 ClientHandler 实例并放进新线程
 * 它会在自己的线程中不断监听客户端发来的消息
 * 每接收到一条消息，就广播给所有客户端（使用共享的 List<PrintWriter>）
 *
 * 📦 每个客户端连接进来：
 * └── 服务器创建 ClientHandler
 *     ├── reader ← 接收这个人的发言
 *     ├── writer → 发消息给这个人
 *     ├── run()：循环监听 → 广播消息
 *     ├── synchronized 操作 clientWriters
 *     └── Socket close → 人下线，线程终止*/
public class ClientHandler implements  Runnable{

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private List<PrintWriter> clientWriters;

    public ClientHandler(Socket socket, List<PrintWriter> clientWriters){
        this.socket = socket;
        this.clientWriters = clientWriters;

        try{
            /**socket.getInputStream() 是 接收从客户端过来的字节流
             用 InputStreamReader 转换成字符流
             再用 BufferedReader 封装，方便 .readLine() 逐行读

             📥 getInputStream → 收（别人写给我）
             📤 getOutputStream → 发（我写给别人）*/
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(),true);

            /**共享资源 + 多线程访问 → 必须加锁
             一行代码也可能“非原子”
             不是代码多才加锁，是资源易冲突就加锁*/
            synchronized (clientWriters){ // 进入广播单
                clientWriters.add(writer);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() { // Thread 的构造方法接受这个runnable对象
        String msg;
        try {
            while ((msg = reader.readLine()) != null){ // server 从每个客户端读信息
                System.out.println("Received: "+ msg);

                synchronized (clientWriters){
                    for(PrintWriter pw:clientWriters){
                        pw.println(msg); //再去挨个广播给所有人
                    }
                }
            }
        }catch (IOException e){
            System.out.println("Client disconnected: " + socket);
        }finally{
            try{
                synchronized (clientWriters){
                    clientWriters.remove(writer);
                }
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }




}
