
import java.security.SecureRandom;

public class Hashifier {
	private long[] zobristKeys;
	private SecureRandom sr;
	
	public Hashifier() {
		sr = new SecureRandom();

		zobristKeys = new long[781];
		for (int i = 0; i < 781; i++) {
			zobristKeys[i] = sr.nextLong();
			//System.out.println(zobristKeys[i]);
		}
		
		// zobrist[0] is hash for black to move
		// zobrist[1] is white king castle, zobrist[2] is white queen castle
		// zobrist[3] is black king castle, zobrist[4] is black queen castle
		// zobrist[5,6,7,8,9,10,11,12] are files of en passant squares (not important for the game as is)
		// zobrist[13-780 inclusive] are for each piece at each square
	}
	
	public long makeHashFrom(Piece[][] board) {
		Long retHash = null;
		int idModifier = 0;
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) { // haha c++ get it like programming reference epic stlye gamer hour!
				if (board[r][c] != null) {
					int realId = board[r][c].getId() + idModifier;
					if (retHash == null) {
						retHash = zobristKeys[realId];
					}
					else {
						retHash = retHash ^ zobristKeys[realId]; // ^ is the XOR function, further explanation and stuff can be found on the chessprogramming.org website
					}
				}
				idModifier++;
			}
		}
		return retHash;
	}
	
	public long makeHashMove(long givenHash, int pieceId, int startRow, int startCol, int endRow, int endCol) {
		long ret = givenHash;
		int startIdModifier = startRow * 8 + startCol;
		int endIdModifier = endRow * 8 + endCol;
		
		int startId = pieceId + startIdModifier;
		ret = ret ^ zobristKeys[startId];
		int finalId = pieceId + endIdModifier;
		ret = ret ^ zobristKeys[finalId];
		return ret;
	}
	
	public long makeHashMove(long givenHash, int piece1Id, int piece1Row, int piece1Col, int piece2Id, int piece2Row, int piece2Col) { // piece 2 is the piece that dies
		// IMPORTANT, THIS DOES NOT ACCOUNT FOR EN PASSANT AND ASSUME PIECE 2 ROW AND COL ARE THE DESTINATION OF PIECE 1
		long ret = givenHash;
		int piece1IdModifier = piece1Row * 8 + piece1Col;
		int piece2IdModifier = piece2Row * 8 + piece2Col;
		
		int true1Id = piece1Id + piece1IdModifier;
		ret = ret ^ zobristKeys[true1Id];
		int true2Id = piece2Id + piece2IdModifier;
		ret = ret ^ zobristKeys[true2Id];
		int final1Id = piece1Id + piece2IdModifier;
		ret = ret ^ zobristKeys[final1Id];
		
		return ret;
	}
	
	public long makeHashPromotion(long givenHash, int pieceId, int upgradeId, int row, int col) {
		int idModifier = row * 8 + col;
		long ret = givenHash;
		ret = ret ^ zobristKeys[pieceId+idModifier];
		ret = ret ^ zobristKeys[upgradeId+idModifier];
		return ret;
	}
	
	public long switchHashTurn(long givenHash) {
		return givenHash ^ zobristKeys[0];
	}
	
	public long castleHash(long givenHash, boolean wQueen, boolean wKing, boolean bQueen, boolean bKing) {
		if (wQueen) {
			return givenHash ^ zobristKeys[2];
		}
		else if (wKing) {
			return givenHash ^ zobristKeys[1];
		}
		else if (bQueen) {
			return givenHash ^ zobristKeys[4];
		}
		else if (bKing) {
			return givenHash ^ zobristKeys[3];
		}
		else {
			return givenHash;
		}
	}
	
}
