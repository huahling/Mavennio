package everyclientonefileYangLi;

import java.io.File;

public class ClientRec {

	private static String basePath= "G:\\one\\";
	public static int a = 0;

	public static void main(String[] args) throws Exception {
		ClientRec.sendFile(basePath);
		//System.out.println(dataOutput);
		Thread.sleep( 10 * 100000);
		//如果服务端接收成功，返回一个消息．告诉客户端

		//客户端线程-模拟多个客户端,每个客户端只发了一个文件
	}

	public static void sendFile(String filePath) throws Exception {
		while(true)
		{
			if(a>5)
			{
				Thread.sleep(10);
			}
			else
			{
				break;
			}
		}

		File file = new File(filePath);
		if (file.isDirectory())// 判断是否为文件夹
		{
			//不发送最基础目录
			if (!filePath.equals(basePath)) {
				NioClientHandlerFolder nioClientHandlerFolder = new NioClientHandlerFolder(filePath,basePath);
				a++;
				nioClientHandlerFolder.start();
				nioClientHandlerFolder.join();
			}
			//递归
			String files []= file.list();
			for (String fPath : files) {
				sendFile(filePath+File.separator + fPath);
			}
		} else {
			a++;
			new NioClientHandlerFile(filePath,basePath).start();
		}
	}
}
