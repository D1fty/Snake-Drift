package GameState;
import java.awt.Point;
import 	java.io.*;
import 	java.util.*;

public class State 
{
	// Actual settings as serializable object
	private SaveInfo 	     settings;
	private ArrayList<Point> mapObjects; 
	private File 			 SaveFile;
	
	// Constructor
	public State()
	{
		// Check for serialized folder
		File userDir 	= new File(System.getProperty("user.home"));
		File storageDir = new File(userDir, ".SnakeDrift");
		if (!storageDir.exists())
		{
		    storageDir.mkdir();
		}

		// Save file in storage dir
		this.SaveFile = new File(storageDir, "SaveInfo.ser");
		if (SaveFile.exists())	
		{
			// Load settings from file
			try 
			{
				FileInputStream 	fileIn 			= new FileInputStream(this.SaveFile);
				ObjectInputStream 	in 				= new ObjectInputStream(fileIn);
									this.settings 	= (SaveInfo)in.readObject();
				in.close();
				fileIn.close();
			}
			catch (IOException i)
			{
				i.printStackTrace();
			}
			catch (ClassNotFoundException c)
			{
				System.out.println("SaveInfo class not found");
		        c.printStackTrace();
			}	
				
		}
		else
		{			
			this.settings 				= new SaveInfo();
			this.settings.startLevel 	= 1;
			this.settings.audioVolume 	= 10;
			this.settings.map			= 1;
			this.settings.highScore		= 0;
			this.settings.highScorer  	= null;
		}	
	}
	
	// Save highscore
	public void setHighScore(int score)
	{
		this.settings.highScore = score;
	}
	
	// Save highscorer
	public void setHighScorer(String name)
	{
		this.settings.highScorer = name;
	}
	
	// Serialize the settings (save to file)
	public void save()
	{
		try
		{
			FileOutputStream 	fileOut 	= new FileOutputStream(this.SaveFile);
			ObjectOutputStream 	out 		= new ObjectOutputStream(fileOut);
								out.writeObject(this.settings);
			out.close();
			fileOut.close();
		}
		catch (IOException i)
		{
			i.printStackTrace();
		}
	}
	
