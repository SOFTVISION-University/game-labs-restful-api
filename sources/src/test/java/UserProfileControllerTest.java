import com.practicaSV.gameLabz.domain.Friend;
import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.domain.UserProfile;
import com.practicaSV.gameLabz.main.Application;
import com.practicaSV.gameLabz.repositories.FriendDAO;
import com.practicaSV.gameLabz.repositories.UserDAO;
import com.practicaSV.gameLabz.repositories.UserProfileDAO;
import com.practicaSV.gameLabz.utils.HttpHeadersConstants;
import com.practicaSV.gameLabz.utils.JsonViews;
import com.practicaSV.gameLabz.utils.PathConstants;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import utils.JsonMapper;
import utils.TestUser;
import utils.UserTestUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@IntegrationTest
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class UserProfileControllerTest {

    private MockMvc mockMvc;

    private TestUser testUser;

    @Autowired
    private UserProfileDAO userProfileDAO;

    @Autowired
    private FriendDAO friendDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        testUser = UserTestUtils.getInstance().registerAndLoginUser(User.UserType.CLIENT, mockMvc);
    }

    @Test
    public void linkClickedTestPoz() throws Exception {

        String linkId = userProfileDAO.getProfileByUser(testUser.getUser()).get().getSharedLinkId();

        mockMvc.perform(put(PathConstants.SHARED_LINK_ID_PATH, linkId))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void linkClickedTestNeg() throws Exception {

        MvcResult mvcResult = mockMvc.perform(put(PathConstants.SHARED_LINK_ID_PATH, "124"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String expected = "Invalid shared link id";
        String response = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals(expected, response);
    }

    @Test
    public void getOwnProfileTestPoz() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get(PathConstants.PROFILE, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId()))
                .andExpect(status().isOk())
                .andReturn();

        UserProfile expectedProfile = userProfileDAO.getProfileByUser(testUser.getUser()).get();
        UserProfile responseProfile = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), UserProfile.class);

        Assert.assertNotNull(responseProfile);
        Assert.assertNotNull(responseProfile.getKeys());
        Assert.assertNotNull(responseProfile.getOwnedGames());
        Assert.assertNotNull(responseProfile.getFriends());
        Assert.assertNotNull(responseProfile.getUser());
        Assert.assertNotNull(responseProfile.getSharedLink());

        Assert.assertEquals(expectedProfile, responseProfile);
    }

    @Test
    public void getOwnProfileTestNeg() throws Exception {

        TestUser userForTest = new TestUser();

        String userName = RandomStringUtils.randomAlphabetic(10);
        String pass = "passwordD1";
        String email = RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".com";

        User somebody = new User.Builder().name(userName).pass(passwordEncoder.encode(pass)).email(email).userType(User.UserType.CLIENT).build();
        userDAO.saveUser(somebody);
        somebody.setPassword(pass);

        String userJson = JsonMapper.objectToJsonWithView(somebody, JsonViews.Hidden.class);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseSessionId = mvcResult.getResponse().getHeader(HttpHeadersConstants.SESSION_ID);

        userForTest.setUser(somebody);
        userForTest.setSessionId(responseSessionId);

        mvcResult = mockMvc.perform(get(PathConstants.PROFILE, userForTest.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, userForTest.getSessionId()))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String expected = "No profile was found!";
        String response = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals(expected, response);
    }

    @Test
    public void getFriendProfileTestPoz() throws Exception {

        TestUser friendUser = UserTestUtils.getInstance().registerAndLoginUser(User.UserType.CLIENT, mockMvc);

        Friend friendship = new Friend();
        friendship.setUser(testUser.getUser());
        friendship.setFriend(friendUser.getUser());
        friendDAO.saveFriend(friendship);

        MvcResult mvcResult = mockMvc.perform(get(PathConstants.PROFILE_OF_FRIEND, testUser.getUser().getUserName(), friendUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId()))
                .andExpect(status().isOk())
                .andReturn();

        UserProfile response = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), UserProfile.class);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getFriends());
        Assert.assertNotNull(response.getUser());
        Assert.assertNotNull(response.getSharedLink());
        Assert.assertNotNull(response.getOwnedGames());

        Assert.assertNull(response.getKeys());
    }

    @Test
    public void getFriendProfileTestNeg() throws Exception {

        TestUser friendUser = UserTestUtils.getInstance().registerAndLoginUser(User.UserType.CLIENT, mockMvc);

        MvcResult mvcResult = mockMvc.perform(get(PathConstants.PROFILE_OF_FRIEND, testUser.getUser().getUserName(), friendUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId()))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String expected = "Users are not friend!";
        String response = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals(expected, response);
    }
}
