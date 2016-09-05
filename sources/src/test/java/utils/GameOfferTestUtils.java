package utils;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.GameOffer;
import com.practicaSV.gameLabz.repositories.GameOfferDAO;
import com.practicaSV.gameLabz.utils.SpringContext;

import java.math.BigDecimal;
import java.util.Arrays;

public class GameOfferTestUtils {

    private GameOfferDAO gameOfferDAO;

    private static final GameOfferTestUtils INSTANCE = new GameOfferTestUtils();

    public static GameOfferTestUtils getInstance() {
        return INSTANCE;
    }

    private GameOfferTestUtils() {
        this.gameOfferDAO = SpringContext.getApplicationContext().getBean(GameOfferDAO.class);
    }

    public GameOffer addNewOffer() throws Exception {

        GameOffer gameOffer = new GameOffer();

        Game game1 = GameTestUtils.getInstance().addNewgame();
        Game game2 = GameTestUtils.getInstance().addNewgame();

        gameOffer.setGames(Arrays.asList(game1, game1, game1, game1, game2, game2));
        gameOffer.setPriceCash(BigDecimal.valueOf(12.42));
        gameOffer.setPricePoints(5L);
        gameOffer.setPromotion(BigDecimal.valueOf(10L));
        gameOffer.setOfferType(GameOffer.OfferType.BUNDLE);

        gameOfferDAO.saveGameOffer(gameOffer);

        return gameOffer;
    }
}
