package ui.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ui.GameManager;

public class OptionsPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final GameManager game;
	
	private final JButton go;
	private final JButton stop;
	
	private final JSlider goSlider;
	
	public OptionsPanel(GameManager game)
	{
		this.game = game;
		
		goSlider = setupGoSlider();
		
		go = setupGo(goSlider);
		stop = setupStop();
		
		this.add(go);
		this.add(stop);
		
		this.add(goSlider);
	}
	
	private JButton setupGo(JSlider slider)
	{
		JButton go = new JButton("Start");
		go.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				game.start(goSlider.getValue() * 1000);
			}
		});
		return go;
	}
	
	private JButton setupStop()
	{
		JButton stop = new JButton("Stop");
		stop.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				game.stop();
			}
		});
		return stop;
	}
	
	private JSlider setupGoSlider()
	{
		JSlider slider = new JSlider(1, 5);
		slider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				go.doClick(0); // 0 minimises the clicking animation on go
			}
		});
		return slider;
	}
}
