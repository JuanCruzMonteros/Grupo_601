package data.remote;



import data.model.EventPost;
import data.model.Post;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIService {

    @POST("register")
    Call<Post> createPost(@Body Post post);

    @POST("login")
    Call<Post> loginUser(@Body Post post);

    @POST("event")
    Call<EventPost> registerEvent(@Header ("token") String token,
                                  @Body EventPost eventPost);

}