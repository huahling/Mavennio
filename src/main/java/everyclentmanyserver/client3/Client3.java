package everyclentmanyserver.client3;

import java.io.File;

public class Client3 {

//	private static String basePath = "C:\\Users\\Administrator\\Desktop\\a\\";
//	private static String basePath = "F:\\linux\\nginx\\";
//		private static String basePath = "F:\\linux\\MQ\\";
//		private static String basepath= "C:\\Users\\user\\Desktop\\a\\a\\c\\";

//private static String basepath= "C:\\Users\\user\\Desktop\\c\\";
//private static String basepath= "C:\\Users\\user\\Desktop\\d\\";
//private static String basepath= "D:\\传输\\夏娜\\";
//private static String basepath= "G:\\two\\";
//private static String basepath= "G:\\one\\";
//    private static String basepath= "C:\\Users\\user\\Desktop\\d\\";
//    private static String basepath= "C:\\Users\\user\\Desktop\\js\\";
    private static String basepath= "C:\\Users\\user\\Desktop\\jt\\";
//	private static String basepath= "C:\\Users\\user\\Desktop\\AB\\";
//	private static String basepath= "C:\\Users\\user\\Desktop\\a\\a\\b\\";
//private static String basepath= "F:\\新传输位置\\夏娜\\webService\\";

//		private static String basePath = "F:\\新传输位置\\大神\\socket\\";
//		private static String basePath = "F:\\linux\\nginx\\";

	public static int mk = 0;

	public static void main(String[] args) throws Exception {
		Client3.sendFile(basepath);
		Thread.sleep(60000);//必须要比较大 不然多线程会卡
//		Thread.sleep(1000000);//必须要比较大 不然多线程会卡
	}

	public static void sendFile(String filePath) throws Exception {


		while(true)
		{
			if(mk > 5)
			{
//				System.out.println("线程数 "+mk);
				Thread.sleep(10);
			}
			else
			{
				break;
			}
		}

		File file = new File(filePath);
		if (file.isDirectory())// �ж��Ƿ�Ϊ�ļ���
		{
			if (!filePath.equals(basepath)) {
				mk++;
				ClientThread3 client = new ClientThread3(filePath);
				client.start();
				client.join();
			}
			String filess[]=file.list();
			for (String s : filess) {
				//System.out.println(s+"---");
				sendFile(filePath+File.separator+s);
			}
		} else {
			mk++;
			new ClientThread3(filePath).start();
		}
//		Thread.sleep(50);
//		Thread.sleep(1500);
	}
}
