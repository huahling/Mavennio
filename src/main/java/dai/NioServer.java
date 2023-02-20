package dai;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer {  
    Selector selector = null;  
    ServerSocketChannel serverSocketChannel = null;  
    private NioserverHandler handler;  
    public NioServer() throws IOException {  
        selector  = Selector.open();  
        // 打开服务器套接字通道  
        serverSocketChannel = ServerSocketChannel.open();  
          
        // 调整通道的阻塞模式非阻塞  
        serverSocketChannel.configureBlocking(false);  
        serverSocketChannel.socket().setReuseAddress(true);  
        serverSocketChannel.socket().bind(new InetSocketAddress(6299));
          
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);  
    }  
      
    public NioServer(NioserverHandler handler) throws IOException {  
          
        this();  
        this.handler = handler;   
        
        while (true) {
			int readyChannelsNum = selector.select(2000);//1.2.3,4,1,2,3,12,123,,123,123,123,
			if (readyChannelsNum == 0) {
				System.out.println("等待连接...");
				continue;
			}

			Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
			
			while (iterator.hasNext()) {
				SelectionKey selectionKey = iterator.next();
				
				if (selectionKey.isAcceptable()) {/* 判断accept事件 */
					ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel(); 
					SocketChannel socketChannel = server.accept();
					socketChannel.configureBlocking(false);
					socketChannel.register(this.selector, SelectionKey.OP_READ);
					
					
					//this.handler.excute(selectionKey);
					
				} else if (selectionKey.isReadable()) {//判断是否可读事件
					this.handler.excute(selectionKey);
				}
				
				iterator.remove();
				
			}
			
			/*while (it.hasNext()) {
				SelectionKey s = it.next();  
                it.remove();  
                this.handler.excute(s);  

			}*/
		}
       /* while(selector.select() > 0) {  
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();  
            while(it.hasNext()) {  
                SelectionKey s = it.next();  
                it.remove();  
                this.handler.excute(s);  
            }  
        }  */
    }  
      
    public static void main(String[] args) throws IOException {  
        new NioServer(new NioserverHandler());  
    }  
}  