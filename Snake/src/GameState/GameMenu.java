package GameState;

import MusicPlayer.Player;
import GamePlay.Game;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.io.*;
import javax.imageio.ImageIO;

public class GameMenu
{
	JFrame  	window;
	JPanel		menu;
	JPanel		settingsMenu;
	State   	settings;
	Player  	audio;
	JPanel		gameSpace;
	JPanel  	gameWindow;
	JLabel  	HighScore;
	JLabel  	livesLabel;
	JLabel  	scoreLabel;
	Game 		game;
	GameMenu 	self;
	KeyAdapter  controls;
	
	public GameMenu(State settings, Player audio) throws IOException
	{
		// Create menu
		this.window 	= new JFrame		("Snake Drift");
		this.settings 	= settings;
		this.audio   	= audio;
		this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.HighScore  = new JLabel		(); 
		this.self 		= this;
		
		// Look and feel
		//Size
		int height;
		int width;
		{
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		 	height 				 = screenSize.height * 2 / 3;
		 	width  				 = screenSize.width  * 2 / 3;
		}
		this.window.setPreferredSize				(new Dimension(width, height));
		this.window.setResizable					(false);
		
		// Remove decorations
		this.window.setUndecorated					(true);
		this.window.getContentPane().setBackground	(Color.BLACK);
		this.window.setLayout						(null);
		
		// Add Icon
		this.window.setIconImage((new ImageIcon("src/Images/Icon.png")).getImage());
		
		// Title bar	
		try
		{
			BufferedImage myPicture;
			myPicture 		= ImageIO.read					(new File("src/Images/titleBar.png"));
			Image picture 	= myPicture.getScaledInstance	(185, 30, Image.SCALE_SMOOTH);
			JLabel titleBar = new JLabel					(new ImageIcon(picture));
			titleBar.setBounds								(0, 5, 185, 30);
			this.window.add									(titleBar);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// the X button
		JLabel theX = getMenuButton ("X", 35, 35, 20);
		theX.setBounds				(width-40, 5, 35, 35);
		
		// Listener for the X
		GameMenu menuForListener = this;
		theX.addMouseListener		(new MouseAdapter() 
		{
			public void mouseEntered(MouseEvent e)
			{
				States.Clickable.theXButton = true;
				theX.setBackground	(new Color(255, 127, 39));
				theX.setForeground	(Color.BLACK);
			}
			
			public void mouseExited(MouseEvent e)
			{
				States.Clickable.theXButton = false;
				theX.setBackground	(Color.BLACK);
				theX.setForeground	(new Color(255, 127, 39));
				theX.setBorder		(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
			}
			
            @Override
            public void mousePressed(final MouseEvent e) 
            {
				if (e.getButton() == MouseEvent.BUTTON1 )
				{
					theX.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.RED));
				}
            }

            @Override
            public void mouseReleased(final MouseEvent e) 
            {
				if ((e.getButton() == MouseEvent.BUTTON1 ) && (States.Clickable.theXButton))
				{
            		States.Commands.ExitProgram(audio, menuForListener);
				}
            }
        });
		this.window.add(theX);
		
		// Load settings menu
		this.settingsMenu   = new JPanel	();
		int menuWidth 	    = 				(width / 2);
		int menuHeight 	    =  				((height/5) + (height/5)/5);
		Dimension menuSize  = new Dimension	(menuWidth, menuHeight);
		Dimension panelSize = new Dimension	(menuWidth / 4, menuHeight);
		this.settingsMenu.setPreferredSize	(menuSize);
		this.settingsMenu.setBackground		(Color.BLACK);
		this.settingsMenu.setVisible		(false);
		this.settingsMenu.setBorder			(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.GRAY));
		this.settingsMenu.setLayout			(new BoxLayout(settingsMenu, BoxLayout.X_AXIS));
		
		// Settings menu (initially invisible)
		// Game Difficulty
			// Level Panel Title		
			JLabel LPTitle 		= getTitleLabel		("<HTML><span text-alignment='center'>Difficulty</span></HTML>");

			// Title panel
			JPanel LPPanel 		= getTitlePane		(LPTitle);
			
			// Level Panel Buttons
			// the Level Up
			JLabel LBLevel		= getSettingsDash	("<HTML><span text-alignment='center'>" + settings.getLevel() + "</span></HTML>");
			
			// the Level Up
			JLabel LBUp 		= getMenuButton		(">", 15, 30, 20);	
			
			// the Level Down
			JLabel LBDown 		= getMenuButton		("<", 15, 30, 20);
				
