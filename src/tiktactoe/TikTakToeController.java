package tiktactoe;

/* Author: Evan Longmuir
 * Date: 04/20/2022
 * Project: Tik Tak Toe
 * runs all the logic and calculations behind the game
 */

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;

import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;

public class TikTakToeController {
	
	//initialize FXML Variables
	@FXML Button b0;
	@FXML Button b1;
	@FXML Button b2;
	@FXML Button b3;
	@FXML Button b4;
	@FXML Button b5;
	@FXML Button b6;
	@FXML Button b7;
	@FXML Button b8;
	
	@FXML GridPane gameBoard;
	
	//initialize imaginaryGameboard that will mirror actual game board and allow the AI to look into future game boards
	public String[] imaginaryGameboard = new String[9];
	//variables that tracks if there is a win currently on the board
	boolean winDetected = false;
	//level of AI variable(0 is player vs player, 1 is random, 2 is unbeatable)
	public int levelOfAI = 0;
	//initialize list of button for easy manipulation
	public List<Button> buttonList;
	//keeps track of which players move it is
	public boolean isPlayerOne = true;
	//keeps track of the symbol for each player (player 2 is CPU if a CPU is playing)
	public String playerOneSymbol = "X";
	public String playerTwoSymbol = "O";
	
	public AudioClip clickSound = new AudioClip(this.getClass().getResource("SoundEffect.wav").toString());
	public AudioClip victorySound = new AudioClip(this.getClass().getResource("Victory.wav").toString());

	//needed for additional windows
	Stage secondaryStage;
	
	//runs on button press
	public void buttonClickHandler(ActionEvent evt) {
		//if buttonList is empty initialize buttonList
		if(buttonList == null) {
			//add buttons to buttonList after the buttons have been initialized
			buttonList = List.of(b0, b1, b2, b3, b4, b5, b6, b7, b8);
		}
		
		//initialized variables with information on the clicked button
		Button clickedButton = (Button)evt.getTarget();
		String buttonLabel = clickedButton.getText();
		
		//if no win is detected allow the player to move
		if(!winDetected) {
			//if the button is empty and its player ones turn place player ones symbol
			if(buttonLabel.equals("") && isPlayerOne) {
				//put down player ones symbol
				clickedButton.setText(playerOneSymbol);
				//set move to player twos move
				isPlayerOne = false;
				//check if playerOne has won and highlight the combo
				if(!findWinsAndPotentialWins(buttonList.indexOf(clickedButton), true)) {
					//play the click sound if move is not a win
					clickSound.play();
				}
				//if no win than make the computer move
				if(!winDetected){
					makeCPUMove(levelOfAI);
				}
			}//make player twos move if button is empty, its not player ones turn, and AI level is 0
			else if (buttonLabel.equals("") && !isPlayerOne && levelOfAI == 0) {
				//put down the symbol for player two
				clickedButton.setText(playerTwoSymbol);
				//set it to player ones turn
				isPlayerOne = true;
				//check if player 2 has won and highlight the combo
				if(!findWinsAndPotentialWins(buttonList.indexOf(clickedButton), true)) {
					//play the click sound if move is not a win
					clickSound.play();
				}
			}
		}
	}
	
	//runs on click of a button from the menu bar
	public void menuClickHandler(ActionEvent evt) {
		//if buttonList is empty initialize buttonList
		if(buttonList == null) {
			//add buttons to buttonList after the buttons have been initialized
			buttonList = List.of(b0, b1, b2, b3, b4, b5, b6, b7, b8);
		}
		
		//initialize variables with info on the clicked menu option
		MenuItem clickedMenu = (MenuItem) evt.getTarget();
		String menuLabel = clickedMenu.getText();
		
		//these three buttons reset the game board
		if(menuLabel.equals("PvP") || menuLabel.equals("CPU (Easy)") || menuLabel.equals("CPU (Hard)")) {
			//clear the game board of all text and highlights
			ObservableList<Node> buttons = gameBoard.getChildren();
			
			buttons.forEach(btn ->{
				((Button) btn).setText("");
				btn.getStyleClass().clear();
				btn.getStyleClass().add("button");
			});
			
			//reset winDetected
			winDetected = false;
			
			//reset the player symbols
			playerOneSymbol = "X";
			playerTwoSymbol = "O";
			if(menuLabel.equals("PvP")) {
				//it its player vs player set playerOne to go first, and set AI to level 0
				isPlayerOne = true;
				levelOfAI = 0;
			}
			else {
				//if its not player vs player, set the AI level to the chosen difficulty (easy = 1, hard = 2)
				if(menuLabel.equals("CPU (Easy)")) {
					levelOfAI = 1;
				}
				else if(menuLabel.equals("CPU (Hard)")) {
					levelOfAI = 2;
				}
				//randomize if player or computer goes first
				if((int)(2 * Math.random()) == 1) {
					//if computer goes first swap symbols (playerOne is player, goes second so symbol is O)
					playerOneSymbol = "O";
					playerTwoSymbol = "X";
					//make the CPU go first
					makeCPUMove(levelOfAI);
				}
				//set playerOne to go next
				isPlayerOne = true;
			}
				
		}//exits the program
		else if(menuLabel.equals("Quit")) {
			Platform.exit();
		}//opens how to play window
		else if (menuLabel.equals("How to play")) {
			openWindow("HowToPlay.fxml");
		}//opens about window
		else if (menuLabel.equals("About")) {
			openWindow("About.fxml");
		}
	}
	
