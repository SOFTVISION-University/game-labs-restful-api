import com.fasterxml.jackson.core.type.TypeReference;
import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.main.Application;
import com.practicaSV.gameLabz.repositories.GameDAO;
import com.practicaSV.gameLabz.utils.GetFilter;
import com.practicaSV.gameLabz.utils.HttpHeadersConstants;
import com.practicaSV.gameLabz.utils.PathConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import utils.GameTestUtils;
import utils.JsonMapper;
import utils.TestUser;
import utils.UserTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@IntegrationTest
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class GameControllerTest {

    private MockMvc mockMvc;

    private TestUser testUser;

    private Game testGame;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private GameDAO gameDAO;

    private static final TypeReference<List<Game>> GAME_LIST_TYPE_REF = new TypeReference<List<Game>>() {};

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        testUser = UserTestUtils.getInstance().registerAndLoginUser(User.UserType.ADMIN, mockMvc);
        testGame = GameTestUtils.getInstance().addNewgame();
    }

    @Test
    public void addGameTestPoz() throws Exception {

        List<Game.GameGenre> gameGenres = new ArrayList<>();

        gameGenres.add(Game.GameGenre.FPS);
        gameGenres.add(Game.GameGenre.RPG);

        Game game = new Game.Builder().name("First Game").desc("Very brutal").relDate(21842743L).genres(gameGenres).build();

        String gameJson = JsonMapper.objectToJson(game);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAMES_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gameJson))
                .andExpect(status().isCreated())
                .andReturn();

        Game responseGame = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), Game.class);

        Assert.assertNotNull(responseGame.getId());
        Assert.assertNotNull(responseGame.getGameGenres());
        Assert.assertEquals(game.getName(), responseGame.getName());
    }

    @Test
    public void addGameTestNeg() throws Exception {

        List<Game.GameGenre> gameGenres = new ArrayList<>();

        gameGenres.add(Game.GameGenre.FPS);
        gameGenres.add(Game.GameGenre.RPG);
        gameGenres.add(Game.GameGenre.RPG);

        Game game = new Game.Builder().name("").desc("Very brutal").relDate(-21842743L).build();

        game.setGameGenres(gameGenres);

        String gameJson = JsonMapper.objectToJson(game);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAMES_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gameJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String expectedHeader = "Invalid game name! Invalid release date! Duplicate genre!";
        String responseHeader = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals(expectedHeader, responseHeader);
    }

    @Test
    public void invalidJsonTest() throws Exception {

        List<Game.GameGenre> gameGenres = new ArrayList<>();

        gameGenres.add(Game.GameGenre.FPS);
        gameGenres.add(Game.GameGenre.RPG);

        Game game = new Game.Builder().name("First Game").desc("Very brutal").relDate(21842743L).genres(gameGenres).build();

        String gameJson = JsonMapper.objectToJson(game);
        gameJson = gameJson.replace(Game.GameGenre.FPS.toString(), "aejfn");

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAMES_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gameJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals("Invalid json payload!", response);
    }

    @Test
    public void getGameByFilterTestPoz() throws Exception {

        Game someGame = new Game.Builder().name("something").desc("akjfne").genres(Arrays.asList(Game.GameGenre.MOBA)).relDate(124124121L).build();
        gameDAO.saveGame(someGame);

        MvcResult mvcResult = mockMvc.perform(get(PathConstants.GAMES_PATH, testUser.getUser().getUserName())
                .param(GetFilter.HAS_OFFER, String.valueOf(false))
                .param(GetFilter.GENRE, Game.GameGenre.RPG.toString())
                .param(GetFilter.RELEASED_AFTER, String.valueOf(2300L))
                .param(GetFilter.RELEASED_BEFORE, String.valueOf(21842800L))
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId()))
                .andExpect(status().isOk())
                .andReturn();

        List<Game> responseGames = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), GAME_LIST_TYPE_REF);

        Assert.assertTrue(responseGames.contains(testGame));

        Game response = responseGames.stream()
                .filter(g -> g.equals(testGame))
                .findFirst().get();

        Assert.assertEquals(testGame.getName(), response.getName());
        Assert.assertEquals(testGame.getDescription(), response.getDescription());
        Assert.assertEquals(testGame.getReleaseDate(), response.getReleaseDate());
    }

    @Test
    public void getGameByFilterTestNeg() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get(PathConstants.GAMES_PATH, testUser.getUser().getUserName())
                .param(GetFilter.HAS_OFFER, String.valueOf(false))
                .param(GetFilter.GENRE, Game.GameGenre.RPG.toString())
                .param(GetFilter.RELEASED_AFTER, String.valueOf(2300L))
                .param(GetFilter.RELEASED_AFTER, "-124121")
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId()))
                .andExpect(status().isBadRequest())
                .andReturn();

        Assert.assertEquals("Invalid released after parameter!", mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE));
    }

    @Test
    public void updateGameTestPoz() throws Exception {

        List<Game.GameGenre> gameGenres = new ArrayList<>();

        gameGenres.add(Game.GameGenre.MOBA);
        gameGenres.add(Game.GameGenre.RPG);

        testGame.setName("Updated Gameeeeeeeeeeee");
        testGame.setReleaseDate(1222222222L);
        testGame.setDescription("Much description");
        testGame.setGameGenres(gameGenres);

        String gameJson = JsonMapper.objectToJson(testGame);

        MvcResult mvcResult = mockMvc.perform(put(PathConstants.GAME_ID_PATH, testUser.getUser().getUserName(), testGame.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gameJson))
                .andExpect(status().isOk())
                .andReturn();

        Game responseGame = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), Game.class);

        Assert.assertEquals(testGame.getName(), responseGame.getName());
        Assert.assertEquals(testGame.getGameGenres(), responseGame.getGameGenres());
        Assert.assertEquals(testGame.getReleaseDate(), responseGame.getReleaseDate());
        Assert.assertEquals(testGame.getDescription(), responseGame.getDescription());
    }

    @Test
    public void updateGameTestNeg() throws Exception {


        List<Game.GameGenre> gameGenres = new ArrayList<>();

        gameGenres.add(Game.GameGenre.MOBA);
        gameGenres.add(Game.GameGenre.RPG);
        gameGenres.add(Game.GameGenre.RPG);

        testGame.setName("Updated Game");
        testGame.setReleaseDate(12345L);
        testGame.setDescription("Much description");
        testGame.setGameGenres(gameGenres);

        String gameJson = JsonMapper.objectToJson(testGame);

        MvcResult mvcResult = mockMvc.perform(put(PathConstants.GAME_ID_PATH, testUser.getUser().getUserName(), testGame.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gameJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        Assert.assertEquals("Duplicate genre!", mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE));
    }

    @Test
    public void testUpdateEmptyGame() throws Exception {

        Game emptyGame = new Game.Builder().build();

        String gameJson = JsonMapper.objectToJson(emptyGame);

        MvcResult mvcResult = mockMvc.perform(put(PathConstants.GAME_ID_PATH, testUser.getUser().getUserName(), testGame.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gameJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actual = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);
        Assert.assertEquals("No new fields were inserted!", actual);
    }
}


