import com.practicaSV.gameLabz.domain.*;
import com.practicaSV.gameLabz.main.Application;
import com.practicaSV.gameLabz.repositories.*;
import com.practicaSV.gameLabz.utils.HttpHeadersConstants;
import com.practicaSV.gameLabz.utils.PathConstants;
import com.practicaSV.gameLabz.utils.websocket.TopicConstants;
import org.apache.commons.lang.RandomStringUtils;
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
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@IntegrationTest
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class GameOrderControllerTest {

    private MockMvc mockMvc;

    private TestUser testUser;

    private TestUser receiverUser;

    private GameOffer testOffer;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FriendDAO friendDAO;

    @Autowired
    private GeneratedKeyDAO generatedKeyDAO;

    @Autowired
    private GameOrderDAO gameOrderDAO;

    @Autowired
    private UserProfileDAO userProfileDAO;

    @Autowired
    private GameOfferDAO gameOfferDAO;

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        testUser = UserTestUtils.getInstance().registerAndLoginUser(User.UserType.CLIENT, mockMvc);
        receiverUser = UserTestUtils.getInstance().registerAndLoginUser(User.UserType.CLIENT, mockMvc);
        testOffer = GameOfferTestUtils.getInstance().addNewOffer();
    }

    @Test
    public void buyGameWithCashTestPoz() throws Exception {

        GameOffer gameOffer = new GameOffer();

        Game game1 = GameTestUtils.getInstance().addNewgame();
        Game game2 = GameTestUtils.getInstance().addNewgame();

        gameOffer.setGames(Arrays.asList(game1, game1, game1, game1, game2, game2));
        gameOffer.setPriceCash(BigDecimal.valueOf(281.23));
        gameOffer.setPricePoints(54L);
        gameOffer.setPromotion(BigDecimal.valueOf(13.68));
        gameOffer.setOfferType(GameOffer.OfferType.BUNDLE);

        gameOfferDAO.saveGameOffer(gameOffer);

        CashGameOrder order = new CashGameOrder();
        GameOffer offer = new GameOffer();
        offer.setId(gameOffer.getId());

        order.setGameOfferList(Arrays.asList(offer));

        String json = JsonMapper.objectToJson(order);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAME_ORDER_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        CashGameOrder response = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), CashGameOrder.class);
        UserProfile profile = userProfileDAO.getProfileByUser(testUser.getUser()).get();

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getId());

        Assert.assertEquals(testUser.getUser(), response.getUser());
        Assert.assertTrue(profile.getKeys().containsAll(response.getKeys()));
        Assert.assertTrue(profile.getOwnedGames().containsAll(response.getOwnedGames()));

        BigDecimal price = gameOffer.getPriceCash().subtract(gameOffer.getPriceCash().multiply(gameOffer.getPromotion().divide(BigDecimal.valueOf(100L))));
        Assert.assertEquals(price, response.getCashValue());
    }

    @Test
    public void buyGameWithCashTestNeg() throws Exception {

        CashGameOrder order = new CashGameOrder();

        order.setUser(testUser.getUser());

        String json = JsonMapper.objectToJson(order);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAME_ORDER_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andReturn();

        String expectedMessage = "Invalid game offers! List is empty!";
        String responseMessage = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals(expectedMessage, responseMessage);
    }

    @Test
    public void buyGamesWithPointsTestPoz() throws Exception {

        PointsGameOrder order = new PointsGameOrder();

        UserProfile profile = userProfileDAO.getProfileByUser(testUser.getUser()).get();
        profile.setPoints(2000L);
        userProfileDAO.updateProfile(profile);

        order.setUser(testUser.getUser());
        order.setGameOfferList(Arrays.asList(testOffer));

        String json = JsonMapper.objectToJson(order);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAME_ORDER_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        PointsGameOrder responseOrder = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), PointsGameOrder.class);

        Assert.assertNotNull(responseOrder);
        Assert.assertNotNull(responseOrder.getId());

        Assert.assertEquals(testUser.getUser(), responseOrder.getUser());
    }

    @Test
    public void buyGamesWithPointsTestNeg() throws Exception {

        PointsGameOrder order = new PointsGameOrder();

        order.setUser(testUser.getUser());
        order.setGameOfferList(Arrays.asList(testOffer));

        String json = JsonMapper.objectToJson(order);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAME_ORDER_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String expected = "Insufficient funds!";
        String response = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals(expected, response);
    }

    @Test
    public void buyGiftTestPoz() throws Exception {

        GiftGameOrder order = new GiftGameOrder();
        GameOffer gameOffer = new GameOffer();
        gameOffer.setId(testOffer.getId());

        order.setRecieverUser(receiverUser.getUser());
        order.setGameOfferList(Arrays.asList(gameOffer));

        Friend friend = new Friend();
        friend.setUser(testUser.getUser());
        friend.setFriend(receiverUser.getUser());

        friendDAO.saveFriend(friend);

        receiverUser.subscribe(TopicConstants.GAME_GIFT);

        String json = JsonMapper.objectToJson(order);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAME_ORDER_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        GiftGameOrder response = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), GiftGameOrder.class);
        GameGiftNotification notification = receiverUser.getMessageFromTopic(TopicConstants.GAME_GIFT, GameGiftNotification.class);
        List<Game> gameList = gameOrderDAO.getGamesByUser(receiverUser.getUser());
        Collection<GeneratedKey> keys = generatedKeyDAO.getGeneratedKeyByUser(receiverUser.getUser());
        List<GeneratedKey> keyList = new ArrayList<>(keys);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getId());

        Assert.assertEquals(receiverUser.getUser(), response.getRecieverUser());

        Assert.assertNotNull(notification);
        Assert.assertTrue(keyList.containsAll(notification.getKeys()));
        Assert.assertTrue(gameList.containsAll(notification.getGames()));
    }

    @Test
    public void buyGiftTestNeg() throws Exception {

        GiftGameOrder order = new GiftGameOrder();

        order.setUser(testUser.getUser());
        order.setRecieverUser(receiverUser.getUser());
        order.setGameOfferList(Arrays.asList(testOffer));

        String json = JsonMapper.objectToJson(order);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAME_ORDER_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String expectedMessage = "Users are not friend!";
        String responseMessage = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals(expectedMessage, responseMessage);
    }

    @Test
    public void buyGameWithKeyTestPoz() throws Exception {

        KeyGameOrder order = new KeyGameOrder();
        GeneratedKey key = new GeneratedKey();

        key.setGeneratedKey(UUID.randomUUID().toString());
        key.setGame(testOffer.getGames().stream().findFirst().get());
        generatedKeyDAO.saveKey(key);

        order.setKeyValue(key.getGeneratedKey());

        String json = JsonMapper.objectToJson(order);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAME_ORDER_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        GameOrder response = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), GameOrder.class);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getId());

        Assert.assertEquals(testUser.getUser(), response.getUser());
    }

    @Test
    public void buyGameWithKeyTestNeg() throws Exception {

        KeyGameOrder order = new KeyGameOrder();
        GeneratedKey key = new GeneratedKey();

        key.setGeneratedKey(UUID.randomUUID().toString());
        key.setGame(testOffer.getGames().stream().findFirst().get());
        key.setLogicalDelete(true);
        generatedKeyDAO.saveKey(key);

        order.setKeyValue(key.getGeneratedKey());

        String json = JsonMapper.objectToJson(order);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.GAME_ORDER_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String expected = "Invalid key!";
        String response = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals(expected, response);
    }
}
