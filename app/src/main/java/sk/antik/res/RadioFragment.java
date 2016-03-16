package sk.antik.res;


import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.util.Util;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.lang.ref.WeakReference;
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
public class RadioFragment extends Fragment implements /*SurfaceHolder.Callback,
        CustomPlayer.Listener, CustomPlayer.CaptionListener, CustomPlayer.Id3MetadataListener,
        AudioCapabilitiesReceiver.Listener, */IVLCVout.Callback, LibVLC.HardwareAccelerationError, MediaPlayer.EventListener {

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
    public ProgressBar progressBar;
    private SurfaceHolder holder;
    private boolean videoPaused = false;
    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    private final static int VideoSizeChanged = -1;
    private MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);


    public RadioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            libvlc = new LibVLC();
        } catch (IllegalStateException e) {
            Toast.makeText(getActivity(),
                    "Error initializing the libVLC multimedia framework!",
                    Toast.LENGTH_LONG).show();
        }

        View rootView = inflater.inflate(R.layout.fragment_radio, container, false);
        videoFrame = (AspectRatioFrameLayout) rootView.findViewById(R.id.surfaceFrame);
        surfaceView = (SurfaceView) rootView.findViewById(R.id.surface_view);
        holder = surfaceView.getHolder();
        //holder.addCallback(this);
        tvFrame = (FrameLayout) rootView.findViewById(R.id.surfaceHolder);
        channelsListView = (ListView) rootView.findViewById(R.id.channels_listView);
        parent = (RelativeLayout) rootView.findViewById(R.id.liveTV_parent_layout);
        controlsLayout = (RelativeLayout) rootView.findViewById(R.id.player_controls_layout);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        /*channels = new ArrayList<>();
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
        channels.add(new Channel("ITV"));*/
        adapter = new ChannelAdapter(getActivity(), channels);
        channelsListView.setAdapter(adapter);
        channelsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contentUri = Uri.parse(channels.get(position).streamURL);
                for (Channel channel : channels) {
                    channel.selected = false;
                }
                channels.get(position).selected = true;
                adapter.notifyDataSetChanged();
                playingVideo = true;
                playPauseButton.setImageResource(R.drawable.ic_pause);
                /*releasePlayer();
                preparePlayer();*/
                if (mMediaPlayer == null)
                    createPlayer(contentUri.toString());
                else {
                    mMediaPlayer.stop();
                    mMediaPlayer.setMedia(new Media(libvlc, contentUri));
                    mMediaPlayer.play();
                }
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

    /*@Override
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
                progressBar.setVisibility(View.VISIBLE);
                break;
            case ExoPlayer.STATE_ENDED:
                Log.e("Status", "ended");
                break;
            case ExoPlayer.STATE_IDLE:
                Log.e("Status", "idle");
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case ExoPlayer.STATE_PREPARING:
                Log.e("Status", "preparing");
                if (!videoPaused)
                    progressBar.setVisibility(View.VISIBLE);
                break;
            case ExoPlayer.STATE_READY:
                Log.e("Status", "ready");
                progressBar.setVisibility(View.INVISIBLE);
                videoPaused = false;
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
    }*/


    private void createPlayer(String media) {
        //releasePlayer();
        try {
            /*if (media.length() > 0) {
                Toast toast = Toast.makeText(getActivity(), media, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                        0);
                toast.show();
            }*/

            // Create LibVLC
            // TODO: make this more robust, and sync with audio demo
            ArrayList<String> options = new ArrayList<>();
            //options.add("--subsdec-encoding <encoding>");
            options.add("--aout=opensles");
            options.add("--audio-time-stretch"); // time stretching
            options.add("-vvv"); // verbosity
            libvlc = new LibVLC(options);
            libvlc.setOnHardwareAccelerationError(this);
            holder.setKeepScreenOn(true);

            // Create media player
            mMediaPlayer = new MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(this);

            // Set up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(surfaceView);
            //vout.setSubtitlesView(mSurfaceSubtitles);
            vout.addCallback(this);
            vout.attachViews();

            Media m = new Media(libvlc, Uri.parse(media));
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error creating player!", Toast.LENGTH_LONG).show();
        }
    }

    // TODO: handle this cleaner
    private void releasePlayer() {
        if (libvlc == null)
            return;
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.removeCallback(this);
            vout.detachViews();
            libvlc.release();
            libvlc = null;
            mMediaPlayer = null;
            mVideoWidth = 0;
            mVideoHeight = 0;
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
                    if (mMediaPlayer != null) {
                        if (playingVideo) {
                            mMediaPlayer.pause();
                            playingVideo = false;
                            videoPaused = true;
                            playPauseButton.setImageResource(R.drawable.ic_play_arrow);
                        } else {
                            mMediaPlayer.play();
                            playingVideo = true;
                            videoPaused = false;
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
        for (Channel channel : channels) {
            channel.selected = false;
        }
    }

    /*public ArrayList<Integer> getMediaDimens() {
        ArrayList<Integer> dimens = new ArrayList<>();
    }
*/

    @Override
    public void onEvent(MediaPlayer.Event event) {
        switch (event.type) {
            case MediaPlayer.Event.EndReached:
                releasePlayer();
                break;
            case MediaPlayer.Event.Playing:
//                    progressBar.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                int trackCount = mMediaPlayer.getMedia().getTrackCount();
                Log.e("SizeSet", String.valueOf(trackCount));
                for (int i = 0; i < trackCount; i++) {
                    if (mMediaPlayer.getMedia().getTrack(i).type == 1) {
                        Log.e("SizeSet", "gotVideo");
                        Media.VideoTrack track = (Media.VideoTrack) mMediaPlayer.getMedia().getTrack(i);
                        setSize(track.width, track.height);
                    }
                }
                break;
            case MediaPlayer.Event.Paused:
            case MediaPlayer.Event.Stopped:
            case MediaPlayer.Event.Opening:
                //progressBar.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNewLayout(IVLCVout vout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (width * height == 0)
            return;

        // store video size
        mVideoWidth = width;
        mVideoHeight = height;
        //setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }

    private static class MyPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<RadioFragment> mOwner;

        public MyPlayerListener(RadioFragment owner) {
            mOwner = new WeakReference<>(owner);
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            RadioFragment player = mOwner.get();

            switch (event.type) {
                case MediaPlayer.Event.EndReached:
                    player.releasePlayer();
                    break;
                case MediaPlayer.Event.Playing:
//                    progressBar.setVisibility(View.INVISIBLE);
                    player.progressBar.setVisibility(View.INVISIBLE);
                    break;
                case MediaPlayer.Event.Paused:
                case MediaPlayer.Event.Stopped:
                case MediaPlayer.Event.Opening:
                    //progressBar.setVisibility(View.VISIBLE);
                    player.progressBar.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void eventHardwareAccelerationError() {
        this.releasePlayer();
        Toast.makeText(getActivity(), "Error with hardware acceleration", Toast.LENGTH_LONG).show();
    }

    public void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        if (holder == null || surfaceView == null)
            return;

        // get screen size
        int w = getActivity().getWindow().getDecorView().getWidth();
        int h = getActivity().getWindow().getDecorView().getHeight();

        /*getWindow().getDecorView() doesn't always take orientation into
         account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }*/

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        // force surface buffer size
        holder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
        lp.width = w;
        lp.height = h;
        surfaceView.setLayoutParams(lp);
        surfaceView.invalidate();
    }
}
