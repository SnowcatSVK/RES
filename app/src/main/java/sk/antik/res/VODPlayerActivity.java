package sk.antik.res;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.util.Util;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import sk.antik.res.player.CustomPlayer;
import sk.antik.res.player.ExtractorRendererBuilder;

public class VODPlayerActivity extends Activity implements SurfaceHolder.Callback,
        CustomPlayer.Listener, CustomPlayer.CaptionListener, CustomPlayer.Id3MetadataListener,
        AudioCapabilitiesReceiver.Listener {

    private AspectRatioFrameLayout videoFrame;
    public SurfaceView surfaceView;
    private CustomPlayer player;
    public FrameLayout tvFrame;
    private boolean playerNeedsPrepare;
    public Uri contentUri;
    private AudioCapabilities audioCapabilities;
    private SeekBar volumeSeekbar = null;
    private SeekBar progressSeekBar = null;
    private AudioManager audioManager = null;
    private ImageButton playPauseButton = null;
    private boolean playingVideo = false;
    private ProgressBar progressBar;

    private Timer t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_vodplayer);
        videoFrame = (AspectRatioFrameLayout) findViewById(R.id.surfaceFrame);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
        tvFrame = (FrameLayout) findViewById(R.id.surfaceHolder);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        volumeSeekbar = (SeekBar) findViewById(R.id.tv_volume_seekBar);
        progressSeekBar = (SeekBar) findViewById(R.id.vod_progress_seekBar);
        playPauseButton = (ImageButton) findViewById(R.id.video_play_pause_button);
        contentUri = Uri.parse(getIntent().getStringExtra("VODSource"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initControls();
        preparePlayer();
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
                progressBar.setVisibility(View.VISIBLE);
                break;
            case ExoPlayer.STATE_READY:
                Log.e("Status", "ready");
                progressBar.setVisibility(View.INVISIBLE);
                playPauseButton.setImageResource(R.drawable.ic_pause);
                progressSeekBar.setMax((int) player.getDuration());
                Log.e("VODPlayer", String.valueOf(player.getDuration()));
                progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        t.cancel();
                        t.purge();
                        t = null;
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

    private ExtractorRendererBuilder createRenderer() {
        String userAgent = Util.getUserAgent(this, "Exo playerTest");
        return new ExtractorRendererBuilder(this, userAgent, contentUri);
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

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
        t.cancel();
        t.purge();
        t = null;
        releasePlayer();
    }

    private class ProgressTimerTask extends TimerTask {

        public ProgressTimerTask() {

        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
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
