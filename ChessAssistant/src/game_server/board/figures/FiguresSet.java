package game_server.board.figures;

import util.Constants;

import java.util.HashMap;
import java.util.Set;

public class FiguresSet {
	private HashMap <String, Integer> figuresHashMap = new HashMap<String, Integer> ();
	
	public FiguresSet() {
		figuresHashMap.put(Constants.PAWN, 8);
		figuresHashMap.put(Constants.BISHOP, 2);
		figuresHashMap.put(Constants.ROOK, 2);
		figuresHashMap.put(Constants.KNIGHT, 2);
		figuresHashMap.put(Constants.QUEEN, 1);
		figuresHashMap.put(Constants.KING, 1);
	}
	
	public FiguresSet(FiguresSet figuresSet) {
		figuresHashMap.put(Constants.PAWN, figuresSet.getFigureCount(Constants.PAWN));
		figuresHashMap.put(Constants.BISHOP, figuresSet.getFigureCount(Constants.BISHOP));
		figuresHashMap.put(Constants.ROOK, figuresSet.getFigureCount(Constants.ROOK));
		figuresHashMap.put(Constants.KNIGHT, figuresSet.getFigureCount(Constants.KNIGHT));
		figuresHashMap.put(Constants.QUEEN, figuresSet.getFigureCount(Constants.QUEEN));
		figuresHashMap.put(Constants.KING, figuresSet.getFigureCount(Constants.KING));
	}
	
	public void increaseFigureCount(String figureConstant) {
		Integer value = figuresHashMap.get(figureConstant);
		if(value != null)
			figuresHashMap.put(figureConstant, value + 1);
	}
	
	public void decreaseFigureCount(String figureConstant) {
		Integer value = figuresHashMap.get(figureConstant);
		if(value != null)
			figuresHashMap.put(figureConstant, value - 1);
	}
	
	public void setFigureCount(String figureConstant, int newCount) {
		Integer value = figuresHashMap.get(figureConstant);
		if(value != null)
			figuresHashMap.put(figureConstant, newCount);
	}
	
	public int getFigureCount(String figureConstant) {
		Integer value = figuresHashMap.get(figureConstant);
		return value == null ? 0 : value.intValue();
	}
	
	@Override
	public String toString() {
		String result = "";
		Set <String> keys = figuresHashMap.keySet();
		for (String constant : keys) 
			result += "{" + constant + " = " + figuresHashMap.get(constant) + "}";
		return result + "\n";
	}
	
	public int getFiguresCount(){
		return figuresHashMap.size();
	}
}
