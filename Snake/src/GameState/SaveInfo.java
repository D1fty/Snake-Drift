package GameState;

import java.io.Serializable;

public class SaveInfo implements Serializable 
{
	public 				 int 	startLevel;
	public 				 int 	audioVolume;
	public  			 int 	map;
	public  			 int 	highScore;
	public  			 String highScorer;
	private static final long 	serialVersionUID = 1L;
}