//tanveer kaur
package com.example.trakkus.Utils;

import com.example.trakkus.Model.User;
import com.example.trakkus.Remote.IFCMService;
import com.example.trakkus.Remote.RetrofitClient;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Commonx {
    public static final String USER_INFORMATION = "UserInformation"; //get userInformation
    public static final String USER_UID_SAVE_KEY = "SaveUid";//get Userz Unique id
    public static final String TOKENS = "Tokens"; //firebase_uid class
    public static final String FROM_NAME = "FromName"; //3rd party username
    public static final String ACCEPT_LIST = "acceptList";// Your accept list
    public static final String FROM_UID = "FromUid"; //#rd party user id
    public static final String TO_UID = "ToUid"; //Your own user id
    public static final String TO_NAME = "ToName"; //Your name
    public static final String FRIEND_REQUEST = "FriendRequests"; //for show friend request
    public static final String PUBLIC_LOCATION = "PublicLocation"; // for show location
    public static User loggedUser; //check the user is already logged or not?
    public static User trackingUser; // for tracking the user
    public static User userProfile;


    //class for Firebase Cloud  messasging services
    public static IFCMService getFCMService() {

        return RetrofitClient.getClient("https://fcm.googleapis.com/")
                .create(IFCMService.class);
    }

    //class for get Current time.
    public static Date convertTimeStampintoDate(long time) {
        return new Date(new Timestamp(time).getTime());
    }

    public static String getDateFormatted(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date).toString();
    }
}

