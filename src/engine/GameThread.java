package engine;

public class GameThread extends Thread {
	GameBase game;
	public GameThread(GameBase game) {
		this.game = game;
	}

	@Override
	public void run() {
		//Make sure no clients interfere
		Server.getLock();

		try {
			game.run();
		} catch (Exception e) { e.printStackTrace(); }
		finally {
			Server.releaseLock();
		}

		try {
			Thread.sleep(game.delayBetweenRuns());
		} catch (Exception e) {e.printStackTrace();}
	}
}
