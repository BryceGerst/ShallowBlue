
public class SimThread extends Thread {
	private ChessGame simGame;
	private boolean done;
	private int result;
	public SimThread(ChessGame simGame) {
		this.simGame = simGame;
	}
	public void run() {
		//System.out.println("given game: \n" + simGame);
		result = simGame.findBestMove(25);
		done = true;
	}
	public boolean getDone() {
		return done;
	}
	public int getResult() {
		return result;
	}

}
