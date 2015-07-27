package com.zhou.nestedlayout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class RecyclerAdapter extends RecyclerView.Adapter<ReViewHolder>{

        String url = "http://l.yimg.com/a/i/us/we/52/%s.gif";
        Context context;

        public RecyclerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ReViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false);
            return new ReViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ReViewHolder holder, int position) {
            String imageUrl = url.replace("%s", String.valueOf(position));
            Picasso.with(context)
                    .load(imageUrl)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }

     class ReViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public ReViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.icon);
        }
    }
