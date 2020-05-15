
public class ThreadTest extends Thread{
	private int start;
	private boolean isDone;
	public ThreadTest(int start) {
		this.start = start;
		isDone = false;
	}
	public void run() {
		int n = 0;
		for (int i = start; i < start + 10000; i++) {
			for (int j = 1; j <= i /2; j++) {
				if (i % j == 0) {
					n++;
				}
			}
		}
		System.out.println("done: " + n);
		isDone = true;
	}
	public boolean getDone() {
		return isDone;
	}
}
