package manymany;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class ClientRec2 {
//    private static String basepath= "C:\\Users\\user\\Desktop\\a\\a\\b\\";
//    private static String basepath= "C:\\Users\\user\\Desktop\\c\\";
    private static String basepath= "G:\\two\\";
    private static SocketChannel socketChannel=null;
    private static SocketAddress socketAddress=null;

    public static void main(String[] args) throws IOException, InterruptedException {
       socketChannel =SocketChannel.open();
       socketAddress=new InetSocketAddress("192.168.31.141",6299);
       socketChannel.connect(socketAddress);

        ClientRec2.sendFile(basepath);
        Thread.sleep(1000000);
    }

    public static void sendFile(String filepath) throws IOException, InterruptedException {

        File files=new File(filepath);
       // System.out.println(files.exists());
        if(files.isDirectory()){
            if(!filepath.equals(basepath)){
                 ByteBuffer buf0=ByteBuffer.allocate(4);
                 buf0.putInt(1);
                 buf0.flip();
                 socketChannel.write(buf0);
                 buf0.clear();

                 String afilepath= files.getPath();
                 String refilepath=afilepath.replace(basepath,"");
                System.out.println(refilepath+"****文件夹");
                 ByteBuffer buf=ByteBuffer.allocate(4);
                 buf.putInt(new String(refilepath.getBytes(),"ISO-8859-1").length());
                     buf.flip();
                     socketChannel.write(buf);
                     buf.clear();

                     ByteBuffer buff=ByteBuffer.allocate(new String(refilepath.getBytes(),"ISO-8859-1").length());
                       buff.put(refilepath.getBytes());
                       buff.flip();
                       socketChannel.write(buff);
                       buff.clear();

            }

            String filess[]=files.list();
            for (String s : filess) {
                //System.out.println(s+"---");
                sendFile(filepath+File.separator+s);
            }

        }else{

            ByteBuffer buf01=ByteBuffer.allocate(4);
            buf01.putInt(2);
            buf01.flip();
            socketChannel.write(buf01);
            buf01.clear();

            String aafilepath= files.getPath();
            String rfilepath=aafilepath.replace(basepath,"");
            System.out.println(rfilepath+"----文件");
            ByteBuffer buf1=ByteBuffer.allocate(4);
            buf1.putInt(new String(rfilepath.getBytes(),"ISO-8859-1").length());
             buf1.flip();
             socketChannel.write(buf1);
             buf1.clear();

             ByteBuffer buf2=ByteBuffer.allocate(new String(rfilepath.getBytes(),"ISO-8859-1").length());
                 buf2.put(rfilepath.getBytes());
                 buf2.flip();
                 socketChannel.write(buf2);
                   buf2.clear();

                   ByteBuffer buf3=ByteBuffer.allocate(8);
                   long filelength=new File(filepath).length();
                   buf3.putLong(filelength);
                   buf3.flip();
                   socketChannel.write(buf3);
                   buf3.clear();

                   ByteBuffer buf4=ByteBuffer.allocate(1024*1024*3);
           FileInputStream fileOutputStream=new FileInputStream(new File(filepath));
            FileChannel fileChannel=fileOutputStream.getChannel();
            long nowlength=0;
            long sumlength=0;

            do{
                nowlength=fileChannel.read(buf4);
                sumlength+=nowlength;
                buf4.flip();
                socketChannel.write(buf4);
                buf4.clear();

            }while( nowlength!=-1&&(sumlength<filelength));


        }

   //  Thread.sleep(100000);
    }

}
