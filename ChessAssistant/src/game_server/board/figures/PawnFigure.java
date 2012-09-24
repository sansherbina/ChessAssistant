package game_server.board.figures;

import util.Constants;
import game_server.board.Board;
import game_server.board.MovementTranslator;

import java.util.HashSet;
import java.util.Set;



public class PawnFigure extends Figure{
	@Override
	public String getFigureConstant() {
		return Constants.PAWN;
	}

	@Override
	public boolean doMovement(Board board, String movement,
			String previousMovement, boolean checkForKingCheck) {
		MovementTranslator movementTranslator = new MovementTranslator(movement);
		String hitPosition = "";
		if(movementTranslator.initialEqualsTarget())
			return false;
		else if(movementTranslator.isOnTheSameVertical()) {
			if(! canBeVerticalMovementDone(board, movement))
				return false;
		}
		else if(movementTranslator.isOnTheSameDiagonal()) {
			hitPosition = canBeDiagonalMovementDone(board, movement, previousMovement); 
			if(hitPosition.isEmpty())
				return false;
		}
		else
			return false;	
		if(checkForKingCheck) {
			Board boardCopy = new Board(board);
			boardCopy.changeFigurePositionByMovement(movement);
			if(boardCopy.isKingInCheck(getFigureColor(), movement, false))
				return false;
			board.hitFigureIfPossible(hitPosition);
			board.changeFigurePositionByMovement(movement);
			boolean isWhiteColor =  getFigureColor().equals(Constants.WHITE_COLOR);
			boolean isOnOppositeRow = isWhiteColor ? (movementTranslator.getTargetRow() == 0) :
				(movementTranslator.getTargetRow() == 7);
			if(isOnOppositeRow) {
				if(movement.length() > 4) {
					String substituteFigureConstant = movement.substring(4, 5);
					board.removeFigureIfPossible(movementTranslator.getTargetPosition());
					Figure substituteFigure = FiguresBuilder.buildDefaultFigure(substituteFigureConstant);
					substituteFigure.color = this.color;
					board.addFigure(movementTranslator.getTargetPosition(), substituteFigure);
				}
			}
		}	
		return true;
	}
	
	@Override
	public Set <String> getAvailableTargetPositions(Board board, String currentPosition, String previousMovement) {
		Set <String> positions = MovementTranslator.getAllPositions(currentPosition, 
				new int [] {-1, 0, 1}, new int [] {-1, 0, 1}, true, true);
		positions.addAll(MovementTranslator.getAllPositions(currentPosition, new int [] {2, -2}, 
				new int [] {0, 0}, false, true));
		HashSet <String> result = new HashSet <String> ();
		for(String targetPosition: positions) {
			String movement = currentPosition + targetPosition;
			if(doMovement(new Board(board), movement, previousMovement, true))
				result.add(targetPosition);
		}
		return result;
	}

	private boolean canBeVerticalMovementDone(Board board, String movement) {
		MovementTranslator movementTranslator = new MovementTranslator(movement);
		int rowsDelta = movementTranslator.getTargetRow() - movementTranslator.getInitialRow();
		if(Math.abs(rowsDelta) > 2)
			return false;
		boolean isWhiteColor = getFigureColor().equals(Constants.WHITE_COLOR);
		if((isWhiteColor && rowsDelta >= 0) || (! isWhiteColor && rowsDelta <= 0))
			return false;
		if(Math.abs(rowsDelta) == 2)
			if((isWhiteColor && movementTranslator.getInitialRow() != 6) || 
					(! isWhiteColor && movementTranslator.getInitialRow() != 1))
				return false;
		int rowsIncrement = movementTranslator.getInitialRow() < movementTranslator.getTargetRow() ? 1 : -1;
		int currentRow = movementTranslator.getInitialRow();
		do {
			currentRow += rowsIncrement;
			if(! board.getBoard()[currentRow][movementTranslator.getInitialColumn()].
					getFigureConstant().equals(Constants.NONE_FIGURE))
				return false;
		} while (currentRow != movementTranslator.getTargetRow());
		return true;
	}
	
	private String canBeDiagonalMovementDone(Board board, String movement, String previousMovement) {
		MovementTranslator currentTranslator = new MovementTranslator(movement);
		int rowsDelta = currentTranslator.getTargetRow() - currentTranslator.getInitialRow();
		int columnsDelta = currentTranslator.getTargetColumn() - currentTranslator.getInitialColumn();
		boolean isWhiteColor = getFigureColor().equals(Constants.WHITE_COLOR);
		if((isWhiteColor && rowsDelta >= 0) || (! isWhiteColor && rowsDelta <= 0))
			return "";
		if(Math.abs(rowsDelta) > 1 || Math.abs(columnsDelta) > 1)
			return "";
		Figure targetPositionFigure = board.getFigureAt(currentTranslator.getTargetPosition());
		if(! targetPositionFigure.getFigureConstant().equals(Constants.NONE_FIGURE)) { 
			if(! targetPositionFigure.getFigureColor().equals(this.getFigureColor()))
				return currentTranslator.getTargetPosition();
			return "";
		}
		else {
			if(previousMovement.isEmpty())
				return "";
			MovementTranslator previousTranslator = new MovementTranslator(previousMovement);
			Figure previousFigure = board.getFigureAt(previousTranslator.getTargetPosition());			
			if(! previousFigure.getFigureConstant().equals(Constants.PAWN))
				return "";
			boolean isPreviousWhite = previousFigure.getFigureColor().equals(Constants.WHITE_COLOR);
			if((isPreviousWhite && previousTranslator.getInitialRow() != 6) ||
					(! isPreviousWhite && previousTranslator.getInitialRow() != 1))
				return "";
			if(Math.abs(previousTranslator.getTargetRow() - previousTranslator.getInitialRow()) != 2)
				return "";
			if(previousTranslator.getTargetColumn() == currentTranslator.getTargetColumn() &&
					currentTranslator.getTargetRow() == 
						(previousTranslator.getInitialRow() + previousTranslator.getTargetRow()) / 2)
				return previousFigure.getFigureColor().equals(this.color) ? "" : previousTranslator.getTargetPosition();
			return "";
		}
	}
}
