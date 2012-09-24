package game_server.board;

import util.Constants;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MovementTranslator {
	private static final HashMap <Character, Integer> columnsHashMap = new HashMap<Character, Integer> ();
	static {
		columnsHashMap.put('a', 0); columnsHashMap.put('b', 1);
		columnsHashMap.put('c', 2); columnsHashMap.put('d', 3);
		columnsHashMap.put('e', 4); columnsHashMap.put('f', 5);
		columnsHashMap.put('g', 6); columnsHashMap.put('h', 7);
	}
	private static final Set <Character> columnsCharactersSet = columnsHashMap.keySet();
	
	private int initialRow = 0;
	private int initialColumn = 0;
	private int targetRow = 0;
	private int targetColumn = 0;
	private String targetPosition = "a1";
	private String initialPosition = "a1";
	
	public MovementTranslator(String movement) {
		initialPosition = movement.substring(0,  2).toLowerCase();
		targetPosition = movement.substring(2, 4).toLowerCase();
		
		initialRow = getRow(initialPosition);
		initialColumn = getColumn(initialPosition);
		targetRow = getRow(targetPosition);
		targetColumn = getColumn(targetPosition);
	}
	
	public static Set <String> getAllPositionsOnTheSameHorizontal(String initialPosition) {
		return getAllPositions(initialPosition, new int [] {0}, new int [] {-1, 1}, true, false);
	}
	
	public static Set <String> getAllPositionsOnTheSameVertical(String initialPosition) {
		return getAllPositions(initialPosition, new int [] {-1, 1}, new int [] {0}, true, false);
	}
	
	public static Set <String> getAllPositionsOnTheSameDiagonal(String initialPosition) {
		return getAllPositions(initialPosition, new int [] {1, -1}, new int [] {1, -1}, true, false);
	}
	
	public static Set <String> getAllPositions(String initialPosition, int [] rowIncrements, 
			int [] columnIncrements, boolean useAllPossibleCombinations, 
			boolean makeJustOneIteration) {
		HashSet <String> result = new HashSet <String> ();
		int initialRow = getRow(initialPosition); 
		int initialColumn = getColumn(initialPosition); 
		HashMap<Integer, HashSet<Integer>> columnConformedIncrementPairsMap = 
				new HashMap<Integer, HashSet<Integer>> ();
		if(useAllPossibleCombinations) {
			HashSet <Integer> rowIncrementsSet = new HashSet <Integer> ();
			for(int i = 0; i < rowIncrements.length; i++)
				rowIncrementsSet.add(rowIncrements[i]);
			for(int i = 0; i < columnIncrements.length; i++)
				columnConformedIncrementPairsMap.put(columnIncrements[i], rowIncrementsSet);
		}
		else {
			for(int i = 0; i < columnIncrements.length; i++) {
				HashSet <Integer> alreadyStored = columnConformedIncrementPairsMap.get(columnIncrements[i]);
				if(alreadyStored != null)
					alreadyStored.add(rowIncrements[i]);
				else {
					alreadyStored = new HashSet <Integer> ();
					alreadyStored.add(rowIncrements[i]);
				}
				columnConformedIncrementPairsMap.put(columnIncrements[i], alreadyStored);
			}
		}
		for(int i = 0; i < columnIncrements.length; i++) {
			HashSet <Integer> rowIncrementsSet = columnConformedIncrementPairsMap.get(columnIncrements[i]);
			for (Integer rowIncrement : rowIncrementsSet) {
				if(rowIncrement.intValue() == 0 && columnIncrements[i] == 0)
					continue;
				int currentRow = initialRow + rowIncrement.intValue();
				int currentColumn = initialColumn + columnIncrements[i];
				while(currentRow >= 0 && currentRow <= 7 && currentColumn >= 0 && currentColumn <= 7) {
					result.add(getPosition(currentRow, currentColumn));
					if(makeJustOneIteration)
						break;
					currentRow += rowIncrement;
					currentColumn += columnIncrements[i];
				}
			}
		}
		return result;
	}
	
	public static int getColumn(String position) {		
		Character alphabetical = position.toLowerCase().charAt(0);
		return columnsHashMap.get(alphabetical).intValue();
	}
	
	public static int getRow(String position) {
		Character numerical = position.toLowerCase().charAt(1);
		Integer value = new Integer(numerical.toString());
		return 8 - value.intValue();
	}
	
	public static String getPosition(int row, int column) {
		String result = "";
		for (Character character : columnsCharactersSet) {
			if(columnsHashMap.get(character).intValue() == column) {
				result = character.toString();
				break;
			}
		}
		result += Integer.toString(8 - row);
		return result;
	}
	
	public boolean isOnTheSameHorizontal() {
		return initialRow == targetRow;
	}
	
	public boolean isOnTheSameVertical() {
		return initialColumn == targetColumn;
	}
	
	public boolean isOnTheSameDiagonal() {
		int rowsDelta = initialRow - targetRow;
		int columnsDelta = initialColumn - targetColumn;
		return Math.abs(rowsDelta) == Math.abs(columnsDelta);
	}
	
	public boolean areAnyFiguresBetweenPositionsByHorizontal(Board board) {
		Set <String> intermediatePositions = getIntermediatePositionsByHorizontal();
		for (String position : intermediatePositions)
			if(! board.getFigureAt(position).getFigureConstant().equals(Constants.NONE_FIGURE))
				return true;
		return false;
	}
	
	public boolean areAnyFiguresBetweenPositionsByVertical(Board board) {
		Set <String> intermediatePositions = getIntermediatePositionsByVertical();
		for (String position : intermediatePositions)
			if(! board.getFigureAt(position).getFigureConstant().equals(Constants.NONE_FIGURE))
				return true;
		return false;
	}
	
	public boolean areAnyFiguresBetweenPositionsByDiagonal(Board board) {
		Set <String> intermediatePositions = getIntermediatePositionsByDiagonal();
		for (String position : intermediatePositions)
			if(! board.getFigureAt(position).getFigureConstant().equals(Constants.NONE_FIGURE))
				return true;
		return false;
	}
	
	public Set <String> getIntermediatePositionsByHorizontal() {
		HashSet <String> result = new HashSet <String> ();
		if(! isOnTheSameHorizontal())
			return result;
		int columnsIncrement = initialColumn < targetColumn ? 1 : -1;
		int currentColumn = initialColumn + columnsIncrement;
		while(currentColumn != targetColumn) {
			result.add(getPosition(initialRow, currentColumn));	
			currentColumn += columnsIncrement;
		}
		return result;
	}
	
	public Set <String> getIntermediatePositionsByVertical() {
		HashSet <String> result = new HashSet <String> ();
		if(! isOnTheSameVertical())
			return result;
		int rowsIncrement = initialRow < targetRow ? 1 : -1;
		int currentRow = initialRow + rowsIncrement;
		while(currentRow != targetRow) {
			result.add(getPosition(currentRow, initialColumn));	
			currentRow += rowsIncrement;
		}
		return result;
	}
	
	public Set <String> getIntermediatePositionsByDiagonal() {
		HashSet <String> result = new HashSet <String> ();
		if(! isOnTheSameDiagonal())
			return result;
		int columnsIncrement = initialColumn < targetColumn ? 1 : -1;
		int rowsIncrement = initialRow < targetRow ? 1 : -1;
		int currentColumn = initialColumn + columnsIncrement;
		int currentRow = initialRow + rowsIncrement;
		while(currentColumn != targetColumn && currentRow != targetRow) {
			result.add(getPosition(currentRow, currentColumn));	
			currentColumn += columnsIncrement;
			currentRow += rowsIncrement;
		}
		return result;
	}
	
	public boolean initialEqualsTarget() {
		return initialPosition.equals(targetPosition);
	}
	
	@Override
	public String toString() {
		return initialPosition + "[" + initialRow + ", " + initialColumn + "], " + targetPosition + 
				"[" + targetRow + ", " + targetColumn + "]" + "\n";
	}

	public int getInitialRow() {
		return initialRow;
	}

	public int getInitialColumn() {
		return initialColumn;
	}

	public int getTargetRow() {
		return targetRow;
	}

	public int getTargetColumn() {
		return targetColumn;
	}

	public String getTargetPosition() {
		return targetPosition;
	}

	public String getInitialPosition() {
		return initialPosition;
	}
}
