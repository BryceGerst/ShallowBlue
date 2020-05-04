import java.util.ArrayList;
import java.util.Random;

public class ChessGame {
	private Piece[][] board;
	private Piece[][] previousBoard;
	// castle booleans
	private boolean whiteCanCastleKing;
	private boolean whiteCanCastleQueen;
	private boolean blackCanCastleKing;
	private boolean blackCanCastleQueen;
	// who's turn is it?
	private boolean whitesTurn;
	// pressure arrays
	private int[][] whitePressure;
	private int[][] blackPressure;
	// possible move arrays
	private ArrayList<String> whiteNPM; // NPM means naive possible moves (doesn't check if move would put king in check)
	private ArrayList<String> whitePM; // PM means possible moves, meaning the move is for sure possible
	private ArrayList<String> whiteSM; // SM means smart moves, meaning they don't move a piece into a position where it could be taken and doesn't take anything itself
	private ArrayList<String> blackNPM;
	private ArrayList<String> blackPM;
	private ArrayList<String> blackSM;
	
	
	
	// Constructor
	public ChessGame() {
		board = new Piece[8][8];
		generateSide(1, 0, "White");
		generateSide(6, 7, "Black");
		
		whiteCanCastleKing = true;
		whiteCanCastleQueen = true;
		blackCanCastleKing = true;
		blackCanCastleQueen = true;
		
		whitesTurn = true;
	}
	
	public ChessGame(Piece[][] givenBoard, boolean wK, boolean wQ, boolean bK, boolean bQ, boolean wT, ArrayList<String> wPM, ArrayList<String> bPM, int[][] wP, int[][] bP) {
		board = dupeBoard(givenBoard);
		
		whiteCanCastleKing = wK;
		whiteCanCastleQueen = wQ;
		blackCanCastleKing = bK;
		blackCanCastleQueen = bQ;
		
		whitesTurn = wT;
		
		whitePM = new ArrayList<String>(wPM);
		blackPM = new ArrayList<String>(bPM);
		
		whitePressure = wP.clone();
		blackPressure = bP.clone();
		
	}
	
	private void generateSide(int pawnRow, int kingRow, String teamColor) {
		for (int col = 0; col < 8; col++) {
			board[pawnRow][col] = new Piece("Pawn", teamColor);
			if (col == 0 || col == 7) {
				board[kingRow][col] = new Piece("Rook", teamColor);
			}
			else if (col == 1 || col == 6) {
				board[kingRow][col] = new Piece("Knight", teamColor);
			}
			else if (col == 2 || col == 5) {
				board[kingRow][col] = new Piece("Bishop", teamColor);
			}
			else if (col == 3) {
				board[kingRow][col] = new Piece("Queen", teamColor);
			}
			else if (col == 4) {
				board[kingRow][col] = new Piece("King", teamColor);
			}
			else {
				System.out.println("Error generating side");
			}
		}
	}
	
