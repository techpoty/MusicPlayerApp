package com.tonevellah.musicplayerapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.tonevellah.musicplayerapp.R;
import com.tonevellah.musicplayerapp.model.Song;
import com.tonevellah.musicplayerapp.view.MainActivity;
import com.tonevellah.musicplayerapp.view.PlayerViewFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //member variable
    List<Song> songs;
    ExoPlayer player;
    //constructor


    public SongsAdapter(List<Song> songs, ExoPlayer player) {
        this.songs = songs;
        this.player = player;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.song_row_item,parent,false);

        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        //current song and view holder
        int pos = position;
        Song song = songs.get(position);
        SongViewHolder viewHolder = (SongViewHolder) holder;

        //set values to views
        viewHolder.titleHolder.setText(song.getName());
        viewHolder.sizeHolder.setText(getSize(song.getSize()));
        viewHolder.numberHolder.setText(String.valueOf(position+1));
        viewHolder.durationHolder.setText(getDuration(song.getDuration()));

        //album art
        Uri albumartUri = song.getAlbumartUri();
        if (albumartUri != null){
            viewHolder.albumartHolder.setImageURI(albumartUri);

            if (viewHolder.albumartHolder.getDrawable() == null){
                viewHolder.albumartHolder.setImageResource(R.drawable.default_albumart);
            }
        }
        else {
            viewHolder.albumartHolder.setImageResource(R.drawable.default_albumart);
        }

        //onclick item
        viewHolder.rowItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //media item
                MediaItem mediaItem = getMediaItem(song);
                if (!player.isPlaying()){
                    player.setMediaItems(getMediaItems(),pos,0);
                }else {
                    player.pause();
                    player.seekTo(pos,0);
                }
               player.prepare();
                player.play();
                Toast.makeText(view.getContext(),"Playing: "+song.getName(), Toast.LENGTH_LONG).show();

                // launch a Player Fragment
                goToPlayerViewFragment(view.getContext());
            }
        });
    }

    private void goToPlayerViewFragment(Context context) {
        PlayerViewFragment playerViewFragment = new PlayerViewFragment();
        String fragmentTag = playerViewFragment.getClass().getName();
        ((MainActivity)context).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_container, playerViewFragment)
                .addToBackStack(fragmentTag)
                .commit();
    }

    private List<MediaItem> getMediaItems() {
        List<MediaItem> mediaItems = new ArrayList<>();
        for (Song song : songs){
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(song.getUri())
                    .setMediaMetadata(getMetadata(song))
                    .build();
            mediaItems.add(mediaItem);
        }

        return mediaItems;
    }

    private MediaItem getMediaItem(Song song) {
        return new MediaItem.Builder()
                .setUri(song.getUri())
                .setMediaMetadata(getMetadata(song))
                .build();
    }

    private MediaMetadata getMetadata(Song song) {
       return new MediaMetadata.Builder()
                .setTitle(song.getName())
                .setArtworkUri(song.getAlbumartUri())
                .build();
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    //view holder
    public  static  class SongViewHolder extends RecyclerView.ViewHolder{

        //member variables
        ConstraintLayout rowItemLayout;
        ImageView albumartHolder;
        TextView numberHolder, titleHolder,durationHolder,sizeHolder;
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            rowItemLayout = itemView.findViewById(R.id.rowItemLayout);
            albumartHolder = itemView.findViewById(R.id.albumart);
            numberHolder = itemView.findViewById(R.id.number);
            titleHolder = itemView.findViewById(R.id.title);
            sizeHolder = itemView.findViewById(R.id.size);
            durationHolder = itemView.findViewById(R.id.duration);
        }
    }

    @SuppressLint("DefaultLocale")
    private String getDuration(int totalDuration){
        String totalDurationText;
            int hrs = totalDuration/(1000*60*60);
            int min = (totalDuration%(1000*60*60))/(1000*60);
            int secs = (((totalDuration%(1000*60*60))%(1000*60*60))%(1000*60))/1000;

            if (hrs<1){ totalDurationText = String.format("%02d:%02d", min, secs); }
            else{
                totalDurationText = String.format("%1d:%02d:%02d", hrs, min, secs);
        }
        return  totalDurationText;
    }

    private  String getSize(long bytes){
        String hrSize;

        double k = bytes/1024.0;
        double m = ((bytes/1024.0)/1024.0);
        double g = (((bytes/1024.0)/1024.0)/1024.0);
        double t = ((((bytes/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format((double) bytes).concat(" Bytes");
        }

        return hrSize;
    }
}
