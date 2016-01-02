package com.android.example.plamena.moviesapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    private ImageAdapter mImageAdapter = null;

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        mImageAdapter = new ImageAdapter(getActivity(), new ArrayList<MovieData>());
        gridView.setAdapter(mImageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence seq = "Clicked " + position;
                Toast.makeText(getContext(), seq, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void updateMovies() {
        new FetchMoviesTask().execute("popularity.desc");
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, MovieData[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        public MovieData[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String sortOrder = params[0];
            String popularMoviesStr = null;

            try {
                final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort"; //popularity.desc
                final String APPKEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortOrder)
                        .appendQueryParameter(APPKEY_PARAM, Keys.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the reuqest to MovieDB and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                popularMoviesStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error closing stream", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            // Parse the data
            try {
                return getMoviesDataFromJson(popularMoviesStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON parse exception", e);
                return new MovieData[0];
            }
        }

        private int getPageNumber(String moviesJsonStr) throws JSONException {
            JSONObject obj = new JSONObject(moviesJsonStr);
            int page =  obj.getInt("page");
            return page;
        }

        /**
         *  Extract movie poster image paths from the results.
         *
         * @param moviesJsonStr
         * @return
         * @throws JSONException
         */
        private MovieData[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {

            String POSTER_PATH = "poster_path";
            String VOTE_AVERAGE = "vote_average";
            String ORIGINAL_TITLE = "original_title";
            String RELEASE_DATE = "release_date";
            String OVERVIEW = "overview";

            JSONObject obj = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = obj.getJSONArray("results");

            MovieData[] movies = new MovieData[moviesArray.length()];

            for(int i = 0; i < moviesArray.length(); i++) {
                JSONObject individualMovie = moviesArray.getJSONObject(i);
                String posterPath = individualMovie.getString(POSTER_PATH);
                String overview = individualMovie.getString(OVERVIEW);
                String originalTitle = individualMovie.getString(ORIGINAL_TITLE);
                Double voteAverage = individualMovie.getDouble(VOTE_AVERAGE);
                String releaseDate = individualMovie.getString(RELEASE_DATE);
                movies[i] = new MovieData(posterPath, overview, originalTitle, releaseDate, voteAverage);
            }

            return movies;
        }

        @Override
        protected void onPostExecute(MovieData[] results) {
            mImageAdapter.clear();
            for (MovieData movie : results) {
                mImageAdapter.add(movie);
            }
        }
    }
}
