import java.util.Scanner;

public class GameRunner {

	public static void main(String[] args) {
		ChessGame game = new ChessGame();
		System.out.println(game);
		System.out.println("\n\n\n");
		Scanner input = new Scanner(System.in);
		boolean running = true;
		String resp;
		
//		int[] info = game.makeMove("e2e4");
//		System.out.println(game);
//		game.unmakeMove("e2e4", info);
//		System.out.println(game);
		
		EvalMults defMults = new EvalMults(1, 26, 1, 2, 0); // after 10 improvements the best it found was 1,26,1,2,11 so I guess I did a pretty good job just guessing 1,25,1,3,10
		EvalMults bestMults = defMults.dupe();
		EvalMults testMults = defMults.dupe();
		testMults.changeValues(10);
		
		System.out.print("Bot only game? (Y/N): ");
		resp = input.nextLine();

		
		int moveInd;
		int iterations = 0;
		int winner = -1;
		
		if (resp.equals("Y")) {
			while (iterations < 10) {
				game = new ChessGame();
				boolean gameDone = false;
				int plies = 0;
				int result;
				while (!gameDone && plies < 140) {
					result = game.botMove(bestMults);
					plies++;
					//System.out.println(game);
					//System.out.println("\n\n\n");
					if (result == -13) {
						gameDone = true;
						winner = 1;
					}
					else {
						result = game.botMove(testMults);
						plies++;
						//System.out.println(game);
						//System.out.println("\n\n\n");
						if (result == -12) {
							gameDone = true;
							winner = 2;
						}
					}
				}
				if (winner == 1) {
					System.out.println("Previous best won with " + bestMults);
				}
				else if (winner == 2) {
					System.out.println("Test won with " + testMults);
					bestMults = testMults.dupe();
					iterations++;
				}
				else {
					System.out.println("Tie between previous of " + bestMults + "and test of " + testMults);
					System.out.println(game);
				}
				testMults = defMults.dupe();
				testMults.changeValues(10 - iterations);
				
			}
		}
		else {
			while (running) {
				resp = input.nextLine();
				running = !resp.equals("-1");
				if (resp.equals("bot")) {
					int res = game.botMove(defMults);
					if (res <= -12) {
						running = false;
					}

				}
				else if (resp.equals("val")) {
					System.out.println(game.getBoardStrength());
				}
				else {
					moveInd = game.inputMove(resp);
					boolean valid = moveInd >= 0;
					if (moveInd <= -12) {
						valid = true;
						running = false;
					}
					
					while(!valid) {
						resp = input.nextLine();
						running = !resp.equals("-1");
						valid = game.inputMove(resp) >= 0;
					}

				}
				System.out.println(game);
				System.out.println("\n\n\n");
				

			}
		}
		input.close();
	}

}
