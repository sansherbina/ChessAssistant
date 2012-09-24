package game_server.board.figures;

import util.Constants;
import game_server.board.Board;
import game_server.board.MovementTranslator;

import java.util.HashSet;
import java.util.Set;


public class KingFigure extends Figure {
	private boolean hasAlreadyMoved = false;
	
	@Override
	public String getFigureConstant() {
		return Constants.KING;
	}

	@Override
	public boolean doMovement(Board board, String movement,
			String previousMovement, boolean checkForKingCheck) {
		MovementTranslator movementTranslator = new MovementTranslator(movement);
		if(movementTranslator.initialEqualsTarget())
			return false;
		int rowsDelta = movementTranslator.getTargetRow() - movementTranslator.getInitialRow();
		int columnsDelta = movementTranslator.getTargetColumn() - movementTranslator.getInitialColumn();
		String castlingRookMovement = "";
		if(Math.abs(rowsDelta) > 1 || Math.abs(columnsDelta) > 1) {
			castlingRookMovement = getCastlingRookMovement(board, movement, previousMovement);
			if(castlingRookMovement.isEmpty())
				return false;
		}
		if(board.getFigureAt(movementTranslator.getTargetPosition()).getFigureColor().equals(color))
			return false;
		if(checkForKingCheck) {
			Board boardCopy = new Board(board);
			boardCopy.changeFigurePositionByMovement(movement);
			if(! castlingRookMovement.isEmpty())
				boardCopy.changeFigurePositionByMovement(castlingRookMovement);
			if(boardCopy.isKingInCheck(getFigureColor(), movement, false))
				return false;
			board.changeFigurePositionByMovement(movement);
			board.changeFigurePositionByMovement(castlingRookMovement);
			hasAlreadyMoved = true;
		}
		return true;
	}
	
	@Override
	public Set<String> getAvailableTargetPositions(Board board, String currentPosition, String previousMovement) {
		Set <String> positions = MovementTranslator.getAllPositions(currentPosition, 
				new int [] {-1, 0, 1}, new int [] {-1, 0, 1}, true, true);
		boolean isWhiteColor = color.equals(Constants.WHITE_COLOR) ? true : false;
		int currentRow = MovementTranslator.getRow(currentPosition);
		boolean canBeCastlingDone = isWhiteColor ? currentRow == 7 : currentRow == 0;
		if(canBeCastlingDone) {
			positions.add(isWhiteColor ? "g1": "g8");
			positions.add(isWhiteColor ? "c1" : "c8");
		}
		HashSet <String> result = new HashSet <String> ();
		for(String targetPosition: positions) {
			String movement = currentPosition + targetPosition;
			if(doMovement(new Board(board), movement, previousMovement, true)) {
				result.add(targetPosition);
				hasAlreadyMoved = false;
			}
		}
		return result;
	}

	private String getCastlingRookMovement(Board board, String movement, String previousMovement) {
		MovementTranslator movementTranslator = new MovementTranslator(movement);
		boolean isWhiteColor = getFigureColor().equals(Constants.WHITE_COLOR);
		if(! movementTranslator.isOnTheSameHorizontal() || (isWhiteColor && movementTranslator.getTargetRow() != 7 ||
				(! isWhiteColor && movementTranslator.getTargetRow() != 0)))
			return "";
		int rookColumn = movementTranslator.getTargetColumn() > movementTranslator.getInitialColumn() ? 7 : 0;
		String rookPosition = isWhiteColor ? MovementTranslator.getPosition(7, rookColumn) :
			MovementTranslator.getPosition(0, rookColumn);
		Figure rookPositionFigure = board.getFigureAt(rookPosition);
		if(! rookPositionFigure.getFigureConstant().equals(Constants.ROOK))
			return "";
		if(((RookFigure)rookPositionFigure).hasAlreadyMoved() || this.hasAlreadyMoved)
			return "";
		if(Math.abs(movementTranslator.getTargetColumn() - movementTranslator.getInitialColumn()) != 2)
			return "";
		MovementTranslator castlingMovement = new MovementTranslator(movementTranslator.getInitialPosition() + rookPosition);
		if(castlingMovement.areAnyFiguresBetweenPositionsByHorizontal(board))
			return "";
		int neighbourFieldColumn = movementTranslator.getInitialColumn() > movementTranslator.getTargetColumn() ?
				3 : 5;
		int [] columnsToCheck = {4, neighbourFieldColumn};
		for(int i = 0; i < columnsToCheck.length; i++) {
			Board copyBoard = new Board(board);
			String position = MovementTranslator.getPosition(movementTranslator.getInitialRow(), columnsToCheck[i]);
			copyBoard.changeFigurePositionByMovement(board.getKingPosition(getFigureColor()) + position);
			if(copyBoard.isKingInCheck(getFigureColor(), previousMovement, false))
				return "";
		}
		return rookPosition + MovementTranslator.getPosition(movementTranslator.getInitialRow(), 
				rookColumn == 7 ? 5 : 3);
	}
	
	public boolean hasAlreadyMoved() {
		return hasAlreadyMoved;
	}
	
	public void setAlreadyMoved(boolean hasAlreadyMoved) {
		this.hasAlreadyMoved = hasAlreadyMoved;
	}
}
