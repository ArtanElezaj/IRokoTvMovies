package artan.com.irokotvmovies.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import artan.com.irokotvmovies.MovieDetailsScreen;
import artan.com.irokotvmovies.R;
import artan.com.irokotvmovies.models.Movies;
import artan.com.irokotvmovies.utils.AllUrlLinks;
import artan.com.irokotvmovies.utils.AnimationUtil;

/**
 * Created by Artan on 6/9/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesVHolder> {

    private List<Movies> moviesData;
    private int previousPosition = 0;

    public MoviesAdapter() {
        moviesData = new ArrayList<>();
    }

    public void setData(List<Movies> data) {
        this.moviesData = data;
        notifyDataSetChanged();
    }

    @Override
    public MoviesVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.homescreen_viewholder, parent, false);
        return new MoviesVHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesVHolder holder, int position) {
        Picasso.with(holder.posterPathImg.getContext()).load(AllUrlLinks.BASE_URL_IMAGE + moviesData.get(position).getPoster_path()).into(holder.posterPathImg);
        holder.id = moviesData.get(position).getId();

        if (position > previousPosition){
            AnimationUtil.animate(holder, true);
        }else{
            AnimationUtil.animate(holder, false);
        }
        previousPosition = position;
    }

    @Override
    public int getItemCount() {
        return moviesData.size();
    }

    public class MoviesVHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView posterPathImg;
        String id = "";

        public MoviesVHolder(View itemView) {
            super(itemView);
            posterPathImg = (ImageView) itemView.findViewById(R.id.poster_path_img);
            posterPathImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), MovieDetailsScreen.class);
            intent.putExtra("MovieId", id);
            view.getContext().startActivity(intent);
        }
    }
}
