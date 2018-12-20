package com.belaku.media;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by naveenprakash on 06/11/18.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {
     TextView textView;

    public MyViewHolder(View itemView) {
        super(itemView);

        textView = (TextView) itemView.findViewById(R.id.nameTxt);

    }
}
