package handler;

import Requestclasses.Authtoken;
import Requestclasses.Registerclass;
import Responseclass.Registerresponse;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.userService;
public class RegisterHandler implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    var register = new Gson().fromJson(request.body(), Registerclass.class);
    Authtoken Authtoken= userService.register(register);
    var res = new Registerresponse(register.username(), Authtoken);
    response.status(200);
    response.body(new Gson().toJson(res));
    return new Gson().toJson(res);
  }
}