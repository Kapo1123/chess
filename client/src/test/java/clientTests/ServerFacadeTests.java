package clientTests;

import client.ServerFacade;
import dataAccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    static ServerFacade serverFacade;
    static Integer ID;

    @BeforeAll
    public static void init() {
        server=new Server();
        var port=server.run(0);
        System.out.println(port);
        System.out.println("Started test HTTP server on " + port);
        serverFacade=new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() throws DataAccessException {
        serverFacade.clear();
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }


    @Test
    @Order(1)
    public void Register_True() throws DataAccessException {
        String[] params = {"username", "password", "email"};;
        var response=serverFacade.register(params);
        assertNotNull(response);
    }
    @Test
    @Order(2)
    public void Register_False() throws DataAccessException {
        String[] params={"username", "password", "email"};
        Assertions.assertThrows(DataAccessException.class, () -> {
            serverFacade.register(params);
        }, "id 0 should never be a game ID");
    }
    @Test
    @Order(3)
    public void Login_False() throws DataAccessException {
        String[] params={"username", "123"};
        Assertions.assertThrows(DataAccessException.class, () -> {
            serverFacade.login(params);
        }, "Should be Error: unauthorized");
    }
    @Test
    @Order(4)
    public void Login_True() throws DataAccessException {
        String[] params={"username", "password"};
        var response=serverFacade.login(params);
        assertNotNull(response);
    }
    @Test
    @Order(5)
    public void CreateGame_True() throws DataAccessException {
        String[] params={"HelloThere"};
        var response=serverFacade.createGame(params);
        ID=response.gameID();
        assertNotNull(response);
    }
    @Test
    @Order(5)
    public void CreateGame_False() throws DataAccessException {
        String[] params={};
        Assertions.assertThrows(java.lang.ArrayIndexOutOfBoundsException.class, () -> {
            serverFacade.createGame(params);
        }, "Should be Error: unauthorized");
    }
    @Test
    @Order(5)
    public void JoinGame_True() throws DataAccessException {
        String[] params={"HelloThere"};
        var response=serverFacade.createGame(params);
        ID=response.gameID();
        assertNotNull(response);
    }




    }