			// Add to horizontal unit
			JPanel LPButtons 	= getButtonsPane	(LBDown, LBLevel, LBUp);
		
			// Add all to Level Panel	
			JPanel LevelPanel 	= getSettingsPane	(LPPanel, LPButtons, panelSize);
		
			// Listener for the LBUp
			LBUp.addMouseListener(new MouseAdapter() 
			{
				public void mouseEntered(MouseEvent e)
				{
					States.Clickable.LBUp = true;
					LBUp.setBackground	(new Color(255, 127, 39));
					LBUp.setForeground	(Color.BLACK);
				}
				
				public void mouseExited(MouseEvent e)
				{
					States.Clickable.LBUp = false;
					LBUp.setBackground	(Color.BLACK);
					LBUp.setForeground	(new Color(255, 127, 39));
					LBUp.setBorder		(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
				}
				
	            @Override
	            public void mousePressed(final MouseEvent e) 
	            {
					if (e.getButton() == MouseEvent.BUTTON1 )
					{
						LBUp.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.RED));
					}
	            }

	            @Override
	            public void mouseReleased(final MouseEvent e) 
	            {
					if ((e.getButton() == MouseEvent.BUTTON1 ) && (States.Clickable.LBUp))
					{
	            		settings.incrimentLevel	();
	            		LBLevel.setText			("<HTML><span text-alignment='center'>" + settings.getLevel() + "</span></HTML>");
	            		LBUp.setBorder			(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
	            		LevelPanel.repaint		();
					}
	            }
	        });
			
			// Listener for the LBDown
			LBDown.addMouseListener(new MouseAdapter() 
			{
				public void mouseEntered(MouseEvent e)
				{
					States.Clickable.LBDown = true;
					LBDown.setBackground	(new Color(255, 127, 39));
					LBDown.setForeground	(Color.BLACK);
				}
				
				public void mouseExited(MouseEvent e)
				{
					States.Clickable.LBDown = false;
					LBDown.setBackground	(Color.BLACK);
					LBDown.setForeground	(new Color(255, 127, 39));
					LBDown.setBorder		(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
				}
				
	            @Override
	            public void mousePressed(final MouseEvent e) 
	            {
					if (e.getButton() == MouseEvent.BUTTON1 )
					{
						LBDown.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.RED));
					}
	            }
	
	            @Override
	            public void mouseReleased(final MouseEvent e) 
	            {
					if ((e.getButton() == MouseEvent.BUTTON1 ) && (States.Clickable.LBDown))
					{
	            		settings.decrementLevel	(); 
	            		LBLevel.setText			("<HTML><span text-alignment='center'>" + settings.getLevel() + "</span></HTML>");
	            		LBDown.setBorder		(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
					}
					
	            }
	        });
				
		// Music Volume		
			// Volume Panel Title		
			JLabel VPTitle = getTitleLabel 	("<HTML><span text-alignment='center'>Volume</span></HTML>");

			// Title pane
			JPanel VPPanel = getTitlePane	(VPTitle);
			
			// Volume Panel Buttons
			// the current Volume setting
			JLabel VBLevel = getSettingsDash("<HTML><span text-alignment='center'>" + settings.getAudioVolume() + "</span></HTML>");
			
			// the Volume Up
			JLabel VBUp = getMenuButton		(">", 15, 30, 20);
			
			// the Volume Down
			JLabel VBDown = getMenuButton		("<", 15, 30, 20);
			
			// Add to horizontal unit
			JPanel VPButtons = getButtonsPane(VBDown, VBLevel, VBUp);
		
			// Add all to Level Panel		
			JPanel VolumePanel = getSettingsPane(VPPanel, VPButtons, panelSize);
			
			// Listener for the LBUp
			VBUp.addMouseListener(new MouseAdapter() 
			{
				public void mouseEntered(MouseEvent e)
				{
					States.Clickable.VBUp = true;
					VBUp.setBackground	(new Color(255, 127, 39));
					VBUp.setForeground	(Color.BLACK);
				}
				
				public void mouseExited(MouseEvent e)
				{
					States.Clickable.VBUp = false;
					VBUp.setBackground	(Color.BLACK);
					VBUp.setForeground	(new Color(255, 127, 39));
					VBUp.setBorder		(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
				}
				
	            @Override
	            public void mousePressed(final MouseEvent e) 
	            {
					if (e.getButton() == MouseEvent.BUTTON1 )
					{
						VBUp.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.RED));
					}
	            }

	            @Override
	            public void mouseReleased(final MouseEvent e) 
	            {
					if ((e.getButton() == MouseEvent.BUTTON1 ) && (States.Clickable.VBUp))
					{
	            		settings.incrimentVolume();
	            		audio.setVolume			((float)settings.getAudioVolume());
	            		VBLevel.setText			("<HTML><span text-alignment='center'>" + settings.getAudioVolume() + "</span></HTML>");
	            		VBUp.setBorder			(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
        				VolumePanel.repaint		();
					}
	            }
	        });
			
