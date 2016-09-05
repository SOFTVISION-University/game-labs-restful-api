import com.practicaSV.gameLabz.domain.*;
import com.practicaSV.gameLabz.main.Application;
import com.practicaSV.gameLabz.repositories.GameOrderDAO;
import com.practicaSV.gameLabz.repositories.GameRatingDAO;
import com.practicaSV.gameLabz.repositories.UserRatingLinkDAO;
import com.practicaSV.gameLabz.utils.HttpHeadersConstants;
import com.practicaSV.gameLabz.utils.PathConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import utils.GameTestUtils;
import utils.JsonMapper;
import utils.TestUser;
import utils.UserTestUtils;

import java.math.BigDecimal;
import java.util.Collection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@IntegrationTest
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class GameRatingControllerTest {

    private MockMvc mockMvc;

    private TestUser testUser;

    private Game testGame;

    @Autowired
    private GameOrderDAO gameOrderDAO;

    @Autowired
    private GameRatingDAO gameRatingDAO;

    @Autowired
    private UserRatingLinkDAO userRatingLinkDAO;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        testUser = UserTestUtils.getInstance().registerAndLoginUser(User.UserType.CLIENT, mockMvc);
        testGame = GameTestUtils.getInstance().addNewgame();
    }

    @Test
    public void rateGameFirstTimeTestPoz() throws Exception {

        OwnedGame ownedGame = new OwnedGame();
        ownedGame.setUser(testUser.getUser());
        ownedGame.setGame(testGame);
        gameOrderDAO.saveBoughtGame(ownedGame);
        BigDecimal rating = BigDecimal.valueOf(76.88);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAME_RATING_ID_PATH, testUser.getUser().getUserName(), testGame.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .param(PathConstants.RATING, rating.toString()))
                .andExpect(status().isOk())
                .andReturn();

        UserRatingLink ratingLink = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), UserRatingLink.class);

        GameRating gameRatingFromDb = gameRatingDAO.getRatingByGame(testGame).get();
        Collection<UserRatingLink> userRatingLinks = userRatingLinkDAO.getRatingLinksByGame(testGame);

        Assert.assertNotNull(ratingLink);
        Assert.assertNotNull(ratingLink.getId());

        Assert.assertEquals(testUser.getUser(), ratingLink.getUser());
        Assert.assertEquals(testGame, ratingLink.getGame());
        Assert.assertEquals(rating, ratingLink.getRating());
        Assert.assertEquals(gameRatingFromDb.getNumberOfRatings(), Long.valueOf(userRatingLinks.size()));

    }

    @Test
    public void rateGameMultipleTimesTestPoz() throws Exception {

        TestUser someUser = UserTestUtils.getInstance().registerAndLoginUser(User.UserType.CLIENT, mockMvc);

        OwnedGame ownedGame = new OwnedGame();
        ownedGame.setUser(testUser.getUser());
        ownedGame.setGame(testGame);
        gameOrderDAO.saveBoughtGame(ownedGame);

        GameRating gameRating = new GameRating();
        gameRating.setGame(testGame);
        gameRating.setRating(BigDecimal.valueOf(12.53));
        gameRating.setNumberOfRatings(1L);
        gameRatingDAO.saveRating(gameRating);

        UserRatingLink link = new UserRatingLink();
        link.setRating(BigDecimal.valueOf(12.53));
        link.setGame(testGame);
        link.setUser(someUser.getUser());
        userRatingLinkDAO.saveRatingLink(link);

        BigDecimal rating = BigDecimal.valueOf(76.12);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAME_RATING_ID_PATH, testUser.getUser().getUserName(), testGame.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .param(PathConstants.RATING, rating.toString()))
                .andExpect(status().isOk())
                .andReturn();

        UserRatingLink ratingLink = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), UserRatingLink.class);

        GameRating gameRatingFromDb = gameRatingDAO.getRatingByGame(testGame).get();
        Collection<UserRatingLink> userRatingLinks = userRatingLinkDAO.getRatingLinksByGame(testGame);

        Assert.assertNotNull(ratingLink);
        Assert.assertNotNull(ratingLink.getId());

        Assert.assertEquals(testUser.getUser(), ratingLink.getUser());
        Assert.assertEquals(testGame, ratingLink.getGame());
        Assert.assertEquals(rating, ratingLink.getRating());
        Assert.assertEquals(gameRatingFromDb.getNumberOfRatings(), Long.valueOf(userRatingLinks.size()));
    }

    @Test
    public void updateGameRatingTestPoz() throws Exception {

        OwnedGame ownedGame = new OwnedGame();
        ownedGame.setUser(testUser.getUser());
        ownedGame.setGame(testGame);
        gameOrderDAO.saveBoughtGame(ownedGame);

        Long ratingCount = 1L;

        GameRating gameRating = new GameRating();
        gameRating.setGame(testGame);
        gameRating.setRating(BigDecimal.valueOf(12.53));
        gameRating.setNumberOfRatings(ratingCount);
        gameRatingDAO.saveRating(gameRating);

        UserRatingLink link = new UserRatingLink();
        link.setRating(BigDecimal.valueOf(12.53));
        link.setGame(testGame);
        link.setUser(testUser.getUser());
        userRatingLinkDAO.saveRatingLink(link);

        BigDecimal rating = BigDecimal.valueOf(76.12);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAME_RATING_ID_PATH, testUser.getUser().getUserName(), testGame.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .param(PathConstants.RATING, rating.toString()))
                .andExpect(status().isOk())
                .andReturn();

        UserRatingLink ratingLink = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), UserRatingLink.class);

        GameRating gameRatingFromDb = gameRatingDAO.getRatingByGame(testGame).get();
        Collection<UserRatingLink> userRatingLinksByGame = userRatingLinkDAO.getRatingLinksByGame(testGame);

        Assert.assertNotNull(ratingLink);
        Assert.assertNotNull(ratingLink.getId());

        Assert.assertEquals(testUser.getUser(), ratingLink.getUser());
        Assert.assertEquals(testGame, ratingLink.getGame());
        Assert.assertEquals(rating, ratingLink.getRating());
        Assert.assertEquals(gameRatingFromDb.getNumberOfRatings(), Long.valueOf(userRatingLinksByGame.size()));
        Assert.assertEquals(Long.valueOf(userRatingLinksByGame.size()), ratingCount);
    }

    @Test
    public void rateGameTestOwnedGameNeg() throws Exception {

        BigDecimal rating = BigDecimal.valueOf(76.12);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAME_RATING_ID_PATH, testUser.getUser().getUserName(), testGame.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .param(PathConstants.RATING, rating.toString()))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String expected = "User doesn't own the game!";
        String response = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals(expected, response);
    }

    @Test
    public void rateGameTestInvalidNeg() throws Exception {

        BigDecimal rating = BigDecimal.valueOf(716.12);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAME_RATING_ID_PATH, testUser.getUser().getUserName(), testGame.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .param(PathConstants.RATING, rating.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();

        String expected = "Invalid rating parameter!";
        String response = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals(expected, response);
    }
}
