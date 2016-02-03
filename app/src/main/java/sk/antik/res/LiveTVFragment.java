package sk.antik.res;


import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sk.antik.res.logic.Channel;
import sk.antik.res.logic.ChannelAdapter;
import sk.antik.res.player.CustomPlayer;
import sk.antik.res.player.HlsRendererBuilder;


/**
 * A simple {@link Fragment} subclass.
 */
public class LiveTVFragment extends Fragment implements SurfaceHolder.Callback,
        CustomPlayer.Listener, CustomPlayer.CaptionListener, CustomPlayer.Id3MetadataListener,
        AudioCapabilitiesReceiver.Listener {

    private AspectRatioFrameLayout videoFrame;
    public SurfaceView surfaceView;
    private CustomPlayer player;
    public FrameLayout tvFrame;
    public RelativeLayout controlsLayout;
    private boolean playerNeedsPrepare;
    public Uri contentUri;
    private AudioCapabilities audioCapabilities;
    public ListView channelsListView;
    public ArrayList<Channel> channels;
    public ChannelAdapter adapter;
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;
    private ImageButton playPauseButton = null;
    private boolean playingVideo = false;
    public RelativeLayout parent;
    public ImageButton channelListButton;
    public LinearLayout buttonSeparatorLayout;
    public LinearLayout separatorLayout;

    public LiveTVFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_live_tv, container, false);
        videoFrame = (AspectRatioFrameLayout) rootView.findViewById(R.id.surfaceFrame);
        surfaceView = (SurfaceView) rootView.findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
        tvFrame = (FrameLayout) rootView.findViewById(R.id.surfaceHolder);
        channelsListView = (ListView) rootView.findViewById(R.id.channels_listView);
        parent = (RelativeLayout) rootView.findViewById(R.id.liveTV_parent_layout);
        controlsLayout = (RelativeLayout) rootView.findViewById(R.id.player_controls_layout);
        channels = new ArrayList<>();
        channels.add(new Channel("Sky Sports"));
        channels.add(new Channel("BBC World"));
        channels.add(new Channel("CNN"));
        channels.add(new Channel("Discovery Channel"));
        channels.add(new Channel("VH-1"));
        channels.add(new Channel("Hallmark TV"));
        channels.add(new Channel("Music Channel"));
        channels.add(new Channel("History Channel"));
        channels.add(new Channel("BBC One"));
        channels.add(new Channel("Sky News"));
        channels.add(new Channel("ITV"));
        adapter = new ChannelAdapter(getActivity(), channels);
        channelsListView.setAdapter(adapter);
        channelsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contentUri = Uri.parse(channels.get(position).streamURL);
                playingVideo = true;
                playPauseButton.setImageResource(R.drawable.ic_pause);
                releasePlayer();
                preparePlayer();
            }
        });

        //contentUri = Uri.parse(channels.get(0).streamURL);
        volumeSeekbar = (SeekBar) rootView.findViewById(R.id.tv_volume_seekBar);
        playPauseButton = (ImageButton) rootView.findViewById(R.id.video_play_pause_button);
        channelListButton = (ImageButton) rootView.findViewById(R.id.channel_list_button);
        buttonSeparatorLayout = (LinearLayout) rootView.findViewById(R.id.button_separator_linear_layout);
        separatorLayout = (LinearLayout) rootView.findViewById(R.id.separator_linear_layout);
        //preparePlayer();
        initControls();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view == channelsListView) {
            channelsListView.setSelection(0);
            channelsListView.getSelectedView().setSelected(true);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.blockingClearSurface();
        }
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
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(Exception e) {
        playerNeedsPrepare = true;
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthAspectRatio) {
        videoFrame.setAspectRatio(
                height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
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

    private HlsRendererBuilder createRenderer() {
        String userAgent = Util.getUserAgent(getActivity(), "Exo playerTest");
        return new HlsRendererBuilder(getActivity(), userAgent, contentUri.toString());
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
        surfaceView.setBackground(null);
        player.setBackgrounded(false);
        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(true);
    }

    public void releasePlayer() {
        if (player != null) {
            //playerPosition = player.getCurrentPosition();
            videoFrame.setBackgroundColor(Color.parseColor("#000000"));
            player.release();
            player = null;

        }
    }

    private void initControls() {
        try {

            audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_SYSTEM));


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

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }
}
