package game_server.board.figures;

import util.Constants;
import game_server.board.Board;
import game_server.board.MovementTranslator;

import java.util.HashSet;
import java.util.Set;



public class QueenFigure extends Figure {
	@Override
	public String getFigureConstant() {
		return Constants.QUEEN;
	}

	@Override
	public boolean doMovement(Board board, String movement,
			String previousMovement, boolean checkForKingCheck) {
		MovementTranslator movementTranslator = new MovementTranslator(movement);
		if(movementTranslator.initialEqualsTarget())
			return false;
		if(movementTranslator.isOnTheSameHorizontal() && movementTranslator.areAnyFiguresBetweenPositionsByHorizontal(board) ||
				movementTranslator.isOnTheSameVertical() &&	movementTranslator.areAnyFiguresBetweenPositionsByVertical(board) ||
				movementTranslator.isOnTheSameDiagonal() && movementTranslator.areAnyFiguresBetweenPositionsByDiagonal(board))
			return false;
		else if(! movementTranslator.isOnTheSameHorizontal() && ! movementTranslator.isOnTheSameVertical() &&
				! movementTranslator.isOnTheSameDiagonal())
			return false;
		if(board.getFigureAt(movementTranslator.getTargetPosition()).getFigureColor().equals(color))
			return false;
		if(checkForKingCheck) {
			Board boardCopy = new Board(board);
			boardCopy.changeFigurePositionByMovement(movement);
			if(boardCopy.isKingInCheck(getFigureColor(), movement, false))
				return false;
			board.changeFigurePositionByMovement(movement);
		}
		return true;
	}

	@Override
	public Set<String> getAvailableTargetPositions(Board board, String currentPosition, String previousMovement) {
		Set <String> positions = MovementTranslator.getAllPositions(currentPosition, 
				new int [] {-1, 0, 1}, new int [] {-1, 0, 1}, true, false);
		HashSet <String> result = new HashSet <String> ();
		for(String targetPosition: positions) {
			String movement = currentPosition + targetPosition;
			if(doMovement(new Board(board), movement, previousMovement, true))
				result.add(targetPosition);
		}
		return result;
	}

}
