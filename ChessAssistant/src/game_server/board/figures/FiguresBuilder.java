package game_server.board.figures;

import util.Constants;



public class FiguresBuilder {
	public static Figure buildDefaultFigure(String figureConstant) {
		if(figureConstant.equals(Constants.PAWN))
			return new PawnFigure();
		else if(figureConstant.equals(Constants.ROOK))
			return new RookFigure();
		else if(figureConstant.equals(Constants.KNIGHT))
			return new KnightFigure();
		else if(figureConstant.equals(Constants.BISHOP))
			return new BishopFigure();
		else if(figureConstant.equals(Constants.QUEEN))
			return new QueenFigure();
		else if(figureConstant.equals(Constants.KING))
			return new KingFigure();
		return new NoneFigure();
	}
	
	public static Figure buildFigure(Figure figure) {
		if(figure == null)
			figure = new NoneFigure();
		Figure newFigure = buildDefaultFigure(figure.getFigureConstant());
		if(figure instanceof RookFigure) {
			RookFigure rookFigure = (RookFigure) figure;
			((RookFigure) newFigure).setAlreadyMoved(rookFigure.hasAlreadyMoved());
		}
		else if(figure instanceof KingFigure) {
			KingFigure kingFigure = (KingFigure) figure;
			((KingFigure) newFigure).setAlreadyMoved(kingFigure.hasAlreadyMoved());
		}
		newFigure.setColor(figure.getFigureColor());
		return newFigure;
	}
}
