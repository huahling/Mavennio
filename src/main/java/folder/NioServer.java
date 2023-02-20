package folder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer {
   Selector selector=null;
   ServerSocketChannel serverSocketChannel=null;
   private NioserverHandler nioserverHandler;

   public NioServer() throws IOException {
            selector= Selector.open();
            serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.socket().bind(new InetSocketAddress(6299));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

   }

   public NioServer(NioserverHandler nioserverHandler) throws IOException, InterruptedException {
            this();
            this.nioserverHandler=nioserverHandler;

            while(true){
                int key=selector.select(3000);
                System.out.println(key);
                if(key==0){
                    System.out.println("等待连接中---");
                    continue;
                }
              Iterator<SelectionKey> itera=this.selector.selectedKeys().iterator();
                while(itera.hasNext()){
                       SelectionKey selectionKey=  itera.next();
                       if(selectionKey.isAcceptable()){
                          SocketChannel socketChannel=((ServerSocketChannel)selectionKey.channel()).accept();
                          socketChannel.configureBlocking(false);
                          socketChannel.register(this.selector,SelectionKey.OP_READ);

                       }else if(selectionKey.isReadable()){
                          this.nioserverHandler.excute(selectionKey);

                    }
                       itera.remove();
                }
            }

   }

    public static void main(String[] args) throws IOException, InterruptedException {
        new NioServer(new NioserverHandler());
    }
}
