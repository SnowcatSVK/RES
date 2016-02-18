package sk.antik.res;


import android.content.Context;
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
import android.widget.ListView;
import android.widget.SeekBar;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.util.Util;
import java.util.ArrayList;
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
    private boolean playingVideo = false;

    private Timer t;


    private ArrayList<Album> albums;

    public MODFragment() {
        // Required empty public constructor
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

        foldersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Album album = (Album) parent.getAdapter().getItem(position);
                MODSongAdapter songAdapter = new MODSongAdapter(getActivity(), album.getSongs());
                songsListView.setAdapter(songAdapter);
            }
        });
        songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = (Song) parent.getAdapter().getItem(position);
                contentUri = Uri.parse(song.getSource());
                preparePlayer();
            }
        });
        volumeSeekbar = (SeekBar) view.findViewById(R.id.mod_volume_seekBar);
        progressSeekBar = (SeekBar) view.findViewById(R.id.mod_player_seekBar);
        playPauseButton = (ImageButton) view.findViewById(R.id.mod_play_pause_imageButton);
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
}