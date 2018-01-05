package com.qiujk.devuser;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class VideoActivity extends AppCompatActivity {
    private final String TAG = "VideoActivity";
    private ImageButton btnplay, btnstop, btnpause;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private int position;
    private String url1 = "http://flashmedia.eastday.com/newdate/news/2016-11/shznews1125-19.mp4";
    private String url2 = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov";
    private String url3 = "http://42.96.249.166/live/388.m3u8";
    private String url4 = "http://61.129.89.191/ThroughTrain/download.html?id=4035&flag=-org-"; //音频url

    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = true;
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        btnplay = (ImageButton) this.findViewById(R.id.btnplay);
        btnstop = (ImageButton) this.findViewById(R.id.btnstop);
        btnpause = (ImageButton) this.findViewById(R.id.btnpause);
        ButtonOnClickListener onClickListener = new ButtonOnClickListener();
        btnstop.setOnClickListener(onClickListener);
        btnplay.setOnClickListener(onClickListener);
        btnpause.setOnClickListener(onClickListener);

        mediaPlayer = new MediaPlayer();
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        //region 设置SurfaceView自己不管理的缓冲区
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (position > 0) {
                    try {
                        // 开始播放
                        play();
                        // 并直接从指定位置开始播放
                        mediaPlayer.seekTo(position);
                        position = 0;
                    } catch (Exception e) {
                        Log.e(TAG,"surfaceView error:"+e.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });
        //endregion
        //region 视频宽高总是等于定义的SurfaceView布局宽高，所以视频可能会被拉伸变形                                   }
        mediaPlayer.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                if (width == 0 || height == 0) {
                    Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
                    return;
                }
                mIsVideoSizeKnown = true;
                mVideoWidth = width;
                mVideoHeight = height;
                if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
                    startVideoPlayback();
                }
            }
        });

        mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mIsVideoReadyToBePlayed = true;
                if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
                    startVideoPlayback();
                }
            }
        });
        //endregion
        mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //mp.start();
                //Toast.makeText(VideoActivity.this, "开始播放！", Toast.LENGTH_LONG).show();
            }
        });
        mediaPlayer.setOnErrorListener(new android.media.MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d(TAG, "OnError - Error code: " + what + " Extra code: " + extra);
                switch (what) {
                    case -1004:
                        Log.d(TAG, "MEDIA_ERROR_IO");
                        break;
                    case -1007:
                        Log.d(TAG, "MEDIA_ERROR_MALFORMED");
                        break;
                    case 200:
                        Log.d(TAG, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                        break;
                    case 100:
                        Log.d(TAG, "MEDIA_ERROR_SERVER_DIED");
                        break;
                    case -110:
                        Log.d(TAG, "MEDIA_ERROR_TIMED_OUT");
                        break;
                    case 1:
                        Log.d(TAG, "MEDIA_ERROR_UNKNOWN");
                        break;
                    case -1010:
                        Log.d(TAG, "MEDIA_ERROR_UNSUPPORTED");
                        break;
                }
                switch (extra) {
                    case 800:
                        Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING");
                        break;
                    case 702:
                        Log.d(TAG, "MEDIA_INFO_BUFFERING_END");
                        break;
                    case 701:
                        Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                        break;
                    case 802:
                        Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                        break;
                    case 801:
                        Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE");
                        break;
                    case 1:
                        Log.d(TAG, "MEDIA_INFO_UNKNOWN");
                        break;
                    case 3:
                        Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                        break;
                    case 700:
                        Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                        break;
                }
                return false;
            }
        });
    }

    private void startVideoPlayback() {
        surfaceView.getHolder().setFixedSize(mVideoWidth, mVideoHeight);
        //mediaPlayer.start();
    }

    class ButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnplay:
                    play();
                    break;
                case R.id.btnpause:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        int resID = getResources().getIdentifier("play", "drawable", "com.qiujk.devuser");
                        btnpause.setBackgroundResource(resID);
                    } else {
                        mediaPlayer.start();
                        int resID = getResources().getIdentifier("pause", "drawable", "com.qiujk.devuser");
                        btnpause.setBackgroundResource(resID);
                    }
                    break;
                case R.id.btnstop:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        //mediaPlayer.prepare();
                        mediaPlayer.seekTo(0);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        // 先判断是否正在播放
        if (mediaPlayer.isPlaying()) {
            // 如果正在播放我们就先保存这个播放位置
            position = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        }
        super.onPause();
    }

    private void play() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // 设置需要播放的视频
            Uri uri = Uri.parse(url1);
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            // 把视频画面输出到SurfaceView
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.prepare();
            // 播放
            mediaPlayer.start();
            Toast.makeText(this, "开始播放！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "play error:" + e.getMessage());
        }
    }
}
