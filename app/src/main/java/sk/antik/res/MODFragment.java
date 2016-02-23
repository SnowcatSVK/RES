package sk.antik.res;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import sk.antik.res.logic.Album;
import sk.antik.res.logic.MODFolderAdapter;
import sk.antik.res.logic.MODSongAdapter;
import sk.antik.res.logic.Song;
import sk.antik.res.player.CustomPlayer;
import sk.antik.res.player.ExtractorRendererBuilder;


/**
 * A simple {@link Fragment} subclass.
 */
public class MODFragment extends Fragment implements CustomPlayer.Listener, CustomPlayer.CaptionListener, CustomPlayer.Id3MetadataListener,
        AudioCapabilitiesReceiver.Listener {

    private ListView foldersListView;
    private ListView songsListView;
    private boolean playerNeedsPrepare;
    public Uri contentUri;
    private CustomPlayer player;
    private AudioCapabilities audioCapabilities;
    private SeekBar volumeSeekbar = null;
    private SeekBar progressSeekBar = null;
    private AudioManager audioManager = null;
    private ImageButton playPauseButton = null;
    private ImageButton stopButton = null;
    private ImageButton nextSongButton = null;
    private ImageButton previousSongButton = null;
    private boolean playingVideo = false;
    private ImageView albumImageView;
    private TextView albumNameTextView;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private DisplayImageOptions options;

    private Timer t;

    private ArrayList<Album> albums;

    private int playSongParentId = -1;
    private int playSongPosition = -1;
    private int albumPosition = -1;
    private int songsAdapterLength = -1;

    public MODFragment() {
        // Required empty public constructor
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .resetViewBeforeLoading(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mod, container, false);

        foldersListView = (ListView) view.findViewById(R.id.folders_listView);
        songsListView = (ListView) view.findViewById(R.id.songs_listView);

        MODFolderAdapter albumAdapter = new MODFolderAdapter(getActivity(), albums);

        foldersListView.setAdapter(albumAdapter);

        /*ArrayList<String> folders = new ArrayList<>();
        folders.add("ABBA - Arrival");
        folders.add("Queen - Innuendo");
        folders.add("Oldfield - The Songs of Distant Earth");
        folders.add("ABBA - Arrival");
        folders.add("Queen - Innuendo");
        folders.add("Oldfield - The Songs of Distant Earth");
        folders.add("ABBA - Arrival");
        folders.add("Queen - Innuendo");
        folders.add("Oldfield - The Songs of Distant Earth");
        folders.add("ABBA - Arrival");
        folders.add("Queen - Innuendo");
        folders.add("Oldfield - The Songs of Distant Earth");*/

        /*ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song("01", "In The Beginning", "01:24"));
        songs.add(new Song("02", "Let There Be Light", "04:52"));
        songs.add(new Song("03", "Supernova", "03:29"));
        songs.add(new Song("04", "Magellan", "04:41"));
        songs.add(new Song("05", "First Landing", "01:18"));
        songs.add(new Song("06", "Oceania", "03:27"));
        songs.add(new Song("07", "Only Time Will Tell", "02:10"));
        songs.add(new Song("08", "Prayer for the Earth", "02:44"));
        songs.add(new Song("09", "Lament for Atlantis", "01:19"));
        songs.add(new Song("10", "The Chamber", "03:32"));
        songs.add(new Song("11", "In The Beginning", "01:24"));
        songs.add(new Song("12", "Let There Be Light", "04:52"));
        songs.add(new Song("13", "Supernova", "03:29"));
        songs.add(new Song("14", "Magellan", "04:41"));
        songs.add(new Song("15", "First Landing", "01:18"));
        songs.add(new Song("16", "Oceania", "03:27"));
        songs.add(new Song("17", "Only Time Will Tell", "02:10"));
        songs.add(new Song("18", "Prayer for the Earth", "02:44"));
        songs.add(new Song("19", "Lament for Atlantis", "01:19"));
        songs.add(new Song("20", "The Chamber", "03:32"));*/

        albumImageView = (ImageView) view.findViewById(R.id.album_picture_imageView);
        albumNameTextView = (TextView) view.findViewById(R.id.album_textView);

        foldersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Album album = (Album) parent.getAdapter().getItem(position);
                MODSongAdapter songAdapter = new MODSongAdapter(getActivity(), album.getSongs());
                songsListView.setAdapter(songAdapter);
                songsAdapterLength = album.getSongs().size();

                if (album.getImageSource() != null) {
                    ImageLoader.getInstance().displayImage(album.getImageSource(), albumImageView, options, animateFirstListener);
                }
                albumNameTextView.setText(album.getName());

                if (playSongParentId != -1 && playSongParentId == album.getId()) {
                    songsListView.setItemChecked(playSongPosition, true);
                    Log.i("MODSongPosition", "Position: " + playSongPosition);
                }

                albumPosition = position;
            }
        });
        songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = (Song) parent.getAdapter().getItem(position);
                contentUri = Uri.parse(song.getSource());
                releasePlayer();
                preparePlayer();

                playSongParentId = song.getAlbumId();
                playSongPosition = position;
            }
        });

        volumeSeekbar = (SeekBar) view.findViewById(R.id.mod_volume_seekBar);
        progressSeekBar = (SeekBar) view.findViewById(R.id.mod_player_seekBar);
        playPauseButton = (ImageButton) view.findViewById(R.id.mod_play_pause_imageButton);
        stopButton = (ImageButton) view.findViewById(R.id.mod_stop_imageButton);
        nextSongButton = (ImageButton) view.findViewById(R.id.mod_forward_imageButton);
        previousSongButton = (ImageButton) view.findViewById(R.id.mod_rewind_imageButton);
        initControls();
        return view;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }

    @Override
    public void onCues(List<Cue> cues) {

    }

    @Override
    public void onId3Metadata(Map<String, Object> metadata) {

    }

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                Log.e("Status", "buffering");

                break;
            case ExoPlayer.STATE_ENDED:
                Log.e("Status", "ended");
                if (t != null) {
                    t.cancel();
                    t.purge();
                    t = null;
                }
                releasePlayer();
                nextSong();
                break;
            case ExoPlayer.STATE_IDLE:
                Log.e("Status", "idle");

                break;
            case ExoPlayer.STATE_PREPARING:
                Log.e("Status", "preparing");

                break;
            case ExoPlayer.STATE_READY:
                Log.e("Status", "ready");
                playingVideo = true;
                playPauseButton.setImageResource(R.drawable.ic_pause);
                progressSeekBar.setMax((int) player.getDuration());
                Log.e("VODPlayer", String.valueOf(player.getDuration()));
                progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        if (t != null) {
                            t.cancel();
                            t.purge();
                            t = null;
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        player.seekTo(seekBar.getProgress());
                    }
                });
                t = new Timer();
                t.schedule(new ProgressTimerTask()
                        , 0, 1000);
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        boolean audioCapabilitiesChanged = !audioCapabilities.equals(this.audioCapabilities);
        if (player == null || audioCapabilitiesChanged) {
            this.audioCapabilities = audioCapabilities;
            releasePlayer();
            preparePlayer();
        } else {
            player.setBackgrounded(false);
        }
    }

    private void initControls() {
        try {

            audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });

            playPauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (player != null) {
                        if (playingVideo) {
                            player.stop();
                            playingVideo = false;
                            playPauseButton.setImageResource(R.drawable.ic_play_arrow);
                            if (t != null) {
                                t.cancel();
                                t.purge();
                                t = null;
                            }
                        } else {
                            player.prepare();
                            playingVideo = true;
                            playPauseButton.setImageResource(R.drawable.ic_pause);
                        }
                    }
                }

            });

            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    songsListView.setItemChecked(playSongPosition, false);
                    if (t != null) {
                        t.cancel();
                        t.purge();
                        t = null;
                    }
                    releasePlayer();
                    playPauseButton.setImageResource(R.drawable.ic_play_arrow);

                    playSongParentId = -1;
                    playSongPosition = -1;
                }
            });

            nextSongButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (t != null) {
                        t.cancel();
                        t.purge();
                        t = null;
                    }
                    releasePlayer();
                    nextSong();
                }
            });

            previousSongButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (t != null) {
                        t.cancel();
                        t.purge();
                        t = null;
                    }
                    releasePlayer();
                    previousSong();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ExtractorRendererBuilder createRenderer() {
        String userAgent = Util.getUserAgent(getActivity(), "Exo playerTest");
        return new ExtractorRendererBuilder(getActivity(), userAgent, contentUri);
    }

    private void preparePlayer() {
        if (player == null) {
            player = new CustomPlayer(createRenderer());
            player.addListener(this);
            player.setCaptionListener(this);
            player.setMetadataListener(this);
            playerNeedsPrepare = true;

        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
        }
        player.setBackgrounded(false);
        player.setPlayWhenReady(true);
    }

    public void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
        progressSeekBar.setProgress(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (t != null) {
            t.cancel();
            t.purge();
            t = null;
        }
        releasePlayer();

        foldersListView.setItemChecked(albumPosition, false);
        playSongPosition = -1;
    }

    private class ProgressTimerTask extends TimerTask {

        public ProgressTimerTask() {

        }

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressSeekBar.setProgress((int) player.getCurrentPosition());
                    Log.e("VODPlayer", String.valueOf(player.getCurrentPosition()));
                    Log.e("VODPlayer", String.valueOf(player.getDuration()));
                }
            });
        }
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 0);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    public void nextSong() {
        Log.i("MODNextSong", "Position: " + playSongPosition + ", Length: " + (songsAdapterLength - 1));
        if (playSongPosition != -1 && playSongPosition < songsAdapterLength - 1) {
            songsListView.setItemChecked(playSongPosition, false);
            playSongPosition++;
            Song song = (Song) songsListView.getItemAtPosition(playSongPosition);
            contentUri = Uri.parse(song.getSource());
            preparePlayer();
            songsListView.setItemChecked(playSongPosition, true);
        } else {
            songsListView.setItemChecked(playSongPosition, false);
            playSongPosition = -1;
        }
    }

    public void previousSong() {
        if (playSongPosition > 0) {
            songsListView.setItemChecked(playSongPosition, false);
            playSongPosition--;
            Song song = (Song) songsListView.getItemAtPosition(playSongPosition);
            contentUri = Uri.parse(song.getSource());
            preparePlayer();
            songsListView.setItemChecked(playSongPosition, true);
        } else {
            songsListView.setItemChecked(playSongPosition, false);
            playSongPosition = -1;
        }
    }
}