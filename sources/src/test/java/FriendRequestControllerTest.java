import com.fasterxml.jackson.core.type.TypeReference;
import com.practicaSV.gameLabz.domain.Friend;
import com.practicaSV.gameLabz.domain.FriendRequest;
import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.domain.UserProfile;
import com.practicaSV.gameLabz.main.Application;
import com.practicaSV.gameLabz.repositories.FriendDAO;
import com.practicaSV.gameLabz.repositories.UserProfileDAO;
import com.practicaSV.gameLabz.utils.HttpHeadersConstants;
import com.practicaSV.gameLabz.utils.PathConstants;
import com.practicaSV.gameLabz.utils.websocket.TopicConstants;
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
import utils.JsonMapper;
import utils.TestUser;
import utils.UserTestUtils;

import java.util.Collection;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@IntegrationTest
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class FriendRequestControllerTest {

    private MockMvc mockMvc;

    private TestUser testUser;

    private TestUser testUserToWhom;

    private FriendRequest testFriendRequest;

    private static final TypeReference<List<FriendRequest>> FRIEND_REQUEST_TYPE_REF = new TypeReference<List<FriendRequest>>() {};

    @Autowired
    private UserProfileDAO userProfileDAO;

    @Autowired
    private FriendDAO friendDAO;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        testUser = UserTestUtils.getInstance().registerAndLoginUser(User.UserType.CLIENT, mockMvc);
        testUser.subscribe(TopicConstants.FRIEND_REQUEST);
        testUserToWhom = UserTestUtils.getInstance().registerAndLoginUser(User.UserType.CLIENT, mockMvc);
        testFriendRequest = createFriendRequest();
    }

    public FriendRequest createFriendRequest() throws Exception {

        testUserToWhom.subscribe(TopicConstants.FRIEND_REQUEST);

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setToWhom(testUserToWhom.getUser());

        String json = JsonMapper.objectToJson(friendRequest);

        mockMvc.perform(post(PathConstants.FRIEND_REQUEST_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return testUserToWhom.getMessageFromTopic(TopicConstants.FRIEND_REQUEST, FriendRequest.class);
    }

    @Test
    public void sendRequestTestPoz() throws Exception {

        TestUser testUserTo = UserTestUtils.getInstance().registerAndLoginUser(User.UserType.CLIENT, mockMvc);

        testUserTo.subscribe(TopicConstants.FRIEND_REQUEST);

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setToWhom(testUserTo.getUser());

        String json = JsonMapper.objectToJson(friendRequest);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.FRIEND_REQUEST_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        FriendRequest requestFromTopic = testUserTo.getMessageFromTopic(TopicConstants.FRIEND_REQUEST, FriendRequest.class);
        FriendRequest responseRequest = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), FriendRequest.class);

        Assert.assertNotNull(requestFromTopic);
        Assert.assertNotNull(requestFromTopic.getId());
        Assert.assertNotNull(requestFromTopic.getFromWho());
        Assert.assertNotNull(requestFromTopic.getToWhom());
        Assert.assertEquals(testUser.getUser(), requestFromTopic.getFromWho());
        Assert.assertEquals(testUserTo.getUser(), requestFromTopic.getToWhom());
        Assert.assertEquals(FriendRequest.Status.CREATED, requestFromTopic.getStatus());
        Assert.assertEquals(responseRequest.getStatus(), requestFromTopic.getStatus());

        Assert.assertNull(requestFromTopic.getToWhom().getPassword());
        Assert.assertNull(requestFromTopic.getFromWho().getPassword());

        testUserTo.unSubscribe(TopicConstants.FRIEND_REQUEST);
        testUserTo.disconnect();
    }

    @Test
    public void sendRequestTestNeg() throws Exception {

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setToWhom(testUser.getUser());

        String json = JsonMapper.objectToJson(friendRequest);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.FRIEND_REQUEST_PATH, testUserToWhom.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUserToWhom.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String expected = "Friend request already exists!";
        String response = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals(expected, response);
    }

    @Test
    public void cancelRequestTestPoz() throws Exception {

        mockMvc.perform(delete(PathConstants.FRIEND_REQUEST_ID_PATH, testUser.getUser().getUserName(), testFriendRequest.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId()))
                .andExpect(status().isOk())
                .andReturn();

        FriendRequest requestFromTopic = testUserToWhom.getMessageFromTopic(TopicConstants.FRIEND_REQUEST, FriendRequest.class);

        Assert.assertNotNull(requestFromTopic);
        Assert.assertNotNull(requestFromTopic.getId());
        Assert.assertNotNull(requestFromTopic.getFromWho());
        Assert.assertNotNull(requestFromTopic.getToWhom());

        Assert.assertEquals(testUser.getUser(), requestFromTopic.getFromWho());
        Assert.assertEquals(testUserToWhom.getUser(), requestFromTopic.getToWhom());
        Assert.assertEquals(FriendRequest.Status.CANCELLED, requestFromTopic.getStatus());

    }

    @Test
    public void cancelRequestTestNeg() throws Exception {

        MvcResult mvcResult = mockMvc.perform(delete(PathConstants.FRIEND_REQUEST_ID_PATH, testUserToWhom.getUser().getUserName(), testFriendRequest.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUserToWhom.getSessionId()))
                .andExpect(status().isForbidden())
                .andReturn();

        String expected = "Not authorized to make this call!";
        String response = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals(expected, response);
    }

    @Test
    public void acceptRequestTestPoz() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.FRIEND_REQUEST_ID_PATH, testUserToWhom.getUser().getUserName(), testFriendRequest.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUserToWhom.getSessionId())
                .param(FriendRequest.CHOICE, FriendRequest.Status.ACCEPTED.toString()))
                .andExpect(status().isOk())
                .andReturn();

        FriendRequest requestFromTopic = testUser.getMessageFromTopic(TopicConstants.FRIEND_REQUEST, FriendRequest.class);
        FriendRequest response = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), FriendRequest.class);

        UserProfile profileTestUser = userProfileDAO.getProfileByUser(testUser.getUser()).get();
        Collection<Friend> friendshipTestUser = friendDAO.getAll(testUser.getUser());

        UserProfile profileTestUserToWhom = userProfileDAO.getProfileByUser(testUserToWhom.getUser()).get();
        Collection<Friend> friendshipTestUserToWhom = friendDAO.getAll(testUserToWhom.getUser());

        Assert.assertNotNull(requestFromTopic);
        Assert.assertNotNull(requestFromTopic.getId());
        Assert.assertNotNull(requestFromTopic.getFromWho());
        Assert.assertNotNull(requestFromTopic.getToWhom());

        Assert.assertEquals(testUser.getUser(), requestFromTopic.getFromWho());
        Assert.assertEquals(testUserToWhom.getUser(), requestFromTopic.getToWhom());
        Assert.assertEquals(FriendRequest.Status.ACCEPTED, requestFromTopic.getStatus());

        Assert.assertNotNull(response.getId());
        Assert.assertNotNull(response.getFromWho());
        Assert.assertNotNull(response.getToWhom());
        Assert.assertEquals(testUser.getUser(), response.getFromWho());
        Assert.assertEquals(testUserToWhom.getUser(), response.getToWhom());

        Assert.assertTrue(profileTestUser.getFriends().containsAll(friendshipTestUser));
        Assert.assertTrue(profileTestUserToWhom.getFriends().containsAll(friendshipTestUserToWhom));
    }

    @Test
    public void acceptRequestTestNeg() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.FRIEND_REQUEST_ID_PATH, testUser.getUser().getUserName(), testFriendRequest.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .param(FriendRequest.CHOICE, FriendRequest.Status.ACCEPTED.toString()))
                .andExpect(status().isForbidden())
                .andReturn();

        String response = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);
        String expected = "Not authorized to make this call!";

        Assert.assertEquals(expected, response);
    }

    @Test
    public void rejectRequestTest() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.FRIEND_REQUEST_ID_PATH, testUserToWhom.getUser().getUserName(), testFriendRequest.getId())
                .header(HttpHeadersConstants.SESSION_ID, testUserToWhom.getSessionId())
                .param(FriendRequest.CHOICE, FriendRequest.Status.REJECTED.toString()))
                .andExpect(status().isOk())
                .andReturn();

        FriendRequest requestFromTopic = testUser.getMessageFromTopic(TopicConstants.FRIEND_REQUEST, FriendRequest.class);
        FriendRequest response = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), FriendRequest.class);

        Assert.assertNotNull(requestFromTopic);
        Assert.assertNotNull(requestFromTopic.getId());
        Assert.assertNotNull(requestFromTopic.getFromWho());
        Assert.assertNotNull(requestFromTopic.getToWhom());

        Assert.assertEquals(testUser.getUser(), requestFromTopic.getFromWho());
        Assert.assertEquals(testUserToWhom.getUser(), requestFromTopic.getToWhom());
        Assert.assertEquals(FriendRequest.Status.REJECTED, requestFromTopic.getStatus());

        Assert.assertEquals(FriendRequest.Status.REJECTED, response.getStatus());
    }

    @Test
    public void getAllRequestsTestPoz() throws Exception {

       MvcResult mvcResult = mockMvc.perform(get(PathConstants.FRIEND_REQUEST_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId()))
                .andExpect(status().isOk())
                .andReturn();

        List<FriendRequest> friendRequestList = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), FRIEND_REQUEST_TYPE_REF);

        Assert.assertEquals(testFriendRequest.getId(), friendRequestList.stream().findFirst().get().getId());
    }
}
