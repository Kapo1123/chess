package dataAccess;

import Requestclasses.GameRequest;
import Requestclasses.joingamerequest;
import Responseclass.ListgameResponse;
import Responseclass.newgameresponse;

public interface GameInterface {
  public  ListgameResponse getList(String username);


  public  void joinGame(String username, joingamerequest body) throws DataAccessException;

  public  newgameresponse createGame(String username, GameRequest body);
  public  void deleteall();
}
