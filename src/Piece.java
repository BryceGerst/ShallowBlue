import java.util.ArrayList;

public class Piece{
	private String name;
	private String team;
	public Piece(String name, String team){
		this.name = name;
		this.team = team;
	}
	public Piece(int num, String team) {
		this.team = team;
		switch (num) {
			case 0: name = "Pawn";break;
			case 1: name = "Knight";break;
			case 2: name = "Bishop";break;
			case 3: name = "Rook";break;
			case 4: name = "Queen";break;
			case 5: name = "King";break;
		}
	}
	
	public String toString() {
		return "| " + team.substring(0,1) + " " + name.substring(0,2) + " |";
	}
	
	public String getType() {
		return name;
	}
	
	public String getTeam() {
		return team;
	}
	
	public Piece dupe() {
		return new Piece(this.name, this.team);
	}
	
	public int getNum() {
		if (name.equals("Pawn")) {
			return 0;
		}
		else if (name.equals("Knight")) {
			return 1;
		}
		else if (name.equals("Bishop")) {
			return 2;
		}
		else if (name.equals("Rook")) {
			return 3;
		}
		else if (name.equals("Queen")) {
			return 4;
		}
		else if (name.equals("King")) {
			return 5;//4;
		}
		else {
			return -1;
		}
	}
	
	public int getValue() {
		// all values subject to change
		if (name.equals("Pawn")) {
			return 1;
		}
		else if (name.equals("Knight")) {
			return 3;
		}
		else if (name.equals("Bishop")) {
			return 3;
		}
		else if (name.equals("Rook")) {
			return 5;
		}
		else if (name.equals("Queen")) {
			return 9;
		}
		else if (name.equals("King")) {
			return 4;//4;
		}
		else {
			return 0;
		}
	}
	
	private String posName(int row, int col) {
		String ret = "";
		ret += (char)(97+col);
		ret += "" + (row+1);
		return ret;
	}
	
	private boolean addMove;
	
