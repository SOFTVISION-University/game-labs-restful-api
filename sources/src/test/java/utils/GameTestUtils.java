package utils;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.repositories.GameDAO;
import com.practicaSV.gameLabz.utils.SpringContext;
import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public class GameTestUtils {

    private GameDAO gameDAO;

    private static final GameTestUtils INSTANCE = new GameTestUtils();

    public static GameTestUtils getInstance() { return INSTANCE; }

    private GameTestUtils() {
        this.gameDAO = SpringContext.getApplicationContext().getBean(GameDAO.class);
    }

    public Game addNewgame() throws Exception {

        List<Game.GameGenre> gameGenres = new ArrayList<>();

        gameGenres.add(Game.GameGenre.FPS);
        gameGenres.add(Game.GameGenre.RPG);

        String name = RandomStringUtils.randomAlphabetic(10);
        String description = RandomStringUtils.randomAlphabetic(15);

        Game game = new Game.Builder().name(name).desc(description).relDate(21842743L).genres(gameGenres).build();

        gameDAO.saveGame(game);

        return game;
    }
}
