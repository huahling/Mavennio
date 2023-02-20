package everyclientonefileYangLi.ClientRec2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioClientHandlerFolder extends Thread {
	
	private String filePath = null;
	private String basePath = null;

	public NioClientHandlerFolder(String filePath, String basePath) {
		this.filePath = filePath;
		this.basePath = basePath;
	}

	@Override
	public void run() {
		try {
			SocketChannel socketChannel = SocketChannel.open();
			SocketAddress socketAddress = new InetSocketAddress(InetAddress.getLocalHost(), 8080);
			
			socketChannel.socket().setReuseAddress(true);
			socketChannel.connect(socketAddress);

			sendData(socketChannel, filePath, basePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 向服务器发送数据
	 * 
	 * @param socketChannel
	 * @param filepath      文件路径
//	 * @param filename      文件名
	 * @throws IOException
	 */
	public void sendData(SocketChannel socketChannel, String filepath,String basePath) throws IOException {

		filepath = filepath.replace(basePath + "\\", "");
		
		ByteBuffer buffer0 = ByteBuffer.allocate(4);
		buffer0.putInt(1);

		buffer0.flip();
		socketChannel.write(buffer0);// 发送
		buffer0.clear();

		// 文件名长度
		ByteBuffer buffer1 = ByteBuffer.allocate(4);
		buffer1.putInt(new String(filepath.getBytes(), "ISO-8859-1").length());

		buffer1.flip();
		socketChannel.write(buffer1);// 发送
		buffer1.clear();

		ByteBuffer buffer2 = ByteBuffer.allocate(new String(filepath.getBytes(), "ISO-8859-1").length());
		buffer2.put(filepath.getBytes());
		buffer2.flip();
		socketChannel.write(buffer2);// 发送
		buffer2.clear();



//        buffer.putInt(filename.length()); // 文件名长度  
//        buffer.put(filename.getBytes());  // 文件名  
//        
//        buffer.putInt(bytes.length);     // 文件长度  
//        buffer.put(ByteBuffer.wrap(bytes));// 文件  
//        
//        buffer.flip();    // 把缓冲区的定位指向开始0的位置 清除已有标记  
//        socketChannel.write(buffer);  
//        buffer.clear();  
//        // 关闭输出流防止接收时阻塞， 就是告诉接收方本次的内容已经发完了，你不用等了
		
//		byte[] bytes = new byte[2];
//		
//		ByteBuffer res = ByteBuffer.allocate(2);
//		int size =0;
//		while (true) {
//			size = socketChannel.read(res);
//			if (size >= 2) {
//				res.flip();
//				res.get(bytes);
//				res.clear();
//				break;
//			}
//		}
//		
//		
//		String status = new String(bytes);
//		if("ok".equals(status))
//		{
//			System.out.println("folder --- " +status);
//			ClientRec.a -- ;
//			socketChannel.close();
//		}
		ClientRec.a--;
		 socketChannel.socket().shutdownOutput();  
	}

	/**
	 * 接收服务器相应的信息
	 * 
	 * @param socketChannel
	 * @return
	 * @throws IOException
	 */
	public String receiveData(SocketChannel socketChannel) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String response = "";
		try {
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			byte[] bytes;
			int count = 0;
			while ((count = socketChannel.read(buffer)) >= 0) {
				buffer.flip();
				bytes = new byte[count];
				buffer.get(bytes);
				baos.write(bytes);
				buffer.clear();
			}

			bytes = baos.toByteArray();
			response = new String(bytes, "UTF-8");
			socketChannel.socket().shutdownInput();
		} finally {
			try {
				baos.close();
			} catch (Exception ex) {
			}
		}
		return response;
	}

	/**
	 * 将文件转换成byte
	 * 
	 * @param fileFath
	 * @return
	 * @throws IOException
	 */
	private byte[] makeFileToByte(String fileFath) throws IOException {
		File file = new File(fileFath);
		FileInputStream fis = new FileInputStream(file);
		int length = (int) file.length();
		byte[] bytes = new byte[length];
		int temp = 0;
		int index = 0;
		while (true) {
			index = fis.read(bytes, temp, length - temp);
			if (index <= 0)
				break;
			temp += index;
		}
		fis.close();
		return bytes;
	}

}