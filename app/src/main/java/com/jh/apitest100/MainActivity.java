package com.jh.apitest100;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);
    private RecyclerView recyclerView;

    private MyRecyclerViewAdapter adapter;
    ArrayList<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        movieList = new ArrayList<>();

        // Retrofit initialization
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of the API service
        ApiService apiService = retrofit.create(ApiService.class);

        // API request
        String apiKey = "000ddb7fd6589cf82b163f9d79e7e8c1";
        Call<MovieResponse> call = apiService.getUpcomingMovies(apiKey, "ko-KR", "no");

        // Asynchronously execute the API request
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    if (movieResponse != null) {
                        ArrayList<Movie> movies = movieResponse.getResults();
                        movieList.addAll(movies);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to fetch movies", Toast.LENGTH_SHORT).show();
            }
        });

        // LayoutManager
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));

        Toolbar toolbar = findViewById(R.id.movieToolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed("버튼을 두 번 누르면 종료됩니다.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.search) {
            Intent search = new Intent(MainActivity.this, Search.class);
            startActivity(search);
            return true;
        } else if (itemId == R.id.myPage) {
            Intent mypage = new Intent(MainActivity.this, Mypage.class);
            startActivity(mypage);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}