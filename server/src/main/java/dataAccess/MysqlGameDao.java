package dataAccess;

import Requestclasses.GameRequest;
import Requestclasses.Joingamerequest;
import Requestclasses.Userclass;
import Responseclass.Games;
import Responseclass.ListgameResponse;
import Responseclass.Newgameresponse;
import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MysqlGameDao implements GameInterface {
  @Override
  public ListgameResponse getList(String username) throws DataAccessException {
    List<Games> list=new ArrayList<Games>();

    try (var conn=DatabaseManager.getConnection()) {
      try (var preparedStatement=conn.prepareStatement("SELECT * FROM  game ")) {
        try (var rs=preparedStatement.executeQuery()) {
          while (rs.next()) {
            var id=rs.getInt("gameID");
            var whiteUsername=rs.getString("whiteUsername");
            var blackUsername=rs.getString("blackUsername");
            var gameName=rs.getString("gameName");
            list.add(new Games(id, whiteUsername, blackUsername, gameName));
          }
          return new ListgameResponse(list);
        }

      }
    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());
    }
  }

  @Override
  public void joinGame(String username, Joingamerequest body) throws DataAccessException {
    try (var conn=DatabaseManager.getConnection()) {
      try (var preparedStatement=conn.prepareStatement("SELECT whiteUsername,blackUsername FROM game WHERE gameID=?")) {
        preparedStatement.setInt(1, body.gameID());
        try (var rs=preparedStatement.executeQuery()) {
          if (rs.next()) {
            var whiteUsername=rs.getString("whiteUsername");
            var blackUsername=rs.getString("blackUsername");
            if (body.playerColor() == null) {
              return;
            }else if (body.playerColor().equalsIgnoreCase("WHITE")) {
              if (whiteUsername != null) {
                throw new DataAccessException("Error: already taken");
              }else {
                try (var preparedStatement2=conn.prepareStatement("UPDATE game SET whiteUsername=? WHERE gameID=?")) {
                  preparedStatement2.setString(1, username);
                  preparedStatement2.setInt(2, body.gameID());
                  preparedStatement2.executeUpdate();
                }
              }
            }else {
              if (blackUsername != null) {
                throw new DataAccessException("Error: already taken");
              }else {
                try (var preparedStatement3=conn.prepareStatement("UPDATE game SET blackUsername=? WHERE gameID=?")) {
                  preparedStatement3.setString(1, username);
                  preparedStatement3.setInt(2, body.gameID());
                  preparedStatement3.executeUpdate();
                }
              }
            }
          }else {
            throw new DataAccessException("Error: bad request");
          }
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());
    }
  }

  @Override
  public Newgameresponse createGame(String username, GameRequest body) throws DataAccessException {
    try (var conn=DatabaseManager.getConnection()) {
      try (var preparedStatement=conn.prepareStatement("INSERT INTO game (gameName,chess ) VALUES(?, ?)", RETURN_GENERATED_KEYS)) {
        preparedStatement.setString(1, body.gameName());
        ChessBoard board=new ChessBoard();
        board.resetBoard();
        ChessGame game=new ChessGame();
        game.setBoard(board);
        String jsonGame=new Gson().toJson(game);
        preparedStatement.setString(2, jsonGame);
        preparedStatement.executeUpdate();
        var resultSet=preparedStatement.getGeneratedKeys();
        var ID=0;
        if (resultSet.next()) {
          ID=resultSet.getInt(1);
        }
        return new Newgameresponse(ID);
      }
    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());
    }

  }

  @Override
  public ChessGame getGame(Integer GameID) throws DataAccessException {
    ChessGame chess_game=new ChessGame();
    try (var conn=DatabaseManager.getConnection()) {
      try (var preparedStatement=conn.prepareStatement("SELECT chess FROM game WHERE gameID=?")) {
        preparedStatement.setInt(1, GameID);
        try (var rs=preparedStatement.executeQuery()) {
          String game="";
          if (rs.next()) {
            game=rs.getString("chess");
          }
          else{
            throw new DataAccessException("Error: no chess game found");
          }
          ChessGame board=new Gson().fromJson(game, ChessGame.class);
          chess_game = board;
          return chess_game;
        }

      }
    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());
    }
  }

  @Override
  public void deleteall() throws DataAccessException {
    try (var conn=DatabaseManager.getConnection()) {
      try (var preparedStatement=conn.prepareStatement("TRUNCATE game")) {
        preparedStatement.executeUpdate();
      }
    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());
    }


  }

  @Override
  public void leave_player(Integer GameID, ChessGame.TeamColor color) throws DataAccessException {
    try (var conn=DatabaseManager.getConnection()) {
      if (color.toString().equalsIgnoreCase("white")) {
        try (var preparedStatement2=conn.prepareStatement("UPDATE game SET whiteUsername=? WHERE gameID=?")) {
          preparedStatement2.setString(1, null);
          preparedStatement2.setInt(2, GameID);
          preparedStatement2.executeUpdate();
        }
      }
      else if (color.toString().equalsIgnoreCase("black")){
        try (var preparedStatement3=conn.prepareStatement("UPDATE game SET blackUsername=? WHERE gameID=?")) {
          preparedStatement3.setString(1, null);
          preparedStatement3.setInt(2, GameID);
          preparedStatement3.executeUpdate();
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());

    }
  }
  @Override
  public  void check_gameID(Integer GameID, ChessGame.TeamColor color,String username) throws DataAccessException{
    try (var conn=DatabaseManager.getConnection()) {
      if (color.toString().equalsIgnoreCase("white")) {
        try (var preparedStatement2=conn.prepareStatement("Select whiteUsername From game WHERE gameID=?")) {
          preparedStatement2.setInt(1, GameID);
          try (var rs=preparedStatement2.executeQuery()) {
            if (rs.next()) {
              String us_name=rs.getString("whiteUsername");
              if (!username.equalsIgnoreCase(us_name)){
                throw new DataAccessException("Error: Username doesn't match");
              }
            }
            else{
              throw new DataAccessException("Error: no chess game found");
            }
          }

        }
      }
      else if (color.toString().equalsIgnoreCase("black")){
        try (var preparedStatement2=conn.prepareStatement("Select blackUsername From game WHERE gameID=?")) {
          preparedStatement2.setInt(1, GameID);
          try (var rs=preparedStatement2.executeQuery()) {
            String game="";
            if (rs.next()) {
              String us_name=rs.getString("blackUsername");
              if (!username.equalsIgnoreCase(us_name)){
                throw new DataAccessException("Error: Username doesn't match");
              }
            }
            else{
              throw new DataAccessException("Error: no chess game found");
            }
          }
        }

      }


    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());

    }
  }
  @Override
  public  void delete_gameID(Integer GameID) throws DataAccessException{
    try (var conn=DatabaseManager.getConnection()) {
      
    }

  }

}
