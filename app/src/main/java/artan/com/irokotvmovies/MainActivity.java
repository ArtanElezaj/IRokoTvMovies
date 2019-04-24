package artan.com.irokotvmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import artan.com.irokotvmovies.adapters.MoviesAdapter;
import artan.com.irokotvmovies.models.Movies;
import artan.com.irokotvmovies.services.MoviesService;
import artan.com.irokotvmovies.utils.AllUrlLinks;
import artan.com.irokotvmovies.utils.NetworkHelper;

public class MainActivity extends AppCompatActivity  implements
        BoomMenuButton.OnSubButtonClickListener,
        BoomMenuButton.AnimatorListener,
        View.OnClickListener {

    private RecyclerView popularRv, topRatedRv;
    private MoviesAdapter popularAdapater;
    private MoviesAdapter topRatedAdapter;
    List<Movies> moviesPopularList;
    List<Movies> moviesTopRatedList;

    private BoomMenuButton boomSort;
    private boolean isInit = false;
    private View customView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        popularRv = (RecyclerView) findViewById(R.id.popular_rv);
        topRatedRv = (RecyclerView) findViewById(R.id.top_rated_rv);

        setupToolbar();
        toolbarTransparent();

        boolean networkOk = NetworkHelper.hasNetworkAccess(this);

        popularAdapater = new MoviesAdapter();
        topRatedAdapter = new MoviesAdapter();

        setUpPopularAdapter();
        setUpTopRatedAdapter();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(AllUrlLinks.M_SERVICE_ARRAY_NAME));

        if (networkOk) {
            Intent intent = new Intent(this, MoviesService.class);
            intent.setData(Uri.parse(AllUrlLinks.JSON_URL_POPULAR));
            startService(intent);
            intent.setData(Uri.parse((AllUrlLinks.JSON_URL_TOP_RATED)));
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

    public void setUpPopularAdapter() {
        popularRv.setAdapter(popularAdapater);
        popularRv.setHasFixedSize(true);
        popularRv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void setUpTopRatedAdapter() {
        topRatedRv.setAdapter(topRatedAdapter);
        topRatedRv.setHasFixedSize(true);
        topRatedRv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Movies[] moviesPopular = (Movies[]) intent.getParcelableArrayExtra(AllUrlLinks.M_SERVICE_ARRAY_POPULAR_DATA);
            if(moviesPopular != null) {
                moviesPopularList = Arrays.asList(moviesPopular);
                popularAdapater.setData(moviesPopularList);
            }

            Movies[] moviesTopRated = (Movies[]) intent.getParcelableArrayExtra(AllUrlLinks.M_SERVICE_ARRAY_TOP_RATED_DATA);
            if(moviesTopRated != null) {
                moviesTopRatedList = Arrays.asList(moviesTopRated);
                topRatedAdapter.setData(moviesTopRatedList);
            }

//            if (moviesPopularList.get(0).getVote_average() <= 8 && moviesPopularList != null) {
//                popularAdapater.setData(moviesPopularList);
//                moviesTopRatedList = moviesPopularList;
//            } else {
//                topRatedAdapter.setData(moviesPopularList);
//            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mBroadcastReceiver);
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);
        customView = mInflater.inflate(R.layout.custom_actionbar, null);
        mActionBar.setCustomView(customView);
        mActionBar.setDisplayShowCustomEnabled(true);
        ((Toolbar) customView.getParent()).setContentInsetsAbsolute(0, 0);
        boomSort = (BoomMenuButton) customView.findViewById(R.id.boom_sort);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!isInit) {
            initInfoBoom();
        }
        isInit = true;
    }

    private void initInfoBoom() {

        Drawable[] drawables = new Drawable[4];
        int[] drawablesResource = new int[]{
                R.drawable.sort09,
                R.drawable.sort90,
                R.drawable.sortaz,
                R.drawable.sortza
        };
        for (int i = 0; i < drawables.length; i++)
            drawables[i] = ContextCompat.getDrawable(this, drawablesResource[i]);

        int[][] colors = new int[4][3];
        for (int i = 0; i < drawables.length; i++) {
            colors[i][2] = ContextCompat.getColor(this, R.color.colorBlack);
            colors[i][1] = ContextCompat.getColor(this, R.color.boom_menu);
            colors[i][0] = Util.getInstance().getPressedColor(colors[i][2]);
        }

        new BoomMenuButton.Builder()
                .subButtons(drawables, colors, new String[]{"Sort Vote Count Ascending", "Sort Vote Count Descending", "Sort Title Ascending", "Sort Title Descending"})
                .button(ButtonType.HAM)
                .boom(BoomType.PARABOLA_2)
                .place(PlaceType.HAM_4_1)
                .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .onSubButtonClick(new BoomMenuButton.OnSubButtonClickListener() {
                    @Override
                    public void onClick(int buttonIndex) {

                        if (buttonIndex == 0) {
                            Collections.sort(moviesPopularList, new Comparator<Movies>() {
                                @Override
                                public int compare(Movies element1, Movies element2) {
                                    return element1.getVote_count() - element2.getVote_count();
                                }
                            });
                            Collections.sort(moviesTopRatedList, new Comparator<Movies>() {
                                @Override
                                public int compare(Movies element1, Movies element2) {
                                    return element1.getVote_count() - element2.getVote_count();
                                }
                            });

                        } else if (buttonIndex == 1) {
                            Collections.sort(moviesPopularList, new Comparator<Movies>() {
                                @Override
                                public int compare(Movies element1, Movies element2) {
                                    return element2.getVote_count() - element1.getVote_count();
                                }
                            });
                            Collections.sort(moviesTopRatedList, new Comparator<Movies>() {
                                @Override
                                public int compare(Movies element1, Movies element2) {
                                    return element2.getVote_count() - element1.getVote_count();
                                }
                            });

                        } else if (buttonIndex == 2) {
                            Collections.sort(moviesPopularList, new Comparator<Movies>() {
                                @Override
                                public int compare(Movies element1, Movies element2) {
                                    return element1.getTitle().compareTo(element2.getTitle());
                                }
                            });
                            Collections.sort(moviesTopRatedList, new Comparator<Movies>() {
                                @Override
                                public int compare(Movies element1, Movies element2) {
                                    return element1.getTitle().compareTo(element2.getTitle());
                                }
                            });

                        } else if (buttonIndex == 3) {
                            Collections.sort(moviesPopularList, new Comparator<Movies>() {
                                @Override
                                public int compare(Movies element1, Movies element2) {
                                    return element2.getTitle().compareTo(element1.getTitle());
                                }
                            });
                            Collections.sort(moviesTopRatedList, new Comparator<Movies>() {
                                @Override
                                public int compare(Movies element1, Movies element2) {
                                    return element2.getTitle().compareTo(element1.getTitle());
                                }
                            });

                        }

                        popularAdapater.notifyDataSetChanged();
                        topRatedAdapter.notifyDataSetChanged();
                    }
                })
                .init(boomSort);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void toShow() {

    }

    @Override
    public void showing(float v) {

    }

    @Override
    public void showed() {

    }

    @Override
    public void toHide() {

    }

    @Override
    public void hiding(float v) {

    }

    @Override
    public void hided() {

    }

    @Override
    public void onClick(int i) {

    }

    @Override
    public void onBackPressed() {
        if (boomSort.isClosed()) {
            super.onBackPressed();

        } else {
            boomSort.dismiss();
        }
    }

}
