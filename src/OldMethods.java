import java.util.ArrayList;

public class OldMethods {
	public Node outputMove() {
		// TODO: make this unnecessary
		generatePressureArrays();
		removeBadMoves();
		// ------------------------------

		int ind;
		
		Node og = new Node(0, true);

		ind = recursiveSim(this, whitesTurn, 0, 4, Integer.MAX_VALUE, og)[0];
		System.out.println("Real: " + ind);
		System.out.println("Node: " + og.getBestOrder()[0]);
		System.out.println(SM.get(ind));
		inputMove(SM.get(ind));
		
		//System.out.println("og stuffs");
		//og.getHeight();
		return og.goToNode(ind);
	}
	
	public Node outputMove(Node given) {
		// TODO: make this unnecessary
		generatePressureArrays();
		removeBadMoves();
		// ------------------------------

		int ind;
		
		ind = recursiveSim(this, whitesTurn, 0, 4, Integer.MAX_VALUE, given)[0];
		System.out.println("Real: " + ind);
		//System.out.println("White given is: " + given.getHeight());
		System.out.println("Node: " + given.getBestOrder()[0]);
		System.out.println(SM.get(ind));
		inputMove(SM.get(ind));
		
		return given.goToNode(ind);
	}
	
	private int[] recursiveSim(ChessGame givenGame, boolean forWhite, int level, int maxLevel, int alpha, Node current) {
		//Random ran = new Random();
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
		
		ArrayList<String> UM;
		
		if (forWhite) {
			if (level % 2 == 0) {
				UM = whiteUM;
			}
			else {
				UM = blackUM;
			}
		}
		else {
			if (level % 2 == 0) {
				UM = blackUM;
			}
			else {
				UM = whiteUM;
			}
		}
		
		
		int result;
		int bestResult = Integer.MIN_VALUE;
		int bestIndex = 0;
		int worstResult = Integer.MAX_VALUE;
		int worstIndex = 0;
		
		int[] ret = {0,0};
		
		ChessGame testGame;
		
		int[] bestOrder = current.getBestOrder();
		
		
		if (level == maxLevel) { // MAX LEVEL SHOULD ALWAYS BE EVEN
			if (bestOrder.length > 0) {
				if (bestOrder.length > UM.size()) {
					System.out.println("you shouldnt see this 0_0");
					System.out.println(current.getHeight());
					System.out.println(current);
				}
				int i;
				for (int a = 0; a < bestOrder.length; a++) {
					i = bestOrder[a];
					testGame = new ChessGame(givenGame.getBoard(), givenGame.getWK(), givenGame.getWQ(), givenGame.getBK(), givenGame.getBQ(), givenGame.getTurn(), givenGame.whitePM, givenGame.blackPM, givenGame.whitePressure, givenGame.blackPressure, givenGame.whitePoints, givenGame.blackPoints);
					result = testGame.simGame(UM.get(i), 0);
					Node child = current.removeNode(i);
					current.addNode(child, result);
					if (result > alpha) {
						System.out.println("Saved from checking " + (bestOrder.length - a - 1) + " options level max");
						for (int b = a + 1; b < bestOrder.length; b++) {
							current.removeNode(bestOrder[b]);
							//current.addNodeToEnd(new Node(bestOrder[b], false));
						}
						return new int[] {0,Integer.MAX_VALUE};
					}
					else if (result > bestResult) { // || (result == bestResult && ran.nextInt(2) == 0)) {
						bestResult = result;
						bestIndex = i;
					}
				}
			}
			else {
				for (int i = 0; i < UM.size(); i++) {
					testGame = new ChessGame(givenGame.getBoard(), givenGame.getWK(), givenGame.getWQ(), givenGame.getBK(), givenGame.getBQ(), givenGame.getTurn(), givenGame.whitePM, givenGame.blackPM, givenGame.whitePressure, givenGame.blackPressure, givenGame.whitePoints, givenGame.blackPoints);
					result = testGame.simGame(UM.get(i), 0);
					Node child = new Node(i, false);
					current.addNode(child, result);
					if (result > alpha) {
						//System.out.println("Saved from checking " + (whiteUM.size() - i - 1) + " options");
						//for (int b = i+1; b < UM.size(); b++) {
						//	current.addNodeToEnd(new Node(b, false));
						//}
						return new int[] {0,Integer.MAX_VALUE};
					}
					else if (result > bestResult) { // || (result == bestResult && ran.nextInt(2) == 0)) {
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
				if (bestOrder.length > 0) {
					if (bestOrder.length > UM.size()) System.out.println("zoinks");
					int i;
					for (int a = 0; a < bestOrder.length; a++) {
						i = bestOrder[a];
						//System.out.println("i: " + i);
						Node child = current.removeNode(i);
						//System.out.println(current.bestOrder);
						//System.out.println("Even Level - Child: " + child + " \nGiven: " + current);
						testGame = new ChessGame(givenGame.getBoard(), givenGame.getWK(), givenGame.getWQ(), givenGame.getBK(), givenGame.getBQ(), givenGame.getTurn(), givenGame.whitePM, givenGame.blackPM, givenGame.whitePressure, givenGame.blackPressure, givenGame.whitePoints, givenGame.blackPoints);
						testGame.botInputMove(UM.get(i));
						result = recursiveSim(testGame, forWhite, level + 1, maxLevel, bestResult, child)[1];
						current.addNode(child, result);
						if (result > alpha) {
							//System.out.println("Saved from checking " + (bestOrder.length - a - 1) + " options level even");
							for (int b = a + 1; b < bestOrder.length; b++) {
								current.removeNode(bestOrder[b]);
								//current.addNodeToEnd(new Node(bestOrder[b], false));
							}
							return new int[] {0,Integer.MAX_VALUE};
						}
						else if (result > bestResult) { // || (result == bestResult && ran.nextInt(2) == 0)) {
							bestResult = result;
							bestIndex = i;
						}
					}
				}
				else {
					for (int i = 0; i < UM.size(); i++) {
						Node child = new Node(i, false);
						testGame = new ChessGame(givenGame.getBoard(), givenGame.getWK(), givenGame.getWQ(), givenGame.getBK(), givenGame.getBQ(), givenGame.getTurn(), givenGame.whitePM, givenGame.blackPM, givenGame.whitePressure, givenGame.blackPressure, givenGame.whitePoints, givenGame.blackPoints);
						testGame.botInputMove(UM.get(i));
						result = recursiveSim(testGame, forWhite, level + 1, maxLevel, bestResult, child)[1];
						current.addNode(child, result);
						if (result > alpha) {
							//System.out.println("Saved from checking " + (whiteUM.size() - i - 1) + " options");
							//for (int b = i+1; b < UM.size(); b++) {
								//current.addNodeToEnd(new Node(b, false));
							//}
							return new int[] {0,Integer.MAX_VALUE};
						}
						else if (result > bestResult) { // || (result == bestResult && ran.nextInt(2) == 0)) {
							bestResult = result;
							bestIndex = i;
						}
					}
				}
				ret[0] = bestIndex;
				ret[1] = bestResult;
			}
			else { // pick the worst from this level
				if (bestOrder.length > 0) {
					if (bestOrder.length > UM.size()) System.out.println("big uh oh " + (current));
					int i;
					for (int a = 0; a < bestOrder.length; a++) {
						i = bestOrder[a];
						Node child = current.removeNode(i);
						testGame = new ChessGame(givenGame.getBoard(), givenGame.getWK(), givenGame.getWQ(), givenGame.getBK(), givenGame.getBQ(), givenGame.getTurn(), givenGame.whitePM, givenGame.blackPM, givenGame.whitePressure, givenGame.blackPressure, givenGame.whitePoints, givenGame.blackPoints);
						testGame.botInputMove(UM.get(i));
						//System.out.println("Odd Level - Child: " + child + " \nGiven: " + current);
						result = recursiveSim(testGame, forWhite, level + 1, maxLevel, worstResult, child)[1];
						current.addNode(child, result);
						if (result < alpha) {
							//System.out.println("Saved from checking " + (bestOrder.length - a - 1) + " options level odd");
							for (int b = a + 1; b < bestOrder.length; b++) {
								current.removeNode(bestOrder[b]);
								//current.addNodeToEnd(new Node(bestOrder[b], true));
							}
							return new int[] {0,Integer.MIN_VALUE};
						}
						else if (result < worstResult) { // || (result == worstResult && ran.nextInt(2) == 0)) {
							worstResult = result;
							worstIndex = i;
						}
					}
				}
				else {
					for (int i = 0; i < UM.size(); i++) {
						Node child = new Node(i, true);
						testGame = new ChessGame(givenGame.getBoard(), givenGame.getWK(), givenGame.getWQ(), givenGame.getBK(), givenGame.getBQ(), givenGame.getTurn(), givenGame.whitePM, givenGame.blackPM, givenGame.whitePressure, givenGame.blackPressure, givenGame.whitePoints, givenGame.blackPoints);
						testGame.botInputMove(UM.get(i));
						result = recursiveSim(testGame, forWhite, level + 1, maxLevel, worstResult, child)[1];
						current.addNode(child, result);
						if (result < alpha) {
							//System.out.println("Saved from checking " + (blackUM.size() - i - 1) + " options");
							//for (int b = i+1; b < UM.size(); b++) {
							//	current.addNodeToEnd(new Node(b, true));
							//}
							return new int[] {0,Integer.MIN_VALUE};
						}
						else if (result < worstResult) { // || (result == worstResult && ran.nextInt(2) == 0)) {
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
}
