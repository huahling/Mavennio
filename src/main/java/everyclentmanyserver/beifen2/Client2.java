package everyclentmanyserver.beifen2;

import java.io.File;

public class Client2 {
	public static int number = 1;

//	private static String basePath = "D:\\课件\\webService\\";
//private static String basePath= "C:\\Users\\user\\Desktop\\d\\";
//private static String basePath= "C:\\Users\\user\\Desktop\\c\\";
//private static String basePath= "C:\\Users\\user\\Desktop\\a\\a\\b\\";
private static String basePath= "G:\\one\\";
//private static String basePath= "D:\\传输\\夏娜\\";
//private static String basePath= "F:\\新传输位置\\夏娜\\webService\\";
	public static void main(String[] args) throws Exception {

		Client2.sendFile(basePath);
		Thread.sleep(1000000);
	}

	public static void sendFile(String filePath) throws InterruptedException {
		while(true)
		{
			if(number>5)
			{
				Thread.sleep(10);
			}
			else
			{
				break;
			}
		}


		File file=new File(filePath);
		//System.out.println(file.exists());
		//System.out.println("是否是文件###"+file.isDirectory());
		if (file.isDirectory())// 判断是否为文件夹
		{
			//不发送最基础目录
			if (!filePath.equals(basePath)) {

				number++;
				//System.out.println("----线文"+number);
				ClientRec2 client = new ClientRec2(filePath);

				client.start();

				client.join();
			}
			//递归
			String files []= file.list();
			for (String fPath : files) {
				//System.out.println(filePath + File.separator + fPath+"循环======");
				sendFile(filePath + File.separator + fPath);
			}
		} else {
			number++;
			//System.out.println("----线"+number);

			//new ClientRec(filePath).start();
			ClientRec2 client = new ClientRec2(filePath);
			client.start();

		//	System.out.println("是否是文件？"+filePath);
		}



		//Thread.sleep(400);


	}
}
