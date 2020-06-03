// Whist.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("serial")
public class Whist extends CardGame {

    // Assumptions:
    // define Game - From start of the program (main), until there is a winner
    // define Turn - From dealing cards to player, until their hands are empty
    // define Round - In each round, each player plays their selected card to get score

    private final String version = "1.0";

    // ***************** graphic-related settings ********************
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };
    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(575, 25),
            new Location(650, 575)
    };
    final String trumpImage[] = {"bigspade.gif","bigheart.gif","bigdiamond.gif","bigclub.gif"};
    private final int handWidth = 400;
    private final int trickWidth = 40;
    private final Location trickLocation = new Location(350, 350);
    private final Location textLocation = new Location(350, 450);
    private Location hideLocation = new Location(-500, - 500);
    private Location trumpsActorLocation = new Location(50, 50);
    Font bigFont = new Font("Serif", Font.BOLD, 36);

    // ******************** card-related settings ***********************
    public enum Suit {
        SPADES, HEARTS, DIAMONDS, CLUBS
    }
    public enum Rank {
        // Reverse order of rank importance (see rankGreater() below)
        // Order of cards is tied to card images
        ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
    }

    // *********************** configurable ***************************
    static final Random random = ThreadLocalRandom.current();

    public final int nbPlayers = 4;
    public final int nbStartCards = 13;
    public final int winningScore = 11;
    private final int thinkingTime = 100; // original 2000
    private boolean enforceRules=false;

    // ************ game-related variables ***************
    private final Deck deck = new Deck(Whist_Original.Suit.values(), Whist_Original.Rank.values(), "cover");
    private Player players[];
    private Player winner;
    private Round round;

    public void setStatus(String string) { setStatusText(string); }

    // return random Enum value
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public Whist () {
        super(700, 700, 30);
        setTitle("Whist (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        // can delay(sometime);
        players = initPlayers();
    }

    public static void main() {
        // System.out.println("Working Directory = " + System.getProperty("user.dir"));

        // read in properties

        // start the game
        Whist game = new Whist();
        game.playGame();

    }

    public static void dealOutCards () {
        Hand[] hands;
        hands = deck.dealingOut(nbPlayers, nbStartCards);
        for (i = 0; i < nbPlayers; i++) {
            Player curr_player = players[i];
            curr_player.setHand(hands[i]);
        }
    }

    private void updateScore(int player_id) {
        players[player_id].incrementScore();
    }

    private void playGame() {
        Player winner = null;
        iniPlayers();
        while (winner == null) {
            if (player[0].hasNoCard()) {
                dealOutCards();
            }
            round = new Round();
            // deal cards to players
            updateScore(round.runRound());
            for (i = 0; i < nbPlayers; i++) {
                Player curr_player = players[i];
                if (curr_player.getScore() == winningScore) {
                    winner = curr_player;
                    break;
                }
            }

        }
        addActor(new Actor("sprites/gameover.gif"), textLocation);
        setStatusText("Game over. Winner is player: " + winner.toString());
        refresh();
    }
}