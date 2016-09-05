package com.practicaSV.gameLabz.controllers;

import com.practicaSV.gameLabz.domain.GameOrder;
import com.practicaSV.gameLabz.exceptions.InvalidValueException;
import com.practicaSV.gameLabz.repositories.GameOfferDAO;
import com.practicaSV.gameLabz.repositories.GameOrderDAO;
import com.practicaSV.gameLabz.repositories.UserDAO;
import com.practicaSV.gameLabz.services.GameOrderService;
import com.practicaSV.gameLabz.utils.PathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = PathConstants.GAME_ORDER_PATH)
public class GameOrderController {

    @Autowired
    private GameOrderService gameOrderService;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private GameOrderDAO gameOrderDAO;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<GameOrder> makeOrder(@RequestBody GameOrder gameOrder, @PathVariable String userName) {

        gameOrder.setUser(userDAO.getUserByUserName(userName).get());

        GameOrder responseGameOrder = gameOrderService.execute(gameOrder);

        return new ResponseEntity(responseGameOrder, HttpStatus.CREATED);
    }
}
