package everyClientOneFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class Server1 {
      private static Map<SelectionKey, FileChannel> filemap=new HashMap<SelectionKey,FileChannel>();

      private static String path="C:\\Users\\xia\\Desktop\\ce";

    public static void main(String[] args) throws IOException {
        ServerSocketChannel socketChannel=ServerSocketChannel.open();
        socketChannel.socket().bind(new InetSocketAddress(6299));
        socketChannel.configureBlocking(false);
       Selector selector= Selector.open();
       socketChannel.register(selector,SelectionKey.OP_ACCEPT);

       while(true){
          int keys= selector.select(3000);
           System.out.println(keys+"---时间---"+new Date());
          if(keys==0){

              continue;
          }
        Set<SelectionKey> set=  selector.selectedKeys();
         Iterator<SelectionKey> keyIter=  set.iterator();
         while(keyIter.hasNext()){
          SelectionKey  key= keyIter.next();

          if(key.isAcceptable()){
             SocketChannel chanel= ((ServerSocketChannel)key.channel()).accept();
             if(chanel==null){
                 continue;
             }
             chanel.configureBlocking(false);
             chanel.register(key.selector(),SelectionKey.OP_READ, ByteBuffer.allocate(1024));
          }
          if(key.isReadable()){
            SocketChannel chanel1  =(SocketChannel)key.channel();

            if(filemap.get(key)==null){
                   ByteBuffer q1=  ByteBuffer.allocate(4);
                   while(true){
                      int readfilelength=chanel1.read(q1);
                      if(readfilelength==4){
                          break;
                      }

                   }
                   q1.flip();
                   int filenamelength= q1.getInt();
                   q1.clear();

                  ByteBuffer q2=  ByteBuffer.allocate(filenamelength);
                  while(true){
                     int readfilelengths= chanel1.read(q2);
                     if(readfilelengths==filenamelength){
                         break;

                     }
                  }
                  q2.flip();
                  byte[] filename=new byte[filenamelength];
                  q2.get(filename);
                  q2.clear();

                  File file=new File(path+ File.separator+new String(filename));
                FileOutputStream outputStream=new FileOutputStream(file);

                  FileChannel fileChannel= outputStream.getChannel();
                  filemap.put(key,fileChannel);


                  //文件内容长度
               ByteBuffer r1= ByteBuffer.allocate(8);
               while(true){
                   int readfilenamelength= chanel1.read(r1);

                   if(readfilenamelength==8){
                       break;
                   }
               }
               r1.flip();
             long filength=  r1.getLong();

              r1.clear();


            }

            //文件内容
             ByteBuffer byteBuffer= ByteBuffer.allocate(1024*1024*5);
          //  System.out.println("-------------"+byteBuffer);
                         chanel1.read(byteBuffer);
                         byteBuffer.flip();
                         filemap.get(key).write(byteBuffer);
                         byteBuffer.clear();


          }
          keyIter.remove();

         }


       }

    }

}
