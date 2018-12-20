package com.belaku.media;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainFragment extends Fragment {
    private static ArrayList<String> createList= new ArrayList<>();
    // Store instance variables
    private String title;
    private int page;
    private static ArrayList<AudioSong> aListAudioSongs = new ArrayList<>();
    private MyAdapter adapter;
    private static Context mContext;

    // newInstance constructor for creating fragment with arguments
    public static MainFragment newInstance(Context context, int page, String title, ArrayList<AudioSong> arrayList) {
        mContext = context;
        aListAudioSongs = arrayList;
        MainFragment fragmentFirst = new MainFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("title", title );

            for (int i = 0 ; i < arrayList.size(); i++) {
                if (page == 0)
                    createList.add(arrayList.get(i).getTitle());
                else if (page == 1)
                    createList.add(arrayList.get(i).getAlbum());
                else if (page == 2)
                    createList.add(arrayList.get(i).getArtist());
            }

        Log.d("SIZZEbefore", title + " - " + createList.size());

        Set<String> set = new HashSet<String>(createList);
        ArrayList<String> clist = new ArrayList<String>(set);

        Log.d("SIZZEafter", title + " - " + clist.size());

        args.putStringArrayList("clist", clist);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("title");
                createList = getArguments().getStringArrayList("clist");
        Log.d("SIZZEhereTHEN", title + " - " + createList.size());
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        Log.d("SIZZEhereNOW", title + " - " + createList.size());
        adapter = new MyAdapter(getActivity(), createList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                makeToast("clicked position:" + position + " - - - " + createList.get(position));
                // do what ever you want to do with it
            }
    });
        return view;
    }


    private void makeToast(String s) {
            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }
}