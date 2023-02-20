package everyclentmanyserver.beifei;

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
//    private final static String dirctory="C:\\Users\\xia\\Desktop\\ce\\";
    private final static String dirctory="F:\\chuan\\";

    public void excute(SelectionKey s) throws IOException, InterruptedException {
            receive(s);
    }

    public static Map<SelectionKey, FileChannel> filemap=new HashMap<SelectionKey, FileChannel>();
    public static Map<SelectionKey,Long> summap=new HashMap<>();
    public static Map<SelectionKey,Long> filelengthmap=new HashMap<>();
    public static Map<SelectionKey,String> filenamemap=new HashMap<>();
    public static Map<SelectionKey,String> filepathmap=new HashMap<>();


//    private static long sum=0;
//    private static long  filelength=0;
//    public static String filename=null;

    public void receive(SelectionKey s) throws IOException, InterruptedException {
     //   SocketChannel socketChannel = (SocketChannel) s.channel();
       // while (true) {
            SocketChannel socketChannel = (SocketChannel) s.channel();

            if (filemap.get(s) == null) {
            int mark = 0;
            ByteBuffer buf0 = ByteBuffer.allocate(4);
            while (true) {
                int sizes = socketChannel.read(buf0);
                if (sizes >= 4) {
                    buf0.flip();
                    mark = buf0.getInt();
                    System.out.println("#########标识" + mark);
                    buf0.clear();
                    break;
                }
            }
            if (mark == 1) {
                ByteBuffer buf = ByteBuffer.allocate(4);
                int filelength = 0;
                int aa = 0;
                while (true) {
                    aa = socketChannel.read(buf);
                    if (aa >= 4) {
                        buf.flip();
                        filelength = buf.getInt();
                        buf.clear();
                        break;
                    }

                }

                byte[] bytes = null;
                String filepath = null;
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
                filepathmap.put(s, filepath);
                System.out.println();
                File file = new File(dirctory + filepath);
                if (!file.exists()) {
                    file.mkdirs();


                }
            } else if (mark == 2) {

                //if (filemap.get(s) == null) {
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
                String filename = new String(bytes);
                filenamemap.put(s, filename);


                ByteBuffer buf3 = ByteBuffer.allocate(8);
                while (true) {
                    size = socketChannel.read(buf3);
                    if (size >= 8) {
                        buf3.flip();
                        long filelength = buf3.getLong();
                        filelengthmap.put(s, filelength);

                        buf3.clear();
                        break;
                    }
                }
                summap.put(s, 0L);
                ByteBuffer buf4 = null;
                System.out.println(filelengthmap.get(s) + "---" + summap.get(s));
                if (filelengthmap.get(s) - summap.get(s) < 1024 * 1024 * 3) {
                    buf4 = ByteBuffer.allocate(Integer.valueOf(String.valueOf(filelengthmap.get(s) - summap.get(s))));
                } else {
                    buf4 = ByteBuffer.allocate(1024 * 1024 * 3);
                    System.out.println("else?");
                }


              //  Thread.sleep(20);
                socketChannel.read(buf4);

                String path = dirctory + File.separator + filename;
                System.out.println(path + "----文件写入");
                FileChannel fileChannel = new FileOutputStream(new File(path)).getChannel();
                buf4.flip();
                long a = fileChannel.write(buf4);
                buf4.clear();

                // sum = sum + a;
                summap.put(s, summap.get(s) + a);
                System.out.println(summap.get(s).longValue() + "===" + filelengthmap.get(s).longValue());

                if (summap.get(s).longValue() == filelengthmap.get(s).longValue()) {

//                         sum = 0;
//                         filelength = 0;
                    summap.put(s, 0L);
                    filelengthmap.put(s, 0L);
                    filemap.put(s, null);
                    fileChannel.close();
                   // filemap.remove(s);
                    System.out.println("写入了");
                } else {
                    filemap.put(s, fileChannel);
                    System.out.println("写入了?");
                    System.out.println("；capacity=" + buf4.capacity());

                    while (true) {

                        ByteBuffer buf44 = null;
                        if (filelengthmap.get(s) - summap.get(s) < 1024 * 1024 * 3) {
                            buf44 = ByteBuffer.allocate(Integer.valueOf(String.valueOf(filelengthmap.get(s) - summap.get(s))));
                        } else {
                            buf44 = ByteBuffer.allocate(1024 * 1024 * 3);
                            System.out.println("else??!");
                        }

                       // Thread.sleep(10);
                        socketChannel.read(buf44);
                        FileChannel fileChannel1 = filemap.get(s);

                        buf44.flip();
                        long aa = fileChannel1.write(buf44);
                        // sum = sum + aa;
                        summap.put(s, summap.get(s) + aa);
                        buf44.clear();

                        System.out.println(summap.get(s).longValue() + "----" + filelengthmap.get(s).longValue());
                        if (summap.get(s).longValue() == filelengthmap.get(s).longValue()) {

                                 /*sum = 0;
                                 filelength = 0;*/
                            summap.put(s, 0L);
                            filelengthmap.put(s, 0L);
                            filemap.put(s, null);
                            fileChannel1.close();
                           // filemap.remove(s);

                            System.out.println("写入完毕了");

                            break;

                        } else {
                            filemap.put(s, fileChannel1);
                            System.out.println("写入完毕了?");
                        }

                    }
                }


                //  }

            }
           // filemap.remove(s);
        }
        //}
    }

}
