package com.example.trakkus.Remote;

import com.example.trakkus.Model.MyResponse;
import com.example.trakkus.Model.Request;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({

            "Content-Type:application/json",
            "Authorization:key=AAAA5d0l5-A:APA91bGWQqal2-HQ8G30_d5Oy7PBS0n4vk7DaQLJlgEql1FsxN1ZMnSPYhRRHTjzuOjqESUKr0PDary42-Djclw2dvWEByb2HVc43hFBmHA4BxcfV_C-YahwGlxlN2fu2FkQYiK5V3G5"

    })

    @POST("fcm/send")
    public Observable<MyResponse> sendFriendRequestToUser(@Body Request body);

}
