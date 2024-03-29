package Moves;
import chess.*;

import java.util.ArrayList;
import java.util.Collection;
public class Kingcalculator implements Calculator {
  public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
    ArrayList moves=new ArrayList<>();
    moves.addAll(helper(board, myPosition, color, 1, 0));
    moves.addAll(helper(board, myPosition, color, 1, 1));
    moves.addAll(helper(board, myPosition, color, 0, 1));
    moves.addAll(helper(board, myPosition, color, -1, 1));
    moves.addAll(helper(board, myPosition, color, -1, 0));
    moves.addAll(helper(board, myPosition, color, -1, -1));
    moves.addAll(helper(board, myPosition, color, 0, -1));
    moves.addAll(helper(board, myPosition, color, 1, -1));
    return moves;
  }

  public Collection<ChessMove> helper(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color, int first, int second) {
    ArrayList moves=new ArrayList<>();
    return getChessMoves(board, myPosition, color, first, second, moves);
  }

  static Collection<ChessMove> getChessMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color, int first, int second, ArrayList moves) {
    int col=myPosition.getColumn() + second;
    int row=myPosition.getRow() + first;
    if (col <= 8 && row <= 8 && col >= 1 && row >= 1) {
      ChessPiece chess=board.getPiece(new ChessPosition(row, col));
      if (chess != null) {
        if (!chess.getTeamColor().equals(color)) {
          moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }
        return moves;
      }
      moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));

    }
    return moves;
  }
}
