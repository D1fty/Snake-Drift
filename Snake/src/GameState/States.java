package GameState;

import java.util.concurrent.TimeUnit;

import MusicPlayer.Player;

public class States
{
	// These exist as quick access states to avoid extending Java Flex Classes
	static public class Clickable
	{
		// Clickables indicate to mouseListeners that the mouse is till within the button when released
		static public boolean theXButton;
		
		static public boolean playButton;
		
		static public boolean settingsButton;
		
		static public boolean exitButton;
		
		static public boolean LBUp;
		
		static public boolean LBDown;
		
		static public boolean VBUp;
		
		static public boolean VBDown;	
		
		static public boolean MBUp;
		
		static public boolean MBDown;	
		
		static public boolean RB;	
	}
	
	static public class Running
	{
		// Running booleans indicate which process the main() thread is running
		static public boolean mainGame;	
	}
	
	static public class Visible
	{
		// Visibles are for menus
		static public boolean mainMenu;
		
		static public boolean settingsMenu;
	}
	static public class Audible
	{
		static public boolean introMusic;
	}
	
	static public class Commands
	{
		// Commands are for "global functions"
		static public void ExitProgram(Player Audio, GameMenu GameMenu)
		{
			// Turn off all clips
			Audio.preClose();
			
			// Save settings
			if (States.Visible.settingsMenu)
			{
				//GameState.SaveSettings();
			}
			
			// Play exit clip (program will close when finished
			Audio.playExit();
			
			// Sleep two seconds for effect
			try
			{
				TimeUnit.MILLISECONDS.sleep(2000);
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			// Dispatch window
			GameMenu.setInvisible();
		}
		
		static public boolean 	playBackingLoop;
	}
}
