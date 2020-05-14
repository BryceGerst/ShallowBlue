
public class SimThread extends Thread {
	ChessGame simGame;
	public SimThread(ChessGame simGame) {
		this.simGame = simGame;
	}
	public void run() {
		System.out.println("given game: \n" + simGame);
		simGame.findBestMove();
	}

}
