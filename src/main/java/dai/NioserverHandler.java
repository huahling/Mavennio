package dai;

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

	private final static Logger logger = Logger.getLogger(NioserverHandler.class.getName());
	private final static String dirctory="F:\\chuan\\";

	/**
	 * 这里边我们处理接收和发送
	 * 
	 * @param
	 * 
	 */
	public void excute(SelectionKey s) {
		try {
			receiveData(s);// 接数据

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取通道中的数据到Object里去
	 * 
	 * @param socketChannel
	 * @return
	 * @throws IOException
	 */

	//头信息
	//feng   1(文件夹) ,2(2000) ,3(1000) ,4(3000) ,5(10000)   SelectionKey1
	//liao   a ,b ,c ,d ,e   SelectionKe2
	
	public static Map<SelectionKey, FileChannel> fileMap = new HashMap<SelectionKey, FileChannel>();
	//outputstream    -->  channel
	
	//代表龙文件累加
	//pan
	public static Map<SelectionKey, Long> sumMap = new HashMap<SelectionKey, Long>();
	
	
	//代表龙文件总长度
	//pan
	public static Map<SelectionKey, Long> fileLengthMap = new HashMap<SelectionKey, Long>();
	
	//代表龙文件名
	//pan
	public static Map<SelectionKey, String> fileNameMap = new HashMap<SelectionKey, String>();
	
	/*private static long sum =0;
	private static long fileLength = 0;
	public static String fileName = null;*/
	
	

	public void receiveData(SelectionKey s) throws IOException, InterruptedException {

		SocketChannel socketChannel = (SocketChannel) s.channel();

		//每次等于null,表示有新的文件或文件夹进来
		if (fileMap.get(s) == null) {

			//标识，1代表文件夹，2代表文件
			
			ByteBuffer buf0 = ByteBuffer.allocate(4);
			int mark = 0;
			int size = 0;
			while (true) {
				size = socketChannel.read(buf0);
				if (size >= 4) {
					buf0.flip();
					mark = buf0.getInt();
					buf0.clear();
					break;
				}
			}
			
			if(mark == 1)
			{
				//文件名或文件夹名字长度
				ByteBuffer buf1 = ByteBuffer.allocate(4);
				int fileNamelength = 0;
				// 拿到文件名的长度
				while (true) {
					size = socketChannel.read(buf1);
					if (size >= 4) {
						buf1.flip();
						fileNamelength = buf1.getInt();
						buf1.clear();
						break;
					}
				}

				byte[] bytes = null;
				ByteBuffer buf2 = ByteBuffer.allocate(fileNamelength);
				while (true) {
					size = socketChannel.read(buf2);
					if (size >= fileNamelength) {
						buf2.flip();
						bytes = new byte[fileNamelength];
						buf2.get(bytes);
						buf2.clear();
						break;
					}

				}
				
				String fileName = new String(bytes);
				
				String path = dirctory + File.separator + fileName;
				
				File f = new File(path);
				
				if(!f.exists())
				{
					f.mkdirs();
				}


				
			}else if(mark ==2)
			{

				//文件名或文件夹名字长度
					ByteBuffer buf1 = ByteBuffer.allocate(4);
					int fileNamelength = 0;
					// 拿到文件名的长度
					while (true) {
						size = socketChannel.read(buf1);
						if (size >= 4) {
							buf1.flip();
							fileNamelength = buf1.getInt();
							buf1.clear();
							break;
						}
					}
	
					byte[] bytes = null;
					ByteBuffer buf2 = ByteBuffer.allocate(fileNamelength);
					while (true) {
						size = socketChannel.read(buf2);
						if (size >= fileNamelength) {
							buf2.flip();
							bytes = new byte[fileNamelength];
							buf2.get(bytes);
							buf2.clear();
							break;
						}
	
					}
					
					String fileName = new String(bytes);
					fileNameMap.put(s, fileName);
	
					ByteBuffer buf3 = ByteBuffer.allocate(8);//文件总长度212312312312
					while (true) {
						size = socketChannel.read(buf3);
						if (size >= 8) {
							buf3.flip();
							// 文件长度是可要可不要的，如果你要做校验可以留下
							long fileLength = buf3.getLong();
							fileLengthMap.put(s, fileLength);
	
							buf3.clear();
	
							break;
						}
	
					}
					
					//500-4-10-8=476(文件内容)，接下一个文件
	
					sumMap.put(s, 0L);
					
					ByteBuffer buf24 = null;
					if(fileLengthMap.get(s) - sumMap.get(s) < 1024*1024)
					{
						buf24 = ByteBuffer.allocate(Integer.valueOf(String.valueOf(fileLengthMap.get(s) - sumMap.get(s))));
						
					}
					else
					{
						buf24 = ByteBuffer.allocate(1024 * 1024);
	
					}
					socketChannel.read(buf24);
					String path = dirctory + File.separator + fileName;
					File f = new File(path);
//					if(!f.exists())
//					{
//						if(!f.getParentFile().exists())
//						{
//							f.getParentFile().mkdirs();
//						}
//						f.createNewFile();
//					}
				                Thread.sleep(20);
					FileChannel fileContentChannel = new FileOutputStream(f).getChannel();
					buf24.flip();
					long a = fileContentChannel.write(buf24);
					buf24.clear();
	
					sumMap.put(s,  sumMap.get(s) + a);
	
					
					//如果一次接收完成。不需要再进行再次接收。需要把以前的信息设置为初始化状态
					if (sumMap.get(s).longValue() == fileLengthMap.get(s).longValue()) {
						sumMap.put(s,0L);
						
						fileLengthMap.put(s, 0L);
						
						fileMap.put(s, null);
						fileContentChannel.close();
						
						
						socketChannel.socket().close();
						socketChannel.close();
					}
					else
					{
						fileMap.put(s, fileContentChannel);
					}
			}

		} else {
			
			ByteBuffer buf24 = null;
			if(fileLengthMap.get(s) - sumMap.get(s) < 1024*1024)
			{
				buf24 = ByteBuffer.allocate(Integer.valueOf(String.valueOf(fileLengthMap.get(s) - sumMap.get(s))));
				
			}
			else
			{
				buf24 = ByteBuffer.allocate(1024 * 1024);

			}
			
			socketChannel.read(buf24);// 每次读取的长度

			// String path = DIRECTORY + File.separator + fileName;
			// FileChannel fileContentChannel = new FileOutputStream(new
			// File(path)).getChannel();
			FileChannel fileContentChannel = fileMap.get(s);
			buf24.flip();
			long a = fileContentChannel.write(buf24);
			
			sumMap.put(s,  sumMap.get(s) + a);

			buf24.clear();

//			if (sumMap.get(s).longValue()== fileLengthMap.get(s).longValue()) {
//				sumMap.put(s,0L);
//
//				fileLengthMap.put(s, 0L);
//
//				fileMap.put(s, null);
//				fileContentChannel.close();
//
//				//s.channel().close();
//
//				//fileContentChannel.close();
//				socketChannel.socket().close();
//				socketChannel.close();
//			}
		}

	}

}