			// Listener for the LBDown
			VBDown.addMouseListener(new MouseAdapter() 
			{
				public void mouseEntered(MouseEvent e)
				{
					States.Clickable.VBDown = true;
					VBDown.setBackground	(new Color(255, 127, 39));
					VBDown.setForeground	(Color.BLACK);
				}
				
				public void mouseExited(MouseEvent e)
				{
					States.Clickable.VBDown = false;
					VBDown.setBackground	(Color.BLACK);
					VBDown.setForeground	(new Color(255, 127, 39));
					VBDown.setBorder		(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
				}
				
	            @Override
	            public void mousePressed(final MouseEvent e) 
	            {
					if (e.getButton() == MouseEvent.BUTTON1 )
					{
						VBDown.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.RED));
					}
	            }

	            @Override
	            public void mouseReleased(final MouseEvent e) 
	            {
					if ((e.getButton() == MouseEvent.BUTTON1 ) && (States.Clickable.VBDown))
					{						
	            		settings.decrementVolume();
	            		audio.setVolume			((float)settings.getAudioVolume());
	            		VBDown.setBorder		(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
	            		VBLevel.setText			("<HTML><span text-alignment='center'>" + settings.getAudioVolume() + "</span></HTML>");
	            		VolumePanel.repaint		();
					}
					
	            }
	        });
					
		// Map Select		
			// Map Panel Title
			JLabel MPTitle = getTitleLabel			("<HTML><span text-alignment='center'>Map</span></HTML>");
			
			// Title Pane
			JPanel MPPanel = getTitlePane			(MPTitle);
			
			// Map Panel Buttons
			// Map Image
			JLabel mapImage = new JLabel			();
			mapImage.setIcon						(new ImageIcon("src/Images/Map0" + settings.getMap() + ".png"));
			mapImage.setMinimumSize					(new Dimension(30, 30));
			mapImage.setMaximumSize					(new Dimension(30, 30));
			
			// Map up
			JLabel 	MBDown 	  	= getMenuButton		("<", 15, 30, 20);
			
			// Map down
			JLabel 	MBUp 	  	= getMenuButton		(">", 15, 30, 20);
			
			// Add to horizontal unit
			JPanel MPButtons 	= getButtonsPane	(MBDown, mapImage, MBUp);
		
			// Add all to Level Panel	
			JPanel MapPanel 	= getSettingsPane	(MPPanel, MPButtons, panelSize);
			
			// Map Listener for the MBUp
			MBUp.addMouseListener(new MouseAdapter() 
			{
				public void mouseEntered(MouseEvent e)
				{
					States.Clickable.MBUp = true;
					MBUp.setBackground	(new Color(255, 127, 39));
					MBUp.setForeground	(Color.BLACK);
				}
				
				public void mouseExited(MouseEvent e)
				{
					States.Clickable.MBUp = false;
					MBUp.setBackground	(Color.BLACK);
					MBUp.setForeground	(new Color(255, 127, 39));
					MBUp.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
				}
				
	            @Override
	            public void mousePressed(final MouseEvent e) 
	            {
					if (e.getButton() == MouseEvent.BUTTON1 )
					{
						MBUp.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.RED));
					}
	            }

