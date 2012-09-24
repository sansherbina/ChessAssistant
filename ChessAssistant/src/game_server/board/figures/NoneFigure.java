package game_server.board.figures;

import util.Constants;
import game_server.board.Board;

import java.util.HashSet;
import java.util.Set;

public class NoneFigure extends Figure{

	@Override
	public String getFigureConstant() {
		return Constants.NONE_FIGURE;
	}

	@Override
	public boolean doMovement(Board board, String movement,
			String previousMovement, boolean checkForKingCheck) {
		return false;
	}

	@Override
	public Set<String> getAvailableTargetPositions(Board board,
			String currentPosition, String previousMovement) {
		return new HashSet <String> ();
	}
}
