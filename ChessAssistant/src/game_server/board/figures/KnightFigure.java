package game_server.board.figures;

import util.Constants;
import game_server.board.Board;
import game_server.board.MovementTranslator;

import java.util.HashSet;
import java.util.Set;

public class KnightFigure extends Figure {
	@Override
	public String getFigureConstant() {
		return Constants.KNIGHT;
	}

	@Override
	public boolean doMovement(Board board, String movement,
			String previousMovement, boolean checkForKingCheck) {
		MovementTranslator movementTranslator = new MovementTranslator(movement);
		if(movementTranslator.initialEqualsTarget())
			return false;
		int rowsDelta = Math.abs(movementTranslator.getTargetRow() - movementTranslator.getInitialRow());
		int columnsDelta = Math.abs(movementTranslator.getTargetColumn() - movementTranslator.getInitialColumn());
		if(columnsDelta < 1 || columnsDelta > 2 || rowsDelta < 1 || rowsDelta > 2)
			return false;
		if(Math.abs(columnsDelta - rowsDelta) != 1)
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
	public Set <String> getAvailableTargetPositions(Board board, String currentPosition, String previousMovement) {
		Set <String> positions = MovementTranslator.getAllPositions(currentPosition,
				new int [] {1, 1, -1, -1, 2, 2, -2, -2}, new int [] {-2, 2, -2, 2, -1, 1, -1, 1}, 
				false, true);
		HashSet <String> result = new HashSet <String> ();
		for(String targetPosition: positions) {
			String movement = currentPosition + targetPosition;
			if(doMovement(new Board(board), movement, previousMovement, true))
				result.add(targetPosition);
		}
		return result;
	}
}
