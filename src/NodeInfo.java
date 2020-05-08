
public class NodeInfo { // the information stored in here is stored in the transposition table
	
	private int bestInd;
	private long zobristHash;
	public NodeInfo(int bestInd, long zobristHash) {
		this.bestInd = bestInd;
		this.zobristHash = zobristHash;
	}
	public int getBestInd() {
		return bestInd;
	}
	public long getZobrist() {
		return zobristHash;
	}

}
