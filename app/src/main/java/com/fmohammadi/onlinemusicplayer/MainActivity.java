package com.fmohammadi.onlinemusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    DatabaseReference mRef;

    TextView SongName, SongArtist;
    MediaPlayer mediaPlayer;

    //make new flag for play
    Boolean Play = true;
    ImageView play;
    ImageView pause;
    ImageView next;
    ImageView prev;

    Integer currentSongIndex = 0;

    SeekBar seekBar;
    TextView pass, due;
    Handler handler;
    String out, out2;
    Integer totalTime;

    ImageView heart, Repeat;

    boolean repeatSong = false;

    ArrayList<String> imageUrls = new ArrayList<>();
    ArrayList<String> musics = new ArrayList<>();
    ArrayList<String> artists = new ArrayList<>();
    ArrayList<String> musicUrls = new ArrayList<>();

    List<SlidersItems> sliderItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager2 = findViewById(R.id.viewpagerimageslider);

        mRef = FirebaseDatabase.getInstance().getReference();

        SongName = findViewById(R.id.songname);
        SongArtist = findViewById(R.id.songartist);

        heart = findViewById(R.id.heart);
        Repeat = findViewById(R.id.repeat);

        seekBar = findViewById(R.id.seek_bar);
        pass = findViewById(R.id.tv_pass);
        due = findViewById(R.id.tv_due);

        handler = new Handler();

        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);


        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // add all image url in list
                    imageUrls.add(ds.child("imageurl").getValue(String.class));

                    // add all Song Name and Artist Name in our list

                    musics.add(ds.child("music").getValue(String.class));
                    artists.add(ds.child("artist").getValue(String.class));

                    // add song urls
                    musicUrls.add(ds.child("musicurl").getValue(String.class));
                }
                for (int i = 0; i < imageUrls.size(); i++) {
                    sliderItems.add(new SlidersItems(imageUrls.get(i)));
                }

                viewPager2.setAdapter(new SliderAdapter(sliderItems));

                viewPager2.setClipToPadding(false);
                viewPager2.setClipChildren(false);

                viewPager2.setOffscreenPageLimit(3);
                viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                    @Override
                    public void transformPage(@NonNull View page, float position) {
                        page.setScaleY(1);
                    }
                });

                viewPager2.setPageTransformer(compositePageTransformer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);


                // make new Function
                init(viewPager2.getCurrentItem());

                // store value of index here
                currentSongIndex = viewPager2.getCurrentItem();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSongIndex < musicUrls.size() - 1) {
                    currentSongIndex = currentSongIndex + 1;
                } else {
                    currentSongIndex = 0;
                }
                viewPager2.setCurrentItem(currentSongIndex);
                init(currentSongIndex);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSongIndex > 0) {
                    currentSongIndex = currentSongIndex - 1;
                } else {
                    currentSongIndex = musicUrls.size() - 1;
                }
                viewPager2.setCurrentItem(currentSongIndex);
                init(currentSongIndex);
            }
        });

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String like = snapshot.child(String.valueOf(currentSongIndex + 1)).child("like").getValue(String.class);
                        if (like.equals("0")) {
                            heart.setImageResource(R.drawable.ic_favorite2);
                            mRef.child(String.valueOf(currentSongIndex + 1)).child("like").setValue("1");
                        } else {
                            heart.setImageResource(R.drawable.ic_favorite1);
                            mRef.child(String.valueOf(currentSongIndex + 1)).child("like").setValue("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        Repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String repeat = snapshot.child(String.valueOf(currentSongIndex + 1)).child("repeat").getValue(String.class);
                        if (repeat.equals("0")) {
                            Repeat.setImageResource(R.drawable.ic_repeat2);
                            mRef.child(String.valueOf(currentSongIndex + 1)).child("repeat").setValue("1");
                            repeatSong = true;
                            repeatSong();
                        } else {
                            Repeat.setImageResource(R.drawable.ic_repeat1);
                            mRef.child(String.valueOf(currentSongIndex + 1)).child("repeat").setValue("0");
                            repeatSong = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void init(int currentItem) {
        try {
            if (mediaPlayer.isPlaying())
                mediaPlayer.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pause.setVisibility(View.VISIBLE);
        play.setVisibility(View.INVISIBLE);
        Play = true;

        SongName.setText(musics.get(currentItem));
        SongArtist.setText(artists.get(currentItem));

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String like = snapshot.child(String.valueOf(currentSongIndex + 1)).child("like").getValue(String.class);
                String repeat = snapshot.child(String.valueOf(currentSongIndex + 1)).child("repeat").getValue(String.class);

                if (like.equals("0")) {
                    heart.setImageResource(R.drawable.ic_favorite2);
                } else {
                    heart.setImageResource(R.drawable.ic_favorite1);
                }

                if (repeat.equals("0")) {
                    Repeat.setImageResource(R.drawable.ic_repeat2);
                    repeatSong = false;
                } else {
                    Repeat.setImageResource(R.drawable.ic_repeat1);
                    repeatSong = true;

                    repeatSong();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(musicUrls.get(currentItem));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    initializeSeekBar();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void repeatSong() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (repeatSong) {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void initializeSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
        seekBar.setProgress(mCurrentPosition);

        MainActivity.this.runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);

                    out = String.format("%02d:%02d", seekBar.getProgress() / 60, seekBar.getProgress() % 60);
                    pass.setText(out);
                }
                handler.postDelayed(this, 1000);
            }
        });

        totalTime = mediaPlayer.getDuration() / 1000;
        out2 = String.format("%02d:%02d", totalTime / 60, totalTime % 60);
        due.setText(out2);

    }

    public void playPauseButton(View view) {
        if (Play) {
            Play = false;
            pause.setVisibility(View.INVISIBLE);
            play.setVisibility(View.VISIBLE);
            mediaPlayer.pause();
        } else {
            Play = true;
            pause.setVisibility(View.VISIBLE);
            play.setVisibility(View.INVISIBLE);
            mediaPlayer.start();
        }
    }
}