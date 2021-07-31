package GamePlay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import GameState.GameMenu;
import GameState.State;
import GameState.States;
import MusicPlayer.Player;

public class Game
{
	private State 			 settings;
	private Player			 audio;
	private ArrayList<Point> mapObjects;
	private int[][]			 mapState;
	private JPanel			 window;
	private int 			 score;
	private int				 lives;
	private int				 level;
	private Snake			 snake;
	private double			 speed;
	private Point			 apple;
	private int			     move;
	JLabel 					 livesLabel;
	JLabel 					 scoreLabel;
	private GameMenu		 UI;
	
	// Constructors
	Game()
	{
		// Default constructor
	}
	
	public Game(JPanel window, Player player)
	{
		// These required based on where game is created in the menu space
		// Load settings
		this.window 		= window;
		this.audio			= player;
	}
	
	// Return score from game
	public int getScore()
	{
		return score;
	}
	
	// Get message from controller
	public void setMove(int m)
	{
		this.move = m;
	}
	
	// Load the game
	public void Load(State settings, JLabel LL, JLabel SL, GameMenu UI)
	{	
		// Store settings for end of game
		this.settings 	 = settings;
		
		// Map Objects
		this.mapObjects  = settings.getMapObjects	();
		
		// Map State
		this.mapState 	 = new int[20][20];
		
		// Add objects to mapState
		{
			Object[] objectArray = this.mapObjects.toArray();
			int i = 0;
			while (i < objectArray.length)
			{
				// Match Directly as Snake could be in any layout
				this.mapState[((Point)objectArray[i]).x][((Point)objectArray[i]).y] = 1;
				i++;
			}
		}
		
		// Initial move is should have no effect
		this.move 		 = -1;
		
		// Start score is 0
		this.score       = 0;

		// Start lives always 3
		this.lives 		 = 3;
		
		// Level
		this.level 		 = settings.getLevel		();
		
		// Make UI Available to thread for cleanup function
		this.UI 		 = UI;
		
		// Speed
		setSpeed									();
					
		// Snake
		setSnake									();
		
		// Lives Label
		livesLabel 		 = LL;
		setLLText									();
		
		// Score label
		scoreLabel 		 = SL;
		setSLText									();
		
		// Apple
		generateApple								();
		
		// Set buffering
		this.window.setDoubleBuffered				(true);
		
		// Set Map
		drawMap										(true);
	}
	
	// Play the game in a separate thread to leave Swing available to repaint
	public void play()
	{	
		// Create the thread
		Thread gameThread = new GameThread();
		gameThread.start();
	}
	
	// Game Thread
	private class GameThread extends Thread implements Runnable
	{
		@Override
		public void run()
		{		
			// Game timers
			long oldTime   = System.currentTimeMillis();
			long time;
			long pOldTime  = System.currentTimeMillis();
			long ptime;
			
			// Game Loop
			while(lives > 0)
			{
				// Check path isn't empty
				if (snake.Path.isEmpty())
				{
					// Get new path if it is
					snake.calculatePath();
				}
	
				// Get input
				snake.getMove();
				
				// Check speed
				time = System.currentTimeMillis();
				if (time - oldTime > speed)
				{
					// Reset movement timer
					oldTime = time;
					 
					// Check collision
					switch(collission(snake.Path.peek()))
					{				
						case 1:
							// Crash! Reset snake
							setSnake();
							lives--;
							setLLText();
							break;
							
						case 2:						
							// Play meme music
							audio.stop();
							if(level < 10)
							{

								audio.playAppleGet(false);
							}
							else
							{
								audio.playAppleGet(true);
							}
							
							// Increment difficulty
							if (level < 10)
							{
								level++;
								setSpeed();
							}
	
							// Increment score
							if(level == 10)
							{
								score += 500;
							}
							
							// Grow snake
							snake.grow();
							
							// Generate new apple
							generateApple();
							break;
							
						default:
							// No collision, move snake
							snake.move();
							break;
					}
				}
				
				// Draw at 30FPS
				ptime = System.currentTimeMillis();
				if (ptime - pOldTime > 35)
				{
					pOldTime = ptime;
					drawMap(true);
				}
			}		
			States.Running.mainGame = false;
			
			// Clean up game
			window.removeAll();
			window.setBackground(Color.BLACK);
			window.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));
			
			// Create game over prompt layouts
			JPanel titleCase = getGOCase	();
			JPanel promptCase = getGOCase	();
			JPanel textCase = getGOCase		();
			
