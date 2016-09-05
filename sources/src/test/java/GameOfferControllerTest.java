import com.fasterxml.jackson.core.type.TypeReference;
import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.GameOffer;
import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.main.Application;
import com.practicaSV.gameLabz.repositories.GameDAO;
import com.practicaSV.gameLabz.repositories.GameOfferDAO;
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
import utils.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@IntegrationTest
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class GameOfferControllerTest {

    private MockMvc mockMvc;

    private TestUser testUser;

    private GameOffer testGameOffer;

    private Game testGame;

    private static final TypeReference<List<GameOffer>> GAME_OFFER_LIST_TYPE_REF = new TypeReference<List<GameOffer>>() {};

    @Autowired
    private GameDAO gameDAO;

    @Autowired
    private GameOfferDAO gameOfferDAO;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        testUser = UserTestUtils.getInstance().registerAndLoginUser(User.UserType.ADMIN, mockMvc);
        testGame = GameTestUtils.getInstance().addNewgame();
        testGameOffer = GameOfferTestUtils.getInstance().addNewOffer();
    }

    @Test
    public void addGameOfferTestPoz() throws Exception {

        GameOffer gameOffer = new GameOffer();

        gameOffer.setGames(Arrays.asList(testGame));
        gameOffer.setOfferType(GameOffer.OfferType.SINGLE);
        gameOffer.setPriceCash(BigDecimal.valueOf(100));
        gameOffer.setPricePoints(91L);

        String gameOfferJson = JsonMapper.objectToJson(gameOffer);

        MvcResult mvcResultAdd = mockMvc.perform(post(PathConstants.GAME_OFFERS_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gameOfferJson))
                .andExpect(status().isCreated())
                .andReturn();

        GameOffer responseOffer = JsonMapper.jsonToObject(mvcResultAdd.getResponse().getContentAsString(), GameOffer.class);

        Assert.assertNotNull(responseOffer.getId());
        Assert.assertNotNull(responseOffer.getGames());

        Game responseGame = responseOffer.getGames().stream().findFirst().get();
        Assert.assertEquals(testGame.getName(), responseGame.getName());
        Assert.assertNotNull(testGame.getGameGenres());
    }

    @Test
    public void addGameOfferTestNeg() throws Exception {

        List<Game.GameGenre> gameGenres = new ArrayList<>();
        gameGenres.add(Game.GameGenre.FPS);
        gameGenres.add(Game.GameGenre.RPG);

        Game game = new Game.Builder().build();
        game.setId(191919191919L);
        List<Game> games = Arrays.asList(game);

        GameOffer gameOffer = new GameOffer();

        gameOffer.setGames(games);
        gameOffer.setOfferType(GameOffer.OfferType.SINGLE);
        gameOffer.setPriceCash(BigDecimal.valueOf(1231.12));
        gameOffer.setPricePoints(190L);

        String gameOfferJson = JsonMapper.objectToJson(gameOffer);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAME_OFFERS_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gameOfferJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals("Invalid game!", response);
    }

    @Test
    public void updateGameOfferTestPoz() throws Exception {

        testGameOffer.setPricePoints(32129L);
        testGameOffer.setPriceCash(BigDecimal.valueOf(12847.56));

        String updatedOffer = JsonMapper.objectToJson(testGameOffer);

        MvcResult mvcResult = mockMvc.perform(put(PathConstants.GAME_OFFER_ID_PATH, testUser.getUser().getUserName(), testGameOffer.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedOffer))
                .andExpect(status().isOk())
                .andReturn();

        GameOffer responseOffer = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), GameOffer.class);

        Assert.assertNotNull(responseOffer.getId());
        Assert.assertNotNull(responseOffer.getGames());
        Assert.assertEquals(testGameOffer.getPriceCash(), responseOffer.getPriceCash());
        Assert.assertEquals(testGameOffer.getPricePoints(), responseOffer.getPricePoints());
    }

    @Test
    public void updateGameOfferTestNeg() throws Exception {

        testGameOffer.setPricePoints(-1241L);

        String updatedOffer = JsonMapper.objectToJson(testGameOffer);

        MvcResult mvcResult = mockMvc.perform(put(PathConstants.GAME_OFFER_ID_PATH, testUser.getUser().getUserName(), testGameOffer.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedOffer))
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals("Invalid price in points!", response);
    }

    @Test
    public void removeGameOfferTestPoz() throws Exception {

        mockMvc.perform(delete(PathConstants.GAME_OFFER_ID_PATH, testUser.getUser().getUserName(), testGameOffer.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void removeGameOfferTestNeg() throws Exception {

        MvcResult mvcResult = mockMvc.perform(delete(PathConstants.GAME_OFFER_ID_PATH, testUser.getUser().getUserName(), 12412L)
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId()))
                .andExpect(status().isBadRequest())
                .andReturn();

        String expected = "Invalid game offer id!";
        String response = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);
        Assert.assertEquals(expected, response);
    }

    @Test
    public void getGameOfferByFiltersTestPoz() throws Exception {

        //single game offer for test
        GameOffer gameOfferSingle = new GameOffer();

        gameOfferSingle.setGames(Arrays.asList(GameTestUtils.getInstance().addNewgame()));
        gameOfferSingle.setPricePoints(65L);
        gameOfferSingle.setPriceCash(BigDecimal.valueOf(1241));
        gameOfferSingle.setOfferType(GameOffer.OfferType.SINGLE);

        GameOffer gameOfferDbSingle = gameOfferDAO.saveGameOffer(gameOfferSingle);

        //bundle game offer for test
        GameOffer gameOfferBundle = new GameOffer();

        List<Game> gameList = new ArrayList<>();
        gameList.add(GameTestUtils.getInstance().addNewgame());
        gameList.add(GameTestUtils.getInstance().addNewgame());
        gameList.add(GameTestUtils.getInstance().addNewgame());

        gameOfferBundle.setGames(gameList);
        gameOfferBundle.setPriceCash(BigDecimal.valueOf(21.21));
        gameOfferBundle.setPricePoints(1L);
        gameOfferBundle.setPromotion(BigDecimal.valueOf(20));
        gameOfferBundle.setOfferType(GameOffer.OfferType.BUNDLE);

        GameOffer gameOfferDbBundle = gameOfferDAO.saveGameOffer(gameOfferBundle);

        MvcResult mvcResult = mockMvc.perform(get(PathConstants.GAME_OFFERS_PATH_PUBLIC, testUser.getUser().getUserName())
                .param(GetFilter.ORDER_BY, GetFilter.PRICE_CASH)
                .param(GetFilter.ORDER_DIRECTION, GetFilter.OrderDirection.DESC.toString())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId()))
                .andExpect(status().isOk())
                .andReturn();

        List<GameOffer> response = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), GAME_OFFER_LIST_TYPE_REF);

        Map<Long, GameOffer> resultMap = response.stream().collect(Collectors.toMap(GameOffer::getId, gameOffer -> gameOffer));

        GameOffer responseOfferSingle = resultMap.get(gameOfferDbSingle.getId());
        GameOffer responseOfferBundle = resultMap.get(gameOfferDbBundle.getId());

        Assert.assertNotNull(responseOfferSingle.getGames());
        Assert.assertEquals(gameOfferDbSingle, responseOfferSingle);

        Assert.assertNotNull(responseOfferBundle.getGames());
        Assert.assertEquals(gameOfferDbBundle, responseOfferBundle);
    }

}
