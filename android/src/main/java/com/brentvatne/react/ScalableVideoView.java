package com.brentvatne.react;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import com.brentvatne.RCTVideo.R;
import com.yqritc.scalablevideoview.ScalableType;
import com.yqritc.scalablevideoview.ScaleManager;
import com.yqritc.scalablevideoview.Size;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * Created by beibei on 2016/5/10.
 */
public class ScalableVideoView extends TextureView implements TextureView.SurfaceTextureListener, MediaPlayer.OnVideoSizeChangedListener {
    protected MediaPlayer mMediaPlayer;
    protected ScalableType mScalableType;

    public ScalableVideoView(Context context) {
        this(context, (AttributeSet)null);
    }

    public ScalableVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScalableVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mScalableType = ScalableType.NONE;
        if(attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.scaleStyle, 0, 0);
            if(a != null) {
                int scaleType = a.getInt(R.styleable.scaleStyle_scalableType, ScalableType.NONE.ordinal());
                a.recycle();
                this.mScalableType = ScalableType.values()[scaleType];
            }
        }
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);
        if(this.mMediaPlayer != null) {
            this.mMediaPlayer.setSurface(surface);
        }

    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        /*if(this.mMediaPlayer != null) {
            if(this.isPlaying()) {
                this.stop();
            }

            this.release();
            this.mMediaPlayer = null;
        }*/
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        this.scaleVideoSize(width, height);
    }

    private void scaleVideoSize(int videoWidth, int videoHeight) {
        if(videoWidth != 0 && videoHeight != 0) {
            Size viewSize = new Size(this.getWidth(), this.getHeight());
            Size videoSize = new Size(videoWidth, videoHeight);
            ScaleManager scaleManager = new ScaleManager(viewSize, videoSize);
            Matrix matrix = scaleManager.getScaleMatrix(this.mScalableType);
            if(matrix != null) {
                this.setTransform(matrix);
            }

        }
    }

    private void initializeMediaPlayer() {
        if(this.mMediaPlayer == null) {
            this.mMediaPlayer = new MediaPlayer();
            this.mMediaPlayer.setOnVideoSizeChangedListener(this);
            this.setSurfaceTextureListener(this);
        } else {
            this.mMediaPlayer.reset();
        }

    }

    public void setRawData(@RawRes int id) throws IOException {
        AssetFileDescriptor afd = this.getResources().openRawResourceFd(id);
        this.setDataSource(afd);
    }

    public void setAssetData(@NonNull String assetName) throws IOException {
        AssetManager manager = this.getContext().getAssets();
        AssetFileDescriptor afd = manager.openFd(assetName);
        this.setDataSource(afd);
    }

    private void setDataSource(@NonNull AssetFileDescriptor afd) throws IOException {
        this.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        afd.close();
    }

    public void setDataSource(@NonNull String path) throws IOException {
        this.initializeMediaPlayer();
        this.mMediaPlayer.setDataSource(path);
    }

    public void setDataSource(@NonNull Context context, @NonNull Uri uri, @Nullable Map<String, String> headers) throws IOException {
        this.initializeMediaPlayer();
        this.mMediaPlayer.setDataSource(context, uri, headers);
    }

    public void setDataSource(@NonNull Context context, @NonNull Uri uri) throws IOException {
        this.initializeMediaPlayer();
        this.mMediaPlayer.setDataSource(context, uri);
    }

    public void setDataSource(@NonNull FileDescriptor fd, long offset, long length) throws IOException {
        this.initializeMediaPlayer();
        this.mMediaPlayer.setDataSource(fd, offset, length);
    }

    public void setDataSource(@NonNull FileDescriptor fd) throws IOException {
        this.initializeMediaPlayer();
        this.mMediaPlayer.setDataSource(fd);
    }

    public void setScalableType(ScalableType scalableType) {
        this.mScalableType = scalableType;
        this.scaleVideoSize(this.getVideoWidth(), this.getVideoHeight());
    }

    public void prepare(@Nullable MediaPlayer.OnPreparedListener listener) throws IOException, IllegalStateException {
        this.mMediaPlayer.setOnPreparedListener(listener);
        this.mMediaPlayer.prepare();
    }

    public void prepareAsync(@Nullable MediaPlayer.OnPreparedListener listener) throws IllegalStateException {
        this.mMediaPlayer.setOnPreparedListener(listener);
        this.mMediaPlayer.prepareAsync();
    }

    public void prepare() throws IOException, IllegalStateException {
        this.prepare((MediaPlayer.OnPreparedListener)null);
    }

    public void prepareAsync() throws IllegalStateException {
        this.prepareAsync((MediaPlayer.OnPreparedListener)null);
    }

    public int getCurrentPosition() {
        return this.mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return this.mMediaPlayer.getDuration();
    }

    public int getVideoHeight() {
        return this.mMediaPlayer.getVideoHeight();
    }

    public int getVideoWidth() {
        return this.mMediaPlayer.getVideoWidth();
    }

    public boolean isLooping() {
        return this.mMediaPlayer.isLooping();
    }

    public boolean isPlaying() {
        return this.mMediaPlayer.isPlaying();
    }

    public void pause() {
        this.mMediaPlayer.pause();
    }

    public void seekTo(int msec) {
        this.mMediaPlayer.seekTo(msec);
    }

    public void setLooping(boolean looping) {
        this.mMediaPlayer.setLooping(looping);
    }

    public void setVolume(float leftVolume, float rightVolume) {
        this.mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public void start() {
        this.mMediaPlayer.start();
    }

    public void stop() {
        this.mMediaPlayer.stop();
    }

    public void release() {
        this.mMediaPlayer.reset();
        this.mMediaPlayer.release();
    }
}
