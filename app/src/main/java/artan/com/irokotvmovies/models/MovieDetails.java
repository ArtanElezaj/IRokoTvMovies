package artan.com.irokotvmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artan on 6/10/2017.
 */

public class MovieDetails implements Parcelable {

    private String vote_average;
    private String runtime;
    private String id;
    private String title;
    private String overview;
    private String poster_path;

    public String getVote_average() {
        return vote_average;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.vote_average);
        dest.writeString(this.runtime);
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.poster_path);
    }

    public MovieDetails() {
    }

    protected MovieDetails(Parcel in) {
        this.vote_average = in.readString();
        this.runtime = in.readString();
        this.id = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.poster_path = in.readString();
    }

    public static final Parcelable.Creator<MovieDetails> CREATOR = new Parcelable.Creator<MovieDetails>() {
        @Override
        public MovieDetails createFromParcel(Parcel source) {
            return new MovieDetails(source);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };
}
