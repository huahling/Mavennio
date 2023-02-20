package everyclentmanyserver.beifen2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class ClientRec extends Thread{
//    private static String basepath= "D:\\课件\\webService\\";
//private static String basepath= "C:\\Users\\user\\Desktop\\d\\";
//private static String basepath= "D:\\传输\\夏娜\\";
//private static String basepath= "F:\\新传输位置\\夏娜\\webService\\";
//private static String basepath= "C:\\Users\\user\\Desktop\\c\\";
private static String basepath= "G:\\two\\";
//private static String basepath= "C:\\Users\\user\\Desktop\\f\\";
//    private static String basepath= "C:\\Users\\user\\Desktop\\d\\";
//    private static SocketChannel socketChannel=null;
//    private static SocketAddress socketAddress=null;
    private String filepath=null;
    FileChannel fileChannel = null;
    FileInputStream fileOutputStream = null;


    public ClientRec(String filepath)
    {
        this.filepath=filepath;
    }

    @Override
    public void run() {
        try {
            SocketChannel socketChannel = SocketChannel.open();
//            SocketAddress  socketAddress = new InetSocketAddress("192.168.2.60", 6299);
            SocketAddress  socketAddress = new InetSocketAddress("192.168.31.141", 6299);
//            SocketAddress  socketAddress = new InetSocketAddress("192.168.64.86", 6299);
            socketChannel.connect(socketAddress);

            sendData(socketChannel,filepath,basepath);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendData(SocketChannel socketChannel, String filepath,String basepath) throws IOException {


        try {
//            socketChannel = SocketChannel.open();
//            socketAddress = new InetSocketAddress("192.168.31.141", 6299);
//            socketChannel.connect(socketAddress);
            // send(socketChannel,basepath,filepath);


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
                    System.out.println(refilepath + "文件夹");
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
//                    //sendFile(filepath+File.separator+s);
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


                ByteBuffer buf4 = ByteBuffer.allocate(1024 * 1024*3);
                fileOutputStream = new FileInputStream(new File(filepath));
                fileChannel = fileOutputStream.getChannel();
                long nowlength = 0;
                long sumlength = 0;

                do {
                    nowlength = fileChannel.read(buf4);
                    sumlength += nowlength;
                    buf4.flip();
                    // System.out.println(buf4+"错误------");
                    socketChannel.write(buf4);
                    buf4.clear();

                } while (nowlength != -1 && (sumlength < filelength));


                // System.out.println("发送成功："+files.getPath());
                fileChannel.close();
                fileOutputStream.close();


            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            System.out.println("线程" + Client.number);
            Client.number--;

        }

    }


    }









