import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.main.Application;
import com.practicaSV.gameLabz.repositories.UserDAO;
import com.practicaSV.gameLabz.utils.HttpHeadersConstants;
import com.practicaSV.gameLabz.utils.JsonViews;
import com.practicaSV.gameLabz.utils.PathConstants;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@IntegrationTest
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
//@TestPropertySource(properties = {"session.expiry.in.seconds=6"})
public class UserControllerTest {

    private TestUser testUser;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${session.expiry.in.seconds}")
    private Long sessionExpiryInSeconds;

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        testUser = UserTestUtils.getInstance().registerAndLoginUser(User.UserType.CLIENT, mockMvc);
    }

    @Test
    public void testRegisterUserOk() throws Exception {

        String userName = RandomStringUtils.randomAlphabetic(10);
        String pass = "passwordD1";
        String email = RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".com";

        User user = new User.Builder().name(userName).pass(pass).email(email).build();

        String userJson = JsonMapper.objectToJsonWithView(user, JsonViews.Hidden.class);

        mockMvc.perform(post(PathConstants.USERS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn();

    }

    @Test
    public void testRegisterUserNotOk() throws Exception {

        User user = new User.Builder().name("").pass("kjcnaAkj").email("ceva@ekfanjke.comwwfwfw").build();

        String userJson = JsonMapper.objectToJsonWithView(user, JsonViews.Hidden.class);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.USERS_PATH).header(HttpHeadersConstants.SESSION_ID, "4b1jbh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseHeader = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);
        String expectedHeader = "Username field is empty! Your password field is invalid! Your email field is invalid!";

        Assert.assertEquals(expectedHeader, responseHeader);

    }

    @Test
    public void testLoginOkay() throws Exception {

        String userName = RandomStringUtils.randomAlphabetic(10);
        String pass = "passwordD1";
        String email = RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".com";

        User user = new User.Builder().name(userName).pass(passwordEncoder.encode(pass)).email(email).build();

        userDAO.saveUser(user);

        user.setPassword(pass);

        String userJson = JsonMapper.objectToJsonWithView(user, JsonViews.Hidden.class);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseHeader = mvcResult.getResponse().getHeader(HttpHeadersConstants.SESSION_ID);
        Assert.assertNotNull(responseHeader);
    }

    @Test
    public void testLoginNotOkay() throws Exception {

        String userName = RandomStringUtils.randomAlphabetic(10);
        String pass = "passwordD1";
        String email = RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".com";

        User user = new User.Builder().name(userName).pass(passwordEncoder.encode(pass)).email(email).build();

        userDAO.saveUser(user);

        user.setPassword("aejhf");

        String userJson = JsonMapper.objectToJson(user);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String responseHeader = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);
        String expectedHeader = "Username or password is wrong!";

        Assert.assertEquals(responseHeader, expectedHeader);
    }

    @Test
    public void testUserUpdateOkay() throws Exception {

        testUser.getUser().setPassword("DaNewPass10");
        testUser.getUser().setEmail("new@email.com");

        String userJson = JsonMapper.objectToJsonWithView(testUser.getUser(), JsonViews.Hidden.class);

        MvcResult mvcResult = mockMvc.perform(put(PathConstants.USER_ID_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andReturn();

        User responseUser = JsonMapper.jsonToObject(mvcResult.getResponse().getContentAsString(), User.class);

        Assert.assertEquals(testUser.getUser().getEmail(), responseUser.getEmail());

    }

    @Test
    public void testUserUpdateNotOkay() throws Exception {

        testUser.getUser().setPassword("ks");
        testUser.getUser().setEmail("email.com");

        String userJson = JsonMapper.objectToJsonWithView(testUser.getUser(), JsonViews.Hidden.class);

        MvcResult mvcResult = mockMvc.perform(put(PathConstants.USER_ID_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseHeader = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals("Your password field is invalid! Your email field is invalid!", responseHeader);
    }

    @Test
    public void testUserUpdateEmpty() throws Exception {

        User userForJson = new User.Builder().name("").build();

        String userJson = JsonMapper.objectToJson(userForJson);

        MvcResult mvcResult = mockMvc.perform(put(PathConstants.USER_ID_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseHeader = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals("No new fields were inserted!", responseHeader);
    }

    @Test
    public void testSessionIdNotOkay() throws Exception {

        testUser.getUser().setPassword("DaNewPass10");
        testUser.getUser().setEmail("new@email.com");

        String userJson = JsonMapper.objectToJson(testUser.getUser());

        MvcResult mvcResult = mockMvc.perform(put(PathConstants.USER_ID_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, "akjefnekj1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String responseHeader = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertEquals("Invalid session id!", responseHeader);

    }

    @Test
    @Ignore
    public void testSessionExpireNotOkay() throws Exception {

        Thread.sleep(sessionExpiryInSeconds * 1000L + 1000L);

        String userJson = JsonMapper.objectToJson(testUser.getUser());

        mockMvc.perform(put(PathConstants.USER_ID_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @Ignore
    public void testSessionExpireOkay() throws Exception {

        Thread.sleep(sessionExpiryInSeconds * 500L);

        String userJson = JsonMapper.objectToJson(testUser.getUser());

        mockMvc.perform(put(PathConstants.USER_ID_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testLogoutOkay() throws Exception {

        mockMvc.perform(post(PathConstants.LOGOUT_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testDeleteUserOkay() throws Exception {

        mockMvc.perform(delete(PathConstants.USER_ID_PATH, testUser.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUser.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testAuthorizationNotOkay() throws Exception {

        TestUser testUserAdmin = UserTestUtils.getInstance().registerAndLoginUser(User.UserType.ADMIN, mockMvc);

        mockMvc.perform(delete(PathConstants.USER_ID_PATH, testUserAdmin.getUser().getUserName())
                .header(HttpHeadersConstants.SESSION_ID, testUserAdmin.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
