import java.util.ArrayList;

public class ThreadTestRunner {
	public static void main(String[] args) {
		int threadCount = 5;
		ThreadTest[] threads = new ThreadTest[threadCount];
		for (int i = 0; i < threadCount; i++) {
			ThreadTest newThread = new ThreadTest(i*100*(i+2));
			threads[i] = newThread;
			newThread.start();
		}
		boolean allDone = false;
		while (!allDone) {
			allDone = true;
			for (int i = 0; i < threadCount; i++) {
				boolean done = threads[i].getDone();
				if (!done) {
					allDone = false;
					//System.out.println("Thread " + i + " is done? " + done);
				}
				
			}
		}
	}
}
