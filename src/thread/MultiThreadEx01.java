package thread;

public class MultiThreadEx01 {

	public static void main(String[] args) {
//		for(int i=0; i<10; i++) {
//			System.out.print(i);
//		}
		Thread digitThread = new DigitThread();
		digitThread.start();
		
		for(char c='a'; c<='z'; c++) {
			System.out.print(c);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
