package utils;

import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.domain.UserProfile;
import com.practicaSV.gameLabz.repositories.UserDAO;
import com.practicaSV.gameLabz.repositories.UserProfileDAO;
import com.practicaSV.gameLabz.utils.HttpHeadersConstants;
import com.practicaSV.gameLabz.utils.JsonViews;
import com.practicaSV.gameLabz.utils.PathConstants;
import com.practicaSV.gameLabz.utils.SpringContext;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserTestUtils {

    private UserDAO userDAO;

    private UserProfileDAO userProfileDAO;

    private PasswordEncoder passwordEncoder;

    private static final UserTestUtils INSTANCE = new UserTestUtils();

    public static UserTestUtils getInstance() {
        return INSTANCE;
    }

    private UserTestUtils() {
        this.userDAO = SpringContext.getApplicationContext().getBean(UserDAO.class);
        this.userProfileDAO = SpringContext.getApplicationContext().getBean(UserProfileDAO.class);
        this.passwordEncoder = SpringContext.getApplicationContext().getBean(PasswordEncoder.class);
    }

    public TestUser registerAndLoginUser(User.UserType userType, MockMvc mockMvc) throws Exception {

        TestUser testUserToReturn = new TestUser();

        String userName = RandomStringUtils.randomAlphabetic(10);
        String pass = "passwordD1";
        String email = RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".com";

        User somebody = new User.Builder().name(userName).pass(passwordEncoder.encode(pass)).email(email).userType(userType).build();

        userDAO.saveUser(somebody);

        String sharedLinkId = UUID.randomUUID().toString();
        UserProfile profile = new UserProfile.Builder().user(somebody).sharedLinkId(sharedLinkId).sharedLink(PathConstants.SHARED_LINK + "/"+ sharedLinkId).points(0L).build();
        userProfileDAO.saveProfile(profile);

        somebody.setPassword(pass);

        String userJson = JsonMapper.objectToJsonWithView(somebody, JsonViews.Hidden.class);

        MvcResult mvcResult = mockMvc.perform(post(PathConstants.LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andReturn();

        String errorMessageHeader = mvcResult.getResponse().getHeader(HttpHeadersConstants.ERROR_MESSAGE);

        Assert.assertNull(errorMessageHeader);

        String responseSessionId = mvcResult.getResponse().getHeader(HttpHeadersConstants.SESSION_ID);

        testUserToReturn.setUser(somebody);
        testUserToReturn.setSessionId(responseSessionId);

        return testUserToReturn;
    }
}
