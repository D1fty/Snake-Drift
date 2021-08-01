import GameState.*;
import MusicPlayer.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.concurrent.TimeUnit;

public class Main 
{
	public static void main(String[] args)
	{
		// mainGame indicates Snake is running
		States.Running.mainGame = false;
		
		// Draw loading screen	
		final JFrame loadSplash = new JFrame("Loading...");
		loadSplash.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try 
		{
			loadSplash.setContentPane	(new JLabel(new ImageIcon(ImageIO.read(new File("src/Images/loadingSplash.png")))));
        } 
		catch (IOException e) 
		{
            e.printStackTrace			();
        }
		loadSplash.setUndecorated		(true);
		loadSplash.pack					();
		loadSplash.setLocationRelativeTo(null);
		loadSplash.setVisible			(true);
		loadSplash.setIconImage			((new ImageIcon("src/Images/Icon.png")).getImage());
	
		// Load game settings
		State GameState = new State();
		
		// Load audio machine
		Player Audio = new Player		();
		Audio.setVolume(GameState.getAudioVolume());
		Audio.playIntro();
		
		// Sleep two seconds for effect
		try
		{
			TimeUnit.SECONDS.sleep		(2);
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace			();
		}
			
		// Load and draw Menu
		try
		{
			@SuppressWarnings("unused")
			GameMenu menu = new GameMenu(GameState, Audio);
		}
		catch (Exception e)
		{
			// Do nothing
		}
		loadSplash.dispose				();
	}
}