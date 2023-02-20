package everyclientonefileYangLi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class NioserverHandler {

	private final static Logger logger = Logger.getLogger(NioserverHandler.class.getName());
	private final static String DIRECTORY = "F:\\chuan\\";

	/**
	 * 这里边我们处理接收和发送
	 * 
//	 * @param serverSocketChannel
	 */
	public void excute(SelectionKey s) {
		try {
			receiveData(s);// 接数据

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 读取通道中的数据到Object里去
	 * 
	 * @param socketChannel
	 * @return
	 * @throws IOException
	 */

	public static Map<SelectionKey, FileChannel> fileMap = new HashMap<SelectionKey, FileChannel>();
	public static Map<SelectionKey, Long> fileSumLength = new HashMap<SelectionKey, Long>();
	public static Map<SelectionKey, Long> sum = new HashMap<SelectionKey, Long>();

	public static String fileName = null;

	public void receiveData(SelectionKey s) throws IOException {

		//1,2,2,1,1,2,2,2,,1
		SocketChannel socketChannel = (SocketChannel) s.channel();

		if (fileMap.get(s) == null) {

			// 标识
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

			// 文件夹
			if (mark == 1) {
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
				fileName = new String(bytes);
				
				File file = new File(DIRECTORY + File.separator  +fileName);
				if(!file.exists())
				{
					file.mkdirs();
				}
				
//				ByteBuffer buffer2 = ByteBuffer.allocate(2);
//				buffer2.put("ok".getBytes());
//				buffer2.flip();
//				socketChannel.write(buffer2);// 发送
//				buffer2.clear();
				
				socketChannel.socket().close();
				socketChannel.close();

			}
			// 文件
			else if (mark == 2) {
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

				fileName = new String(bytes);

				long fileLengh = 0;
				ByteBuffer buf3 = ByteBuffer.allocate(8);
				while (true) {
					size = socketChannel.read(buf3);
					if (size >= 8) {
						buf3.flip();
						// 文件长度是可要可不要的，如果你要做校验可以留下
						fileLengh = buf3.getLong();

						buf3.clear();

						break;
					}

				}

				ByteBuffer buf24 = ByteBuffer.allocate(1024 * 1024);
				socketChannel.read(buf24);
				String path = DIRECTORY + File.separator + fileName;
				FileChannel fileContentChannel = new FileOutputStream(new File(path)).getChannel();
				buf24.flip();
				long a = fileContentChannel.write(buf24);
				buf24.clear();

				a = (sum.get(s) == null ? 0 : sum.get(s)) + a;
				sum.put(s, a);

				if (sum.get(s) == fileSumLength.get(s)) {
					fileContentChannel.close();
					
					
					
//					ByteBuffer buffer2 = ByteBuffer.allocate(2);
//					buffer2.put("ok".getBytes());
//					buffer2.flip();
//					socketChannel.write(buffer2);// 发送
//					buffer2.clear();
					socketChannel.socket().close();
					socketChannel.close();
				}

				fileMap.put(s, fileContentChannel);
				fileSumLength.put(s, fileLengh);
			}

		} else {
			ByteBuffer buf24 = ByteBuffer.allocate(1024 * 1024);
			socketChannel.read(buf24);// 每次读取的长度

			// String path = DIRECTORY + File.separator + fileName;
			// FileChannel fileContentChannel = new FileOutputStream(new File(path)).getChannel();
			FileChannel fileContentChannel = fileMap.get(s);
			buf24.flip();
			long a = fileContentChannel.write(buf24);

			a = (sum.get(s) == null ? 0 : sum.get(s)) + a;
			sum.put(s, a);

			buf24.clear();

			if (sum.get(s).longValue() == fileSumLength.get(s).longValue()) {
				fileContentChannel.close();
				

//				ByteBuffer buffer2 = ByteBuffer.allocate(2);
//				buffer2.put("ok".getBytes());
//				buffer2.flip();
//				socketChannel.write(buffer2);// 发送
//				buffer2.clear();
				socketChannel.socket().close();
				
				socketChannel.close();
				
			}
		}

	}

}