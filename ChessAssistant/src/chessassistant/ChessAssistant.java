package chessassistant;
import util.Constants;
import util.ErrorsList;
import game_server.board.*;
public class ChessAssistant {
	
	public static MovementResult analizePosition(String move, String gameMoves){
		String[] game=null;
		if(gameMoves!=null && gameMoves.length()!=0){
			game=gameMoves.split("\\s");
		}
		MovementResult momentResult=null;
		Board board=new Board();
		if(game!=null){
			for(int i=0;i<game.length;i++){
				momentResult=board.makeMovement(game[i]);
				if(!momentResult.isSucced() || momentResult.getGameState()==Constants.DRAW_3_STATES_REPETITION
						||momentResult.getGameState()==Constants.DRAW_50_MOVES_RULE_STATE ||
						||momentResult.getGameState()==Constants.DRAW_3_STATES_REPETITION){
					return momentResult;
				}
				
			}
		}
		if(move!=null && move.length()!=0){
			
		}
		return momentResult;
	}
}
