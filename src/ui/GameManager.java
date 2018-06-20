package ui;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import core.logic.Generation;
import core.structures.Board;
import datastructures.Pair;
import main.interfaces.GenericObservable;

public class GameManager extends GenericObservable<Board>
{
	private Board board;
	private Optional<Timer> timer;
	private final Pair<Boolean, Integer> fixedGen;
	private int currentGen;

	private TimerTask notify;

	/**
	 * Create a new game manager with the game stopped.
	 * The game will not stop after a fixed number of generations.
	 * @param board The starting state of the board.
	 */
	public GameManager(Board board)
	{
		this(board, -1);
	}

	/**
	 * Create a new game manager with the game stopped,
	 * to run generations number of generations.
	 * @param board
	 * @param generations >= 0 
	 */
	public GameManager(Board board, int generations)
	{
		this.board = board;
		this.timer = Optional.empty();
		this.currentGen = 0;

		if (generations < 0)
		{
			this.fixedGen = new Pair<Boolean, Integer>(false, 0);
		}
		else
		{
			this.fixedGen = new Pair<Boolean, Integer>(true, generations);
		}
	}
	
	public Board getCurrentData()
	{
		return board;
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
	 * This function will result in an Observer update
	 * after the period has expired.
	 * This function will stop and then continue the game
	 * after the period.
	 * @param period Milliseconds between executions.
	 * Must be >= 1.
	 */
	public void start(long period)
	{
		if (running())
		{
			stop();
		}

		notify = runGame();
		timer = Optional.of(new Timer());
		timer.get().schedule(notify, period, period);
	}

	private TimerTask runGame()
	{
		return new TimerTask()
		{
			@Override
			public void run()
			{
				if (!fixedGen.left() || currentGen < fixedGen.right())
				{
					GameManager.this.board = Generation.runGeneration(GameManager.this.board);
					currentGen++;
				}

				GameManager.this.notifyObservers(GameManager.this.board);

				if (fixedGen.left() && currentGen >= fixedGen.right())
				{
					GameManager.this.stop();
				}
			}
		};
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
		if (running())
		{
			notify.cancel();
			timer.get().cancel();
			timer = Optional.empty();
		}
	}
}
