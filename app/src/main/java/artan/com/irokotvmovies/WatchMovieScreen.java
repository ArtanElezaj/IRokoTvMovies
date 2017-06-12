package artan.com.irokotvmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import artan.com.irokotvmovies.services.MoviesService;
import artan.com.irokotvmovies.utils.AllUrlLinks;
import artan.com.irokotvmovies.utils.NetworkHelper;

public class WatchMovieScreen extends YouTubeBaseActivity {

    YouTubePlayerView mYouTubePlayerView;
    YouTubePlayer.OnInitializedListener mOnInitializedListener;
    String ytWatchKey = "";
    String idMovie = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_movie_screen);

        Bundle bundle = getIntent().getExtras();
        idMovie = bundle.getString("idMovie");

        mYouTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_play);
        String movieURL = AllUrlLinks.BASE_URL_MOVIE_DETAILS + idMovie + AllUrlLinks.VIDEOS + AllUrlLinks.END_POINT_MOVIE_DETAILS;

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(AllUrlLinks.M_SERVICE_VIDEO_NAME));

        boolean networkOk = NetworkHelper.hasNetworkAccess(this);
        if (networkOk) {
            Intent intent = new Intent(this, MoviesService.class);
            intent.setData(Uri.parse(movieURL));
            startService(intent);
        } else {
            Toast.makeText(this, "Network not available!", Toast.LENGTH_SHORT).show();
        }

    }

    private void youtubeListener() {

        mOnInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(ytWatchKey);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ytWatchKey = intent.getStringExtra(AllUrlLinks.M_SERVICE_VIDEO_DATA);
            if (ytWatchKey.equals("null")){
                toast();
            }else {
                youtubeListener();
                mYouTubePlayerView.initialize(AllUrlLinks.YOUTUBE_API_KEY, mOnInitializedListener);
            }
        }
    };

    private void toast() {
        super.onBackPressed();
        Toast.makeText(this, "We are sorry, there is no trailer for this movie!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mBroadcastReceiver);
    }
}
