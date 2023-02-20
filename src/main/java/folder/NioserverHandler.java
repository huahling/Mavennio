package folder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class NioserverHandler {
    private final  static Logger logger= Logger.getLogger(NioserverHandler.class.getName());
    private final static String dirctory="F:\\chuan\\";

    public void excute(SelectionKey s) throws IOException, InterruptedException {
            receive(s);
    }

    public static Map<SelectionKey, FileChannel> filemap=new HashMap<>();
    private static long sum=0;
    private static long  filelength=0;
    public static String filename=null;

    public void receive(SelectionKey s) throws IOException, InterruptedException {
        SocketChannel socketChannel = (SocketChannel) s.channel();
        while (true) {


                int mark=0;
                ByteBuffer buf0=ByteBuffer.allocate(4);
                while(true){
                    int sizes=socketChannel.read(buf0);
                    if(sizes>=4){
                        buf0.flip();
                        mark=buf0.getInt();
                       System.out.println(mark);
                        buf0.clear();
                        break;
                    }
                }
                if(mark==1){
                    ByteBuffer buf=ByteBuffer.allocate(4);
                     int filelength=0;
                     int aa=0;
                     while(true){
                         aa=socketChannel.read(buf);
                         if(aa>=4){
                             buf.flip();
                             filelength=buf.getInt();
                             buf.clear();
                              break;
                         }

                     }

                    byte[] bytes = null;
                     String filepath=null;
                    ByteBuffer buff = ByteBuffer.allocate(filelength);
                    while (true) {
                        aa = socketChannel.read(buff);
                        if (aa == filelength) {
                            buff.flip();
                            bytes = new byte[filelength];
                            buff.get(bytes);
                            buff.clear();
                            break;

                        }

                    }
                    filepath = new String(bytes);
                     System.out.println();
                    File file=new File(dirctory+filepath);
                    if(!file.exists()){
                        file.mkdirs();

                    }
                }


            else if(mark==2) {

                 if (filemap.get(s) == null) {
                     System.out.println(filemap.get(s) + "===值");

                     ByteBuffer buf1 = ByteBuffer.allocate(4);
                     int filenamelength = 0;
                     int size = 0;
                     while (true) {
                         size = socketChannel.read(buf1);
                         if (size >= 4) {
                             buf1.flip();
                             filenamelength = buf1.getInt();
                             buf1.clear();
                             break;
                         }
                     }

                     byte[] bytes = null;
                     ByteBuffer buf2 = ByteBuffer.allocate(filenamelength);
                     while (true) {
                         size = socketChannel.read(buf2);
                         if (size == filenamelength) {
                             buf2.flip();
                             bytes = new byte[filenamelength];
                             buf2.get(bytes);
                             buf2.clear();
                             break;

                         }

                     }
                     filename = new String(bytes);


                     ByteBuffer buf3 = ByteBuffer.allocate(8);
                     while (true) {
                         size = socketChannel.read(buf3);
                         if (size >= 8) {
                             buf3.flip();
                             filelength = buf3.getLong();
                             buf3.clear();
                             break;
                         }
                     }
                  //  while (true){
                     ByteBuffer buf4 = null;
                     if (filelength - sum < 1024 * 1024 * 3) {
                         buf4 = ByteBuffer.allocate(Integer.valueOf(String.valueOf(filelength - sum)));
                     } else {
                         buf4 = ByteBuffer.allocate(1024 * 1024 * 3);
                         System.out.println("else?");
                     }


                     Thread.sleep(300);
                     socketChannel.read(buf4);

                     String path = dirctory + File.separator + filename;
                     System.out.println(path + "----文件写入");
                     FileChannel fileChannel = new FileOutputStream(new File(path)).getChannel();
                     buf4.flip();
                     long a = fileChannel.write(buf4);
                     buf4.clear();

                     sum = sum + a;
                     System.out.println(sum + "===" + filelength);


                     if (sum == filelength) {

                         System.out.println("写入");
                         sum = 0;
                         filelength = 0;
                         filemap.put(s, null);
                         fileChannel.close();
                         System.out.println(sum + "^^^^" + filelength);
                         System.out.println("写入了");
//                         break; //加上可一直接收文件
                     } else {
                         filemap.put(s, fileChannel);
                         System.out.println("写入了?");

                         while(true) {

                             ByteBuffer buf5 = null;
                             if (filelength - sum < 1024 * 1024 * 3) {
                                 buf5 = ByteBuffer.allocate(Integer.valueOf(String.valueOf(filelength - sum)));
                             } else {
                                 buf5 = ByteBuffer.allocate(1024 * 1024 * 3);
                                 System.out.println("else??!");
                             }

                             //Thread.sleep(1000);
                             socketChannel.read(buf5);
                             FileChannel fileChannel1 = filemap.get(s);

                             buf5.flip();
                             long b = fileChannel1.write(buf5);
                             sum = sum + b;
                             buf5.clear();

                             System.out.println(sum + "----" + filelength);
                             if (sum == filelength) {
                                 System.out.println("写入完毕");
                                 sum = 0;
                                 filelength = 0;
                                 filemap.put(s, null);
//                                 fileChannel.close();
                                 fileChannel1.close();
                                 System.out.println(sum + "----" + filelength);
                                 System.out.println("写入完毕了");
                                 break;
                             } else {
                                 filemap.put(s, fileChannel1);
                                 System.out.println("写入完毕了?");

                             }

                         }
                     }
                // }

                 }
//                 else {
//                     while(true) {
//
//                         ByteBuffer buf4 = null;
//                         if (filelength - sum < 1024 * 1024 * 3) {
//                             buf4 = ByteBuffer.allocate(Integer.valueOf(String.valueOf(filelength - sum)));
//                         } else {
//                             buf4 = ByteBuffer.allocate(1024 * 1024 * 3);
//                             System.out.println("else??!");
//                         }
//
//                         //Thread.sleep(1000);
//                         socketChannel.read(buf4);
//                         FileChannel fileChannel1 = filemap.get(s);
//
//                         buf4.flip();
//                         long a = fileChannel1.write(buf4);
//                         sum = sum + a;
//                         buf4.clear();
//
//                         System.out.println(sum + "----" + filelength);
//                         if (sum == filelength) {
//                             System.out.println("写入完毕");
//                             sum = 0;
//                             filelength = 0;
//                             filemap.put(s, null);
//                             fileChannel1.close();
//                             System.out.println(sum + "----" + filelength);
//                             System.out.println("写入完毕了");
//                         } else {
//                             filemap.put(s, fileChannel1);
//                             System.out.println("写入完毕了?");
//
//                         }
//
//                     }
//                 }

             }
        }
    }

}
