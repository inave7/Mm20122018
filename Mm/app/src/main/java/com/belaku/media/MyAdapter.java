package com.belaku.media;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by naveenprakash on 06/11/18.
 */

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

    Context c;
    ArrayList<String> sNames;
    CustomItemClickListener listener;

    public MyAdapter(Context c, ArrayList<String> names, CustomItemClickListener listener) {
        this.c = c;
        this.sNames = names;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs, parent, false);
        final MyViewHolder mViewHolder = new MyViewHolder(mView);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, mViewHolder.getPosition());
            }
        });

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        //BIND DATA
        holder.textView.setText(sNames.get(position));

    }

    @Override
    public int getItemCount() {
        return sNames.size();
    }


}
