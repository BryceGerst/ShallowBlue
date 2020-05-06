import java.util.Scanner;

public class GameRunner {

	public static void main(String[] args) {
		ChessGame game = new ChessGame();
		System.out.println(game);
		System.out.println("\n\n\n");
		Scanner input = new Scanner(System.in);
		boolean running = true;
		String resp;
		System.out.print("Bot only game? (Y/N): ");
		resp = input.nextLine();
		
		Node whiteNode = null;
		Node blackNode = null;
		
		Node botNode = null;
		
		int moveInd;
		
		if (resp.equals("Y")) {
			while (true) {
//				if (whiteNode == null) {
//					whiteNode = game.outputMove();
//				}
//				else {
//					whiteNode = game.outputMove(whiteNode);
//				}
//				moveInd = whiteNode.getInd();
//				//System.out.println("White: " + whiteNode.getHeight());
//				if (blackNode != null) {
//					blackNode = blackNode.goToNode(moveInd);
//				}
				
				game.botMove();
				System.out.println(game);
				System.out.println("\n\n\n");

//				if (blackNode == null) {
//					blackNode = game.outputMove();
//				}
//				else {
//					blackNode = game.outputMove(blackNode);
//				}
//				moveInd = blackNode.getInd();
//				//System.out.println("Black: " + blackNode.getHeight());
//				
//				whiteNode = whiteNode.goToNode(moveInd);
				
				game.botMove();
				System.out.println(game);
				System.out.println("\n\n\n");
			}
		}
		else {
			while (running) {
				resp = input.nextLine();
				running = !resp.equals("-1");
				if (resp.equals("bot")) {
					game.botMove();
//					if (botNode == null) {
//						botNode = game.outputMove();
//					}
//					else {
//						botNode = game.outputMove(botNode);
//					}
				}
				else if (resp.equals("val")) {
					System.out.println(game.getBoardStrength());
				}
				else {
					moveInd = game.inputMove(resp);
					boolean valid = moveInd >= 0;
					while(!valid) {
						resp = input.nextLine();
						running = !resp.equals("-1");
						valid = game.inputMove(resp) >= 0;
					}
//					if (botNode != null) {
//						botNode = botNode.goToNode(moveInd);
//					}
				}
				System.out.println(game);
				System.out.println("\n\n\n");
				
	//			game.outputMove();
	//			System.out.println(game);
	//			System.out.println("\n\n\n");
	//			
	//			boolean valid = true;//false;
	//			while(!valid) {
	//				String resp = input.nextLine();
	//				running = !resp.equals("-1");
	//				valid = game.inputMove(resp);
	//			}
	//			game.outputMove();
	//			System.out.println(game);
	//			System.out.println("\n\n\n");
			}
		}
		input.close();
	}

}
