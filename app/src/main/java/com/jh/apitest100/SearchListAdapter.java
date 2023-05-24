package com.jh.apitest100;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.RecyclerViewHolders>{

//    리사이클러뷰 어뎁터

    private ArrayList<Movie> mMovieList;
    private LayoutInflater mInflate;
    private Context mContext;

    //constructor
    public SearchListAdapter(Context context, ArrayList<Movie> itemList) {
        this.mContext = context;
        this.mInflate = LayoutInflater.from(context);
        this.mMovieList = itemList;
    }

    @NonNull
    @Override
    public RecyclerViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.search_item, parent, false);
        RecyclerViewHolders viewHolder = new RecyclerViewHolders(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolders holder, @SuppressLint("RecyclerView") final int position) {
        // 포스터 출력
        // 베이스 url에 Poster_path 결합하면 해당 영화 포스터가 출력됨
        String url = "https://image.tmdb.org/t/p/w500" + mMovieList.get(position).getPoster_path();
        Glide.with(mContext)
                .load(url)
                .centerCrop()
                .crossFade()
                .into(holder.img_item);

        //각 아이템 클릭 이벤트 (포스터 클릭시)
        holder.itemView.setOnClickListener(view -> {
            // 영화정보창으로 이동
            // 아래 5개 정보를 putExtra해서 정보창에 띄움 (다른 원하는 정보도 정보창에 띄울 수 있음)
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra("title", mMovieList.get(position).getTitle());
            intent.putExtra("original_title", mMovieList.get(position).getOriginal_title());
            intent.putExtra("poster_path", mMovieList.get(position).getPoster_path());
            intent.putExtra("overview", mMovieList.get(position).getOverview());
            intent.putExtra("release_date", mMovieList.get(position).getRelease_date());
            mContext.startActivity(intent);
            Log.d("Adapter", "Clcked: " + position);
        });
    }

    @Override
    public int getItemCount() {
        return this.mMovieList.size();
    }


    //뷰홀더 - 따로 클래스 파일로 만들어도 된다.
    public static class RecyclerViewHolders extends RecyclerView.ViewHolder {
        public ImageView img_item;
        public TextView tv_item_title;
        public TextView tvContent;

        public RecyclerViewHolders(View itemView) {
            super(itemView);
            img_item = (ImageView) itemView.findViewById(R.id.img_item);
            tv_item_title = (TextView) itemView.findViewById(R.id.tv_item_title);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
        }
    }

}