	public void alterPressure(int row, int col, Piece[][] board, int[][] pressure, int[][] otherPressure, ArrayList<String> npm, boolean modifyMoves) {
		addMove = modifyMoves;
		String startPos = posName(row,col);
		// PAWN STARTS HERE
		if (name.equals("Pawn")) {
			if (team.equals("White")) {
				if (row == 1) {
					checkPawnMove2(row+2,col,startPos,npm,board);
				}
				checkPawnPlace(row+1,col-1,startPos,pressure,npm,board);
				checkPawnPlace(row+1,col+1,startPos,pressure,npm,board);
				checkPawnMove(row+1,col,startPos,npm,board);
			}
			else {
				if (row == 6) {
					checkPawnMove2(row-2,col,startPos,npm,board);
				}
				checkPawnPlace(row-1,col-1,startPos,pressure,npm,board);
				checkPawnPlace(row-1,col+1,startPos,pressure,npm,board);
				checkPawnMove(row-1,col,startPos,npm,board);
			}
		}
		// ROOK STARTS HERE
		else if (name.equals("Rook")) {
			for (int r = row+1; r < 8; r++) {
				if (board[r][col] == null) {
					checkPlace(r,col,startPos,pressure,npm,board);
				}
				else {
					checkPlace(r,col,startPos,pressure,npm,board);
					r = 8;
				}
			}
			for (int r = row-1; r > -1; r--) {
				if (board[r][col] == null) {
					checkPlace(r,col,startPos,pressure,npm,board);
				}
				else {
					checkPlace(r,col,startPos,pressure,npm,board);
					r = -1;
				}
			}
			for (int c = col+1; c < 8; c++) {
				if (board[row][c] == null) {
					checkPlace(row,c,startPos,pressure,npm,board);
				}
				else {
					checkPlace(row,c,startPos,pressure,npm,board);
					c = 8;
				}
			}
			for (int c = col-1; c > -1; c--) {
				if (board[row][c] == null) {
					checkPlace(row,c,startPos,pressure,npm,board);
				}
				else {
					checkPlace(row,c,startPos,pressure,npm,board);
					c = -1;
				}
			}
		}
		// KNIGHT STARTS HERE
		else if (name.equals("Knight")) {
			checkPlace(row+2,col-1,startPos,pressure,npm,board);
			checkPlace(row+2,col+1,startPos,pressure,npm,board);
			checkPlace(row+1,col-2,startPos,pressure,npm,board);
			checkPlace(row+1,col+2,startPos,pressure,npm,board);
			checkPlace(row-1,col-2,startPos,pressure,npm,board);
			checkPlace(row-1,col+2,startPos,pressure,npm,board);
			checkPlace(row-2,col-1,startPos,pressure,npm,board);
			checkPlace(row-2,col+1,startPos,pressure,npm,board);
		}
		// BISHOP STARTS HERE
		else if (name.equals("Bishop")) {
			int ro = row;
			int co = col;
			while (ro < 7 && co < 7) {
				ro++;
				co++;
				if (board[ro][co] == null) {
					checkPlace(ro,co,startPos,pressure,npm,board);
				}
				else {
					checkPlace(ro,co,startPos,pressure,npm,board);
					ro = 7;
					co = 7;
				}
			}
			ro = row;
			co = col;
			while (ro < 7 && co > 0) {
				ro++;
				co--;
				if (board[ro][co] == null) {
					checkPlace(ro,co,startPos,pressure,npm,board);
				}
				else {
					checkPlace(ro,co,startPos,pressure,npm,board);
					ro = 7;
					co = 0;
				}
			}
			ro = row;
			co = col;
			while (ro > 0 && co > 0) {
				ro--;
				co--;
				if (board[ro][co] == null) {
					checkPlace(ro,co,startPos,pressure,npm,board);
				}
				else {
					checkPlace(ro,co,startPos,pressure,npm,board);
					ro = 0;
					co = 0;
				}
			}
			ro = row;
			co = col;
			while (ro > 0 && co < 7) {
				ro--;
				co++;
				if (board[ro][co] == null) {
					checkPlace(ro,co,startPos,pressure,npm,board);
				}
				else {
					checkPlace(ro,co,startPos,pressure,npm,board);
					ro = 0;
					co = 7;
				}
			}
		}
		// QUEEN STARTS HERE
		else if (name.equals("Queen")) {
			int ro = row;
			int co = col;
			while (ro < 7 && co < 7) {
				ro++;
				co++;
				if (board[ro][co] == null) {
					checkPlace(ro,co,startPos,pressure,npm,board);
				}
				else {
					checkPlace(ro,co,startPos,pressure,npm,board);
					ro = 7;
					co = 7;
				}
			}
			ro = row;
			co = col;
			while (ro < 7 && co > 0) {
				ro++;
				co--;
				if (board[ro][co] == null) {
					checkPlace(ro,co,startPos,pressure,npm,board);
				}
				else {
					checkPlace(ro,co,startPos,pressure,npm,board);
					ro = 7;
					co = 0;
				}
			}
			ro = row;
			co = col;
			while (ro > 0 && co > 0) {
				ro--;
				co--;
				if (board[ro][co] == null) {
					checkPlace(ro,co,startPos,pressure,npm,board);
				}
				else {
					checkPlace(ro,co,startPos,pressure,npm,board);
					ro = 0;
					co = 0;
				}
			}
			ro = row;
			co = col;
			while (ro > 0 && co < 7) {
				ro--;
				co++;
				if (board[ro][co] == null) {
					checkPlace(ro,co,startPos,pressure,npm,board);
				}
				else {
					checkPlace(ro,co,startPos,pressure,npm,board);
					ro = 0;
					co = 7;
				}
			}
			for (int r = row+1; r < 8; r++) {
				if (board[r][col] == null) {
					checkPlace(r,col,startPos,pressure,npm,board);
				}
				else {
					checkPlace(r,col,startPos,pressure,npm,board);
					r = 8;
				}
			}
			for (int r = row-1; r > -1; r--) {
				if (board[r][col] == null) {
					checkPlace(r,col,startPos,pressure,npm,board);
				}
				else {
					checkPlace(r,col,startPos,pressure,npm,board);
					r = -1;
				}
			}
			for (int c = col+1; c < 8; c++) {
				if (board[row][c] == null) {
					checkPlace(row,c,startPos,pressure,npm,board);
				}
				else {
					checkPlace(row,c,startPos,pressure,npm,board);
					c = 8;
				}
			}
			for (int c = col-1; c > -1; c--) {
				if (board[row][c] == null) {
					checkPlace(row,c,startPos,pressure,npm,board);
				}
				else {
					checkPlace(row,c,startPos,pressure,npm,board);
					c = -1;
				}
			}
		}
		// KING STARTS HERE
		else if (name.equals("King")) {
			checkPlaceKing(row+1,col-1,startPos,pressure,otherPressure,npm,board);
			checkPlaceKing(row+1,col,startPos,pressure,otherPressure,npm,board);
			checkPlaceKing(row+1,col+1,startPos,pressure,otherPressure,npm,board);
			checkPlaceKing(row,col-1,startPos,pressure,otherPressure,npm,board);
			checkPlaceKing(row,col+1,startPos,pressure,otherPressure,npm,board);
			checkPlaceKing(row-1,col-1,startPos,pressure,otherPressure,npm,board);
			checkPlaceKing(row-1,col,startPos,pressure,otherPressure,npm,board);
			checkPlaceKing(row-1,col+1,startPos,pressure,otherPressure,npm,board);
		}
	}
	
