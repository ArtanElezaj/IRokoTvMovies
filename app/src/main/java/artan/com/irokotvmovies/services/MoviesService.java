package artan.com.irokotvmovies.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import artan.com.irokotvmovies.models.MovieDetails;
import artan.com.irokotvmovies.models.MovieVideo;
import artan.com.irokotvmovies.models.Movies;
import artan.com.irokotvmovies.utils.AllUrlLinks;
import artan.com.irokotvmovies.utils.HttpHelper;

/**
 * Created by Artan on 6/9/2017.
 */

public class MoviesService extends IntentService {

    public MoviesService() {
        super("MoviesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Uri uri = intent.getData();

        String response;
        try {
            response = HttpHelper.downloadUrl(uri.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Gson gson = new Gson();

        String checkWhichUrl = uri.toString();

        try {
            if (checkWhichUrl.contains("videos")) {
                MovieVideo movieVideo = gson.fromJson(response, MovieVideo.class);
                String ytVideoKey;
                try {
                    ytVideoKey = movieVideo.getResults()[0].getKey();
                } catch (Exception e) {
                    ytVideoKey = "null";
                }
                Intent messageIntent = new Intent(AllUrlLinks.M_SERVICE_VIDEO_NAME);
                messageIntent.putExtra(AllUrlLinks.M_SERVICE_VIDEO_DATA, ytVideoKey);
                LocalBroadcastManager manager =
                        LocalBroadcastManager.getInstance(getApplicationContext());
                manager.sendBroadcast(messageIntent);
            } else if (checkWhichUrl.contains("popular")) {
                response = response.substring(60, response.length() - 1);
                Movies[] movies = gson.fromJson(response, Movies[].class);
                Intent messageIntent = new Intent(AllUrlLinks.M_SERVICE_ARRAY_NAME);
                messageIntent.putExtra(AllUrlLinks.M_SERVICE_ARRAY_POPULAR_DATA, movies);
                LocalBroadcastManager manager =
                        LocalBroadcastManager.getInstance(getApplicationContext());
                manager.sendBroadcast(messageIntent);
            } else if (checkWhichUrl.contains("top_rated")) {
                response = response.substring(59, response.length() - 1);
                Movies[] movies = gson.fromJson(response, Movies[].class);
                Intent messageIntent = new Intent(AllUrlLinks.M_SERVICE_ARRAY_NAME);
                messageIntent.putExtra(AllUrlLinks.M_SERVICE_ARRAY_TOP_RATED_DATA, movies);
                LocalBroadcastManager manager =
                        LocalBroadcastManager.getInstance(getApplicationContext());
                manager.sendBroadcast(messageIntent);
            } else {
                MovieDetails movieDetails = gson.fromJson(response, MovieDetails.class);
                Intent messageIntent = new Intent(AllUrlLinks.M_SERVICE_DETAILS_NAME);
                messageIntent.putExtra(AllUrlLinks.M_SERVICE_DETAILS_DATA, movieDetails);
                LocalBroadcastManager manager =
                        LocalBroadcastManager.getInstance(getApplicationContext());
                manager.sendBroadcast(messageIntent);
            }
        } catch (JsonSyntaxException e) {
            Toast.makeText(getApplicationContext(), "Can not fetch data!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
