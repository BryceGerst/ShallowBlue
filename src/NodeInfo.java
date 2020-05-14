
public class NodeInfo { // the information stored in here is stored in the transposition table
	
	private int bestInd;
	private long zobristHash;
	private int depthSearched;
	private int score;
	private String move;
	public NodeInfo(int bestInd, long zobristHash, int depthSearched, int score, String move) {
		this.bestInd = bestInd;
		this.zobristHash = zobristHash;
		this.depthSearched = depthSearched;
		this.score = score;
		this.move = move;
	}
	public int getBestInd() {
		return bestInd;
	}
	public long getZobrist() {
		return zobristHash;
	}
	public int getDepth() {
		return depthSearched;
	}
	public int getScore() {
		return score;
	}
	public String getMove() {
		return move;
	}

}
