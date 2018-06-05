package ui;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import core.logic.Generation;
import core.structures.Board;
import main.interfaces.GenericObservable;

public class GameManager extends GenericObservable<Board>
{
	private Board board;
	private Optional<Timer> timer;
	
	private final TimerTask notify = new TimerTask()
	{
		@Override
		public void run()
		{
			GameManager.this.board = Generation.runGeneration(GameManager.this.board);
			GameManager.this.notifyObservers(GameManager.this.board);
		}
	};
	
	/**
	 * Create a new game manager with the game stopped.
	 * @param board The starting state of the board.
	 */
	public GameManager(Board board)
	{
		this.board = board;
		this.timer = Optional.empty();
	}

	/**
	 * Run or resume the game, running as fast as possible.
	 * This function behaves the same as start(long).
	 */
	public void start()
	{
		start(1);
	}
	
	/**
	 * Start or resume the current game of life.
	 * This function will result in an Observer update.
	 * This function does nothing if the game is
	 * not stopped.
	 * @param period Milliseconds between executions.
	 * Must be >= 1.
	 */
	public void start(long period)
	{
		if (!timer.isPresent())
		{
			timer = Optional.of(new Timer());
			timer.get().schedule(notify, 0, period);
		}
	}
	
	/**
	 * Test whether the game is currently running.
	 * @return True if the game has start()ed, false
	 * if it has not or is currently stop()ed.
	 */
	public boolean running()
	{
		return timer.isPresent();
	}

	/**
	 * Pause the current game of life. The game
	 * can be resumed by calling start().
	 * This function does nothing if the game
	 * is already stopped.
	 */
	public void stop()
	{
		if (timer.isPresent())
		{
			timer.get().cancel();
			timer = Optional.empty();
		}
	}
}
