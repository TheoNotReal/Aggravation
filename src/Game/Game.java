package game;
////Manages the game's turns.
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import app.Aggravation;
import app.Utility.Log;
import board.Token;
import board.space.Home;
import board.space.Space;


public class Game implements ActionListener {
	public static enum Status {
		NEW, STARTED, ENDED, 
		WAITING_FOR_ROLL, 
		WAITING_FOR_Token_SELECTION,
		WAITING_FOR_MOVE_CHOICE,
		PROCESSING
	}
	public static final String
		AK_ROLL = "AK_ROLL";
	
	private static Game currInstance;
	private static final int DIE_SIDES = 6;
	
	private Player[] players = new Player[Aggravation.MAX_PLAYERS];
	private ArrayList<Integer> turnOrder = new ArrayList<Integer>();	
	private View display;
	private Game.Status currentStatus = Game.Status.NEW;
	private int roll = 0;	
	private Random rand = new Random(System.currentTimeMillis());
	private int currPlayerIndex;				
	private Player currPlayer;					
	private Map<Space,ArrayList<Space>> allPossDst;		
	private Space selectedSource;					
	private Space selectedDestination;				
	private boolean TokenMoved;
	
	
	public Game() {
		init();
	}
	
	
	public static void load() {
		Game.currInstance = new Game();
	}
	public static Game getCurrentInstance() {
		return Game.currInstance;
	}
	public void init() {
	
		for(int i = 0; i < this.players.length; ++i)
			this.players[i] = null;
		
		this.setStatus(Game.Status.NEW);
		Log.v("GAME","Initialized.");
	}
	
	public void start(Player[] pSet) {
		this.setStatus(Game.Status.STARTED);
		definePlayers(pSet);
		this.display = new View();
		this.startTurn();
	}
	
	public void end() {
		this.setStatus(Game.Status.ENDED);
		display.getTool().addLogMessage(currPlayer + " has won!", false);
		
		//update statuses
		for(int i : turnOrder)
			players[i].setStatus(Player.Status.LOSER);
		currPlayer.setStatus(Player.Status.WINNER);
		display.refresh();
	}
	private void definePlayers(Player[] pSet) {
		for(int i = 0; i < pSet.length; ++i) {
			this.players[i] = pSet[i];
		}

		turnOrder = new ArrayList<Integer>();
		for(int i = 0; i < Aggravation.MAX_PLAYERS; ++i)
			if(this.players[i] != null)
				turnOrder.add(i);
		Collections.shuffle(turnOrder);
		
		
		currPlayerIndex = 0;
		
		String str = turnOrder.size()+" Players defined with order ";
		for(int i : turnOrder)
			str+= i+" ";
		Log.v("GAME", str);
	}

	public void updatePlayers() {
		//Reset statuses
		for(int i : turnOrder)
			players[i].setStatus(Player.Status.WAITING);
		currPlayer.setStatus(Player.Status.CURRENT_PLAYER);
	}

	public Player getPlayer(int i) throws NullPointerException {
		return this.players[i];
	}
	public ArrayList<Integer> getTurnOrder() {
		return this.turnOrder;
	}
	
	public Player getCurrentPlayer() {
		return players[turnOrder.get(currPlayerIndex)];
	}
	
	public View getDisplay() {
		return this.display;
	}