	// Load the map
	private void loadMap()
	{
		this.mapObjects = new ArrayList<Point>();
		switch(this.settings.map)
		{
			case 1:
				this.mapObjects.add(new Point( 7,  6));
				this.mapObjects.add(new Point( 7,  7));
				this.mapObjects.add(new Point( 7, 11));
				this.mapObjects.add(new Point( 7, 12));
				this.mapObjects.add(new Point( 8,  6));
				this.mapObjects.add(new Point( 8,  7));
				this.mapObjects.add(new Point( 8, 11));
				this.mapObjects.add(new Point( 8, 12));
				this.mapObjects.add(new Point(11,  6));
				this.mapObjects.add(new Point(11,  7));
				this.mapObjects.add(new Point(11, 11));
				this.mapObjects.add(new Point(11, 12));
				this.mapObjects.add(new Point(12,  6));
				this.mapObjects.add(new Point(12,  7));
				this.mapObjects.add(new Point(12, 11));
				this.mapObjects.add(new Point(12, 12));
				break;
			
			case 2:
				this.mapObjects.add(new Point( 2,  3));
				this.mapObjects.add(new Point( 2,  4));
				this.mapObjects.add(new Point( 2,  5));
				this.mapObjects.add(new Point( 2,  6));
				this.mapObjects.add(new Point( 2,  7));
				this.mapObjects.add(new Point( 2,  8));
				this.mapObjects.add(new Point( 2,  9));
				this.mapObjects.add(new Point( 2, 10));
				this.mapObjects.add(new Point( 2, 11));
				this.mapObjects.add(new Point( 2, 12));
				this.mapObjects.add(new Point( 2, 13));
				this.mapObjects.add(new Point( 2, 14));
				this.mapObjects.add(new Point( 2, 15));
				this.mapObjects.add(new Point( 2, 16));
				this.mapObjects.add(new Point( 3,  3));
				this.mapObjects.add(new Point( 3,  4));
				this.mapObjects.add(new Point( 3,  5));
				this.mapObjects.add(new Point( 3,  6));
				this.mapObjects.add(new Point( 3,  7));
				this.mapObjects.add(new Point( 3,  8));
				this.mapObjects.add(new Point( 3,  9));
				this.mapObjects.add(new Point( 3, 10));
				this.mapObjects.add(new Point( 3, 11));
				this.mapObjects.add(new Point( 3, 12));
				this.mapObjects.add(new Point( 3, 13));
				this.mapObjects.add(new Point( 3, 14));
				this.mapObjects.add(new Point( 3, 15));
				this.mapObjects.add(new Point( 3, 16));
				this.mapObjects.add(new Point(16,  3));
				this.mapObjects.add(new Point(16,  4));
				this.mapObjects.add(new Point(16,  5));
				this.mapObjects.add(new Point(16,  6));
				this.mapObjects.add(new Point(16,  7));
				this.mapObjects.add(new Point(16,  8));
				this.mapObjects.add(new Point(16,  9));
				this.mapObjects.add(new Point(16, 10));
				this.mapObjects.add(new Point(16, 11));
				this.mapObjects.add(new Point(16, 12));
				this.mapObjects.add(new Point(16, 13));
				this.mapObjects.add(new Point(16, 14));
				this.mapObjects.add(new Point(16, 15));
				this.mapObjects.add(new Point(16, 16));
				this.mapObjects.add(new Point(17,  3));
				this.mapObjects.add(new Point(17,  4));
				this.mapObjects.add(new Point(17,  5));
				this.mapObjects.add(new Point(17,  6));
				this.mapObjects.add(new Point(17,  7));
				this.mapObjects.add(new Point(17,  8));
				this.mapObjects.add(new Point(17,  9));
				this.mapObjects.add(new Point(17, 10));
				this.mapObjects.add(new Point(17, 11));
				this.mapObjects.add(new Point(17, 12));
				this.mapObjects.add(new Point(17, 13));
				this.mapObjects.add(new Point(17, 14));
				this.mapObjects.add(new Point(17, 15));
				this.mapObjects.add(new Point(17, 16));
				break;
				
			case 3:
				this.mapObjects.add(new Point( 4,  3));
				this.mapObjects.add(new Point( 4,  4));
				this.mapObjects.add(new Point( 4,  5));		
				this.mapObjects.add(new Point( 4,  6));
				this.mapObjects.add(new Point( 4,  7));
				this.mapObjects.add(new Point( 4,  8));
				this.mapObjects.add(new Point( 4,  9));
				this.mapObjects.add(new Point( 4, 10));
				this.mapObjects.add(new Point( 4, 11));
				this.mapObjects.add(new Point( 4, 12));
				this.mapObjects.add(new Point( 4, 13));
				this.mapObjects.add(new Point( 4, 14));
				this.mapObjects.add(new Point( 4, 15));
				this.mapObjects.add(new Point( 4, 16));
				this.mapObjects.add(new Point( 5,  3));
				this.mapObjects.add(new Point( 5, 16));
				this.mapObjects.add(new Point( 6,  3));
				this.mapObjects.add(new Point( 6, 16));
				this.mapObjects.add(new Point( 7,  3));
				this.mapObjects.add(new Point( 7, 16));
				this.mapObjects.add(new Point( 8,  3));
				this.mapObjects.add(new Point( 8,  16));
				this.mapObjects.add(new Point( 9,  3));
				// Gate Left
				this.mapObjects.add(new Point(10,  3));
				//Gate Right
				this.mapObjects.add(new Point(11,  3));
				this.mapObjects.add(new Point(11, 16));
				this.mapObjects.add(new Point(12,  3));
				this.mapObjects.add(new Point(12, 16));
				this.mapObjects.add(new Point(13,  3));
				this.mapObjects.add(new Point(13, 16));
				this.mapObjects.add(new Point(14,  3));
				this.mapObjects.add(new Point(14, 16));
				this.mapObjects.add(new Point(15,  3));
				this.mapObjects.add(new Point(15,  4));				
				this.mapObjects.add(new Point(15,  5));	
				this.mapObjects.add(new Point(15,  6));	
				this.mapObjects.add(new Point(15,  7));	
				this.mapObjects.add(new Point(15,  8));	
				this.mapObjects.add(new Point(15,  9));	
				this.mapObjects.add(new Point(15, 10));	
				this.mapObjects.add(new Point(15, 11));	
				this.mapObjects.add(new Point(15, 12));				
				this.mapObjects.add(new Point(15, 13));					
				this.mapObjects.add(new Point(15, 14));	
				this.mapObjects.add(new Point(15, 15));	
				this.mapObjects.add(new Point(15, 16));
				break;
			
			default:
				// Never called
				break;
		}
	}
	
	// Get the MapObjects
	public ArrayList<Point> getMapObjects()
	{
		this.loadMap();
		return this.mapObjects;
	}
	
	// Methods to return data
	public int getLevel() 
	{
		return this.settings.startLevel;
	}
	
	public int getAudioVolume() 
	{
		return this.settings.audioVolume;
	}
	
	public int getMap()
	{
		return this.settings.map;
	}
	
	public int getHighScore()
	{
		return this.settings.highScore;
	}
	
	public String getHighScorer()
	{
		return this.settings.highScorer;
	}
	
	// Methods to modify settings
	// Difficulty
	public void incrimentLevel()
	{
		if (this.settings.startLevel < 10)
		{
			this.settings.startLevel++;
		}
	}
	
	public void decrementLevel()
	{
		if (this.settings.startLevel > 1)
		{
			this.settings.startLevel--;
		}
	}
	
	// Audio
	public void incrimentVolume()
	{
		if (this.settings.audioVolume < 10)
		{
			this.settings.audioVolume++;
		}
	}
	
	public void decrementVolume()
	{
		if (this.settings.audioVolume > 0)
		{
			this.settings.audioVolume--;
		}
	}
	
	// Map
	public void mapUp()
	{
		if (this.settings.map < 3)
		{
			this.settings.map++;
		}
	}
	
	public void mapDown()
	{
		if (this.settings.map > 1)
		{
			this.settings.map--;
		}
	}
	
	public void clearHighScore()
	{
		this.settings.highScore 	= 0;
		this.settings.highScorer 	= null;
	}
}