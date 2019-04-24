package artan.com.irokotvmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import artan.com.irokotvmovies.models.MovieDetails;
import artan.com.irokotvmovies.services.MoviesService;
import artan.com.irokotvmovies.utils.AllUrlLinks;
import artan.com.irokotvmovies.utils.NetworkHelper;

public class MovieDetailsScreen extends AppCompatActivity {

    TextView titleTv, synopsisTv, durationTv, ratingTv;
    ImageView posterPathImg;
    String movieId = "";
    MovieDetails movieDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_screen);

        toolbarTransparent();

        titleTv = (TextView) findViewById(R.id.title_tv);
        synopsisTv = (TextView) findViewById(R.id.synopsis_tv);
        durationTv = (TextView) findViewById(R.id.duration_tv);
        ratingTv = (TextView) findViewById(R.id.rating_tv);
        posterPathImg = (ImageView) findViewById(R.id.poster_path_detail_img);

        Bundle bundle = getIntent().getExtras();
        movieId = bundle.getString("MovieId");

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(AllUrlLinks.M_SERVICE_DETAILS_NAME));

        boolean networkOk = NetworkHelper.hasNetworkAccess(this);
        if (networkOk) {
            Intent intent = new Intent(this, MoviesService.class);
            intent.setData(Uri.parse(AllUrlLinks.BASE_URL_MOVIE_DETAILS + movieId + AllUrlLinks.END_POINT_MOVIE_DETAILS));
            startService(intent);
        } else {
            Toast.makeText(this, "Network not available!", Toast.LENGTH_SHORT).show();
        }
    }

    private void toolbarTransparent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusBarColor));
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            movieDetailsList = (MovieDetails) intent.getParcelableExtra(AllUrlLinks.M_SERVICE_DETAILS_DATA);
            setData();
        }
    };

    private void setData() {
        Picasso.with(this).load(AllUrlLinks.BASE_URL_IMAGE + movieDetailsList.getPoster_path()).into(posterPathImg);
        titleTv.setText(movieDetailsList.getTitle());
        synopsisTv.setText(movieDetailsList.getOverview());
        ratingTv.setText(ratingTv.getText().toString() + movieDetailsList.getVote_average().substring(0,3) + "/10");

        int runtime = Integer.parseInt(movieDetailsList.getRuntime());
        int hours = runtime / 60;
        int minutes = runtime % 60;
        durationTv.setText(hours + "hrs " + minutes + "min");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mBroadcastReceiver);
    }

    public void watchMovie(View view) {
        Intent intent = new Intent(this, WatchMovieScreen.class);
        intent.putExtra("idMovie", movieId);
        startActivity(intent);
    }

    public void onArrowClick(View view) {
        super.onBackPressed();
    }
}
