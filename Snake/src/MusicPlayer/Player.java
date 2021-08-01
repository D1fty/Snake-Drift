package MusicPlayer;

import java.io.File;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

import GameState.States;

public class Player 
{
	
	// Data Members
	private Clip 	LoopClip;
	private Clip 	AppleGetClip01;
	private Clip 	AppleGetClip02;
	private Clip 	IntroClip;
	private Clip 	ExitClip;
	private float 	Gain;
	
	// Constructor
	public Player()
	{
		// Before all else we need the intro music
		AudioClip Intro = loadAudio("src/MusicPlayer/Intro.wav");
		try
		{
			// Get a clip to use
			this.IntroClip = AudioSystem.getClip();
			
			// Load Data into clip
			this.IntroClip.open					(Intro.getAudioFormat(),Intro.getData(), 0, (int)Intro.getBufferSize());
			
			// Set looper
			this.IntroClip.addLineListener		(event -> 
			{
				if (event.getType() == LineEvent.Type.STOP) 
				{
					States.Audible.introMusic = false;
					
					// Sleep two seconds for effect
					try
					{
						TimeUnit.SECONDS.sleep(2);
					} 
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					
					// Check if main game is running
					if (!States.Running.mainGame)
					{
						// If not, loop the clip
						playIntro();
					}
				}
			});	
		}
		catch(Exception exception) 
		{
			// Display Error Message
			System.out.println("Error loading Intro Audio Clip\n");
			
		}	
		
		// Load rest of clips
		// Loop Track
		AudioClip LoopTrack = loadAudio("src/MusicPlayer/Loop.wav");
		try
		{
			// Get a clip to use
			this.LoopClip = AudioSystem.getClip();
			
			// Load Data into clip
			this.LoopClip.open(LoopTrack.getAudioFormat(),LoopTrack.getData(), 0, (int)LoopTrack.getBufferSize());	
		}
		catch(Exception exception) 
		{
			// Display Error Message
			System.out.println("Error loading Looped Audio Clip\n");
			
		}	
		
		// AppleGet Music
		// Load the audio clips
		AudioClip AppleGet01 = loadAudio("src/MusicPlayer/AppleGet01.wav");
		AudioClip AppleGet02 = loadAudio("src/MusicPlayer/AppleGet02.wav");	
		try
		{
			// Get clips to use
			this.AppleGetClip01 = AudioSystem.getClip();
			this.AppleGetClip02 = AudioSystem.getClip();
			
			// Load Data into clips
			this.AppleGetClip01.open(AppleGet01.getAudioFormat(),AppleGet01.getData(), 0, (int)AppleGet01.getBufferSize());	
			this.AppleGetClip02.open(AppleGet02.getAudioFormat(),AppleGet02.getData(), 0, (int)AppleGet02.getBufferSize());
			
			// Set clips to call backing track on stop
			this.AppleGetClip01.addLineListener(event -> 
			{
				// On Stop Event
				if (event.getType() == LineEvent.Type.STOP) 
				{			
					// If the main game is running
					if ((States.Running.mainGame) && (States.Commands.playBackingLoop))
					{
						// Play the backing track
						playBackingLoop();
					}
				}
			});	
			
			// Repeat above for other AppleGet clip
			this.AppleGetClip02.addLineListener(event -> 
			{
				if (event.getType() == LineEvent.Type.STOP) 
				{			
					if ((States.Running.mainGame) && (States.Commands.playBackingLoop))
					{
						playBackingLoop();
					}
				}
			});	
		}
		catch(Exception exception) 
		{
			// Display Error Message
			System.out.println("Error loading AppleGet Audio Clips\n");
			
		}	
		
		// Finally we need the exit clip
		AudioClip Exit = loadAudio("src/MusicPlayer/Exit.wav");
		try
		{
			// Get a clip to use
			this.ExitClip = AudioSystem.getClip();
			
			// Load Data into clip
			this.ExitClip.open(Exit.getAudioFormat(),Exit.getData(), 0, (int)Exit.getBufferSize());
		}
		catch(Exception exception) 
		{
			// Display Error Message
			System.out.println("Error loading Exit Audio Clip\n");
			
		}
		
		// Set exit behaviour
		this.ExitClip.addLineListener(event -> 
		{
			if (event.getType() == LineEvent.Type.STOP) 
			{
				System.exit(0);
			}
		});	
	}

	// Set volume of all clips
	public void setVolume(float volume) 
	{	
		// Create Controls for Intro Clip and volume metrics
		FloatControl control 	= (FloatControl)this.IntroClip.getControl(FloatControl.Type.MASTER_GAIN);
		float range 			= control.getMaximum() - control.getMinimum();
		this.Gain 				= (range * (volume / 10)) + control.getMinimum();
		
		// Set Volume
		control.setValue(this.Gain );
		
		// Create Controls for Backing Loop Clip
		control = (FloatControl)this.LoopClip.getControl(FloatControl.Type.MASTER_GAIN);

		// Set Volume
		control.setValue(this.Gain );
		
		// Create Controls for Apple Get 01 Clip
		control = (FloatControl)this.AppleGetClip01.getControl(FloatControl.Type.MASTER_GAIN);

		// Set Volume
		control.setValue(this.Gain );
		
		// Create Controls for Apple Get 02 Clip
		control = (FloatControl)this.AppleGetClip02.getControl(FloatControl.Type.MASTER_GAIN);

		// Set Volume
		control.setValue(this.Gain );	
		
		// Create Controls for Exit Clip
		control = (FloatControl)this.ExitClip.getControl(FloatControl.Type.MASTER_GAIN);

		// Set Volume
		control.setValue(this.Gain );	
	}
	