	private void checkPlace(int row, int col, String startPos, int[][] pressure, ArrayList<String> npm, Piece[][] board) {
		if (row > -1 && row < 8 && col > -1 && col < 8) {
			if (board[row][col] != null) {
				if (!board[row][col].getTeam().equals(team)) {
					String movePos = posName(row, col);
					if (addMove) npm.add(startPos+movePos);
				}
			}
			else {
				String movePos = posName(row, col);
				if (addMove) npm.add(startPos+movePos);
			}
			pressure[row][col]++;
		}
	}
	
	private void checkPlaceKing(int row, int col, String startPos, int[][] pressure, int[][] enemyPressure, ArrayList<String> npm, Piece[][] board) {
		if (row > -1 && row < 8 && col > -1 && col < 8) {
			if (enemyPressure[row][col] == 0) {
				if (board[row][col] != null) {
					if (!board[row][col].getTeam().equals(team)) {
						String movePos = posName(row, col);
						if (addMove) npm.add(startPos+movePos);
					}
				}
				else {
					String movePos = posName(row, col);
					if (addMove) npm.add(startPos+movePos);
				}
				pressure[row][col]++;
			}
		}
	}
	
	private void checkPawnPlace(int row, int col, String startPos, int[][] pressure, ArrayList<String> npm, Piece[][] board) {
		if (row > -1 && row < 8 && col > -1 && col < 8) {
			if (board[row][col] != null) {
				if (!board[row][col].getTeam().equals(team)) {
					String movePos = posName(row, col);
					if (addMove) npm.add(startPos+movePos);
				}
			}
			pressure[row][col]++;
		}
	}
	
	private void checkPawnMove(int row, int col, String startPos, ArrayList<String> npm, Piece[][] board) {
		if (row > -1 && row < 8 && col > -1 && col < 8) {
			if (board[row][col] == null) {
				String movePos = posName(row, col);
				if (addMove) npm.add(startPos+movePos);
			}
		}
	}
	
	private void checkPawnMove2(int row, int col, String startPos, ArrayList<String> npm, Piece[][] board) {
		if (row > -1 && row < 8 && col > -1 && col < 8) {
			if (row == 3) {
				if (board[row][col] == null && board[row-1][col] == null) {
					String movePos = posName(row, col);
					if (addMove) npm.add(startPos+movePos);
				}
			}
			else if (row == 4) {
				if (board[row][col] == null && board[row+1][col] == null) {
					String movePos = posName(row, col);
					if (addMove) npm.add(startPos+movePos);
				}
			}
		}
	}

}
