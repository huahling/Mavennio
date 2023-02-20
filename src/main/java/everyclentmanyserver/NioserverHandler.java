package everyclentmanyserver;

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
    private final static Logger logger= Logger.getLogger(NioserverHandler.class.getName());
    private final static String dirctory="F:\\chuan\\";

    public void excute(SelectionKey s) throws IOException, InterruptedException {
        receive(s);
    }

    //    public static Map<SelectionKey, FileChannel> filemap=new HashMap<>();
//    private static long sum=0;
//    private static long  filelength=0;
//    public static String filename=null;

    //每个客户端传多个文件 没有半包粘贴的概念   也不用使用Map 来接 因为开了线程不用区分也可以
    //这里是因为先写的多对多   复制多对多的代码的   可以不使用Map
    public static Map<SelectionKey, FileChannel> fileMap = new HashMap<SelectionKey, FileChannel>();
    public static Map<SelectionKey, Long> sumMap = new HashMap<>();
    public static Map<SelectionKey, Long> fileLengthMap = new HashMap<>();
    public static Map<SelectionKey, String> fileNameMap = new HashMap<>();
    public static Map<SelectionKey,String> filepathmap=new HashMap<>();

    public void receive(SelectionKey s) throws IOException, InterruptedException {
        SocketChannel socketChannel = (SocketChannel) s.channel();
//        while (true) {  //传多个不可以循环

        if (fileMap.get(s) == null) {
//            System.out.println("--------- map: "+fileMap.get(s));

            int mark = 0;
            ByteBuffer buf0 = ByteBuffer.allocate(4);
            while (true) {
                int sizes = socketChannel.read(buf0);
                if (sizes >= 4) {
                    buf0.flip();
                    mark = buf0.getInt();
                    System.out.println("标识: "+mark);
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

                File file = new File(dirctory+File.separator+filepath);
//                System.out.println("-------------传来的目录"+file);
                if (!file.exists()) {
                    file.mkdir();
//                    System.out.println("=============创建父目录" + file.getPath());
                }

                socketChannel.socket().close();
                socketChannel.close();
                //不关闭会出现部分文件夹未创建，文件夹里的文件先创建的情况，导致出错
            } else if (mark == 2) {

//                System.out.println(fileMap.get(s) + "===值");

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
                    if (size >= filenamelength) {
                        buf2.flip();
                        bytes = new byte[filenamelength];
                        buf2.get(bytes);
                        buf2.clear();
                        break;

                    }

                }

//                     filename = new String(bytes);

                String filename = new String(bytes);
                fileNameMap.put(s, filename);


                ByteBuffer buf3 = ByteBuffer.allocate(8);
                while (true) {
                    size = socketChannel.read(buf3);
                    if (size >= 8) {
                        buf3.flip();
                        long filelength = buf3.getLong();
//                        System.out.println("文件长度filelength: "+filelength);
                        fileLengthMap.put(s, filelength);
                        buf3.clear();
                        break;
                    }
                }
                //  while (true){
                sumMap.put(s, 0L);
                ByteBuffer buf4 = null;
                if (fileLengthMap.get(s) - sumMap.get(s) < 1024 * 1024 * 3) {
                    buf4 = ByteBuffer.allocate(Integer.valueOf(String.valueOf(fileLengthMap.get(s) - sumMap.get(s))));
                } else {
                    buf4 = ByteBuffer.allocate(1024 * 1024 * 3);
//                    System.out.println("else?");
                }


//                Thread.sleep(20);  //先去掉
                socketChannel.read(buf4);

                String path = dirctory + File.separator + filename;
                System.out.println(path+ "----文件写入");

//                File f = new File(path);
//                if (!f.exists()) {
//                    if (!f.getParentFile().exists()) {
//                        f.getParentFile().mkdirs();
//                        System.out.println("-------------创建父目录" + f.getParentFile());
//                    }
//                    f.createNewFile();
//                }

                FileChannel fileChannel = new FileOutputStream(new File(path)).getChannel();
//                    FileOutputStream  fC =  new FileOutputStream(path,true);
//                FileChannel fileChannel = fC.getChannel();

                buf4.flip();
                long a = fileChannel.write(buf4);
                buf4.clear();

//                 sum = sum + a;
//                 System.out.println(sum + "===" + filelength);

                sumMap.put(s, sumMap.get(s) + a);

//                System.out.println("--------111"+sumMap.get(s).longValue()+"=="+fileLengthMap.get(s).longValue());
                if (sumMap.get(s).longValue() == fileLengthMap.get(s).longValue()) {

//                  System.out.println("写入1");
                    sumMap.put(s, 0L);
                    fileLengthMap.put(s, 0L);
                    fileMap.put(s, null);

//                    fileMap.remove(s);
//                    fC.close();
                    fileChannel.close();
                    socketChannel.close();
//                  System.out.println(sum + "^^^^" + filelength);
//                  System.out.println("写入了1");
//                  break; //加上可一直接收文件
                } else {
                    fileMap.put(s, fileChannel);
                    System.out.println("写入了?2");
                }
//                    while(true) {
//
//                        ByteBuffer buf5 = null;
//                        if (fileLengthMap.get(s) - sumMap.get(s) < 1024 * 1024 * 3) {
//                            buf5 = ByteBuffer.allocate(Integer.valueOf(String.valueOf(fileLengthMap.get(s) - sumMap.get(s))));
//                        } else {
//                            buf5 = ByteBuffer.allocate(1024 * 1024 * 3);
////                            System.out.println("else??!2");
//                        }
//
////                        Thread.sleep(10); 先去掉
//                        socketChannel.read(buf5);
////                        FileChannel fileChannel = fileMap.get(s);
////                        FileChannel fileChannel1 = fileMap.get(s);
//
//                        buf5.flip();
//                        long b = fileChannel.write(buf5);
////                        long b = fileChannel1.write(buf5);
//                        sumMap.put(s,  sumMap.get(s) + b);
//
//                        buf5.clear();
//
////                             System.out.println(sum + "----" + filelength);
////                        System.out.println(sumMap.get(s).longValue()+"=="+fileLengthMap.get(s).longValue());
//                        if (sumMap.get(s).longValue() == fileLengthMap.get(s).longValue()) {
////                            System.out.println("写入完毕2");
//                            sumMap.put(s,0L);
//                            fileLengthMap.put(s, 0L);
//                            fileMap.put(s, null);
//
////                            fileMap.remove(s);
////                            fileLengthMap.remove(s);
////                            sumMap.remove(s);
////                            fC.close();
//
//                            fileChannel.close();
////                            fileChannel1.close();
//                            socketChannel.close();
////                                 System.out.println(sum + "----" + filelength);
////                            System.out.println("写入完毕了2");
//                            break;
//                        } else {
////                            fileMap.put(s, fileChannel1);
//                            fileMap.put(s, fileChannel);
////                            System.out.println("写入完毕了?2");
//                        }
//                    }
//                }
            }
        } else {

                          ByteBuffer buf5 = null;
                        if (fileLengthMap.get(s) - sumMap.get(s) < 1024 * 1024 * 3) {
                            buf5 = ByteBuffer.allocate(Integer.valueOf(String.valueOf(fileLengthMap.get(s) - sumMap.get(s))));
                        } else {
                            buf5 = ByteBuffer.allocate(1024 * 1024 * 3);
//                            System.out.println("else??!2");
                        }

//                        Thread.sleep(10); 先去掉
                        socketChannel.read(buf5);
                        FileChannel fileChannel = fileMap.get(s);

                        buf5.flip();
                        long b = fileChannel.write(buf5);
                        sumMap.put(s,  sumMap.get(s) + b);

                        buf5.clear();

                if (fileLengthMap.get(s).longValue() == sumMap.get(s).longValue()) {
                    fileChannel.close();
                    socketChannel.socket().close();
                    socketChannel.close();
                    //不关闭会出现部分文件夹未创建，文件夹里的文件先创建的情况，导致出错
                }

            }
        }
    }

