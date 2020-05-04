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
		if (resp.equals("Y")) {
			while (true) {
				game.outputMove();
				System.out.println(game);
				System.out.println("\n\n\n");

				game.outputMove();
				System.out.println(game);
				System.out.println("\n\n\n");
			}
		}
		else {
			while (running) {
				resp = input.nextLine();
				running = !resp.equals("-1");
				if (resp.equals("bot")) {
					game.outputMove();
				}
				else if (resp.equals("val")) {
					System.out.println(game.getBoardStrength(true,false,false));
				}
				else {
					boolean valid = game.inputMove(resp);
					while(!valid) {
						resp = input.nextLine();
						running = !resp.equals("-1");
						valid = game.inputMove(resp);
					}		
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
