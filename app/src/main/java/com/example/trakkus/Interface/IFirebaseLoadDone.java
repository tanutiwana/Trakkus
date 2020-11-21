package com.example.trakkus.Interface;

import java.util.List;

public interface IFirebaseLoadDone {
    public void onFirebaseLoadUserNameDone(List<String> lstEmail);

    public void onFirebaseLoadFailed(String message);


}