	//closes the current pop up window
	public void closeCurrentWindow(final ActionEvent evt) {
	    final Node source = (Node) evt.getSource();
	    final Stage stage = (Stage) source.getScene().getWindow();
	    stage.close();
	}

	//opens a pop up window from a desired filename
	private void openWindow(String fileName) {
		try {
			// load the pop up you created
			Pane howTo = (Pane)FXMLLoader.load(getClass().getResource(fileName));
				
			// create a new scene
			Scene howToScene = new Scene(howTo,250,250);

			// add css to the new scene		
			howToScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			//create new stage to put scene in
			secondaryStage = new Stage();
			secondaryStage.setScene(howToScene);
			secondaryStage.setResizable(false);
			secondaryStage.showAndWait();
		} catch(Exception e) {
					e.printStackTrace();
		}
	}
	
	//calculate and execute the CPU move
	public void makeCPUMove(int level) {
		//initialize the variable that contains the move that is to be made
		int moveToMake = 0;
		//if level is 0 leave the function (let the other human make the next move)
		if(level == 0) return;
		//easy mode
		if(level == 1) {
			//find the number of blank spaces
			int numberOfBlankSpaces = 0;
			for(int i = 0; i < buttonList.size(); i++) {
				if(buttonList.get(i).getText().equals("")) {
					numberOfBlankSpaces++;
				}
			}
			//pick a random space out of the blank spaces
			moveToMake = (int)((numberOfBlankSpaces) * Math.random());
		}
		//hard mode
		else if(level == 2) {
			//refresh the imaginary game board
			refreshImaginaryGameboard();
			//decide the move to make using the minMax algorithm 
			moveToMake = minMax(6, true, -1, true);
		}
		//find the empty button that is 'moveToMake' over (ex. if move to make = 3, it would be the 3rd empty space)
		for(int i = 0; i < buttonList.size(); i++) {
			if(buttonList.get(i).getText().equals("")) {
				if(moveToMake == 0) {
					//place the playerTwo symbol at the moveToMake position
					buttonList.get(i).setText(playerTwoSymbol);
					//check if move is a win
					findWinsAndPotentialWins(i, true);
					//break out of the loop
					break;
				}
				moveToMake--;
			}
		}
		//set playerOne as next to move
		isPlayerOne = true;
	}
	
	//update the imaginary game board used for calculations to the current state of the real game board
	private void refreshImaginaryGameboard(){
		//set the imaginary game board to be identical to the text on the button list
		for(int i = 0; i < buttonList.size(); i++) {
			imaginaryGameboard[i] = buttonList.get(i).getText();
		}
	}
	
	//returns true if the given input would result in a win (the currentGameboard boolean dictates whether the winning combo will be highlighted)
	private boolean findWinsAndPotentialWins(int inputLocation, boolean currentGameboard){
		//steps needed is 2 (0,1,2) for 3 in a row
		int stepsNeeded = 2;
		boolean highlight = false;
		//if its currentGameboard refresh the game board and highlight if win is found
		if(currentGameboard){
			highlight = true;
			refreshImaginaryGameboard();
		}
		
		//check a line in every direction around the current move to check if its a win
		if(searchLine(inputLocation, stepsNeeded, 'r', highlight)) return true; //search the row that the input is in (left, right)
		if(searchLine(inputLocation, stepsNeeded, 'c', highlight)) return true; //search the column that the input is in (up, down)
		if(searchLine(inputLocation, stepsNeeded, 'd', highlight)) return true; //search the 1st diagonal that the input is in (upLeft, downRight)
		if(searchLine(inputLocation, stepsNeeded, 'e', highlight)) return true; //search the 2nd diagonal that the input is in (upRight, downLeft)
		
		return false;
	}
	
	//returns true if the line is all the same character(search direction: r = row, c = column, d = diagonal down right, e = diagonal down left)
	private boolean searchLine(int originalCell, int stepsLeft, char searchDirection, boolean highlightWin) {
		boolean winFound = false;
		//if the search line last to 0 than highlight because its a win
		if(stepsLeft == 0){
			//highlight if enabled
			if(highlightWin) {
				highlightButtonForWin(originalCell);
				//play victory sound
				victorySound.play();
			}
			return true;//return true for a win
		}
		//find the next cell that needs to be checked
		int nextCell = findNextCell(originalCell, searchDirection);
		
		if(nextCell == 9) return false; //9 is an error so return false
		//if the currentCell is equal to the next cell
		if(imaginaryGameboard[originalCell].equals(imaginaryGameboard[nextCell])) {
			//steps left is decreased by 1, and set the new original cell to the next cell, if true than win has been found
			winFound = searchLine(nextCell, stepsLeft-1, searchDirection, highlightWin);
		}
		if(winFound) {
			//if win is found highlight the button if enabled, and set winFound to true
			if(highlightWin) highlightButtonForWin(originalCell);
			winFound = true;
		}
		//return winFound (defaults to false unless set to true)
		return winFound;
	}
	
