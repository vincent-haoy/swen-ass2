// Whist.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import java.io.IOException;
import java.io.FileReader;
import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.text.StyledEditorKit.BoldAction;

import java.util.Properties;
import java.io.File;

@SuppressWarnings("serial")
public class Whist extends CardGame {
	private static String version;
	public final int nbPlayers = 4;
	public static int nbStartCards;
	public static int winningScore;
	public static int SEED;
	Round round;
	static Random random;
	public static Boolean enforceRules;
	// a player list is added here
	private final Location[] scoreLocations = {
		new Location(575, 675),
		new Location(25, 575),
		new Location(575, 25),
		new Location(650, 575)
	};
	  
	private Actor[] scoreActors = {null, null, null, null };
	public int[] scores = new int[nbPlayers];
	public void setStatus(String string) { setStatusText(string); }
	private final Location textLocation = new Location(350, 450);

	Font bigFont = new Font("Serif", Font.BOLD, 36);
	// return random Enum value
	private void initScore() {
		for (int i = 0; i < nbPlayers; i++) {
			scores[i] = 0;
			scoreActors[i] = new TextActor("0", Color.WHITE, bgColor, bigFont);
			addActor(scoreActors[i], scoreLocations[i]);
		}
  	}

	public void updateScore(int player) {
		removeActor(scoreActors[player]);
		scoreActors[player] = new TextActor(String.valueOf(scores[player]), Color.WHITE, bgColor, bigFont);
		addActor(scoreActors[player], scoreLocations[player]);
	}

	public Whist(){
    	super(700, 700, 30);
    	setTitle("Whist (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
		setStatusText("Initializing...");
		initScore();
		this.round = new Round(nbPlayers,nbStartCards,this);
		random = ThreadLocalRandom.current();
		//random.setSeed(SEED);
		this.run();
	}
	
	public void run(){
		Optional<Integer> winner;
		this.round.Roundgraphics(nbPlayers, this);
		do { 			
			winner = round.playRound(this);
			initScore();
			this.round = new Round(nbPlayers,nbStartCards,this);
		  }while (!winner.isPresent());
		  addActor(new Actor("sprites/gameover.gif"), textLocation);
		  setStatusText("Game over. Winner is player: " + winner.get());
		refresh();
	}

	public static void main(String[] args) throws IOException{
		FileReader inStream = null;
		Properties whistProperties = new Properties();

		// Default properties
		whistProperties.setProperty("nbStartCards", "13");
		whistProperties.setProperty("winningScore", "11");
		whistProperties.setProperty("SEED", "30006");
		whistProperties.setProperty("NUM_RANDOM_NPC", "3");
		whistProperties.setProperty("NUM_LEGAL_player", "0");
		whistProperties.setProperty("NUM_interation_player", "1");
		whistProperties.setProperty("version", "1.0");
		whistProperties.setProperty("enforceRules","false");
		//read from file
	  	try {
			inStream = new FileReader("whist.properties");
			whistProperties.load(inStream);
	  	}finally {	
			if (inStream != null) {
				inStream.close();
			}
		}	  
	 	//read
		 nbStartCards = Integer.parseInt(whistProperties.getProperty("nbStartCards"));
		 System.out.println(nbStartCards); 
	 	winningScore = Integer.parseInt(whistProperties.getProperty("winningScore")); 
	 	SEED = Integer.parseInt(whistProperties.getProperty("SEED")); 
		version = whistProperties.getProperty("version"); 
		enforceRules = Boolean.parseBoolean(whistProperties.getProperty("enforceRules"));
    	new Whist();
	}

}
