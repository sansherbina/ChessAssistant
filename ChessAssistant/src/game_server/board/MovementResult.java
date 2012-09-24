package game_server.board;

import util.Constants;

public class MovementResult{
	/**
	 * Game state like check, mate, salemate and other. All defined in Constants class
	 */
	private String gameState = Constants.NONE_STATE;
	/**
	 * Current board position in speccial format
	 * Each field is one symbol. Symbol for each figure defined in Constants.
	 * Delimiter between horizontals '_' 
	 */
	private String boardConfiguration = "";
	/**
	 * Current color side
	 */
	private String currentTurnColor = Constants.NONE_COLOR;
	/**
	 * Available moments in position
	 */
	private String availableMovements = "";
	/**
	 * Is last momet correct
	 */
	private boolean isSucced;
	/**
	 * Status code - contants defined in file ErrorList
	 */
	private String statusCode;
	
	public MovementResult(boolean isSucceed, String gameState, String statusCode, 
			String availableMovements, String boardConfiguration, String currentTurnColor) {
		this.isSucced = isSucceed;
		this.gameState = gameState;
		this.statusCode = statusCode;
		this.availableMovements = availableMovements;
		this.boardConfiguration = boardConfiguration;
		this.currentTurnColor = currentTurnColor;
	}
	
	public String getGameState() {
		return gameState;
	}
	public void setGameState(String gameState) {
		this.gameState = gameState;
	}

	public String getAvailableMovements() {
		return availableMovements;
	}

	public void setAvailableMovements(String availableMovements) {
		this.availableMovements = availableMovements;
	}

	public String getBoardConfiguration() {
		return boardConfiguration;
	}

	public void setBoardConfiguration(String boardConfiguration) {
		this.boardConfiguration = boardConfiguration;
	}

	public String getCurrentTurnColor() {
		return currentTurnColor;
	}

	public void setCurrentTurnColor(String currentTurnColor) {
		this.currentTurnColor = currentTurnColor;
	}

	public boolean isSucced() {
		return isSucced;
	}

	public void setSucced(boolean isSucced) {
		this.isSucced = isSucced;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}