	            @Override
	            public void mouseReleased(final MouseEvent e) 
	            {
	            	
					if ((e.getButton() == MouseEvent.BUTTON1 ) && (States.Clickable.MBUp))
					{
						MBUp.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
	            		settings.mapUp();
	            		try
	            		{
	            			mapImage.setIcon(new ImageIcon("src/Images/Map0" + settings.getMap() + ".png"));
	            		}
	            		catch (Exception err)
	            		{
	            			// Do nothing
	            		}
					}
	            }
	        });
			
			// Listener for the BBDown
			MBDown.addMouseListener		(new MouseAdapter() 
			{
				public void mouseEntered(MouseEvent e)
				{
					States.Clickable.MBDown = true;
					MBDown.setBackground	(new Color(255, 127, 39));
					MBDown.setForeground	(Color.BLACK);
				}
				
				public void mouseExited(MouseEvent e)
				{
					States.Clickable.MBDown = false;
					MBDown.setBackground	(Color.BLACK);
					MBDown.setForeground	(new Color(255, 127, 39));
					MBDown.setBorder		(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
				}
				
	            @Override
	            public void mousePressed(final MouseEvent e) 
	            {
					if (e.getButton() == MouseEvent.BUTTON1 )
					{
						MBDown.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.RED));
					}
	            }

	            @Override
	            public void mouseReleased(final MouseEvent e) 
	            {
					if ((e.getButton() == MouseEvent.BUTTON1 ) && (States.Clickable.MBDown))
					{
	            		settings.mapDown();
		            	MBDown.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
	            		try
	            		{
	            			mapImage.setIcon(new ImageIcon("src/Images/Map0" + settings.getMap() + ".png"));
	            		}
	            		catch (Exception err)
	            		{
	            			// Do nothing
	            		}
					}
					
	            }
	        });
	
		// Reset high score button	
			// Reset Title			
			JLabel RPTitle 	 = getTitleLabel	("<HTML><span text-alignment='center'>Score</span></HTML>");
			
			// Reset Title panel
			JPanel RPPanel 	 = getTitlePane		(RPTitle);
			
			// Reset Panel Buttons
			// Reset
			JLabel RB 		 = getMenuButton	("<HTML><span text-alignment='center'>Reset</span></HTML>", 100, 30, 20);
					
			// Add to horizontal unit
			JPanel RPButtons = new JPanel		();
			RPButtons.setLayout					(new BoxLayout(RPButtons, BoxLayout.X_AXIS));
			RPButtons.setBackground				(Color.BLACK);
			RPButtons.add						(Box.createHorizontalGlue());
			RPButtons.add						(RB);
			RPButtons.add						(Box.createHorizontalGlue());
		
			// Add all to Level Panel
			JPanel ResetPanel = getSettingsPane(RPPanel, RPButtons, panelSize);
			
			// Listener for the RB
			RB.addMouseListener(new MouseAdapter() 
			{
				public void mouseEntered(MouseEvent e)
				{
					States.Clickable.RB = true;
					RB.setBackground	(new Color(255, 127, 39));
					RB.setForeground	(Color.BLACK);
				}
				
				public void mouseExited(MouseEvent e)
				{
					States.Clickable.RB = false;
					RB.setBackground	(Color.BLACK);
					RB.setForeground	(new Color(255, 127, 39));
					RB.setBorder		(null);
				}
				
	            @Override
	            public void mousePressed(final MouseEvent e) 
	            {
					if (e.getButton() == MouseEvent.BUTTON1 )
					{
						RB.setBorder	(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.RED));
					}
	            }

	            @Override
	            public void mouseReleased(final MouseEvent e) 
	            {
	            	RB.setBorder	(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));;
					if ((e.getButton() == MouseEvent.BUTTON1 ) && (States.Clickable.RB))
					{
	            		settings.clearHighScore ();
	            		if(settings.getHighScorer() != null)
	            		{
	            			if (settings.getHighScore() > 999999)
	            			{
	            				HighScore.setText("<html>"
	            									+ "<p style='color:white;font-size:30px;width:100%;text-align:center;'>" + settings.getHighScore()  + "</p>"
	            									+ "<p style='color:white;font-size:30px;width:100%;text-align:center;' >" + settings.getHighScorer() + "</p>"
	            							   +  "</html>");	
	            			}
	            			else
	            			{
	            				HighScore.setText("<html>"
	            									+ "<p style='color:white;font-size:50px;width:100%;text-align:center;'>" + settings.getHighScore()  + "</p>"
	            									+ "<p style='color:white;font-size:50px;width:100%;text-align:center;' >" + settings.getHighScorer() + "</p>"
	            							   +  "</html>");
	            			}
	            		}
	            		else
	            		{
	            			HighScore.setText("<html>"
	            								+ "<p style='color:white;font-size:50px;width:100%;text-align:center;'>" + settings.getHighScore()  + "</p>"
	            								+ "<p style='color:white;font-size:45px;width:100%;text-align:center;' >Not Set</p>"
	            						   +  "</html>");
	            		}
					}
	            }
	        });
		
		// Back to setting up overall menu
		this.settingsMenu.add	(LevelPanel);
		this.settingsMenu.add	(VolumePanel);
		this.settingsMenu.add	(MapPanel);
		this.settingsMenu.add	(ResetPanel);

		// Load Game Space
		int GSHeight = height - 160;
		this.gameSpace = new JPanel		();
		this.gameSpace.setBounds		(5, 80, width - 10, GSHeight);
		this.gameSpace.setBackground	(Color.BLACK);
		this.gameSpace.setOpaque		(true);
		this.gameSpace.setVisible		(false);
		this.gameSpace.setLayout		(new BoxLayout(this.gameSpace, BoxLayout.X_AXIS));
		
			// Game Space Components
			// Left GameSpace panel (Lives)
				// Lives Banner
				JLabel LivesBanner 	= getGameSpacePaneLabel	("<HTML><span text-alignment='center'>Lives</span></HTML>");
				
				// Lives Number
				livesLabel 			= getGameSpacePaneLabel ("<HTML><span text-alignment='center'>3</span></HTML>");
			
				// Actual Panel
				JPanel GSLeft  		= getGameSpacePane		(LivesBanner, livesLabel, height);
			
			// Game
			JPanel GSGame  = new JPanel	();
			GSGame.setBackground		(Color.BLACK);
			GSGame.setMaximumSize		(new Dimension(GSHeight, GSHeight));
			GSGame.setMinimumSize		(new Dimension(GSHeight, GSHeight));
			GSGame.setPreferredSize		(new Dimension(GSHeight, GSHeight));
			GSGame.setBorder			(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.WHITE));		
			this.gameWindow = GSGame;
			
			// Right panel (Score)
				// Score Banner
				JLabel ScoreBanner 	= getGameSpacePaneLabel	("<HTML><span text-alignment='center'>Score</span></HTML>");
				
				// Score Number
				scoreLabel 			= getGameSpacePaneLabel	("<HTML><span text-alignment='center'>0</span></HTML>");
		
				// Create panel	
				JPanel GSRight 		= getGameSpacePane		(ScoreBanner, scoreLabel, height);
			
		// Add to game space
		this.gameSpace.add	(Box.createHorizontalGlue());
		this.gameSpace.add	(GSLeft);
		this.gameSpace.add	(Box.createRigidArea(new Dimension(5, height - 160)));
		this.gameSpace.add	(GSGame);
		this.gameSpace.add	(Box.createRigidArea(new Dimension(5, height - 160)));
		this.gameSpace.add	(GSRight);
		this.gameSpace.add	(Box.createHorizontalGlue());
		this.window.add		(gameSpace);	
		
		// Actual game object
		this.game = new Game(GSGame, audio);
		
		// Draw menu
		this.drawMenu		(height, width);
		
		// Set visible
		this.window.pack					();
		this.window.setLocationRelativeTo	(null);
		this.window.setVisible				(true);	
	}
	
	// Set window invisible (called by States exit Command)
	public void setInvisible()
	{
		this.window.setVisible (false);
	}
	
	public void drawMenu(int height, int width)
	{
		// Create menu space
		this.menu = new JPanel	();
		this.menu.setBackground	(Color.BLACK);
		this.menu.setOpaque		(true);
		this.menu.setBounds		(0, height / 5, width, 4*(height / 5));
		this.menu.setLayout		(new BoxLayout(this.menu, BoxLayout.Y_AXIS));

		// Upper layout
		// Panel
		int panelSize = height / 5;
		JPanel UpperLayout = new JPanel	();
		UpperLayout.setBackground		(Color.BLACK);
		UpperLayout.setPreferredSize	(new Dimension(width, panelSize + 3*(panelSize / 8)));
		UpperLayout.setLayout			(new BoxLayout(UpperLayout, BoxLayout.X_AXIS));

		// High Score
		if(settings.getHighScorer() != null)
		{
			if (settings.getHighScore() > 999999)
			{
				HighScore.setText("<html>"
									+ "<p style='color:white;font-size:30px;width:100%;text-align:center;'>" + settings.getHighScore()  + "</p>"
									+ "<p style='color:white;font-size:30px;width:100%;text-align:center;' >" + settings.getHighScorer() + "</p>"
							   +  "</html>");	
			}
			else
			{
				HighScore.setText("<html>"
									+ "<p style='color:white;font-size:50px;width:100%;text-align:center;'>" + settings.getHighScore()  + "</p>"
									+ "<p style='color:white;font-size:50px;width:100%;text-align:center;' >" + settings.getHighScorer() + "</p>"
							   +  "</html>");
			}
		}
		else
		{
			HighScore.setText("<html>"
								+ "<p style='color:white;font-size:50px;width:100%;text-align:center;'>" + settings.getHighScore()  + "</p>"
								+ "<p style='color:white;font-size:45px;width:100%;text-align:center;' >Not set</p>"
						   +  "</html>");
		}
		HighScore.setHorizontalTextPosition	(JLabel.CENTER);
		
		JPanel HighScoreContainer = new JPanel		();
		HighScoreContainer.setMaximumSize			(new Dimension(2*(width / 5), panelSize + (panelSize / 2)));
		HighScoreContainer.setMinimumSize			(new Dimension(2*(width / 5), panelSize + (panelSize / 2)));
		HighScoreContainer.setPreferredSize			(new Dimension(2*(width / 5), panelSize + (panelSize / 2)));
		HighScoreContainer.setOpaque				(false);
		HighScoreContainer.add						(HighScore);
		HighScoreContainer.setBorder				(new LineBorder(Color.WHITE));
		
		UpperLayout.add						(Box.createHorizontalGlue());
		UpperLayout.add						(HighScoreContainer);
		UpperLayout.add						(Box.createHorizontalGlue());
		
		// Menu buttons
		// Layout panel
		JPanel LowerLayout = new JPanel		();
		LowerLayout.setLayout				(new BoxLayout(LowerLayout, BoxLayout.X_AXIS));
		LowerLayout.setBackground			(Color.BLACK);
		
		// Settings Button
		JLabel settingsButton = getMenuButton		("Settings", 300, 75, 45);

		// Settings mouse listener
		settingsButton.addMouseListener				(new MouseAdapter() 
		{
			public void mouseEntered(MouseEvent e)
			{
				States.Clickable.settingsButton = true;
				settingsButton.setBackground	(new Color(255, 127, 39));
				settingsButton.setForeground	(Color.BLACK);
			}
			
			public void mouseExited(MouseEvent e)
			{
				States.Clickable.settingsButton = false;
				settingsButton.setBackground	(Color.BLACK);
				settingsButton.setForeground	(new Color(255, 127, 39));
				settingsButton.setBorder		(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
			}
			
            @Override
            public void mousePressed(final MouseEvent e) 
            {
				if (e.getButton() == MouseEvent.BUTTON1 )
				{
					settingsButton.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.RED));
				}
            }

            @Override
            public void mouseReleased(final MouseEvent e) 
            {
				if (e.getButton() == MouseEvent.BUTTON1 )
				{
					settingsButton.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
	            	if (States.Visible.settingsMenu)
	            	{
	            		if(States.Clickable.settingsButton)
	            		{
		            		// Save settings
	            			settings.save				();
		            		settingsButton.setText		("Settings");
		            		settingsMenu.setVisible		(false);
		            		States.Visible.settingsMenu = false;
	            		}
	            	}
	            	else
	            	{
	            		if(States.Clickable.settingsButton)
	            		{
		            		settingsButton.setText		("Save");
		            		settingsMenu.setVisible		(true);
		            		States.Visible.settingsMenu = true;
	            		}
	            	}
				}
            }
        });
		
		// Play Button
		JLabel playButton = getMenuButton	("Play", 300, 75, 45);
		
		// Mouse listener for play button
		playButton.addMouseListener			(new MouseAdapter() 
		{
			public void mouseEntered(MouseEvent e)
			{
				States.Clickable.playButton = true;
				playButton.setBackground	(new Color(255, 127, 39));
				playButton.setForeground	(Color.BLACK);
			}
			
			public void mouseExited(MouseEvent e)
			{
				States.Clickable.playButton = false;
				playButton.setBackground	(Color.BLACK);
				playButton.setForeground	(new Color(255, 127, 39));
				playButton.setBorder		(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
			}
			
            @Override
            public void mousePressed(final MouseEvent e) 
            {
				if (e.getButton() == MouseEvent.BUTTON1 )
				{
					playButton.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.RED));
				}
            }

            @Override
            public void mouseReleased(final MouseEvent e) 
            {
				if ((e.getButton() == MouseEvent.BUTTON1 ) && (States.Clickable.playButton))
				{
					// Remove button
					playButton.setBorder	(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
					
					// Save settings if they are open
					if (States.Visible.settingsMenu)
					{
	            		// Save settings
            			settings.save				();
	            		settingsButton.setText		("Settings");
	            		settingsMenu.setVisible		(false);
	            		States.Visible.settingsMenu = false;
					}
					
					// Load game
	            	game.Load				(settings, livesLabel, scoreLabel, self);
	            	
	            	// Controls
	            	controls = new KeyAdapter() 
	            	{
	                    @Override
	                    public void keyPressed(KeyEvent keyEvent) 
	                    {
	                        if (keyEvent.getKeyCode() == KeyEvent.VK_W) 
	                        {
		                        game.setMove(0);
		                    }
	                        else if (keyEvent.getKeyCode() == KeyEvent.VK_D) 
	                        {
	                        	game.setMove(1);
	                        }
	                        else if (keyEvent.getKeyCode() == KeyEvent.VK_S) 
	                        {
	                        	game.setMove(2);
	                        }
	                        else if (keyEvent.getKeyCode() == KeyEvent.VK_A) 
	                        {
	                        	game.setMove(3);
	                        }
	                    }     
	            	};
	            	
	            	// Add controls to game
	            	window.setFocusable			(true);
	                window.addKeyListener		(controls);
	                window.requestFocusInWindow	();
	                
	            	// Swap visibility
	            	menu.setVisible				(false);
	            	gameSpace.setVisible		(true);
	    			
	            	// Play game
	            	game.play					();          
				}
            }
        });
	
		// Settings Button
		JLabel exitButton = getMenuButton		("Exit", 300, 75, 45);

		// Scope temp variables
		{
			// Create menu reference for listener
			GameMenu menu = this;
			exitButton.addMouseListener(new MouseAdapter() 
			{
				public void mouseEntered(MouseEvent e)
				{
					States.Clickable.exitButton = true;
					exitButton.setBackground	(new Color(255, 127, 39));
					exitButton.setForeground	(Color.BLACK);
				}
				
				public void mouseExited(MouseEvent e)
				{
					States.Clickable.exitButton = false;
					exitButton.setBackground	(Color.BLACK);
					exitButton.setForeground	(new Color(255, 127, 39));
					exitButton.setBorder		(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
				}
				
	            @Override
	            public void mousePressed(final MouseEvent e) 
	            {
					if (e.getButton() == MouseEvent.BUTTON1 )
					{
						exitButton.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.RED));
					}
	            }
	
	            @Override
	            public void mouseReleased(final MouseEvent e) 
	            {
	            	if ((e.getButton() == MouseEvent.BUTTON1 ) && (States.Clickable.exitButton))
	            	{	
		            	States.Commands.ExitProgram(audio, menu);
	            	}
	            }
	        });
		}
		
		// Add all buttons to menu
		LowerLayout.add						(Box.createHorizontalGlue());
		LowerLayout.add						(playButton);
		LowerLayout.add						(Box.createHorizontalGlue());
		LowerLayout.add						(settingsButton);
		LowerLayout.add						(Box.createHorizontalGlue());
		LowerLayout.add						(exitButton);
		LowerLayout.add						(Box.createHorizontalGlue());

		// Settings panel
		JPanel SettingsArea = new JPanel	();
		SettingsArea.setBackground			(Color.BLACK);
		SettingsArea.setPreferredSize		(new Dimension(width, panelSize));
		SettingsArea.add					(this.settingsMenu);
		
		// Add all to menu
		this.menu.add						(UpperLayout);
		this.menu.add						(Box.createVerticalGlue());
		this.menu.add						(LowerLayout);
		this.menu.add						(Box.createVerticalGlue());
		this.menu.add						(SettingsArea);
		this.menu.add						(Box.createVerticalGlue());
		this.menu.setVisible				(true);

		// Add to window
		this.window.add						(this.menu);

		// Make visible boolean
		States.Visible.mainMenu = true;
	}
	
	public void reset()
	{
    	// Don't stop introduction music if playing
		if (!States.Audible.introMusic)
		{
			audio.stop();
			audio.playIntro();
		}
		
		// High Score
		if(settings.getHighScorer() != null)
		{
			if (settings.getHighScore() > 999999)
			{
				HighScore.setText("<html>"
									+ "<p style='color:white;font-size:30px;width:100%;text-align:center;'>" + settings.getHighScore()  + "</p>"
									+ "<p style='color:white;font-size:30px;width:100%;text-align:center;' >" + settings.getHighScorer() + "</p>"
							   +  "</html>");	
			}
			else
			{
				HighScore.setText("<html>"
									+ "<p style='color:white;font-size:50px;width:100%;text-align:center;'>" + settings.getHighScore()  + "</p>"
									+ "<p style='color:white;font-size:50px;width:100%;text-align:center;' >" + settings.getHighScorer() + "</p>"
							   +  "</html>");
			}
		}
		else
		{
			HighScore.setText("<html>"
								+ "<p style='color:white;font-size:50px;width:100%;text-align:center;'>" + settings.getHighScore()  + "</p>"
								+ "<p style='color:white;font-size:45px;width:100%;text-align:center;' >Not set</p>"
						   +  "</html>");
		}
						
		// Remove controls
		window.removeKeyListener	(controls);
		this.gameSpace.setVisible	(false);
		this.menu.setVisible		(true);
	}
	
	// Private methods to clean up repeated code
	private JLabel getMenuButton(String text, int width, int height, int fontSize)
	{
		JLabel button = new JLabel		(text);
		button.setForeground			(new Color(255, 127, 39));
		button.setBackground			(Color.BLACK);
		button.setMinimumSize			(new Dimension(width, height));
		button.setMaximumSize			(new Dimension(width, height));
		button.setPreferredSize			(new Dimension(width, height));
		button.setFont					(new Font("Arial", Font.BOLD, fontSize));
		button.setBorder				(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
		button.setVerticalAlignment		(JLabel.CENTER);
		button.setHorizontalAlignment	(JLabel.CENTER);
		button.setOpaque				(true);
		return button;
	}
	
	private JPanel getSettingsPane(JPanel top, JPanel bottom, Dimension panelSize)
	{
		JPanel pane = new JPanel	();
		pane.setLayout				(new BoxLayout(pane, BoxLayout.X_AXIS));
		pane.setBackground			(Color.BLACK);
		pane.setBackground			(Color.BLACK);
		pane.setMinimumSize			(panelSize);
		pane.setMaximumSize			(panelSize);
		pane.setPreferredSize		(panelSize);
		pane.setOpaque				(true);
		pane.setLayout				(new BoxLayout(pane, BoxLayout.Y_AXIS));
		pane.add					(Box.createVerticalGlue());
		pane.add					(Box.createVerticalGlue());
		pane.add					(top);
		pane.add					(Box.createVerticalGlue());
		pane.add					(bottom);
		pane.add					(Box.createVerticalGlue());
		pane.add					(Box.createVerticalGlue());
		return pane;
	}
	
	private JPanel getTitlePane(JLabel title)
	{
		JPanel pane = new JPanel	();
		pane.setLayout				(new BoxLayout(pane, BoxLayout.X_AXIS));
		pane.setBackground			(Color.BLACK);
		pane.add					(Box.createHorizontalGlue());
		pane.add					(title);
		pane.add					(Box.createHorizontalGlue());
		return pane;
	}
	
	private JLabel getTitleLabel(String text)
	{
		JLabel label = new JLabel	();
		label.setText				(text);
		label.setMaximumSize		(new Dimension(100, 20));
		label.setFont				(new Font("Arial", Font.BOLD, 20));
		label.setForeground			(Color.WHITE);
		return label;
	}
	
	private JLabel getSettingsDash(String text)
	{
		JLabel dash = new JLabel	();
		dash.setHorizontalAlignment	(JLabel.CENTER);
		dash.setFont				(new Font("Arial", Font.BOLD, 20));
		dash.setForeground			(Color.WHITE);
		dash.setMaximumSize			(new Dimension(30,30));
		dash.setMinimumSize			(new Dimension(30,30));
		dash.setPreferredSize		(new Dimension(30,30));
		dash.setText				(text);
		return dash;
	}
	
	private JPanel getButtonsPane(JLabel left, JLabel mid, JLabel right)
	{
		JPanel panel = new JPanel	();
		panel.setLayout				(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBackground			(Color.BLACK);
		panel.add					(Box.createHorizontalGlue());
		panel.add					(left);
		panel.add					(Box.createRigidArea(new Dimension(10,20)));
		panel.add 					(mid);
		panel.add					(Box.createRigidArea(new Dimension(10,20)));
		panel.add					(right);
		panel.add					(Box.createHorizontalGlue());
		return panel;
	}
	
	private JPanel getGameSpacePane(JLabel title, JLabel currect, int height)
	{
		JPanel pane  = new JPanel	();
		pane.setBackground			(Color.BLACK);
		pane.setLayout				(new BoxLayout(pane, BoxLayout.Y_AXIS));
		pane.add  					(Box.createVerticalGlue());
		pane.add					(title);
		pane.add					(Box.createRigidArea(new Dimension(pane.getHeight(), 5)));
		pane.add					(currect);
		pane.add  					(Box.createVerticalGlue());
		pane.setMinimumSize			(new Dimension(300, height));
		pane.setMaximumSize			(new Dimension(300, height));
		pane.setPreferredSize		(new Dimension(300, height));
		return pane;
	}
	
	private JLabel getGameSpacePaneLabel(String text)
	{
		JLabel label = new JLabel		(text);
		label.setHorizontalAlignment	(JLabel.CENTER);
		label.setFont					(new Font("Arial", Font.BOLD, 20));
		label.setForeground				(Color.WHITE);
		return label;
	}
}