	//finds the next cell in a direction (same direction character code from searchLine)
	private int findNextCell(int originalCell, char searchDirection) {
		int newCell;
		if(searchDirection == 'r') { //row
			if((originalCell+1)%3 == 0) { //if in the rightmost column
				newCell = originalCell - 2; //move to leftmost row
			}
			else {
				newCell = originalCell + 1; //move right a row
			}
		}
		else if(searchDirection == 'c') { //column
			if(originalCell >= 6) { //if in bottom row
				newCell = originalCell - 6; //move to top row
			}
			else {
				newCell = originalCell + 3; //move down a row
			}
		}
		else if(searchDirection == 'd') { //diagonal 1 (down left)
			if(originalCell == 0 || originalCell == 4) {
				newCell = originalCell + 4;
			} 
			else if(originalCell == 8){//if end of diagonal set to beginning of diagonal
				newCell = 0;
			}
			else return 9; //if not on diagonal
		}
		else if(searchDirection == 'e') { //diagonal 2 (down right)
			if(originalCell == 2 || originalCell == 4) {
				newCell = originalCell + 2;
			} 
			else if(originalCell == 6){//if end of diagonal set to beginning of diagonal
				newCell = 2;
			}
			else return 9; //if not on diagonal
		}
		else { //error for debugging
			System.out.println("error, invalid searchDirection character inputted");
			return 9;
		}
		//return the determined newCell
		return newCell;
	}
	
	//highlight a selectedButton adding a new styleClass
	private void highlightButtonForWin(int buttonPosition) {
		//set winDetected to true
		winDetected = true;
		//add new Style class to the button to highlight it
		buttonList.get(buttonPosition).getStyleClass().add("winning-button");
	}
	
	//runs the minMax algorithm to look into the future and determine the best move
	private int minMax(int searchDepth, boolean maximizingPlayer, int currentMove, boolean parentMinMax) {
		//initialize variables that are needed for calculations
		int moveToMake = -1;
		int currentBest;
		int currentBoardScore;
		int numberOfBlanks = 0;
		//parent of minMax shouldn't check for win 
		if(!parentMinMax) {
				if(findWinsAndPotentialWins(currentMove, false)) {
					if(maximizingPlayer) {
						return -100;
					}
					else {
						return 100;
					}
				}
		}
		//if searchDepth is 0, return 0 because its a neutral game board
		if(searchDepth <= 0 ) {
			return 0;
		}
		//maximizing player selects the HIGHEST scoring future game board
		if(maximizingPlayer) {
			//set the current best to the lowest possible value so it will always be overwritten
			currentBest = Integer.MIN_VALUE;
			//loop through all board spots
			for(int currentSpace = 0; currentSpace <= 8; currentSpace++) {
				if(imaginaryGameboard[currentSpace].equals("")) {
					//for every blank space replace with the current simulated players symbol and rerun minMax to find new board score in the future
					imaginaryGameboard[currentSpace] = playerTwoSymbol;
					currentBoardScore = minMax(searchDepth-1, false, currentSpace, false);
					//replace changed place with blank so the program can loop and try another possible move
					imaginaryGameboard[currentSpace] = "";
					//causes the board score to get closer to 0 (makes AI prioritize quicker wins)
					if(currentBoardScore > 0) {
						currentBoardScore--;
					}
					else if(currentBoardScore < 0) {
						currentBoardScore++;
					}
					//replace currentBest if currentBoardScore is higher
					if(currentBoardScore > currentBest) {
						currentBest = currentBoardScore;
						//set move To make to the current number of the blank
						moveToMake = numberOfBlanks;
					}
					numberOfBlanks++;
				}
			}
		}
		//minimizing player selects the LOWEST scoring future game board (works exact same as above but prioritizes low instead of high)
		else {
			currentBest = Integer.MAX_VALUE;
			for(int currentSpace = 0; currentSpace <= 8; currentSpace++) {
				if(imaginaryGameboard[currentSpace].equals("")) {
					imaginaryGameboard[currentSpace] = playerOneSymbol;
					currentBoardScore = minMax(searchDepth-1, true, currentSpace, false);
					imaginaryGameboard[currentSpace] = "";
					if(currentBoardScore > 0) {
						currentBoardScore--;
					}
					else if(currentBoardScore < 0) {
						currentBoardScore++;
					}
					
					if(currentBoardScore < currentBest) {
						currentBest = currentBoardScore;
						moveToMake = numberOfBlanks;
					}
					numberOfBlanks++;
				}
			}
		}
		//if there are no more blanks (board is full) and no win is detected, its a neutral game board so return 0
		if(numberOfBlanks == 0) {
			return 0;
		}
		//if its parentMinMax return the moveToMake rather than the current best score
		else if(parentMinMax) {
			System.out.println("bestScore" + currentBest);
			return moveToMake;
		}
		//otherwise return the currentBestScore
		else {
			return currentBest;
		}
	}
	
}
