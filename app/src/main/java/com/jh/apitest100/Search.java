package com.jh.apitest100;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Search extends AppCompatActivity {

    private RecyclerView search_recycler_view;
    private SearchListAdapter adapter;
    private TextView resultTextView;

    ArrayList<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_recycler_view = findViewById(R.id.search_recycler_view);
        movieList = new ArrayList<>();

        // LayoutManager
        search_recycler_view.setLayoutManager(new LinearLayoutManager(Search.this));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 뒤로가기 버튼을 툴바에 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed(); // 뒤로가기 버튼을 클릭하면 액티비티 종료
        });

        // 커스텀 서치뷰 레이아웃을 툴바에 설정
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View searchViewLayout = inflater.inflate(R.layout.custom_search_view, toolbar, false);
        toolbar.addView(searchViewLayout);

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setQueryHint("프로그램, 영화, 배우 검색...");  // 원하는 힌트로 변경
        searchView.setIconified(false); // SearchView 확장

        resultTextView = findViewById(R.id.result_text_view); // 결과를 표시할 TextView

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);

        searchView.setOnClickListener(v -> {
            searchView.setIconified(false); // SearchView 확장
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색어 제출 시 처리할 내용을 작성합니다.
                performSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 검색어가 변경될 때마다 처리할 내용을 작성합니다.
                resultTextView.setText(newText); // 텍스트뷰에 검색어 표시
                performSearch(newText);
                return true;
            }
        });
    }

    private void performSearch(String query) {
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
                        movieList.clear();
                        movieList.addAll(movies);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("Search", "Failed to fetch movies: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("Search", "Failed to fetch movies: " + t.getMessage());
            }
        });

        // Show progress dialog
//        ProgressDialog progressDialog = new ProgressDialog(Search.this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setMessage("\t로딩중...");
//        progressDialog.show();
//        progressDialog.setOnDismissListener(dialog -> {
//            call.cancel();
//        });

        // Adapter setup
        adapter = new SearchListAdapter(Search.this, movieList);
        search_recycler_view.setAdapter(adapter);

        // Dismiss progress dialog and notify adapter after the search request completes
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
//                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
//                progressDialog.dismiss();
                Log.e("Search", "Search request failed: " + t.getMessage());
            }
        });
    }
}