	private void startTurn() {
		currPlayer = getCurrentPlayer();
		Log.d("GAME", "New turn started for Player #"
				+ turnOrder.get(currPlayerIndex) + ", \'"
				+ currPlayer+"\'");
		
		TokenMoved = false;
		display.getTool().addLogMessage(
				currPlayer
				+" it is your turn to roll!",false);
		display.getTool().getRollButton().setEnabled(true);
		display.getTool().getRollButton().addActionListener(this);
		updatePlayers();
		
		this.setStatus(Game.Status.WAITING_FOR_ROLL);
		display.refresh();
	}
	private void endCurrentTurn() {
		boolean gameOver = true;
		for(Space s : display.getBoard().getPlayerHomes(currPlayer)) {
			if(!s.hasToken())
				gameOver = false;
		}
		
		if(gameOver) {
			this.end();
			this.setStatus(Game.Status.ENDED);
		} else {
			if(roll != 6 || !TokenMoved) {
				++currPlayerIndex;
				if(currPlayerIndex == turnOrder.size())
					currPlayerIndex = 0;
			}
			this.startTurn();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Log.v("GAME-ACTION", e.getActionCommand());
		switch(e.getActionCommand()) {
		case Game.AK_ROLL:
			display.getTool().getRollButton().setEnabled(false);
			if(this.currentStatus == Game.Status.WAITING_FOR_ROLL) {
				this.setStatus(Game.Status.PROCESSING);
				roll = this.rand.nextInt(Game.DIE_SIDES)+1;
				display.getTool().addLogMessage(currPlayer + " rolls " + roll,false);
				Log.d("GAME", "Player "+currPlayer + " rolled a " + roll);
				
				this.findPossibleDestinations();
				
				String str = "Possible Destinations ("+allPossDst.size()+"): ";
				for(Space key : allPossDst.keySet()) {
					str += key + "[";
					for(Space dst : allPossDst.get(key))
						str += dst + " ";
					str += "] ";
				}
				Log.d("GAME", str);
				
				if(allPossDst.isEmpty()) {
					//player cannot move
					Log.d("GAME", "Player cannot move, skipping");
					display.getTool().addLogMessage(currPlayer+" cannot move any Tokens!");
					display.refresh();
					
					this.endCurrentTurn();
				} else {
					this.setStatus(Game.Status.WAITING_FOR_Token_SELECTION);
					
					display.getTool().addLogMessage(currPlayer + ", Please select one of your Tokens to move "+roll);
					display.refresh();
				}		
			}
			
		}
	}
	
	private void setStatus(Status s) {
		Log.d("GAME-Status Change",s.name());
		this.currentStatus = s;
	}
	public Game.Status getStatus() {
		return this.currentStatus;
	}
	public void onSpaceClicked(Space s) {
		final String KEY = "GAME-SpaceSelect";
		switch(this.currentStatus) {
		case WAITING_FOR_Token_SELECTION:
			if(chooseTokenSource(s)) {
				Log.d(KEY, currPlayer + " selected Token at " + s);
			}
			break;
		case WAITING_FOR_MOVE_CHOICE:
			if(checkTokenDestination(s)) {
				this.setStatus(Game.Status.PROCESSING);
				Log.d(KEY, currPlayer + " selected open space at " + s);
				this.selectedDestination = s;
				this.makeMove();
			} else if(chooseTokenSource(s)) {
				Log.d(KEY, currPlayer + " selected Token at " + s);
			} else {
				Log.d("GAME", "Player " + currPlayer+ " chose invalid move for Token at "+selectedSource);
			}
			break;
		default:
			break;
		}
	}

	private boolean chooseTokenSource(Space s) {
		if(s.hasToken() && s.getToken().getOwner() == currPlayer) {
			this.setStatus(Game.Status.PROCESSING);
			this.selectedSource = s;
			s.setFocus(true);

			if(allPossDst.get(selectedSource) == null) {
				Log.d("GAME", "Player "+currPlayer+" selected invalid Token at " + selectedSource);
				s.setFocus(false);
				selectedSource = null;
				setStatus(Game.Status.WAITING_FOR_Token_SELECTION);
				display.getTool().addLogMessage("You cannot move that Token!");
				display.refresh();
				return false;
			} else {
				this.setStatus(Game.Status.WAITING_FOR_MOVE_CHOICE);
				display.getTool().addLogMessage(currPlayer+", Please select a space to move the Token " + roll);
				display.refresh();
				return true;
			}
		} else {
			return false;
		}
	}

	private boolean checkTokenDestination(Space s) {
		for(Space valid : allPossDst.get(selectedSource))
			if(s == valid)
				return true;
		
		return false;
	}

	private void makeMove() {
		Token m = this.selectedSource.getToken();

		this.selectedSource.clearToken();
		this.selectedSource.setFocus(false);
		this.selectedDestination.setToken(m);
		
		Log.d("GAME-Move", "Token moved from "+this.selectedSource+" to "+this.selectedDestination);
		this.TokenMoved = true;
		this.endCurrentTurn();
	}

	private void findPossibleDestinations() {
		allPossDst = new HashMap<Space, ArrayList<Space>>();
		for(Space initial : display.getBoard().getPlayerTokenSpaces(currPlayer)) {
			ArrayList<Space> possDst = new ArrayList<Space>();
			switch(initial.getType()) {
			case BASE:
				if(roll == 1 || roll == 6) {
					Space start = display.getBoard().getPlayerStart(currPlayer);
					if(!start.hasToken() || start.getToken().getOwner() != currPlayer)
						possDst.add(start);
				}
				break;
			case CENTER:
				if(roll == 1) {
					Space[] corners = display.getBoard().getCorners();
					for(Space c : corners) {
						if(!c.hasToken() || c.getToken().getOwner() != currPlayer)
							possDst.add(c);
					}
				}
				break;
			case LOOP:
			case CORNER:
			case HOME:
				findPaths(initial, possDst, initial, roll);
			}
			if(!possDst.isEmpty())
				allPossDst.put(initial, possDst);
		}
	}
	private void findPaths(Space initial, ArrayList<Space> possDst, Space src, int moves) {
		Boolean goodDst;
		if(moves == 0) {
			possDst.add(src);
		} else {
			Space[] adj = display.getBoard().getNextSpaces(src);
			
			for(Space s : adj) {
				Log.e("PATH", ""+src);
				if(!s.hasToken() || s.getToken().getOwner() != currPlayer) {
					goodDst = true;
					switch(src.getType()) {
					case LOOP:
						goodDst = !((s.getType() == Space.Type.HOME &&
							((Home)s).getOwner() != currPlayer) ||
							src == display.getBoard().getPlayerHomeEntrance(currPlayer));
						break;
					case CENTER:
						goodDst = moves == roll;
						break;
					case CORNER:
						goodDst = !((s.getType() == Space.Type.CORNER &&
								initial.getType() != Space.Type.CORNER) || 
								(src != initial &&
										initial.getType() == Space.Type.CORNER &&
								s.getType() != Space.Type.CORNER));	
						break;
					default: break;
					}
					
					if(goodDst)
						findPaths(initial, possDst,s, moves - 1);
				}
			}
		}
	}
	
	
}