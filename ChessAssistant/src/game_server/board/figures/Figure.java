package game_server.board.figures;

import util.Constants;
import game_server.board.Board;
import java.util.Set;



public abstract class Figure {
	protected String color = Constants.NONE_COLOR;
	
	public String getFigureColor() {
		return color;
	}
	
	public void setColor(String color) {
		if(color.equals(Constants.WHITE_COLOR) || 
				color.equals(Constants.BLACK_COLOR) || color.equals(Constants.NONE_COLOR))
			this.color = color;
	}
	
	public abstract String getFigureConstant();
	public abstract boolean doMovement(Board board, String movement, String previousMovement,
			boolean checkForKingCheck);
	public abstract Set <String> getAvailableTargetPositions(Board board, String currentPosition, 
			String previousMovement);
}
