package everyclentmanyserver.Client2;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class ClientThread2 extends Thread {

    private String filepath;

//    private static String basepath= "C:\\Users\\user\\Desktop\\d\\";
//    private static String basepath= "C:\\Users\\user\\Desktop\\jt\\";
//        private static String basepath= "G:\\one\\";
private static String basepath= "C:\\Users\\user\\Desktop\\ce\\";
//    private static String basepath= "D:\\传输\\夏娜\\";
//    private static String basepath= "G:\\two\\";
//    private static String basepath= "C:\\Users\\user\\Desktop\\AB\\";
//    private static String basepath= "G:\\of\\";
//private static String basepath= "C:\\Users\\user\\Desktop\\d\\";
//    private static String basepath= "F:\\新传输位置\\夏娜\\webService\\";



    public ClientThread2(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public void run() {

        try {
//            SocketChannel socketChannel = null;
//            SocketAddress socketAddress = null;

            SocketChannel socketChannel = SocketChannel.open();
            SocketAddress  socketAddress = new InetSocketAddress("192.168.31.141", 6299);
//            SocketAddress  socketAddress = new InetSocketAddress("192.168.2.60", 6299);
            socketChannel.connect(socketAddress);

            sendData(socketChannel,filepath,basepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        public void sendData(SocketChannel socketChannel, String filepath,String basepath) throws IOException {

        try{
            File files = new File(filepath);
            // System.out.println(files.exists());
            if (files.isDirectory()) {
                if (!filepath.equals(basepath)) {
                    ByteBuffer buf0 = ByteBuffer.allocate(4);
                    buf0.putInt(1);
                    buf0.flip();
                    socketChannel.write(buf0);
                    buf0.clear();

                    String afilepath = files.getPath();
                    String refilepath = afilepath.replace(basepath, "");
                    System.out.println(refilepath + "****文件夹");
                    ByteBuffer buf = ByteBuffer.allocate(4);
                    buf.putInt(new String(refilepath.getBytes(), "ISO-8859-1").length());
                    buf.flip();
                    socketChannel.write(buf);
                    buf.clear();

                    ByteBuffer buff = ByteBuffer.allocate(new String(refilepath.getBytes(), "ISO-8859-1").length());
                    buff.put(refilepath.getBytes());
                    buff.flip();
                    socketChannel.write(buff);
                    buff.clear();
                }
//                String filess[]=files.list();
//                for (String s : filess) {
//                    //System.out.println(s+"---");
//                    sendFile(filepath+File.separator+s);
//                }
            } else {

                ByteBuffer buf01 = ByteBuffer.allocate(4);
                buf01.putInt(2);
                buf01.flip();
                socketChannel.write(buf01);
                buf01.clear();

                String aafilepath = files.getPath();
                String rfilepath = aafilepath.replace(basepath, "");
                System.out.println(rfilepath + "----文件");
                ByteBuffer buf1 = ByteBuffer.allocate(4);
                buf1.putInt(new String(rfilepath.getBytes(), "ISO-8859-1").length());
                buf1.flip();
                socketChannel.write(buf1);
                buf1.clear();

                ByteBuffer buf2 = ByteBuffer.allocate(new String(rfilepath.getBytes(), "ISO-8859-1").length());
                buf2.put(rfilepath.getBytes());
                buf2.flip();
                socketChannel.write(buf2);
                buf2.clear();

                ByteBuffer buf3 = ByteBuffer.allocate(8);
                long filelength = new File(filepath).length();
                buf3.putLong(filelength);
                buf3.flip();
                socketChannel.write(buf3);
                buf3.clear();

                ByteBuffer buf4 = ByteBuffer.allocate(1024 * 1024 * 3);
                FileInputStream fileOutputStream = new FileInputStream(new File(filepath));
                FileChannel fileChannel = fileOutputStream.getChannel();
                long nowlength = 0;
                long sumlength = 0;

                do {
                    nowlength = fileChannel.read(buf4);
                    sumlength += nowlength;
                    buf4.flip();
                    socketChannel.write(buf4);
                    buf4.clear();

                } while (nowlength != -1 && (sumlength < filelength));

            }
//            Client3.mk--;//减自己的mark
//        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Client2.mk--;//减自己的mark
        }
        System.out.println("Client正在传输"+ "线程"+ Client2.mk);
    }
    }
