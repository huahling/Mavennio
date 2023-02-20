package dai;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class ClientThread extends Thread{
	
	private String filePath;
	private static String basePath= "G:\\one\\";
	
	public ClientThread(String filePath)
	{
		this.filePath = filePath;
	}
	
	@Override
	public void run() {
		
		
		SocketChannel socketChannel = null;
		SocketAddress socketAddress = null;
		
		try
		{
			socketChannel = SocketChannel.open();
			socketAddress  =  new InetSocketAddress("192.168.31.141", 6299);
			socketChannel.connect(socketAddress);
			
			
			File file = new File(filePath);
			if (file.isDirectory())// 判断是否为文件夹
			{
				//不发送最基础目录
				if (!filePath.equals(basePath)) {
					String rfilepath = filePath.replace(basePath + "\\", "");

					ByteBuffer buffer0 = ByteBuffer.allocate(4);
					buffer0.putInt(1);
					buffer0.flip();
					socketChannel.write(buffer0);// 发送
					buffer0.clear();
					
					// 文件名长度
					ByteBuffer buffer1 = ByteBuffer.allocate(4);
					buffer1.putInt(new String(rfilepath.getBytes(), "ISO-8859-1").length());

					buffer1.flip();
					socketChannel.write(buffer1);// 发送
					buffer1.clear();

					ByteBuffer buffer2 = ByteBuffer.allocate(new String(rfilepath.getBytes(), "ISO-8859-1").length());
					buffer2.put(rfilepath.getBytes());
					buffer2.flip();
					socketChannel.write(buffer2);// 发送
					buffer2.clear();
				}
				
			} else {
				
				String rfilepath = filePath.replace(basePath + "\\", "");

				
				ByteBuffer buffer0 = ByteBuffer.allocate(4);
				buffer0.putInt(2);
				buffer0.flip();
				socketChannel.write(buffer0);// 发送
				buffer0.clear();
				
				
				// 文件名长度
				ByteBuffer buffer1 = ByteBuffer.allocate(4);
				buffer1.putInt(new String(rfilepath.getBytes(), "ISO-8859-1").length());

				buffer1.flip();
				socketChannel.write(buffer1);// 发送
				buffer1.clear();

				ByteBuffer buffer2 = ByteBuffer.allocate(new String(rfilepath.getBytes(), "ISO-8859-1").length());
				buffer2.put(rfilepath.getBytes());
				buffer2.flip();
				socketChannel.write(buffer2);// 发送
				buffer2.clear();

				ByteBuffer buffer3 = ByteBuffer.allocate(8);
				long fileContentLength = new File(filePath).length();
				buffer3.putLong(fileContentLength);
				buffer3.flip();
				socketChannel.write(buffer3);// 发送
				buffer3.clear();

				ByteBuffer buffer4 = ByteBuffer.allocate(1024 * 1024);
				FileInputStream fileInputStream = new FileInputStream(new File(filePath));
				FileChannel fileChannel = fileInputStream.getChannel();
				long nowReadLength = 0;// 每次读取的文件内容长度
				long sumReadLength = 0;// 累加长度
				do {
					nowReadLength = fileChannel.read(buffer4);
					sumReadLength += nowReadLength;
					buffer4.flip();
					socketChannel.write(buffer4);
					buffer4.clear();
				} while (nowReadLength != -1 && sumReadLength < fileContentLength);
			}
			
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			Client.mark--;
		}

	}
}