	private void generatePressureArrays() {
		whiteNPM = new ArrayList<String>();
		blackNPM = new ArrayList<String>();
		whitePressure = new int[8][8];
		blackPressure = new int[8][8];
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if(board[row][col] != null) {
					Piece current = board[row][col];
					String team = current.getTeam();
					if (team.equals("White")) {
						current.alterPressure(row, col, board, whitePressure, whiteNPM);
					}
					else {
						current.alterPressure(row, col, board, blackPressure, blackNPM);
					}
				}
			}
		}
	}
	
	private void genTestPressure() {
		ArrayList<String>garb1 = new ArrayList<String>();
		ArrayList<String>garb2 = new ArrayList<String>();
		whitePressure = new int[8][8];
		blackPressure = new int[8][8];
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if(board[row][col] != null) {
					Piece current = board[row][col];
					String team = current.getTeam();
					if (team.equals("White")) {
						current.alterPressure(row, col, board, whitePressure, garb1);
					}
					else {
						current.alterPressure(row, col, board, blackPressure, garb2);
					}
				}
			}
		}
	}
	
	private Piece[][] dupeBoard(Piece[][] dupeBoard) {
		Piece[][] retBoard = new Piece[8][8];
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				if (dupeBoard[r][c] != null) {
					retBoard[r][c] = dupeBoard[r][c].dupe();
				}
			}
		}
		return retBoard;
	}
	
	private String checkIfPawnUpgrade(String move) {
		String ret = move;
		int startCol = (int)move.charAt(0) - 97; // 97 is the unicode value for lower case a
		int startRow = Integer.parseInt(move.substring(1,2)) - 1; // minus 1 for zero based indexing
		if (board[startRow][startCol] == null) {
			//System.out.println("error finding piece at [" + startRow + "][" + startCol + "]");
		}
		else {
			Piece movePiece = board[startRow][startCol].dupe();
			if ((movePiece.getType()).equals("Pawn")) {
				if ((movePiece.getTeam()).equals("White")) {
					int endRow = Integer.parseInt(move.substring(3,4)) - 1;
					if (endRow == 7) {
						ret += "Queen";
						//System.out.println("UPGRADE UPGRADE WEE WOO WEE WOO");
					}
				}
				else {
					int endRow = Integer.parseInt(move.substring(3,4)) - 1;
					if (endRow == 0) {
						ret += "Queen";
						//System.out.println("UPGRADE UPGRADE WEE WOO WEE WOO");
					}
				}
			}
		}
		return ret;
	}
	
	private void removeBadMoves() {
		whitePM = new ArrayList<String>();
		blackPM = new ArrayList<String>();
		whiteSM = new ArrayList<String>();
		blackSM = new ArrayList<String>();
		boolean captured;
		boolean valid;
		Piece[][] originalBoard = dupeBoard(board);
		if (whitesTurn) {
			if (whiteCanCastleQueen) {
				if(board[0][1] == null && board[0][2] == null && board[0][3] == null && blackPressure[0][2] == 0 && blackPressure[0][3] == 0 && blackPressure[0][4] == 0) {
					whitePM.add("e1c1");
				}
			}
			if (whiteCanCastleKing) {
				if(board[0][5] == null && board[0][6] == null && blackPressure[0][5] == 0 && blackPressure[0][6] == 0 && blackPressure[0][4] == 0) {
					whitePM.add("e1g1");
				}
			}
			for(int i = 0; i < whiteNPM.size(); i++) {
				valid = false;
				board = dupeBoard(originalBoard);
				String testMove = whiteNPM.get(i);
				testMove = checkIfPawnUpgrade(testMove);
				captured = forceMove(testMove);
				genTestPressure();
				for (int r = 0; r < 8; r++) {
					for (int c = 0; c < 8; c++) {
						if (board[r][c] != null && board[r][c].getTeam().equals("White") && board[r][c].getType().equals("King")) {
							if(blackPressure[r][c] == 0) {
								whitePM.add(testMove);
								valid = true;
							}
						}
					}
				}
				if (valid) {
					if (captured) {
						whiteSM.add(testMove);
					}
					else {
						int endCol = (int)testMove.charAt(2) - 97;
						int endRow = Integer.parseInt(testMove.substring(3,4)) - 1;
						if(!(blackPressure[endRow][endCol] > 0 && whitePressure[endRow][endCol] == 0)) { // basically if piece could NOT be captured without retaliation
							whiteSM.add(testMove);
						}
					}
				}
			}
		}
		else { // black's turn
			if (blackCanCastleQueen) {
				if(board[7][1] == null && board[7][2] == null && board[7][3] == null && whitePressure[7][2] == 0 && whitePressure[7][3] == 0 && whitePressure[7][4] == 0) {
					blackPM.add("e8c8");
				}
			}
			if (blackCanCastleKing) {
				if(board[7][5] == null && board[7][6] == null && whitePressure[7][5] == 0 && whitePressure[7][6] == 0 && whitePressure[7][4] == 0) {
					blackPM.add("e8g8");
				}
			}
			for(int i = 0; i < blackNPM.size(); i++) {
				valid = false;
				board = dupeBoard(originalBoard);
				String testMove = blackNPM.get(i);
				testMove = checkIfPawnUpgrade(testMove);
				captured = forceMove(testMove);
				genTestPressure();
				for (int r = 0; r < 8; r++) {
					for (int c = 0; c < 8; c++) {
						if (board[r][c] != null && board[r][c].getTeam().equals("Black") && board[r][c].getType().equals("King")) {
							if(whitePressure[r][c] == 0) {
								blackPM.add(testMove);
								valid = true;
							}
						}
					}
				}
				if (valid) {
					if (captured) {
						blackSM.add(testMove);
					}
					else {
						int endCol = (int)testMove.charAt(2) - 97;
						int endRow = Integer.parseInt(testMove.substring(3,4)) - 1;
						if(!(whitePressure[endRow][endCol] > 0 && blackPressure[endRow][endCol] == 0)) { // basically if piece could NOT be captured without retaliation
							blackSM.add(testMove);
						}
					}
				}
			}
		}
		board = dupeBoard(originalBoard);
	}
	
	private boolean forceMove(String move) {
		boolean captured = false;
		// example, "e2e4"
		if (move.length() == 4) {	// simple move (meaning no upgrade)
			int startCol = (int)move.charAt(0) - 97; // 97 is the unicode value for lower case a
			int startRow = Integer.parseInt(move.substring(1,2)) - 1; // minus 1 for zero based indexing
			if (board[startRow][startCol] == null) {
				//System.out.println("error finding piece at [" + startRow + "][" + startCol + "]");
			}
			else {
				Piece movePiece = board[startRow][startCol].dupe();
				board[startRow][startCol] = null;
				int endCol = (int)move.charAt(2) - 97;
				int endRow = Integer.parseInt(move.substring(3,4)) - 1;
				if (board[endRow][endCol] != null) {
					captured = true;
				}
				board[endRow][endCol] = movePiece;
			}
		}
		else {
			int startCol = (int)move.charAt(0) - 97; // 97 is the unicode value for lower case a
			int startRow = Integer.parseInt(move.substring(1,2)) - 1; // minus 1 for zero based indexing
			String upgradeName = move.substring(4);
			if (board[startRow][startCol] == null) {
				//System.out.println("error finding piece at [" + startRow + "][" + startCol + "]");
			}
			else {
				Piece movePiece = new Piece(upgradeName, board[startRow][startCol].getTeam());
				board[startRow][startCol] = null;
				int endCol = (int)move.charAt(2) - 97;
				int endRow = Integer.parseInt(move.substring(3,4)) - 1;
				if (board[endRow][endCol] != null) {
					captured = true;
				}
				board[endRow][endCol] = movePiece;
			}
		}
		return captured;
	}
	
	private void printPressure(String teamName) {
		if (teamName.equals("White")) {
			for (int r = 7; r > -1; r--) {
				for (int c = 0; c < 8; c++) {
					System.out.print("|" + whitePressure[r][c] + "|");
				}
				System.out.println();
			}
		}
		else {
			for (int r = 7; r > -1; r--) {
				for (int c = 0; c < 8; c++) {
					System.out.print("|" + blackPressure[r][c] + "|");
				}
				System.out.println();
			}
		}
	}
	
	public void outputMove() {
		// TODO: make this unnecessary
		generatePressureArrays();
		removeBadMoves();
		// ------------------------------
		ArrayList<String> whiteUM; // white using moves, aka SM if exists, otherwise PM
		ArrayList<String> blackUM;
		if (whiteSM.size() > 0) {
			whiteUM = whiteSM;
		}
		else {
			whiteUM = whitePM;
		}
		if (blackSM.size() > 0) {
			blackUM = blackSM;
		}
		else {
			blackUM = blackPM;
		}
		int ind;
		if (whitesTurn) {
			ind = recursiveSim(this, whitesTurn, 0, 2)[0];
			System.out.println(whiteUM.get(ind));
			inputMove(whiteUM.get(ind));
		}
		else {
			ind = recursiveSim(this, whitesTurn, 0, 2)[0];
			System.out.println(blackUM.get(ind));
			inputMove(blackUM.get(ind));
		}
	}
	
	public boolean getTurn() {
		return whitesTurn;
	}
	
	public Piece[][] getBoard() {
		return board;
	}
	public boolean getWK() {
		return whiteCanCastleKing;
	}
	public boolean getWQ() {
		return whiteCanCastleQueen;
	}
	public boolean getBK() {
		return blackCanCastleKing;
	}
	public boolean getBQ() {
		return blackCanCastleQueen;
	}
	
	private int[] recursiveSim(ChessGame givenGame, boolean forWhite, int level, int maxLevel) {
		Random ran = new Random();
		givenGame.generatePressureArrays();
		givenGame.whitePM = new ArrayList<String>();
		givenGame.blackPM = new ArrayList<String>();
		givenGame.whiteSM = new ArrayList<String>();
		givenGame.blackSM = new ArrayList<String>();
		givenGame.removeBadMoves();
		
		ArrayList<String> whiteUM; // white using moves, aka SM if exists, otherwise PM
		ArrayList<String> blackUM;
		if (givenGame.whiteSM.size() > 0) {
			whiteUM = givenGame.whiteSM;
			//System.out.println("cut down size by " + (givenGame.whitePM.size() - givenGame.whiteSM.size()));
		}
		else {
			whiteUM = givenGame.whitePM;
		}
		if (givenGame.blackSM.size() > 0) {
			blackUM = givenGame.blackSM;
			//System.out.println("cut down size by " + (givenGame.blackPM.size() - givenGame.blackSM.size()));
		}
		else {
			blackUM = givenGame.blackPM;
		}
		
		int result;
		int bestResult = -1000000;
		int bestIndex = 0;
		int worstResult = 1000000;
		int worstIndex = 0;
		
		int[] ret = {0,0};
		
		ChessGame testGame;
		
		if (level == maxLevel) { // MAX LEVEL SHOULD ALWAYS BE EVEN
			if (forWhite) {
				for (int i = 0; i < whiteUM.size(); i++) {
					testGame = new ChessGame(givenGame.getBoard(), givenGame.getWK(), givenGame.getWQ(), givenGame.getBK(), givenGame.getBQ(), givenGame.getTurn(), givenGame.whitePM, givenGame.blackPM, givenGame.whitePressure, givenGame.blackPressure);
					result = testGame.simGame(whiteUM.get(i), 0);
					if (result > bestResult || (result == bestResult && ran.nextInt(2) == 0)) {
						bestResult = result;
						bestIndex = i;
					}
				}
			}
			else {
				for (int i = 0; i < blackUM.size(); i++) {
					testGame = new ChessGame(givenGame.getBoard(), givenGame.getWK(), givenGame.getWQ(), givenGame.getBK(), givenGame.getBQ(), givenGame.getTurn(), givenGame.whitePM, givenGame.blackPM, givenGame.whitePressure, givenGame.blackPressure);
					result = testGame.simGame(blackUM.get(i), 0);
					if (result > bestResult || (result == bestResult && ran.nextInt(2) == 0)) {
						bestResult = result;
						bestIndex = i;
					}
				}
			}
			ret[0] = bestIndex;
			ret[1] = bestResult;
		}
		else {
			if (level % 2 == 0) { // pick the best of this level
				if (forWhite) {
					for (int i = 0; i < whiteUM.size(); i++) {
						testGame = new ChessGame(givenGame.getBoard(), givenGame.getWK(), givenGame.getWQ(), givenGame.getBK(), givenGame.getBQ(), givenGame.getTurn(), givenGame.whitePM, givenGame.blackPM, givenGame.whitePressure, givenGame.blackPressure);
						testGame.botInputMove(whiteUM.get(i));
						result = recursiveSim(testGame, forWhite, level + 1, maxLevel)[1];
						if (result > bestResult || (result == bestResult && ran.nextInt(2) == 0)) {
							bestResult = result;
							bestIndex = i;
						}
					}
				}
				else {
					for (int i = 0; i < blackUM.size(); i++) {
						testGame = new ChessGame(givenGame.getBoard(), givenGame.getWK(), givenGame.getWQ(), givenGame.getBK(), givenGame.getBQ(), givenGame.getTurn(), givenGame.whitePM, givenGame.blackPM, givenGame.whitePressure, givenGame.blackPressure);
						testGame.botInputMove(blackUM.get(i));
						result = recursiveSim(testGame, forWhite, level + 1, maxLevel)[1];
						if (result > bestResult || (result == bestResult && ran.nextInt(2) == 0)) {
							bestResult = result;
							bestIndex = i;
						}
					}
				}
				ret[0] = bestIndex;
				ret[1] = bestResult;
			}
			else { // pick the worst from this level
				if (forWhite) {
					for (int i = 0; i < blackUM.size(); i++) {
						testGame = new ChessGame(givenGame.getBoard(), givenGame.getWK(), givenGame.getWQ(), givenGame.getBK(), givenGame.getBQ(), givenGame.getTurn(), givenGame.whitePM, givenGame.blackPM, givenGame.whitePressure, givenGame.blackPressure);
						testGame.botInputMove(blackUM.get(i));
						result = recursiveSim(testGame, forWhite, level + 1, maxLevel)[1];
						if (result < worstResult || (result == worstResult && ran.nextInt(2) == 0)) {
							worstResult = result;
							worstIndex = i;
						}
					}
				}
				else {
					for (int i = 0; i < whiteUM.size(); i++) {
						testGame = new ChessGame(givenGame.getBoard(), givenGame.getWK(), givenGame.getWQ(), givenGame.getBK(), givenGame.getBQ(), givenGame.getTurn(), givenGame.whitePM, givenGame.blackPM, givenGame.whitePressure, givenGame.blackPressure);
						testGame.botInputMove(whiteUM.get(i));
						result = recursiveSim(testGame, forWhite, level + 1, maxLevel)[1];
						if (result < worstResult || (result == worstResult && ran.nextInt(2) == 0)) {
							worstResult = result;
							worstIndex = i;
						}
					}
				}
				ret[0] = worstIndex;
				ret[1] = worstResult;
			}
		}
		return ret;
	}
	
	public void botInputMove(String move) {
		//generatePressureArrays();
		//printPressure("White");
		//whitePM = new ArrayList<String>();
		//blackPM = new ArrayList<String>();
		//removeBadMoves();
		// castling stuff
		if (move.contains("e1")) {
			whiteCanCastleQueen = false;
			whiteCanCastleKing = false;
		}
		else if (move.contains("a1")) {
			whiteCanCastleQueen = false;
		}
		else if (move.contains("h1")) {
			whiteCanCastleKing = false;
		}
		else if (move.contains("e8")) {
			blackCanCastleQueen = false;
			blackCanCastleKing = false;
		}
		else if (move.contains("a8")) {
			blackCanCastleQueen = false;
		}
		else if (move.contains("h8")) {
			blackCanCastleKing = false;
		}
		// end first castling stuff
		//System.out.println("valid move");
		forceMove(move);
		// second castling stuff
		if (move.equals("e1c1")) {
			forceMove("a1d1");
		}
		else if (move.equals("e1g1")) {
			forceMove("h1f1");
		}
		else if (move.equals("e8c8")) {
			forceMove("a8d8");
		}
		else if (move.equals("e8g8")) {
			forceMove("h8f8");
		}
		// end second castling stuff
		whitesTurn = !whitesTurn;
					
	}
	
	public int simGame(String move, int level) {
		boolean simForWhite = whitesTurn;
		botInputMove(move);
		boolean whiteCastled = (move.equals("e1g1") || move.equals("e1c1"));
		boolean blackCastled = (move.equals("e8g8") || move.equals("e8c8"));
		int val = getBoardStrength(simForWhite, whiteCastled, blackCastled);
		//System.out.println(val);
		//System.out.println(this);
		return val;
	}
	
	
	
	public int getBoardStrength(boolean forWhite, boolean whiteCastled, boolean blackCastled) {
		generatePressureArrays();
		removeBadMoves();
		int strength = 0;
		int pressureValue, rawValue;
		int[][] netPressure = new int[8][8];
		// the following comments show the values that for sure are decent-ish
		int myTurnMult = 1; // 1;
		int rawModifier = 15;
		int spaceValue = 1; // 1;
		int pressureModifier = 2; // 2;
		int castleValue = 7; // 0
		
		if (whiteCanCastleQueen || whiteCanCastleKing || whiteCastled) {
			strength += castleValue;
		}
		if (blackCanCastleKing || blackCanCastleQueen || blackCastled) {
			strength -= castleValue;
		}
		
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				netPressure[r][c] = whitePressure[r][c] - blackPressure[r][c];
				if (board[r][c] != null) {
					Piece current = board[r][c];
					if (current.getType().equals("King")) {
						if (current.getTeam().equals("Black")) {
							if (blackPM.size() == 0 && whitePressure[r][c] > 0) {
								if (forWhite) return 100000; // this means black is in checkmate, which is fantastic
								else return -100000;
							}
						}
						else {
							if (whitePM.size() == 0 && blackPressure[r][c] > 0) {
								if (forWhite) return -100000; // this means white is in checkmate, which is bad
								else return 100000;
							}
						}
					}
					if (current.getTeam().equals("White")) {
						rawValue = current.getValue();
					}
					else {
						rawValue = -1 * current.getValue();
					}
					rawValue *= rawModifier;
					pressureValue = current.getValue() * netPressure[r][c];
					if (netPressure[r][c] < 0) {
						if (!whitesTurn) {
							pressureValue *= myTurnMult;
						}
					}
					else { // if (netPressure[r][c] >= 0) {
						if (whitesTurn) {
							pressureValue *= myTurnMult;
						}
					}
					pressureValue *= pressureModifier;
					
					strength += rawValue;
					strength += pressureValue;
				}
				else {
					pressureValue = netPressure[r][c] * spaceValue;
					strength += pressureValue;
				}
			}
		}
		if (!forWhite) {
			return strength * -1;
		}
		else {
			return strength;
		}
	}
	
	
	
	public boolean inputMove(String move) {
		previousBoard = dupeBoard(board);
		//Random ran = new Random();
		generatePressureArrays();
		//printPressure("White");
		boolean valid;
		removeBadMoves();
		if(whitesTurn) {
			valid = whitePM.contains(move);
		}
		else {
			valid = blackPM.contains(move);
		}
		if (valid) {
			// castling stuff
			if (move.contains("e1")) {
				whiteCanCastleQueen = false;
				whiteCanCastleKing = false;
			}
			else if (move.contains("a1")) {
				whiteCanCastleQueen = false;
			}
			else if (move.contains("h1")) {
				whiteCanCastleKing = false;
			}
			else if (move.contains("e8")) {
				blackCanCastleQueen = false;
				blackCanCastleKing = false;
			}
			else if (move.contains("a8")) {
				blackCanCastleQueen = false;
			}
			else if (move.contains("h8")) {
				blackCanCastleKing = false;
			}
			// end first castling stuff
			//System.out.println("valid move");
			forceMove(move);
			// second castling stuff
			if (move.equals("e1c1")) {
				forceMove("a1d1");
			}
			else if (move.equals("e1g1")) {
				forceMove("h1f1");
			}
			else if (move.equals("e8c8")) {
				forceMove("a8d8");
			}
			else if (move.equals("e8g8")) {
				forceMove("h8f8");
			}
			// end second castling stuff
			whitesTurn = !whitesTurn;
			return true;
		}
		else {
			System.out.println("illegal move");
			return false;
		}
	}
	
	public String toString() {
		String ret = "";
		for (int row = board.length-1; row > -1; row--) {
			for (int col = 0; col < board[0].length; col++) {
				if (board[row][col] == null) {
					ret += "|      |";
				}
				else {
					ret += board[row][col];
				}
			}
			ret += "\n";
		}
		return ret;
	}
}
