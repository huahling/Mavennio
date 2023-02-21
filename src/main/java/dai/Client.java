package dai;

import java.io.File;

public class Client {

	private static String basePath= "G:\\one\\";
	public static int mark = 1;

	public static void main(String[] args) throws Exception {
		Client.sendFile(basePath);
		Thread.sleep(1000000);
	}

	public static void sendFile(String filePath) throws Exception {


		while(true)
		{
			if(mark > 5)
			{
				Thread.sleep(10);
			}
			else
			{
				break;
			}
		}
		//1(mark--) 2 3 4 5     6 

		File file = new File(filePath);
		if (file.isDirectory())// �ж��Ƿ�Ϊ�ļ���
		{
			//�����������Ŀ¼
			if (!filePath.equals(basePath)) {

				mark++;
				ClientThread client = new ClientThread(filePath);
				client.start();

				client.join();
				//ֱ�ӷ���
			}
			//�ݹ�
			String files []= file.list();
			for (String fPath : files) {
				sendFile(filePath + File.separator + fPath);
			}
		} else {
			mark++;
			new ClientThread(filePath).start();

			//ֱ�ӷ���
		}
	}
}
