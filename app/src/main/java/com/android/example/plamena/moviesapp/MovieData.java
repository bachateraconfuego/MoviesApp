package com.android.example.plamena.moviesapp;

/**
 * Created by plamenapetrova on 1/2/16.
 */
public class MovieData {

    private String posterPath;
    private String overview;
    private String releaseDate;

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    private String originalTitle;

    public double getVoteAverage() {
        return voteAverage;
    }

    private double voteAverage;

    public MovieData(String posterPath,
                     String overview,
                     String originalTitle,
                     String releaseDate,
                     double voteAverage) {
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.originalTitle = originalTitle;
        this.voteAverage = voteAverage;
    }
}
