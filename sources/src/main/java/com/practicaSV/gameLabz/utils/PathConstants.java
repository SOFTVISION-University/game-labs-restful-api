package com.practicaSV.gameLabz.utils;

public class PathConstants {

    public static final String USER_NAME_KEY = "userName";

    public static final String GAME_ID_KEY = "gameId";

    public static final String GAME_OFFER_ID_KEY = "gameOfferId";

    //   USERS_PATH CONTROLLER

    public static final String USERS_PATH = "/users";

    public static final String LOGIN_PATH = "/login";

    public static final String USER_ID_PATH = USERS_PATH +"/{"+ USER_NAME_KEY +"}";

    public static final String LOGOUT_PATH = USERS_PATH +"/{"+ USER_NAME_KEY +"}/logout";

    // ADMIN CONTROLLER

    public static final String ADMINS_PATH = "/admins";

    public static final String GAMES_PATH = ADMINS_PATH + "/{" + USER_NAME_KEY + "}/games";

    public static final String GAME_ID_PATH = GAMES_PATH + "/{"+ GAME_ID_KEY +"}";

    public static final String GAME_OFFERS_PATH = ADMINS_PATH + "/{"+ USER_NAME_KEY +"}/gameOffers";

    public static final String GAME_OFFERS_PATH_PUBLIC = USERS_PATH + "/{"+ USER_NAME_KEY +"}/gameOffers";

    public static final String GAME_OFFER_ID_PATH = GAME_OFFERS_PATH + "/{"+ GAME_OFFER_ID_KEY +"}";

    //FRIEND REQUEST CONTROLLER

    public static final String FRIEND_REQUEST_PATH = USER_ID_PATH + "/firendRequest";

    public static final String FRIEND_REQUEST_ID_KEY = "friendRequestId";

    public static final String FRIEND_REQUEST_ID_PATH = FRIEND_REQUEST_PATH + "/{" + FRIEND_REQUEST_ID_KEY + "}";

    // GAME ORDER CONTROLLER

    public static final String GAME_ORDER_PATH = USER_ID_PATH + "/gameOrder";

    // USER PROFILE CONTROLLER

    public static final String SHARED_LINK = "/share";

    public static final String SHARED_LINK_ID_KEY = "sharedLinkId";

    public static final String SHARED_LINK_ID_PATH = SHARED_LINK + "/{"+ SHARED_LINK_ID_KEY +"}";

    public static final String FRIEND_ID_KEY = "friendUserName";

    public static final String PROFILE = USER_ID_PATH + "/profile";

    public static final String PROFILE_OF_FRIEND = PROFILE + "/{"+FRIEND_ID_KEY+"}";

    // GAME RATING CONTROLLER

    public static final String RATING = "rating";

    public static final String GAME_RATING = "/gameRating";

    public static final String GAME_RATING_PATH = USER_ID_PATH + GAME_RATING;

    public static final String GAME_RATING_ID_PATH = GAME_RATING_PATH + "/{" + GAME_ID_KEY + "}";
}
