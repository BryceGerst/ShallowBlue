import java.util.ArrayList;
import java.util.Hashtable;

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
	private ArrayList<String> NPM; // NPM means naive possible moves (doesn't check if move would put king in check)
	private ArrayList<String> PM; // PM means possible moves, meaning the move is for sure possible
	private ArrayList<String> SM; // SM means smart moves, meaning they don't move a piece into a position where it could be taken and doesn't take anything itself
	
	private Hashifier hfer = new Hashifier();
	private long boardHash;
	Hashtable<Long, NodeInfo> transpoTable;
	
	private int whitePoints;
	private int blackPoints;
	
	private boolean whiteCastled;
	private boolean blackCastled;
	
	
	
	// Constructor
	public ChessGame() {
		board = new Piece[8][8];
		generateSide(1, 0, "White");
		generateSide(6, 7, "Black");
		
		whiteCanCastleKing = true;
		whiteCanCastleQueen = true;
		blackCanCastleKing = true;
		blackCanCastleQueen = true;
		
		whiteCastled = false;
		blackCastled = false;
		
		whitesTurn = true;
		
		whitePoints = 43; // 8+6+6+10+9+4
		blackPoints = 43;
		
		transpoTable = new Hashtable<Long, NodeInfo>(128);
		
		boardHash = hfer.makeHashFrom(board);
		System.out.println(boardHash);
	}
	public ChessGame(Piece[][] board, long boardHash, Hashifier hfer, boolean wK, boolean wQ, boolean wC, boolean bK, boolean bQ, boolean bC, Hashtable<Long, NodeInfo> transpoTable, boolean whitesTurn) {
		this.board = dupeBoard(board);
		this.boardHash = boardHash;
		this.hfer = hfer;
		this.transpoTable = transpoTable;
		this.whitesTurn = whitesTurn;
		whiteCanCastleKing = wK;
		whiteCanCastleQueen  = wQ;
		whiteCastled = wC;
		blackCanCastleKing = wK;
		blackCanCastleQueen  = wQ;
		blackCastled = wC;
	}
	
	public ChessGame cloneGame() {
		return new ChessGame(board, boardHash, hfer, whiteCanCastleKing, whiteCanCastleQueen, whiteCastled, blackCanCastleKing, blackCanCastleQueen, blackCastled, transpoTable, whitesTurn);
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
		NPM = new ArrayList<String>();
		whitePressure = new int[8][8];
		blackPressure = new int[8][8];
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if(board[row][col] != null) {
					Piece current = board[row][col];
					String team = current.getTeam();
					if (team.equals("White")) {
						current.alterPressure(row, col, board, whitePressure, blackPressure, NPM, whitesTurn);
					}
					else {
						current.alterPressure(row, col, board, blackPressure, whitePressure, NPM, !whitesTurn);
					}
				}
			}
		}
	}
	
	private void genTestPressure() {
		ArrayList<String>garb = new ArrayList<String>();
		whitePressure = new int[8][8];
		blackPressure = new int[8][8];
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if(board[row][col] != null) {
					Piece current = board[row][col];
					String team = current.getTeam();
					if (team.equals("White")) {
						current.alterPressure(row, col, board, whitePressure, blackPressure, garb, false);
					}
					else {
						current.alterPressure(row, col, board, blackPressure, whitePressure, garb, false);
					}
				}
			}
		}
	}
	
	public long getHash() {
		return boardHash;
	}
	public long getTrueHash() {
		return hfer.makeHashFrom(board);
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
		
		
		ArrayList<String> freeCaptures = new ArrayList<String>();
		ArrayList<String> winningCaptures = new ArrayList<String>();
		ArrayList<String> equalCaptures = new ArrayList<String>();
		ArrayList<String> nonCaptures = new ArrayList<String>();
		ArrayList<String> losingCaptures = new ArrayList<String>();
		ArrayList<String> sacrifices = new ArrayList<String>();
		
		
		PM = new ArrayList<String>();
		SM = new ArrayList<String>();
		boolean captured;
		boolean valid;
		Piece[][] originalBoard = dupeBoard(board);
		if (whitesTurn) {
			if (whiteCanCastleQueen) {
				if(board[0][1] == null && board[0][2] == null && board[0][3] == null && blackPressure[0][2] == 0 && blackPressure[0][3] == 0 && blackPressure[0][4] == 0) {
					PM.add("e1c1");
					SM.add("e1c1");
				}
			}
			if (whiteCanCastleKing) {
				if(board[0][5] == null && board[0][6] == null && blackPressure[0][5] == 0 && blackPressure[0][6] == 0 && blackPressure[0][4] == 0) {
					PM.add("e1g1");
					SM.add("e1g1");
				}
			}
		}
		else {
			if (blackCanCastleQueen) {
				if(board[7][1] == null && board[7][2] == null && board[7][3] == null && whitePressure[7][2] == 0 && whitePressure[7][3] == 0 && whitePressure[7][4] == 0) {
					PM.add("e8c8");
					SM.add("e8c8");
				}
			}
			if (blackCanCastleKing) {
				if(board[7][5] == null && board[7][6] == null && whitePressure[7][5] == 0 && whitePressure[7][6] == 0 && whitePressure[7][4] == 0) {
					PM.add("e8g8");
					SM.add("e8g8");
				}
			}
		}
		for(int i = 0; i < NPM.size(); i++) {
			valid = false;
			board = dupeBoard(originalBoard);
			String testMove = NPM.get(i);
			testMove = checkIfPawnUpgrade(testMove);
			int capturedVal = forceMove(testMove)[0];
			captured = capturedVal > 0;
			genTestPressure();
			
			int[][] enemyPressure, myPressure;
			String myTeam;
			if (whitesTurn) {
				enemyPressure = blackPressure;
				myPressure = whitePressure;
				myTeam = "White";
			}
			else {
				enemyPressure = whitePressure;
				myPressure = blackPressure;
				myTeam = "Black";
			}
			
			for (int r = 0; r < 8; r++) {
				for (int c = 0; c < 8; c++) {
					if (board[r][c] != null && board[r][c].getTeam().equals(myTeam) && board[r][c].getType().equals("King")) {
						if(enemyPressure[r][c] == 0) {
							valid = true;
						}
					}
				}
			}
			if (valid) {
				PM.add(testMove);
				int endCol = (int)testMove.charAt(2) - 97;
				int endRow = Integer.parseInt(testMove.substring(3,4)) - 1;
				int enemyPressureOn = enemyPressure[endRow][endCol];
				int myPressureOn = myPressure[endRow][endCol];
				int myVal = board[endRow][endCol].getValue();
				if (captured) {
					if (enemyPressureOn == 0) {
						freeCaptures.add(testMove);
					}
					else if (capturedVal > myVal) {
						winningCaptures.add(testMove);
					}
					else if (capturedVal == myVal) {
						equalCaptures.add(testMove);
					}
					else {
						losingCaptures.add(testMove);
					}
					
				}
				else {
					if(enemyPressureOn > 0 && myPressureOn == 0) { // basically if piece could NOT be captured without retaliation
						sacrifices.add(testMove);
					}
					else {
						nonCaptures.add(testMove);
					}
				}
			}
		}
		SM.addAll(freeCaptures);
		SM.addAll(winningCaptures);
		SM.addAll(equalCaptures);
		SM.addAll(nonCaptures);
		SM.addAll(losingCaptures);
		SM.addAll(sacrifices);
		
		if (!whitesTurn && SM.size() != PM.size()) {
			System.out.println("error, black sizes not equal");
			System.out.println("SM: " + SM);
			System.out.println("PM: " + PM);
		}
		if (whitesTurn && SM.size() != PM.size()) {
			System.out.println("error, white sizes not equal");
			System.out.println("SM: " + SM);
			System.out.println("PM: " + PM);
		}
		board = dupeBoard(originalBoard);
	}
	
	private int[] forceMove(String move) {
		int capturedVal = 0;
		int capturedNum = -1;
		// example, "e2e4"
		if (move.length() == 4) {	// simple move (meaning no upgrade)
			int startCol = (int)move.charAt(0) - 97; // 97 is the unicode value for lower case a
			int startRow = Integer.parseInt(move.substring(1,2)) - 1; // minus 1 for zero based indexing
			if (board[startRow][startCol] == null) {
				System.out.println("error finding piece at [" + startRow + "][" + startCol + "]");
			}
			else {
				Piece movePiece = board[startRow][startCol].dupe();
				board[startRow][startCol] = null;
				int endCol = (int)move.charAt(2) - 97;
				int endRow = Integer.parseInt(move.substring(3,4)) - 1;
				if (board[endRow][endCol] != null) {
					capturedVal = board[endRow][endCol].getValue();
					capturedNum = board[endRow][endCol].getNum();
				}
				board[endRow][endCol] = movePiece;
			}
		}
		else {
			int startCol = (int)move.charAt(0) - 97; // 97 is the unicode value for lower case a
			int startRow = Integer.parseInt(move.substring(1,2)) - 1; // minus 1 for zero based indexing
			String upgradeName = move.substring(4);
			if (board[startRow][startCol] == null) {
				System.out.println("error finding piece at [" + startRow + "][" + startCol + "]");
			}
			else {
				Piece movePiece = new Piece(upgradeName, board[startRow][startCol].getTeam());
				board[startRow][startCol] = null;
				int endCol = (int)move.charAt(2) - 97;
				int endRow = Integer.parseInt(move.substring(3,4)) - 1;
				if (board[endRow][endCol] != null) {
					capturedVal = board[endRow][endCol].getValue();
					capturedNum = board[endRow][endCol].getNum();
				}
				board[endRow][endCol] = movePiece;
			}
		}
		return new int[]{capturedVal, capturedNum};
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
	
	private long startTime;
	private long maxTime = 30000; // 60000
	private int totalTested;
	private int maxTested;
	
	public int findBestMove(int depth) {
		int bestInd = 0;
		int maxDepth = depth; //25
		startTime = System.currentTimeMillis();
		for (int i = 1; i <= maxDepth; i++) { // iterative deepening
			int testInd = PVS(Integer.MIN_VALUE, Integer.MAX_VALUE, i)[1];
			if (testInd != -1) {
				bestInd = testInd;
			}
			else {
				System.out.println("In " + (maxTime / 1000.0) + " seconds, searched to a depth of " + (i-1));
				break;
			}
			if (i == maxDepth) {
				System.out.println("In " + ((System.currentTimeMillis()-startTime) / 1000.0) + " seconds, searched to the max depth of " + i);
				break;
			}
		}
		return bestInd;
	}
	
	private EvalMults currentBotMults;
	
	public int botMove(EvalMults EM) {
		currentBotMults = EM;
		totalTested = 0;
		maxTested = 0;
		
		generatePressureArrays();
		removeBadMoves();
		ArrayList<String> UM = new ArrayList<String>(SM);
		String testMove;
		int[] moveInfo;
		int maxThreads = 1;//5;
		SimThread[] threads = new SimThread[maxThreads];
//		for (int i = 0; i < UM.size(); i++) {
//			if (i < maxThreads) {
//				testMove = UM.get(i);
//				moveInfo = makeMove(testMove);
//				ChessGame dupeGame = this.cloneGame();
//				SimThread newThread = new SimThread(dupeGame);
//				threads[i] = newThread;
//				newThread.start();
//				unmakeMove(testMove, moveInfo);
//			}
//		}
//		findBestMove(25);
		
//		for (int i = 0; i < maxThreads; i++) {
//			ChessGame dupeGame = this.cloneGame();
//			SimThread newThread = new SimThread(dupeGame);
//			threads[i] = newThread;
//			newThread.start();
//		}
//		boolean allDone;
//		do {
//			allDone = true;
//			for (int i = 0; i < threads.length; i++) {
//				boolean done = threads[i].getDone();
//				if (!done) allDone = false;
//			}
//		} while(!allDone);
//		NodeInfo root = transpoTable.get(boardHash);
//		System.out.println("Searched to a depth of " + root.getDepth());
//		int bestInd = root.getBestInd(); //findBestMove();
		//System.out.println("Max: " + maxTested + "\nActual: " + totalTested);
		
		int bestInd = findBestMove(25);
		generatePressureArrays();
		removeBadMoves();
		//System.out.println(SM);
		String bestMove = SM.get(bestInd);
		System.out.println("Doing: " + bestMove);
		testMove = UM.get(bestInd);
		moveInfo = makeMove(testMove);
		NodeInfo expected = transpoTable.get(boardHash);
		System.out.println("Expecting: " + expected.getMove());
		unmakeMove(testMove, moveInfo);
		return inputMove(bestMove);
	}
	
	private int[] PVS(int alpha, int beta, int depthLeft) {
		if (depthLeft <= 0) return new int[] {getBoardStrength(), 0};
		else {
			long endTime = System.currentTimeMillis();
			if (endTime - startTime >= maxTime) {
				return new int[]{0,-1};
			}
			generatePressureArrays();
			removeBadMoves();
			
			ArrayList<String> UM = new ArrayList<String>(SM);
			
			String testMove;
			int score;
			int[] result;
			int testTime;
			int[] moveInfo;
			int bestInd = 0;
			
			// TODO: make a boolean in instance data to check if the player going is in check, if so do the following stuff, although this code might suffice for now
			if (UM.size() == 0) { // either a checkmate or a stalemate
				return new int[] {getBoardStrength(), 0};
			}
			
			maxTested += UM.size();
			
			boolean addTranspo = false;
			int prevBest = -1;
			int bestScore = Integer.MIN_VALUE;
			NodeInfo transposition = transpoTable.get(boardHash);
			if (transposition == null) {
				addTranspo = true;
			}
			else {
				if (boardHash != transposition.getZobrist()) {
					addTranspo = true;
					System.out.println("Type 1 collision");
				}
				else {
					prevBest = transposition.getBestInd();
					if (UM.contains(transposition.getMove())){ //prevBest < UM.size()) {
						if (depthLeft <= transposition.getDepth()) {
							return new int[] {transposition.getScore(), prevBest};
						}
						totalTested++;
						testMove = UM.get(prevBest);
						moveInfo = makeMove(testMove);
						result = PVS(-1 * beta, -1 * alpha, depthLeft - 1);
						bestScore = -1 * result[0];
						bestInd = prevBest;
						testTime = result[1];
						unmakeMove(testMove, moveInfo);
						if (testTime == -1) {
							return new int[] {0, -1};
						}
						if (bestScore > alpha) {
							if (bestScore >= beta) {
								//System.out.println("trimmed " + (UM.size() - 1) + " items from remembering");
								return new int[] {bestScore, prevBest};
							}
							alpha = bestScore;
						}
					}
					else {
						System.out.println("Error: with a depth left of " + depthLeft + " node contains impossible move " + prevBest + " when the max is " + UM.size());
						System.out.println("Wants move " + transposition.getMove() + " with moves list:\n" + UM + "\nand given board\n" + this);
					}
				}
			}
			for (int i = 0; i < UM.size(); i++) {
				if (i != prevBest) {
					totalTested++;
					testMove = UM.get(i);
					moveInfo = makeMove(testMove);
					result = PVS(-alpha - 1, -alpha, depthLeft - 1);
					score = -1 * result[0];
					testTime = result[1];
					if (testTime == -1) {
						unmakeMove(testMove, moveInfo);
						return new int[] {0, -1};
					}
					if (score > alpha && score < beta) {
						result = PVS(-beta, -alpha, depthLeft - 1);
						score = -1 * result[0];
						testTime = result[1];
						if (testTime == -1) {
							unmakeMove(testMove, moveInfo); // TODO: add these cut nodes to the transposition table
							return new int[] {0, -1};
						}
						if (score > alpha) {
							alpha = score;
						}
					}
					unmakeMove(testMove, moveInfo);
					if (score > bestScore) {
						if (score >= beta) {
							if (addTranspo) {
								transpoTable.put(boardHash, new NodeInfo(i, boardHash, depthLeft, score, testMove));
							}
							else {
								transpoTable.replace(boardHash, new NodeInfo(i, boardHash, depthLeft, score, testMove));
							}
							//System.out.println("trimmed " + ((double)(UM.size() - i - 1)/UM.size()) + "% of items the old school way");
							return new int[] {score, i};
						}
						bestScore = score;
						bestInd = i;
					}
				}
			}
			if (addTranspo) {
				transpoTable.put(boardHash, new NodeInfo(bestInd, boardHash, depthLeft, bestScore, UM.get(bestInd)));
			}
			else {
				transpoTable.replace(boardHash, new NodeInfo(bestInd, boardHash, depthLeft, bestScore, UM.get(bestInd)));
			}
			return new int[] {bestScore, bestInd};
		}
	}

	
	public int[] makeMove(String move) {
		
		int[] info = new int[8];
		int capturedVal = 0;
		int capturedNum = -1;
		if (whiteCastled) info[2] = 1;
		else {
			if (whiteCanCastleQueen) info[0] = 1;
			if (whiteCanCastleKing) info[1] = 1;
		}
		if (blackCastled) info[5] = 1;
		else {
			if (blackCanCastleQueen) info[3] = 1;
			if (blackCanCastleKing) info[4] = 1;
		}
		
		if (whiteCanCastleQueen && move.equals("e1c1")) {
			whiteCastled = true;
			boardHash = hfer.makeHashMove(boardHash, board[0][0].getId(), 0, 0, 0, 3);
			forceMove("a1d1");
			boardHash = hfer.castleHash(boardHash, true, false, false, false);
		}
		else if (whiteCanCastleKing && move.equals("e1g1")) {
			whiteCastled = true;
			boardHash = hfer.makeHashMove(boardHash, board[0][7].getId(), 0, 7, 0, 5);
			forceMove("h1f1");
			boardHash = hfer.castleHash(boardHash, false, true, false, false);
		}
		else if (blackCanCastleQueen && move.equals("e8c8")) {
			blackCastled = true;
			boardHash = hfer.makeHashMove(boardHash, board[7][0].getId(), 7, 0, 7, 3);
			forceMove("a8d8");
			boardHash = hfer.castleHash(boardHash, false, false, true, false);
		}
		else if (blackCanCastleKing && move.equals("e8g8")) {
			blackCastled = true;
			boardHash = hfer.makeHashMove(boardHash, board[7][7].getId(), 7, 7, 7, 5);
			forceMove("h8f8");
			boardHash = hfer.castleHash(boardHash, false, false, false, true);
		}
		
		if (move.contains("e1")) { whiteCanCastleQueen = false; whiteCanCastleKing = false; }
		else if (move.contains("a1")) { whiteCanCastleQueen = false; }
		else if (move.contains("h1")) { whiteCanCastleKing = false; }
		else if (move.contains("e8")) { blackCanCastleQueen = false; blackCanCastleKing = false; }
		else if (move.contains("a8")) { blackCanCastleQueen = false; }
		else if (move.contains("h8")) { blackCanCastleKing = false; }
		// end first castling stuff
		
		int startCol = (int)move.charAt(0) - 97; // 97 is the unicode value for lower case a
		int startRow = Integer.parseInt(move.substring(1,2)) - 1; // minus 1 for zero based indexing
		int endCol = (int)move.charAt(2) - 97;
		int endRow = Integer.parseInt(move.substring(3,4)) - 1;
		int startId = board[startRow][startCol].getId();
		if (board[endRow][endCol] != null) {
			boardHash = hfer.makeHashMove(boardHash, board[startRow][startCol].getId(), startRow, startCol, board[endRow][endCol].getId(), endRow, endCol);
			//System.out.println("?: " + boardHash);
		}
		else {
			boardHash = hfer.makeHashMove(boardHash, board[startRow][startCol].getId(), startRow, startCol, endRow, endCol);
			//System.out.println("current: " + boardHash);
		}
		
		
		int[] capturedInfo = forceMove(move);
		
		if (move.length() > 4) {
			boardHash = hfer.makeHashPromotion(boardHash, startId, board[endRow][endCol].getId(), endRow, endCol);
		}
		
		capturedVal = capturedInfo[0];
		capturedNum = capturedInfo[1];
		
		
		
		
		
		info[6] = capturedVal;
		info[7] = capturedNum;
		if (whitesTurn) {
			blackPoints -= capturedVal;
		}
		else {
			whitePoints -= capturedVal;
		}
		
		boardHash = hfer.switchHashTurn(boardHash);
		
		whitesTurn = !whitesTurn;

		return info;
		
	}
	
	public void unmakeMove(String move, int[] info) {
		// unpacking info
		
		boardHash = hfer.switchHashTurn(boardHash);
		
		whitesTurn = !whitesTurn;
		
		whiteCanCastleQueen = (info[0] == 1);
		whiteCanCastleKing = (info[1] == 1);
		whiteCastled = (info[2] == 1);
		blackCanCastleQueen = (info[3] == 1);
		blackCanCastleKing = (info[4] == 1);
		blackCastled = (info[5] == 1);
		
		int capturedVal = info[6];
		int capturedNum = info[7];
		
		int startCol = (int)move.charAt(0) - 97; // 97 is the unicode value for lower case a
		int startRow = Integer.parseInt(move.substring(1,2)) - 1; // minus 1 for zero based indexing
		int endCol = (int)move.charAt(2) - 97;
		int endRow = Integer.parseInt(move.substring(3,4)) - 1;
		
		//System.out.println(endRow + " " + endCol);
		//System.out.println(this);
		String capturedTeam;
		if (whitesTurn) {
			capturedTeam = "Black";
		}
		else {
			capturedTeam = "White";
		}

		String antimove = move.substring(2, 4) + move.substring(0,2);

		
		
		
		if (move.length() == 4) { // no promo
			if (whitesTurn && !whiteCastled) {
				if (whiteCanCastleQueen && move.equals("e1c1")) {
					forceMove("d1a1");
					boardHash = hfer.castleHash(boardHash, true, false, false, false);
					boardHash = hfer.makeHashMove(boardHash, board[0][0].getId(), 0, 0, 0, 3);
				}
				else if (whiteCanCastleKing && move.equals("e1g1")) {
					forceMove("f1h1");
					boardHash = hfer.castleHash(boardHash, false, true, false, false);
					boardHash = hfer.makeHashMove(boardHash, board[0][7].getId(), 0, 7, 0, 5);
				}
			}
			else if (!whitesTurn && !blackCastled) {
				if (blackCanCastleQueen && move.equals("e8c8")) {
					forceMove("d8a8");
					boardHash = hfer.castleHash(boardHash, false, false, true, false);
					boardHash = hfer.makeHashMove(boardHash, board[7][0].getId(), 7, 0, 7, 3);
				}
				else if (blackCanCastleKing && move.equals("e8g8")) {
					forceMove("f8h8");
					boardHash = hfer.castleHash(boardHash, false, false, false, true);
					boardHash = hfer.makeHashMove(boardHash, board[7][7].getId(), 7, 7, 7, 5);
				}
			}
			forceMove(antimove);
			
			if (capturedVal != 0) {
				//System.out.println("I enjoy eating");
				board[endRow][endCol] = new Piece(capturedNum, capturedTeam);
			}
		}
		else { // is a PROMO
			board[startRow][startCol] = new Piece("Pawn", board[endRow][endCol].getTeam());
			boardHash = hfer.makeHashPromotion(boardHash, board[startRow][startCol].getId(), board[endRow][endCol].getId(), endRow, endCol);
			if (capturedVal == 0) {
				board[endRow][endCol] = null;
			}
			else {
				//System.out.println("brogars");
				board[endRow][endCol] = new Piece(capturedNum, capturedTeam);
			}
			
		}

		if (board[endRow][endCol] != null) {
			boardHash = hfer.makeHashMove(boardHash, board[startRow][startCol].getId(), startRow, startCol, board[endRow][endCol].getId(), endRow, endCol);
		}
		else {
			boardHash = hfer.makeHashMove(boardHash, board[startRow][startCol].getId(), startRow, startCol, endRow, endCol);
		}
	}
	

	
	
	
	public int getBoardStrength() {
		boolean forWhite = whitesTurn;
		generatePressureArrays();
		removeBadMoves();
		int strength = 0;
		int pressureValue, rawValue;
		int[][] netPressure = new int[8][8];
		// the following comments show the values that for sure are decent-ish
		int myTurnMult = currentBotMults.myTurnMult; // 1;
		int rawModifier = currentBotMults.rawModifier; // 25
		int spaceValue = currentBotMults.spaceValue; // 1;
		int pressureModifier = currentBotMults.pressureModifier; // 3;
		int castleValue = currentBotMults.castleValue; // 10
		
		if (whiteCanCastleQueen || whiteCanCastleKing || whiteCastled) {
			strength += castleValue;
		}
		if (blackCanCastleKing || blackCanCastleQueen || blackCastled) {
			strength -= castleValue;
		}
		boolean stalemate = PM.size() == 0;
		
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				netPressure[r][c] = whitePressure[r][c] - blackPressure[r][c];
				if (board[r][c] != null) {
					Piece current = board[r][c];
					if (current.getType().equals("King")) {
						if (current.getTeam().equals("White")) {
							if (PM.size() == 0 && blackPressure[r][c] > 0) {
								if (forWhite) return -100000; // getting checkmated
							}
						}
						else {
							if (PM.size() == 0 && whitePressure[r][c] > 0) {
								if (!forWhite) return -100000; // getting checkmated
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
		if (stalemate) return 0;
		if (!forWhite) {
			return strength * -1;
		}
		else {
			return strength;
		}
	}
	
	
	
	public int inputMove(String move) {
		previousBoard = dupeBoard(board);
		//Random ran = new Random();
		generatePressureArrays();
		removeBadMoves();
		//printPressure("White");
		boolean valid;
		int ind;
		ind = SM.indexOf(move);
		valid = ind >= 0;
		if (valid) {
			// castling stuff
			if (whiteCanCastleQueen && move.equals("e1c1")) {
				whiteCastled = true;
				boardHash = hfer.makeHashMove(boardHash, board[0][0].getId(), 0, 0, 0, 3);
				forceMove("a1d1");
				boardHash = hfer.castleHash(boardHash, true, false, false, false);
			}
			else if (whiteCanCastleKing && move.equals("e1g1")) {
				whiteCastled = true;
				boardHash = hfer.makeHashMove(boardHash, board[0][7].getId(), 0, 7, 0, 5);
				forceMove("h1f1");
				boardHash = hfer.castleHash(boardHash, false, true, false, false);
			}
			else if (blackCanCastleQueen && move.equals("e8c8")) {
				blackCastled = true;
				boardHash = hfer.makeHashMove(boardHash, board[7][0].getId(), 7, 0, 7, 3);
				forceMove("a8d8");
				boardHash = hfer.castleHash(boardHash, false, false, true, false);
			}
			else if (blackCanCastleKing && move.equals("e8g8")) {
				blackCastled = true;
				boardHash = hfer.makeHashMove(boardHash, board[7][7].getId(), 7, 7, 7, 5);
				forceMove("h8f8");
				boardHash = hfer.castleHash(boardHash, false, false, false, true);
			}
			
			if (move.contains("e1")) { whiteCanCastleQueen = false; whiteCanCastleKing = false; }
			else if (move.contains("a1")) { whiteCanCastleQueen = false; }
			else if (move.contains("h1")) { whiteCanCastleKing = false; }
			else if (move.contains("e8")) { blackCanCastleQueen = false; blackCanCastleKing = false; }
			else if (move.contains("a8")) { blackCanCastleQueen = false; }
			else if (move.contains("h8")) { blackCanCastleKing = false; }
			// end first castling stuff
			
			int startCol = (int)move.charAt(0) - 97; // 97 is the unicode value for lower case a
			int startRow = Integer.parseInt(move.substring(1,2)) - 1; // minus 1 for zero based indexing
			int endCol = (int)move.charAt(2) - 97;
			int endRow = Integer.parseInt(move.substring(3,4)) - 1;
			int startId = board[startRow][startCol].getId();
			if (board[endRow][endCol] != null) {
				boardHash = hfer.makeHashMove(boardHash, board[startRow][startCol].getId(), startRow, startCol, board[endRow][endCol].getId(), endRow, endCol);
				//System.out.println("?: " + boardHash);
			}
			else {
				boardHash = hfer.makeHashMove(boardHash, board[startRow][startCol].getId(), startRow, startCol, endRow, endCol);
				//System.out.println("current: " + boardHash);
			}
			
			
			int capturedVal = forceMove(move)[0];
			
			if (move.length() > 4) {
				boardHash = hfer.makeHashPromotion(boardHash, startId, board[endRow][endCol].getId(), endRow, endCol);
			}
			
			if (whitesTurn) {
				blackPoints -= capturedVal;
			}
			else {
				whitePoints -= capturedVal;
			}
			
			boardHash = hfer.switchHashTurn(boardHash);
			
			whitesTurn = !whitesTurn;
			
			
			//System.out.println("checking real: " + boardHashTest);
			//System.out.println("checking what game sees: " + boardHash);
//			long boardHashTest = hfer.makeHashFrom(board);
//			if (boardHash != boardHashTest) {
//				System.out.println("ERROR: board hash was desynched!");
//				System.out.println("real: " + boardHashTest);
//				System.out.println("what game thought: " + boardHash);
//				boardHash = boardHashTest;
//			}
			
			generatePressureArrays();
			removeBadMoves();
			if (PM.size() == 0) {
				if (!whitesTurn) {
					System.out.println("White wins!");
					return -13;
				}
				else {
					System.out.println("Black wins!");
					return -12;
				}
			}
			
			
			
			return ind;
		}
		else {
			System.out.println("illegal move");
			return ind;
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