	// Close all clips except exit clip
	public void preClose()
	{
		this.IntroClip.close		();
		this.AppleGetClip01.close	();
		this.AppleGetClip02.close	();
		this.LoopClip.close			();
	}
	
	// Plays the intro clip
	public void playIntro() 
	{
		States.Audible.introMusic = true;
		
		// Rewind clip
		this.IntroClip.setFramePosition(0);
		
		// Play Clip
		this.IntroClip.start();
	}
	
	// Stops the intro clip
	public void stopIntro() 
	{
		// Play Clip
		this.IntroClip.stop();
		
		States.Audible.introMusic = false;
	}
	
	// Plays the exit clip
	public void playExit() 
	{
		// Rewind clip
		this.ExitClip.setFramePosition(0);
		
		// Play Clip
		this.ExitClip.start();
	}
	
	// Plays the playAppleGet clip
	public void playAppleGet(boolean biggestApple) 
	{
		// The apple that incriments to level 10 players clip 2
		if (biggestApple)
		{
			// Rewind clip
			this.AppleGetClip02.setFramePosition(0);
			
			this.AppleGetClip02.start();
		}
		else
		{
			// Rewind clip
			this.AppleGetClip01.setFramePosition(0);
			
			// All previous apples use clip 01
			this.AppleGetClip01.start();
		}
	}
	
	// Stops the playAppleGet clip
	public void stopAppleGet(boolean biggestApple) 
	{
		// Tell AppleGet clips to play backing loop
		States.Commands.playBackingLoop = true;
		
		// Must have specified which apple clip to stop
		if (biggestApple)
		{
			this.AppleGetClip01.stop();
		}
		else
		{
			this.AppleGetClip02.stop();
		}
	}
	
	// Stop all audio
	public void stop()
	{
		// Tell ApplyGet clips to not start backing loop
		States.Commands.playBackingLoop = false;
		
		// Stop all music
		this.IntroClip.stop		();
		this.AppleGetClip01.stop();
		this.AppleGetClip02.stop();
		stopBackingLoop			();
	}
	
	// Plays the background loop track on loop
	private void playBackingLoop() 
	{
		// Set Frame Position to 0
		this.LoopClip.setFramePosition(0);

		// Set Clip to Loop
		this.LoopClip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	// Stops the background loop track
	public void stopBackingLoop() 
	{
		// Play Clip
		this.LoopClip.stop();
	}
	
	// Class used to store an audio clip
	private class AudioClip 
	{
		// Format
		AudioFormat mFormat;

		// Audio Data
		byte[] mData;

		// Buffer Length
		long mLength;

		// Loop Clip
		Clip mLoopClip;

		@SuppressWarnings("unused")
		public Clip getLoopClip() 
		{
			// return mLoopClip
			return mLoopClip;
		}

		@SuppressWarnings("unused")
		public void setLoopClip(Clip clip) 
		{
			// Set mLoopClip to clip
			mLoopClip = clip;
		}

		public AudioFormat getAudioFormat() 
		{
			// Return mFormat
			return mFormat;
		}

		public byte[] getData() 
		{
			// Return mData
			return mData;
		}

		public long getBufferSize() 
		{
			// Return mLength
			return mLength;
		}

		public AudioClip(AudioInputStream stream) 
		{
			// Get Format
			mFormat = stream.getFormat();

			// Get length (in Frames)
			mLength = stream.getFrameLength() * mFormat.getFrameSize();

			// Allocate Buffer Data
			mData = new byte[(int)mLength];

			try 
			{
				// Read data
				stream.read(mData);
			} 
			catch(Exception exception) 
			{
				// Print Error
				System.out.println("Error reading Audio File\n");

				// Exit
				System.exit(1);
			}

			// Set LoopClip to null
			mLoopClip = null;
		}
	}

	
	// Loads the AudioClip stored in the file specified by filename
	private AudioClip loadAudio(String filename) 
	{
		try 
		{
			// Open File
			File file = new File(filename);

			// Open Audio Input Stream
			AudioInputStream audio = AudioSystem.getAudioInputStream(file);

			// Create Audio Clip
			AudioClip clip = new AudioClip(audio);

			// Return Audio Clip
			return clip;
		} 
		catch(Exception e) 
		{
			// Catch Exception
			System.out.println("Error: cannot open Audio File " + filename + "\n");
		}

		// Return Null
		return null;
	}
}