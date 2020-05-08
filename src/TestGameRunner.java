
public class TestGameRunner {
	public static void main(String[] args) {
		ChessGame game = new ChessGame();
		System.out.println(game.getHash());
		System.out.println(game.getTrueHash());
		System.out.println(game);
		System.out.println("\n\n\n");
		
		game.makeMove("e2e4");
		System.out.println(game.getHash());
		System.out.println(game.getTrueHash());
		System.out.println(game);
		System.out.println("\n\n\n");
		
		game.makeMove("d7d5");
		System.out.println(game.getHash());
		System.out.println(game.getTrueHash());
		System.out.println(game);
		System.out.println("\n\n\n");
		
		int[] info = game.makeMove("e4d5");
		System.out.println(game.getHash());
		System.out.println(game.getTrueHash());
		System.out.println(game);
		System.out.println("\n\n\n");
		
		game.unmakeMove("e4d5", info);
		System.out.println(game.getHash());
		System.out.println(game.getTrueHash());
		System.out.println(game);
		System.out.println("\n\n\n");
	}
}
