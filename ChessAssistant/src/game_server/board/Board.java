package game_server.board;

import util.Constants;
import game_server.board.figures.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import util.ErrorsList;

public class Board {	
	private FiguresSet whiteFiguresSet = new FiguresSet();
	private FiguresSet blackFiguresSet = new FiguresSet();
	private Figure [][] board = new Figure [8][8];
	private int movementsCounter = 0;
	private String whiteKingPosition = "e1";
	private String blackKingPosition = "e8";
	private int lastPawnMovingStepNumber = 0;
	private int lastFigureHitingStepNumber = 0;
	private String gameState = Constants.NONE_STATE;
	private String currentTurnColor = Constants.WHITE_COLOR;
	private String previousMovement = "";
	
	public Board() {
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++)
				board[i][j] = new NoneFigure();
		setPawnsAsInitial();
		setRooksAsInitial();
		setKnightsAsInitial();
		setBishopsAsInitial();
		setQueensAsInitial();
		setKingsAsInitial();
		for(int i = 0; i < 2; i++)
			for(int j = 0; j < 8; j++) {
				board[i][j].setColor(Constants.BLACK_COLOR);
				board[7 - i][j].setColor(Constants.WHITE_COLOR);
			}
	}
	
	public Board(Board board) {
		whiteFiguresSet = new FiguresSet(board.whiteFiguresSet);
		blackFiguresSet = new FiguresSet(board.blackFiguresSet);
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++)
				this.board[i][j] = FiguresBuilder.buildFigure(board.board[i][j]);
		lastFigureHitingStepNumber = board.lastFigureHitingStepNumber;
		lastPawnMovingStepNumber = board.lastPawnMovingStepNumber;
		movementsCounter = board.movementsCounter;
		whiteKingPosition = board.whiteKingPosition;
		blackKingPosition = board.blackKingPosition;
		gameState = board.gameState;
		currentTurnColor = board.currentTurnColor;
		previousMovement = board.previousMovement;
	}

	public Figure getFigureAt(String position) {
		int row = MovementTranslator.getRow(position), 
				column = MovementTranslator.getColumn(position);
		return board[row][column];
	}
	
	public void setKingPosition(String color, String position) {
		if(color.equals(Constants.WHITE_COLOR))
			whiteKingPosition = position;
		if(color.equals(Constants.BLACK_COLOR))
			blackKingPosition = position;
	}
	
	public MovementResult makeMovement(String movement) {
		movement = movement.toLowerCase();
		previousMovement = previousMovement.toLowerCase();
		MovementTranslator translator = new MovementTranslator(movement);
		Figure figure = board[translator.getInitialRow()][translator.getInitialColumn()];
		MovementResult wrongMoveResult = new MovementResult(false, Constants.NONE_STATE, ErrorsList.getStatusCode(ErrorsList.WRONG_MOVE_ERROR), "", "", "");
		if(! figure.getFigureColor().equals(currentTurnColor))
			return new MovementResult(false, Constants.NONE_STATE, ErrorsList.getStatusCode(ErrorsList.WRONG_MOVE_ERROR), "", "", "");
		if(figure.getFigureConstant().equals(Constants.NONE_FIGURE))
			return wrongMoveResult;
		if(! figure.doMovement(this, movement, previousMovement, true))
			return wrongMoveResult;
		movementsCounter++;
		gameState = processNewGameState(movement);
		MovementResult result = new MovementResult(true, gameState, ErrorsList.getStatusCode(ErrorsList.OK), 
				getAvailableTargetPositionsInStringFormatFor(currentTurnColor.equals(Constants.WHITE_COLOR) ?
					Constants.BLACK_COLOR : Constants.WHITE_COLOR), getBoardConfigurationInStringFormat(), "");
		currentTurnColor = currentTurnColor.equals(Constants.WHITE_COLOR) ?
				Constants.BLACK_COLOR : Constants.WHITE_COLOR;
		result.setCurrentTurnColor(currentTurnColor);
		previousMovement = movement;
		return result;
	}
	
	public boolean canGameBeContinued() {
		return gameState.equals(Constants.NONE_STATE) || gameState.equals(Constants.WHITE_KING_IN_CHECK_STATE) ||
				gameState.equals(Constants.BLACK_KING_IN_CHECK_STATE);
	}
	
	public Figure [][] getBoard() {
		return board;
	}
	
	public String getKingPosition(String color) {
		return color.equals(Constants.WHITE_COLOR) ? whiteKingPosition : blackKingPosition;
	}
	
	public FiguresSet getWhiteFiguresSet() {
		return whiteFiguresSet;
	}
	
	public FiguresSet getBlackFiguresSet() {
		return blackFiguresSet;
	}
	
	public String getCurrentTurnColor() {
		return currentTurnColor;
	}
	
	@Override
	public String toString() {
		String result = "";
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				String constant = board[i][j].getFigureConstant();
				if(constant.equals(Constants.NONE_FIGURE)) {
					result += (i + j) % 2 == 1 ? "*" : "_";
					continue;
				}
				if(constant.equals(Constants.ROOK)) {
					constant = ((RookFigure)board[i][j]).hasAlreadyMoved() ? "M" : "R";
				}
				else if(constant.equals(Constants.KING)) { 
					constant = ((KingFigure)board[i][j]).hasAlreadyMoved() ? "M" : "K";
				}
				if(board[i][j].getFigureColor().equals(Constants.WHITE_COLOR)) {
					result += board[i][j].getFigureConstant().toUpperCase();
				}
				else {
					result += board[i][j].getFigureConstant().toLowerCase();
				}
			}
			result += "\n";
		}
		result += "White set: " + whiteFiguresSet;
		result += "Black set: " + blackFiguresSet;
		result += "White king: " + whiteKingPosition + "\n";
		result += "Black king: " + blackKingPosition + "\n";
		result += "Movements: " + movementsCounter + "\n";
		return result + "\n";
	}
	
	public void removeFigureIfPossible(String position) {
		if(position.isEmpty())
			return;
		int row = MovementTranslator.getRow(position), column = MovementTranslator.getColumn(position);
		String figureConstant = board[row][column].getFigureConstant();
		if(figureConstant.equals(Constants.NONE_FIGURE))
			return;
		FiguresSet targetSetRef = board[row][column].getFigureColor().equals(Constants.WHITE_COLOR) ?
				whiteFiguresSet : blackFiguresSet;
		board[row][column] = new NoneFigure();
		targetSetRef.decreaseFigureCount(figureConstant);
	}
	
	public void addFigure(String position, Figure figure) {
		if(position.isEmpty())
			return;
		int row = MovementTranslator.getRow(position), column = MovementTranslator.getColumn(position);
		String figureConstant = figure.getFigureConstant();
		if(figureConstant.equals(Constants.NONE_FIGURE))
			return;
		FiguresSet targetSetRef = board[row][column].getFigureColor().equals(Constants.WHITE_COLOR) ?
				whiteFiguresSet : blackFiguresSet;
		board[row][column] = figure;
		targetSetRef.increaseFigureCount(figureConstant);
	}
	
	public void changeFigurePositionByMovement(String movement) {
		if(movement.isEmpty())
			return;
		MovementTranslator movementTranslator = new MovementTranslator(movement);
		hitFigureIfPossible(movementTranslator.getTargetPosition());
		Figure movingFigure = board[movementTranslator.getInitialRow()][movementTranslator.getInitialColumn()];
		board[movementTranslator.getTargetRow()][movementTranslator.getTargetColumn()] = movingFigure;
		board[movementTranslator.getInitialRow()][movementTranslator.getInitialColumn()] = new NoneFigure();
		if(movingFigure.getFigureConstant().equals(Constants.KING))
			setKingPosition(movingFigure.getFigureColor(), movementTranslator.getTargetPosition());
		if(movingFigure.getFigureConstant().equals(Constants.PAWN))
			lastPawnMovingStepNumber = movementsCounter;
	}
	
	public boolean isKingInCheck(String kingColor, String previousMovement, boolean makeCopy) {
		String oponentColor = kingColor.equals(Constants.WHITE_COLOR) ? Constants.BLACK_COLOR : 
			Constants.WHITE_COLOR;
		String kingPosition = getKingPosition(kingColor);
		Board targetBoard = makeCopy ? new Board(this) : this;
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++) {
				Figure figure = targetBoard.board[i][j];
				if(! figure.getFigureColor().equals(oponentColor))
					continue;
				String movement = MovementTranslator.getPosition(i, j);
				movement += kingPosition;
				if(figure.doMovement(targetBoard, movement, previousMovement, false))
					return true;
			}
		return false;
	}
	
	public void hitFigureIfPossible(String position) {
		if(position.isEmpty())
			return;
		removeFigureIfPossible(position);
		lastFigureHitingStepNumber = movementsCounter;
	}
	
	public HashMap <String, Set <String>> getAvailableTargetPositionsFor(String color) {
		HashMap <String, Set <String>> result = new HashMap <String, Set<String>>();
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++)
				if(board[i][j].getFigureColor().equals(color)) {
					String position = MovementTranslator.getPosition(i, j);
					result.put(position, board[i][j].getAvailableTargetPositions(this, position, previousMovement));
				}
		return result;
	}
	
	public String getAvailableTargetPositionsInStringFormatFor(String color) {
		HashMap <String, Set <String>> targetPositionsMap = getAvailableTargetPositionsFor(color);
		StringBuilder stringBuilder = new StringBuilder(100 * targetPositionsMap.size());
		Set <String> initialPositionsSet = targetPositionsMap.keySet();
		for (String initialPosition : initialPositionsSet) {
			if(targetPositionsMap.get(initialPosition).size() == 0)
				continue;
			if(stringBuilder.length() != 0)
				stringBuilder.append(";");
			stringBuilder.append(initialPosition + ":");
			Set <String> targetPositionsSet = targetPositionsMap.get(initialPosition);
			boolean innerLoopFirstIteration = true;
			for (String targetPosition : targetPositionsSet) {
				if(! innerLoopFirstIteration)
					stringBuilder.append(",");
				stringBuilder.append(targetPosition);
				innerLoopFirstIteration = false;
			}
		}
		return stringBuilder.toString();
	}
	
	public String getBoardConfigurationInStringFormat() {
		StringBuilder resultStringBuilder = new StringBuilder(64);
		for(int i = 7; i >= 0; i--)
			for(int j = 0; j < 8; j++) {
				Figure currentPositionFigure = board[i][j];
				String figureConstant = currentPositionFigure.getFigureConstant();
				if(figureConstant.equals(Constants.NONE_FIGURE))
					resultStringBuilder.append("_");
				else if(currentPositionFigure.getFigureColor().equals(Constants.WHITE_COLOR))
					resultStringBuilder.append(figureConstant.toUpperCase());
				else if(currentPositionFigure.getFigureColor().equals(Constants.BLACK_COLOR))
					resultStringBuilder.append(figureConstant.toLowerCase());
			}
		return resultStringBuilder.toString();
	}
	
	public String getGameState() {
		return gameState;
	}
	
	public int getMovementsCounter() {
		return movementsCounter;
	}
	
	private String processNewGameState(String previousMovement) {
		String gameState = Constants.NONE_STATE;
		gameState = checkForDrawn();
		if(! gameState.equals(Constants.NONE_STATE))
			return gameState;
		gameState = checkForCheckMateOrStaleMate(previousMovement);
		if(! gameState.equals(Constants.NONE_STATE))
			return gameState;
		return Constants.NONE_STATE;
	}
	
	private String checkForDrawn() {
		if(movementsCounter - lastFigureHitingStepNumber > Constants.STEP_COUNT_WHEN_DRAW 
				&& movementsCounter-lastPawnMovingStepNumber > Constants.STEP_COUNT_WHEN_DRAW)
			return Constants.DRAW_50_MOVES_RULE_STATE;
		else if(whiteFiguresSet.getFiguresCount() >= 3 || blackFiguresSet.getFiguresCount() >= 3)
			return Constants.NONE_STATE;
		else if(whiteFiguresSet.getFigureCount(Constants.QUEEN) > 0   ||
				whiteFiguresSet.getFigureCount(Constants.ROOK) > 0 ||
				whiteFiguresSet.getFigureCount(Constants.PAWN) > 0 ||
				blackFiguresSet.getFigureCount(Constants.QUEEN) > 0||
				blackFiguresSet.getFigureCount(Constants.ROOK) > 0 ||
				blackFiguresSet.getFigureCount(Constants.PAWN) > 0){
			return Constants.NONE_STATE;
		}
		else if(whiteFiguresSet.getFiguresCount() <= 2 && blackFiguresSet.getFiguresCount() <= 2 )
			return Constants.NO_SUFFICIENT_MATERIAL_STATE;
		else if(whiteFiguresSet.getFiguresCount()<=2)
			return Constants.NO_SUFFICIENT_WHITE_MATERIAL_STATE;
		else if(whiteFiguresSet.getFiguresCount()<=2)
			return Constants.NO_SUFFICIENT_BLACK_MATERIAL_STATE;
		return Constants.NONE_STATE;
	}
	
	private String checkForCheckMateOrStaleMate(String previousMovement) {
		MovementTranslator translator = new MovementTranslator(previousMovement);
		String previousMovementColor = getFigureAt(translator.getTargetPosition()).getFigureColor();
		String nextMovementColor = previousMovementColor.equals(Constants.WHITE_COLOR) ? 
				Constants.BLACK_COLOR : Constants.WHITE_COLOR;
		boolean isNextMovementWhite = nextMovementColor.equals(Constants.WHITE_COLOR);
		boolean isNextKingInCheck = isKingInCheck(nextMovementColor, previousMovement, true);
		if(isNextKingInCheck) {
			if(checkForMateOnly(nextMovementColor, previousMovement))
				return isNextMovementWhite ? Constants.MATE_BLACK_WIN_STATE : Constants.MATE_WHITE_WIN_STATE;
			else if(isNextMovementWhite)
				return Constants.WHITE_KING_IN_CHECK_STATE;
			else if(! isNextMovementWhite) 
				return Constants.BLACK_KING_IN_CHECK_STATE;
		}
		else
			if(checkForStaleMateOnly(nextMovementColor, previousMovement))
				return isNextMovementWhite ? Constants.STALEMATE_WHITE_NO_MOVES_STATE :
					Constants.STALEMATE_BLACK_NO_MOVES_STATE;
		return Constants.NONE_STATE;
	}
	
	private boolean checkForMateOnly(String kingColor, String previousMovement) {
		String kingPosition = getKingPosition(kingColor);
		boolean canKingBeMoved = ! getFigureAt(kingPosition).getAvailableTargetPositions(this, kingPosition, 
				previousMovement).isEmpty();
		if(canKingBeMoved)
			return false;
		ArrayList <String> kingHittingFigurePositions = new ArrayList <String> ();
		String opponentColor = kingColor.equals(Constants.WHITE_COLOR) ? Constants.BLACK_COLOR : 
			Constants.WHITE_COLOR;
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++)
				if(! board[i][j].getFigureColor().equals(opponentColor))
					continue;
				else {
					String position = MovementTranslator.getPosition(i, j);
					if(board[i][j].doMovement(this, position + kingPosition, previousMovement, false))
						kingHittingFigurePositions.add(position);
				}
		if(kingHittingFigurePositions.isEmpty())
			return false;
		else if(! canKingBeMoved && kingHittingFigurePositions.size() >= 2)
			return true;
		Set <String> coveredPositionsSet = new HashSet <String>();
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++) {
				if(! board[i][j].getFigureColor().equals(kingColor))
					continue;
				String position = MovementTranslator.getPosition(i, j);
				coveredPositionsSet.addAll(board[i][j].getAvailableTargetPositions(this, 
						position, previousMovement));
			}
		String theOnlyHittingFigurePosition = kingHittingFigurePositions.get(0);
		if(coveredPositionsSet.contains(theOnlyHittingFigurePosition))
			return false;
		MovementTranslator translator = new MovementTranslator(theOnlyHittingFigurePosition + kingPosition);
		Set <String> intermediatePositions = new HashSet <String> ();
		intermediatePositions.addAll(translator.getIntermediatePositionsByHorizontal());
		intermediatePositions.addAll(translator.getIntermediatePositionsByVertical());
		intermediatePositions.addAll(translator.getIntermediatePositionsByDiagonal());
		intermediatePositions.retainAll(coveredPositionsSet);
		return intermediatePositions.isEmpty();
	}
	
	private boolean checkForStaleMateOnly(String kingColor, String previousMovement) {
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++)
				if(board[i][j].getFigureColor().equals(kingColor) && 
						! board[i][j].getAvailableTargetPositions(this, MovementTranslator.getPosition(i, j), previousMovement).isEmpty())
					return false;
		return true;
	}
	
	private void setPawnsAsInitial() {
		for(int j = 0; j < 8; j++) {
			board[1][j] = new PawnFigure();
			board[6][j] = new PawnFigure();
		}
	}
	
	private void setRooksAsInitial() {
		board[0][7] = new RookFigure(); 
		board[0][0] = new RookFigure();
		board[7][7] = new RookFigure();
		board[7][0] = new RookFigure();
	}
	
	private void setKnightsAsInitial() {
		board[0][6] = new KnightFigure();
		board[0][1] = new KnightFigure();
		board[7][6] = new KnightFigure();
		board[7][1] = new KnightFigure();
	}
	
	private void setBishopsAsInitial() {
		board[0][5] = new BishopFigure();
		board[0][2] = new BishopFigure();
		board[7][5] = new BishopFigure();
		board[7][2] = new BishopFigure();
	}
	
	private void setQueensAsInitial() {
		board[0][3] = new QueenFigure();
		board[7][3] = new QueenFigure();
	}
	
	private void setKingsAsInitial() {
		board[0][4] = new KingFigure();
		board[7][4] = new KingFigure();
	}
}
	