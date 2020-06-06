import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import java.io.IOException;
import java.io.FileReader;
import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Properties;
import java.io.File;
public class Round {
    public enum Suit{
      SPADES, HEARTS, DIAMONDS, CLUBS
    }
    private Hand[] hands;
    public enum Rank{
      // Reverse order of rank importance (see rankGreater() below)
      // Order of cards is tied to card images
      ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
    }
    
    RowLayout[] layouts;
    final String trumpImage[] = {"bigspade.gif","bigheart.gif","bigdiamond.gif","bigclub.gif"};

    static final Random random = ThreadLocalRandom.current();

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
    // return random Card from Hand
    public static Card randomCard(Hand hand){
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

// return random Card from ArrayList
public static Card randomCard(ArrayList<Card> list){
    int x = random.nextInt(list.size());
    return list.get(x);
}

    private final int handWidth = 400;
    private final int trickWidth = 40;
    private final Location trickLocation = new Location(350, 350);
    private Location trumpsActorLocation = new Location(50, 50);
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final Location[] handLocations = {
        new Location(350, 625),
        new Location(75, 350),
        new Location(350, 75),
        new Location(625, 350)
    };

    private Location hideLocation = new Location(-500, - 500);
public boolean rankGreater(Card card1, Card card2) {
	  return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
}

public Round(int nbPlayers, int nbStartCards,CardGame game) {
    hands = deck.dealingOut(nbPlayers, nbStartCards); // Last element of hands is leftover cards; these are ignored
    for (int i = 0; i < nbPlayers; i++) {
          hands[i].sort(Hand.SortType.SUITPRIORITY, true);
    }
    
    // graphics
    this.layouts= new RowLayout[nbPlayers];
    for (int i = 0; i < nbPlayers; i++) {
        layouts[i] = new RowLayout(handLocations[i], handWidth);
        layouts[i].setRotationAngle(90 * i);
        // layouts[i].setStepDelay(10);
        hands[i].setView(game, layouts[i]);
        hands[i].setTargetArea(new TargetArea(trickLocation));
        hands[i].setTargetArea(new TargetArea(trickLocation));
    }
}

public void Roundgraphics(int nbPlayers,CardGame game){
    for (int i = 0; i < nbPlayers; i++) {
        hands[i].draw();
    }
}  
public Optional<Integer> playRound(Whist game){
    final Suit trumps = randomEnum(Suit.class);
    final Actor trumpsActor = new Actor("sprites/"+trumpImage[trumps.ordinal()]);
    game.addActor(trumpsActor, trumpsActorLocation);
    Card selected;
    Hand trick;
	int winner;
	Card winningCard;
	Suit lead;
	int nextPlayer = random.nextInt(4); // randomly select player to lead for this round
	for (int i = 0; i < Whist.nbStartCards; i++) {
		trick = new Hand(deck);
        selected = null;
        if (false) {  // Select lead depending on player type
        } else {
    		game.setStatusText("Player " + nextPlayer + " thinking...");
            CardGame.delay(100);
            selected = randomCard(hands[nextPlayer]);
        }
        trick.setView(game, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
        trick.draw();
        selected.setVerso(false); 
        // No restrictions on the card being lead
        lead = (Suit) selected.getSuit();
        selected.transfer(trick, true); // transfer to trick (includes graphic effect) ？？
        winner = nextPlayer;
        winningCard = selected;
            // End Lead
            for (int j = 1; j < game.nbPlayers; j++) {
                if (++nextPlayer >= game.nbPlayers) nextPlayer = 0;  // From last back to first
                selected = null;
                if (false) {
                } else {
                    game.setStatusText("Player " + nextPlayer + " thinking...");
                    Whist.delay(100);
                    selected = randomCard(hands[nextPlayer]);
                }
                // Follow with selected card
                    trick.setView(game, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
                    trick.draw();
                    selected.setVerso(false);  // In case it is upside down
                    // Check: Following card must follow suit if possible
                        if (selected.getSuit() != lead && hands[nextPlayer].getNumberOfCardsWithSuit(lead) > 0) {
                             // Rule violation
                             String violation = "Follow rule broken by player " + nextPlayer + " attempting to play " + selected;
                             System.out.println(violation);
                             if (Whist.enforceRules) 
                                 try {
                                     throw(new BrokeRuleException(violation));
                                    } catch (BrokeRuleException e) {
                                        e.printStackTrace();
                                        System.out.println("A cheating player spoiled the game!");
                                        System.exit(0);
                                    }  
                         }
                    // End Check
                     selected.transfer(trick, true); // transfer to trick (includes graphic effect)
                     System.out.println("winning: suit = " + winningCard.getSuit() + ", rank = " + winningCard.getRankId());
                     System.out.println(" played: suit = " +    selected.getSuit() + ", rank = " +    selected.getRankId());
                     if ( // beat current winner with higher card
                         (selected.getSuit() == winningCard.getSuit() && rankGreater(selected, winningCard)) ||
                          // trumped when non-trump was winning
                         (selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
                         System.out.println("NEW WINNER");
                         winner = nextPlayer;
                         winningCard = selected;
                     }
                // End Follow
                
            }
        Whist.delay(600);
		trick.setView(game, new RowLayout(hideLocation, 0));
		trick.draw();		
		nextPlayer = winner;
		game.setStatusText("Player " + nextPlayer + " wins trick.");
		game.scores[nextPlayer]++;
		game.updateScore(nextPlayer);
		if (Whist.winningScore == game.scores[nextPlayer]) return Optional.of(nextPlayer);
	}
	game.removeActor(trumpsActor);
	return Optional.empty();
    }    
}

