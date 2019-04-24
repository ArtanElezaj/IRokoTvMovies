package artan.com.irokotvmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artan on 6/9/2017.
 */

public class Movies implements Parcelable {

    private String id;
    private String poster_path;
    private String title;
    private int vote_count;

    public String getId() {
        return id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getTitle() {
        return title;
    }

    public int getVote_count() {
        return vote_count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.poster_path);
        dest.writeString(this.title);
        dest.writeInt(this.vote_count);
    }

    public Movies() {
    }

    protected Movies(Parcel in) {
        this.id = in.readString();
        this.poster_path = in.readString();
        this.title = in.readString();
        this.vote_count = in.readInt();
    }

    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel source) {
            return new Movies(source);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };
}