			// Check high score
			if (score > settings.getHighScore())
			{
				// Set high score
				settings.setHighScore(score);
				
				// Alert label
				JLabel ptitle =getGOTitleLabel("<HTML><span text-alignment='center'>New High Score!</span></HTML>", new Color(255, 127, 39));
				titleCase.add					(Box.createHorizontalGlue());
				titleCase.add					(ptitle);
				titleCase.add					(Box.createHorizontalGlue());
						
				// Enter name label
				JLabel prompt = getGOTitleLabel("<HTML><span text-alignment='center'>Enter name:</span></HTML>", Color.WHITE);			
				promptCase.add(Box.createHorizontalGlue());
				promptCase.add(prompt);
				promptCase.add(Box.createHorizontalGlue());
				
				// Text field for name entry
				JTextField textField = new JTextField	(12); 
				textField.addActionListener				(new ActionListener() 
				{
				    @Override
				    public void actionPerformed(ActionEvent e) 
				    {
				    	if (textField.getText().length() == 0)
				    	{
				    		settings.setHighScorer("Takumi");
				    	}
				    	else
				    	{
				    		settings.setHighScorer	(textField.getText());
				    	}
				    	textField.setText		("");
				    							
						// Reset UI
				    	UI.reset();
				    }
				});
				textField.setMaximumSize		(new Dimension(500, 100));
				
				// Add field to case
				textCase.add					(Box.createHorizontalGlue());
				textCase.add					(textField);
				textCase.add					(Box.createHorizontalGlue());
				
				// Add all to layout panel
				JPanel allPanel = getGOPanel	();
				allPanel.add					(Box.createVerticalGlue());
				allPanel.add					(titleCase);
				allPanel.add					(Box.createRigidArea(new Dimension(5, 10)));	
				allPanel.add					(promptCase);
				allPanel.add					(textCase);
				allPanel.add					(Box.createVerticalGlue());

				// Add all to window
				window.add						(Box.createVerticalGlue());
				window.add						(allPanel);
				window.add						(Box.createVerticalGlue());		
			}
			else
			{				
				// Game over label
				JLabel ptitle =	getGOTitleLabel	("<HTML><span text-alignment='center'>Game Over!</span></HTML>", new Color(255, 127, 39));		
				titleCase.add					(Box.createHorizontalGlue());
				titleCase.add					(ptitle);
				titleCase.add					(Box.createHorizontalGlue());
				
				// Layout panel
				JPanel allPanel = getGOPanel	();				
				allPanel.add					(Box.createVerticalGlue());
				allPanel.add					(titleCase);
				allPanel.add					(Box.createVerticalGlue());				
				
				// Add to window
				window.add						(Box.createVerticalGlue());
				window.add						(allPanel);
				window.add						(Box.createVerticalGlue());
				
				// Sleep two seconds for effect
				try
				{
					TimeUnit.SECONDS.sleep		(2);
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace			();
				}
								
				// Reset UI
				UI.reset();
			}
		}
	}
	
	// Generate an apple
	private void generateApple()
	{
		Random random 	= new Random	();
		apple 			= new Point		(random.nextInt(19 - 0), random.nextInt(19 - 0));
		while (collission(apple) == 1)
		{
			random 	= new Random	();
			apple 	= new Point		(random.nextInt(19 - 0), random.nextInt(19 - 0));
		}
		mapState[apple.x][apple.y] = 2;
	}
	
	// Set the text on the Lives Label
	private void setLLText()
	{
		livesLabel.setText("<HTML><span text-alignment='center'>" + lives + "</span></HTML>");
	}
	
	// Set the text on the score label
	private void setSLText()
	{
		scoreLabel.setText("<HTML><span text-alignment='center'>" + score + "</span></HTML>");
	}
	
	// Check the next space in path for collision
	private int collission(Point nextSpace)
	{
		// Check apple
		if ((nextSpace.x == apple.x) && (nextSpace.y == apple.y))
		{
			return 2;
		}
		
		// Check with borders
		if((nextSpace.x > 19 || nextSpace.x < 0)			// Collides with side borders
	    || (nextSpace.y > 19 || nextSpace.y < 0))			// Collides with top/bottom borders
		{
			return 1;
		}
		
		// Check with map objects
		switch (mapState[nextSpace.x][nextSpace.y])
		{
		 	// Inner walls
			case 1:
				return 1;
				
			// Snake body
			case 4:
				return 1;
				
			// Snake tail
			case 5:
				return 1;
			
			// No collision
			default:
				return 0;
		}
	}
	
	// Set the level based on the difficulty
	private void setSpeed()
	{
		
		// Start speed
		switch (level)
		{
			case 1:
				this.speed = 500;
				break;
				
			case 2:
				this.speed = 450;
				break;
				
			case 3:
				this.speed = 400;
				break;
							
			case 4:
				this.speed = 375;
				break;
							
			case 5:
				this.speed = 350;
				break;
							
			case 6:
				this.speed = 325;
				break;
							
			case 7:
				this.speed = 300;
				break;
						
			case 8:
				this.speed = 275;
				break;
						
			case 9:
				this.speed = 250;
				break;
							
			case 10:
				this.speed = 225;
				break;		
		}
	}	
	
	private void setSnake()
	{		
		// Clear current snake
		if (snake != null)
		{
			// Only clear head if in array (when colliding with borders it will be outside the array)
			if ((snake.Head.x >= 0) && (snake.Head.x < 20) && (snake.Head.y >= 0) && (snake.Head.y < 20))
			{
				mapState[snake.Head.x][snake.Head.y] = 0;
			}
			
			// Clear the body from the array
			while(!snake.Body.isEmpty())
			{
				Point toClear = snake.Body.removeFirst();
				mapState[toClear.x][toClear.y] = 0;
			}
			
			// Clear the tail from the state array		
			mapState[snake.Tail.x][snake.Tail.y] = 0;
		}
		
		// Snake size is determined by level
		snake = new Snake();
		int x = level;
		if (x < 4)
		{
			// Minimum size of snake is 4 blocks
			x = 4;
		}
		
		// Set head
		snake.Head 		= new Point				(x, 0);
		mapState[x][0]  = 3;
		
		// Set body
		snake.Body 		= new LinkedList<Point>	();
		x--;
		while(x > 0)
		{
			snake.Body.addFirst					(new Point (x, 0));
			mapState[x][0]  = 4;
			x--;
		}
		snake.Tail 		= new Point				(snake.Body.removeFirst());
		mapState[snake.Tail.x][0]  = 5;
		
		// Create new blank path
		snake.Path 		= new LinkedList<Point>	();
		
		// Set starting direction
		snake.Direction = 1;
	}
	
	// Redraw the map
	private void drawMap(boolean isPractice)
	{	
		JPanel insert = new JPanel();
		insert.setBackground(Color.WHITE);
		insert.setLayout(new GridLayout(20, 20, 0, 0));
		
		// Iterate through each spot
		for (int y = 0; y < 20; y++)
		{
			for (int x = 0; x < 20; x++)
			{	
				// Create a panel in the spot and draw its component
				JPanel temp = new JPanel();
				switch(mapState[x][y])
				{
					case 0:
						// Draw empy panel
						temp.setBackground(Color.BLACK);
						break;
						
					case 1:
						// Draw map objects
						temp.setBackground(Color.DARK_GRAY);
						break;
						
					case 3:
						// Draw map snake head
						temp.setBackground(new Color(255, 127, 39));
						break;
						
					case 4:
						// Draw snake body
						temp.setBackground(Color.DARK_GRAY);
						break;

					case 5:
						// Draw snake tail
						temp.setBackground(Color.GRAY);
						break;

					case 2:
						// Draw apple (least likely scenario so goes last)
						temp.setBackground(Color.GREEN);
						break;
				}
				insert.add(temp);
			}	
		}
		
		// Repaint game window
		// Clear window
		window.removeAll();
		window.setLayout(new BorderLayout());
		window.add(insert, BorderLayout.CENTER);
		window.revalidate();
		window.repaint();
	}
	
	// Snake object
	class Snake
	{
		// Private data
		private Point		 		Head;
		private LinkedList<Point> 	Body;
		private Point		 		Tail;
		private LinkedList<Point> 	Path;
		private int			 		Direction;
		
		// Constructors
		private Snake()
		{
			// Null constructor
		}

		// Public methods
		// Grow the snake
		private void grow()
		{
			// Add head to body
			mapState[Head.x][Head.y] = 4;
			Body.addLast(new Point(Head.x, Head.y));
			
			// Check path isn't empty
			if (Path.isEmpty())
			{
				calculatePath();	
			}
			
			// Set head to next square
			Head = new Point(Path.removeFirst());
			mapState[Head.x][Head.y] = 3;
		}
		
		// Move the snake
		private void move()
		{
			// Add head to body
			mapState[Head.x][Head.y] = 4;
			Body.addLast		(new Point(Head.x, Head.y));
			
			// Check path isn't empty
			if (Path.isEmpty())
			{
				calculatePath();	
			}
			
			// Set head to next square
			Head = new Point	(Path.removeFirst());
			mapState[Head.x][Head.y] = 3;
			
			// Move tail
			mapState[Tail.x][Tail.y] = 0;
			Tail = new Point	(Body.removeFirst()); 
			mapState[Tail.x][Tail.y] = 5;
			
			// Movement bonus
			score += level*10;
			setSLText			();
		}
			
		// Create a straight path if it is null
		private void calculatePath()
		{
			Path.clear();
			
			switch(Direction)
			{
				// Moving up
				case 0:
					Path.addFirst(new Point(Head.x, Head.y - 1));
					Path.addLast(new Point(Head.x, Head.y - 2));
					Path.addLast(new Point(Head.x, Head.y - 3));
					Path.addLast(new Point(Head.x, Head.y - 4));
					Path.addLast(new Point(Head.x, Head.y - 5));
					Path.addLast(new Point(Head.x, Head.y - 6));		
					break;			
					
				// Moving right
				case 1:
					Path.addFirst(new Point(Head.x + 1, Head.y));
					Path.addLast(new Point(Head.x + 2, Head.y));
					Path.addLast(new Point(Head.x + 3, Head.y));
					Path.addLast(new Point(Head.x + 4, Head.y));
					Path.addLast(new Point(Head.x + 5, Head.y));
					Path.addLast(new Point(Head.x + 6, Head.y));
					break;
					
				// Moving down
				case 2:
					Path.addFirst(new Point(Head.x, Head.y + 1));
					Path.addLast(new Point(Head.x, Head.y + 2));
					Path.addLast(new Point(Head.x, Head.y + 3));
					Path.addLast(new Point(Head.x, Head.y + 4));
					Path.addLast(new Point(Head.x, Head.y + 5));
					Path.addLast(new Point(Head.x, Head.y + 6));
					break;
					
				// Moving left
				case 3:
					Path.addFirst(new Point(Head.x - 1, Head.y));
					Path.addLast(new Point(Head.x - 2, Head.y));
					Path.addLast(new Point(Head.x - 3, Head.y));
					Path.addLast(new Point(Head.x - 4, Head.y));
					Path.addLast(new Point(Head.x - 5, Head.y));
					Path.addLast(new Point(Head.x - 6, Head.y));
					break;
			}
		}
		
		// Get the next move
		private void getMove()
		{
			if ((move == snake.Direction) || (move + 2 == snake.Direction) || ((move + 2) % 4 == snake.Direction))
			{
				// Do nothing
			}
			else if (move != -1)
			{		
				// Clear path
				Path.clear();
					
				// Get new path
				switch(move)
				{
					// Turning up
					case 0:
						getUpPath();	
						break;			
						
					// Turning right
					case 1:
						getRightPath();
						break;
						
					// Turning down
					case 2:
						getDownPath();
						break;
						
					// Moving left
					case 3:
						// Turning left
						getLeftPath();
						break;
				}
				
				// Set direction
				Direction = move;
				
				// Set move to value that has no effect
				move = -1;
			}
		}
		
		// Calculate paths based on direction and movement direction
		// Get the path for turning upwards
		private void getUpPath()
		{
			if (Direction == 1)
			{
				// From moving right
				switch(level)
				{
					case 1:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						break;
						
					case 2:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						break;
						
					case 3:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y - 1));
						Path.addLast	(new Point(Head.x + 2, Head.y - 2));
						break;
						
					case 4:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y - 1));
						Path.addLast	(new Point(Head.x + 2, Head.y - 2));
						break;
						
					case 5:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y - 1));
						Path.addLast	(new Point(Head.x + 3, Head.y - 2));
						Path.addLast	(new Point(Head.x + 3, Head.y - 3));
						break;
						
					case 6:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y - 1));
						Path.addLast	(new Point(Head.x + 3, Head.y - 2));
						Path.addLast	(new Point(Head.x + 3, Head.y - 3));
						break;
						
					case 7:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y - 1));
						Path.addLast	(new Point(Head.x + 3, Head.y - 2));
						Path.addLast	(new Point(Head.x + 4, Head.y - 3));
						Path.addLast	(new Point(Head.x + 4, Head.y - 4));
						break;
						
					case 8:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y - 1));
						Path.addLast	(new Point(Head.x + 3, Head.y - 2));
						Path.addLast	(new Point(Head.x + 4, Head.y - 3));
						Path.addLast	(new Point(Head.x + 4, Head.y - 4));
						break;
						
					case 9:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y - 1));
						Path.addLast	(new Point(Head.x + 3, Head.y - 2));
						Path.addLast	(new Point(Head.x + 4, Head.y - 3));
						Path.addLast	(new Point(Head.x + 5, Head.y - 4));
						Path.addLast	(new Point(Head.x + 5, Head.y - 5));
						break;
						
					case 10:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y - 1));
						Path.addLast	(new Point(Head.x + 3, Head.y - 2));
						Path.addLast	(new Point(Head.x + 4, Head.y - 3));
						Path.addLast	(new Point(Head.x + 5, Head.y - 4));
						Path.addLast	(new Point(Head.x + 5, Head.y - 5));
						break;
				}
			}
			else
			{
				// From moving left
				switch(level)
				{
					case 1:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						break;
						
					case 2:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						break;
						
					case 3:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y - 1));
						Path.addLast	(new Point(Head.x - 2, Head.y - 2));
						break;
						
					case 4:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y - 1));
						Path.addLast	(new Point(Head.x - 2, Head.y - 2));
						break;
						
					case 5:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y - 1));
						Path.addLast	(new Point(Head.x - 3, Head.y - 2));
						Path.addLast	(new Point(Head.x - 3, Head.y - 3));
						break;
						
					case 6:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y - 1));
						Path.addLast	(new Point(Head.x - 3, Head.y - 2));
						Path.addLast	(new Point(Head.x - 3, Head.y - 3));
						break;
						
					case 7:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y - 1));
						Path.addLast	(new Point(Head.x - 3, Head.y - 2));
						Path.addLast	(new Point(Head.x - 4, Head.y - 3));
						Path.addLast	(new Point(Head.x - 4, Head.y - 4));
						break;
						
					case 8:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y - 1));
						Path.addLast	(new Point(Head.x - 3, Head.y - 2));
						Path.addLast	(new Point(Head.x - 4, Head.y - 3));
						Path.addLast	(new Point(Head.x - 4, Head.y - 4));
						break;
						
					case 9:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y - 1));
						Path.addLast	(new Point(Head.x - 3, Head.y - 2));
						Path.addLast	(new Point(Head.x - 4, Head.y - 3));
						Path.addLast	(new Point(Head.x - 5, Head.y - 4));
						Path.addLast	(new Point(Head.x - 5, Head.y - 5));
						break;
						
					case 10:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y - 1));
						Path.addLast	(new Point(Head.x - 3, Head.y - 2));
						Path.addLast	(new Point(Head.x - 4, Head.y - 3));
						Path.addLast	(new Point(Head.x - 5, Head.y - 4));
						Path.addLast	(new Point(Head.x - 5, Head.y - 5));
						break;
				}
			}
		}
		
		// Get the path for turning right
		private void getRightPath()
		{
			if (Direction == 0)
			{
				// From moving up
				switch(level)
				{
					case 1:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						break;
						
					case 2:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						break;
						
					case 3:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x + 1, Head.y - 2));
						Path.addLast	(new Point(Head.x + 2, Head.y - 2));
						break;
						
					case 4:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x + 1, Head.y - 2));
						Path.addLast	(new Point(Head.x + 2, Head.y - 2));
						break;
						
					case 5:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x + 1, Head.y - 2));
						Path.addLast	(new Point(Head.x + 2, Head.y - 3));
						Path.addLast	(new Point(Head.x + 3, Head.y - 3));
						break;
						
					case 6:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x + 1, Head.y - 2));
						Path.addLast	(new Point(Head.x + 2, Head.y - 3));
						Path.addLast	(new Point(Head.x + 3, Head.y - 3));
						break;
						
					case 7:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x + 1, Head.y - 2));
						Path.addLast	(new Point(Head.x + 2, Head.y - 3));
						Path.addLast	(new Point(Head.x + 3, Head.y - 4));
						Path.addLast	(new Point(Head.x + 4, Head.y - 4));
						break;
						
					case 8:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x + 1, Head.y - 2));
						Path.addLast	(new Point(Head.x + 2, Head.y - 3));
						Path.addLast	(new Point(Head.x + 3, Head.y - 4));
						Path.addLast	(new Point(Head.x + 4, Head.y - 4));
						break;
						
					case 9:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x + 1, Head.y - 2));
						Path.addLast	(new Point(Head.x + 2, Head.y - 3));
						Path.addLast	(new Point(Head.x + 3, Head.y - 4));
						Path.addLast	(new Point(Head.x + 4, Head.y - 5));
						Path.addLast	(new Point(Head.x + 5, Head.y - 5));
						break;
						
					case 10:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x + 1, Head.y - 2));
						Path.addLast	(new Point(Head.x + 2, Head.y - 3));
						Path.addLast	(new Point(Head.x + 3, Head.y - 4));
						Path.addLast	(new Point(Head.x + 4, Head.y - 5));
						Path.addLast	(new Point(Head.x + 5, Head.y - 5));
						break;
				}
			}
			else
			{
				// From moving down
				switch(level)
				{
					case 1:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						break;
						
					case 2:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						break;
						
					case 3:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x + 1, Head.y + 2));
						Path.addLast	(new Point(Head.x + 2, Head.y + 2));
						break;
						
					case 4:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x + 1, Head.y + 2));
						Path.addLast	(new Point(Head.x + 2, Head.y + 2));
						break;
						
					case 5:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x + 1, Head.y + 2));
						Path.addLast	(new Point(Head.x + 2, Head.y + 3));
						Path.addLast	(new Point(Head.x + 3, Head.y + 3));
						break;
						
					case 6:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x + 1, Head.y + 2));
						Path.addLast	(new Point(Head.x + 2, Head.y + 3));
						Path.addLast	(new Point(Head.x + 3, Head.y + 3));
						break;
						
					case 7:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x + 1, Head.y + 2));
						Path.addLast	(new Point(Head.x + 2, Head.y + 3));
						Path.addLast	(new Point(Head.x + 3, Head.y + 4));
						Path.addLast	(new Point(Head.x + 4, Head.y + 4));
						break;
						
					case 8:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x + 1, Head.y + 2));
						Path.addLast	(new Point(Head.x + 2, Head.y + 3));
						Path.addLast	(new Point(Head.x + 3, Head.y + 4));
						Path.addLast	(new Point(Head.x + 4, Head.y + 4));
						break;
						
					case 9:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x + 1, Head.y + 2));
						Path.addLast	(new Point(Head.x + 2, Head.y + 3));
						Path.addLast	(new Point(Head.x + 3, Head.y + 4));
						Path.addLast	(new Point(Head.x + 4, Head.y + 5));
						Path.addLast	(new Point(Head.x + 5, Head.y + 5));
						break;
						
					case 10:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x + 1, Head.y + 2));
						Path.addLast	(new Point(Head.x + 2, Head.y + 3));
						Path.addLast	(new Point(Head.x + 3, Head.y + 4));
						Path.addLast	(new Point(Head.x + 4, Head.y + 5));
						Path.addLast	(new Point(Head.x + 5, Head.y + 5));
						break;
				}
			}
		}
		
		// Get the path for turning downwards
		private void getDownPath()
		{
			if (Direction == 1)
			{
				// From moving right
				switch(level)
				{
					case 1:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						break;
						
					case 2:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						break;
						
					case 3:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y + 1));
						Path.addLast	(new Point(Head.x + 2, Head.y + 2));
						break;
						
					case 4:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y + 1));
						Path.addLast	(new Point(Head.x + 2, Head.y + 2));
						break;
						
					case 5:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y + 1));
						Path.addLast	(new Point(Head.x + 3, Head.y + 2));
						Path.addLast	(new Point(Head.x + 3, Head.y + 3));
						break;
						
					case 6:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y + 1));
						Path.addLast	(new Point(Head.x + 3, Head.y + 2));
						Path.addLast	(new Point(Head.x + 3, Head.y + 3));
						break;
						
					case 7:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y + 1));
						Path.addLast	(new Point(Head.x + 3, Head.y + 2));
						Path.addLast	(new Point(Head.x + 4, Head.y + 3));
						Path.addLast	(new Point(Head.x + 4, Head.y + 4));
						break;
						
					case 8:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y + 1));
						Path.addLast	(new Point(Head.x + 3, Head.y + 2));
						Path.addLast	(new Point(Head.x + 4, Head.y + 3));
						Path.addLast	(new Point(Head.x + 4, Head.y + 4));
						break;
						
					case 9:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y + 1));
						Path.addLast	(new Point(Head.x + 3, Head.y + 2));
						Path.addLast	(new Point(Head.x + 4, Head.y + 3));
						Path.addLast	(new Point(Head.x + 5, Head.y + 4));
						Path.addLast	(new Point(Head.x + 5, Head.y + 5));
						break;
						
					case 10:
						Path.addFirst	(new Point(Head.x + 1, Head.y));
						Path.addLast	(new Point(Head.x + 2, Head.y + 1));
						Path.addLast	(new Point(Head.x + 3, Head.y + 2));
						Path.addLast	(new Point(Head.x + 4, Head.y + 3));
						Path.addLast	(new Point(Head.x + 5, Head.y + 4));
						Path.addLast	(new Point(Head.x + 5, Head.y + 5));
						break;
				}
			}
			else
			{
				// From moving left
				switch(level)
				{
					case 1:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						break;
						
					case 2:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						break;
						
					case 3:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y + 1));
						Path.addLast	(new Point(Head.x - 2, Head.y + 2));
						break;
						
					case 4:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y + 1));
						Path.addLast	(new Point(Head.x - 2, Head.y + 2));
						break;
						
					case 5:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y + 1));
						Path.addLast	(new Point(Head.x - 3, Head.y + 2));
						Path.addLast	(new Point(Head.x - 3, Head.y + 3));
						break;
						
					case 6:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y + 1));
						Path.addLast	(new Point(Head.x - 3, Head.y + 2));
						Path.addLast	(new Point(Head.x - 3, Head.y + 3));
						break;
						
					case 7:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y + 1));
						Path.addLast	(new Point(Head.x - 3, Head.y + 2));
						Path.addLast	(new Point(Head.x - 4, Head.y + 3));
						Path.addLast	(new Point(Head.x - 4, Head.y + 4));
						break;
						
					case 8:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y + 1));
						Path.addLast	(new Point(Head.x - 3, Head.y + 2));
						Path.addLast	(new Point(Head.x - 4, Head.y + 3));
						Path.addLast	(new Point(Head.x - 4, Head.y + 4));
						break;
						
					case 9:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y + 1));
						Path.addLast	(new Point(Head.x - 3, Head.y + 2));
						Path.addLast	(new Point(Head.x - 4, Head.y + 3));
						Path.addLast	(new Point(Head.x - 5, Head.y + 4));
						Path.addLast	(new Point(Head.x - 5, Head.y + 5));
						break;
						
					case 10:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						Path.addLast	(new Point(Head.x - 2, Head.y + 1));
						Path.addLast	(new Point(Head.x - 3, Head.y + 2));
						Path.addLast	(new Point(Head.x - 4, Head.y + 3));
						Path.addLast	(new Point(Head.x - 5, Head.y + 4));
						Path.addLast	(new Point(Head.x - 5, Head.y + 5));
						break;
				}
			}
		}
		
		// Get the path for turning left
		private void getLeftPath()
		{
			if (Direction == 0)
			{
				// From moving up
				switch(level)
				{
					case 1:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						break;
						
					case 2:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						break;
						
					case 3:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x - 1, Head.y - 2));
						Path.addLast	(new Point(Head.x - 2, Head.y - 2));
						break;
						
					case 4:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x - 1, Head.y - 2));
						Path.addLast	(new Point(Head.x - 2, Head.y - 2));
						break;
						
					case 5:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x - 1, Head.y - 2));
						Path.addLast	(new Point(Head.x - 2, Head.y - 3));
						Path.addLast	(new Point(Head.x - 3, Head.y - 3));
						break;
						
					case 6:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x - 1, Head.y - 2));
						Path.addLast	(new Point(Head.x - 2, Head.y - 3));
						Path.addLast	(new Point(Head.x - 3, Head.y - 3));
						break;
						
					case 7:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x - 1, Head.y - 2));
						Path.addLast	(new Point(Head.x - 2, Head.y - 3));
						Path.addLast	(new Point(Head.x - 3, Head.y - 4));
						Path.addLast	(new Point(Head.x - 4, Head.y - 4));
						break;
						
					case 8:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x - 1, Head.y - 2));
						Path.addLast	(new Point(Head.x - 2, Head.y - 3));
						Path.addLast	(new Point(Head.x - 3, Head.y - 4));
						Path.addLast	(new Point(Head.x - 4, Head.y - 4));
						break;
						
					case 9:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x - 1, Head.y - 2));
						Path.addLast	(new Point(Head.x - 2, Head.y - 3));
						Path.addLast	(new Point(Head.x - 3, Head.y - 4));
						Path.addLast	(new Point(Head.x - 4, Head.y - 5));
						Path.addLast	(new Point(Head.x - 5, Head.y - 5));
						break;
						
					case 10:
						Path.addFirst	(new Point(Head.x, Head.y - 1));
						Path.addLast	(new Point(Head.x - 1, Head.y - 2));
						Path.addLast	(new Point(Head.x - 2, Head.y - 3));
						Path.addLast	(new Point(Head.x - 3, Head.y - 4));
						Path.addLast	(new Point(Head.x - 4, Head.y - 5));
						Path.addLast	(new Point(Head.x - 5, Head.y - 5));
						break;
				}
			}
			else
			{
				// From moving down
				switch(level)
				{
					case 1:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						break;
						
					case 2:
						Path.addFirst	(new Point(Head.x - 1, Head.y));
						break;
						
					case 3:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x - 1, Head.y + 2));
						Path.addLast	(new Point(Head.x - 2, Head.y + 2));
						break;
						
					case 4:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x - 1, Head.y + 2));
						Path.addLast	(new Point(Head.x - 2, Head.y + 2));
						break;
						
					case 5:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x - 1, Head.y + 2));
						Path.addLast	(new Point(Head.x - 2, Head.y + 3));
						Path.addLast	(new Point(Head.x - 3, Head.y + 3));
						break;
						
					case 6:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x - 1, Head.y + 2));
						Path.addLast	(new Point(Head.x - 2, Head.y + 3));
						Path.addLast	(new Point(Head.x - 3, Head.y + 3));
						break;
						
					case 7:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x - 1, Head.y + 2));
						Path.addLast	(new Point(Head.x - 2, Head.y + 3));
						Path.addLast	(new Point(Head.x - 3, Head.y + 4));
						Path.addLast	(new Point(Head.x - 4, Head.y + 4));
						break;
						
					case 8:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x - 1, Head.y + 2));
						Path.addLast	(new Point(Head.x - 2, Head.y + 3));
						Path.addLast	(new Point(Head.x - 3, Head.y + 4));
						Path.addLast	(new Point(Head.x - 4, Head.y + 4));
						break;
						
					case 9:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x - 1, Head.y + 2));
						Path.addLast	(new Point(Head.x - 2, Head.y + 3));
						Path.addLast	(new Point(Head.x - 3, Head.y + 4));
						Path.addLast	(new Point(Head.x - 4, Head.y + 5));
						Path.addLast	(new Point(Head.x - 5, Head.y + 5));
						break;
						
					case 10:
						Path.addFirst	(new Point(Head.x, Head.y + 1));
						Path.addLast	(new Point(Head.x - 1, Head.y + 2));
						Path.addLast	(new Point(Head.x - 2, Head.y + 3));
						Path.addLast	(new Point(Head.x - 3, Head.y + 4));
						Path.addLast	(new Point(Head.x - 4, Head.y + 5));
						Path.addLast	(new Point(Head.x - 5, Head.y + 5));
						break;
				}
			}		
		}
	}
	
	// Private methods for code reduction
	private JPanel getGOPanel()
	{
		JPanel pane = new JPanel	();
		pane.setBackground			(Color.BLACK);
		pane.setBorder				(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.WHITE));
		pane.setMinimumSize			(new Dimension(window.getWidth()/2, window.getHeight()/3));
		pane.setMaximumSize			(new Dimension(window.getWidth()/2, window.getHeight()/3));
		pane.setPreferredSize		(new Dimension(window.getWidth()/2, window.getHeight()/3));
		pane.setLayout				(new BoxLayout(pane, BoxLayout.Y_AXIS));
		return pane;
	}
	
	private JLabel getGOTitleLabel(String text, Color color)
	{
		JLabel label = new JLabel	(text);
		label.setBackground			(Color.BLACK);
		label.setFont				(new Font("Arial", Font.BOLD, 20));
		label.setForeground			(color);
		label.setMaximumSize		(new Dimension(500, 100));
		return label;
	}
	
	private JPanel getGOCase()
	{
		JPanel GOCase = new JPanel	();
		GOCase.setLayout				(new BoxLayout(GOCase, BoxLayout.X_AXIS));
		GOCase.setBackground			(Color.BLACK);
		return GOCase;
	}
	
}