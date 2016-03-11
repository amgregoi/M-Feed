package com.teioh.m_feed.Utils.MAL;


import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

public interface MALService {

    //NOTE: I'm using this until they update the release of their spotify api to include this clal

    @GET("/account/verify_credentials.xml")
    void verifyUserAccount(Callback<String> callback);

//    @GET("/users/{user_id}/playlists/{playlist_id}/tracks")
//    void getPlaylistTracks(@Header("Authorization") String token, @Path("user_id") String userId, @Path("playlist_id") String playlistId, SpotifyCallback<Pager<PlaylistTrack>> callback);



}
