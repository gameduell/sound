/*
 * Copyright (c) 2003-2014 GameDuell GmbH, All Rights Reserved
 * This document is strictly confidential and sole property of GameDuell GmbH, Berlin, Germany
 */
package org.haxe.duell.sound;

import android.util.Log;
import org.haxe.duell.hxjni.HaxeObject;
import org.haxe.duell.sound.helper.SoundIdProvider;
import org.haxe.duell.sound.listener.OnSoundCompleteListener;
import org.haxe.duell.sound.listener.OnSoundReadyListener;
import org.haxe.duell.sound.manager.SoundManager;

/**
 * @author jxav
 */
public final class Music implements OnSoundReadyListener, OnSoundCompleteListener
{
    private static final String TAG = Music.class.getSimpleName();

    private final HaxeObject haxeMusic;
    private final String fileUrl;
    private final boolean fromAssets;
    private final int uniqueKey;

    private int id;
    private float volume;
    private boolean looped;
    private SoundState state;

    private long duration;
    private long position;

    private boolean playAfterPreload;

    public static Music create(final HaxeObject haxeObject, final String fileUrl, final boolean fromAssets)
    {
        return new Music(haxeObject, fileUrl, fromAssets);
    }

    private Music(final HaxeObject haxeMusic, final String fileUrl, final boolean fromAssets)
    {
        this.haxeMusic = haxeMusic;
        this.fileUrl = fileUrl;
        this.fromAssets = fromAssets;

        // the unique id has to be different for every sound, so a hash of file URL is not sufficient
        uniqueKey = SoundIdProvider.getId();

        duration = -1;
        volume = 1.0f;
        looped = false;

        unload();

        Log.d(TAG, "Music created for file: " + fileUrl);
    }

    public void preloadMusic(final boolean playAfterPreload)
    {
        Log.d(TAG, "Music is preloading");

        if (state != SoundState.UNLOADED)
        {
            return;
        }

        state = SoundState.LOADING;
        this.playAfterPreload = playAfterPreload;

        SoundManager.getSharedInstance().initializeMusic(this);
    }

    public void playMusic()
    {
        Log.d(TAG, "Music playing");

        if (state == SoundState.UNLOADED)
        {
            preloadMusic(true);
            return;
        }
        else if (state != SoundState.IDLE && state != SoundState.PAUSED)
        {
            return;
        }

        state = SoundState.PLAYING;

        SoundManager.getSharedInstance().playMusic(this);
    }

    public void stopMusic()
    {
        Log.d(TAG, "Music stopped");

        if (state != SoundState.PLAYING && state != SoundState.PAUSED)
        {
            return;
        }

        state = SoundState.IDLE;

        SoundManager.getSharedInstance().stopMusic();

        // reset sound position
        position = 0;
    }

    public void pauseMusic()
    {
        Log.d(TAG, "Music paused");

        if (state != SoundState.PLAYING)
        {
            return;
        }

        state = SoundState.PAUSED;

        SoundManager.getSharedInstance().pauseMusic();

        // cache current sound position on pause
        position = SoundManager.getSharedInstance().getCurrentMusicPosition();
    }

    public void setVolume(final float volume)
    {
        Log.d(TAG, "Set volume: " + volume);

        this.volume = volume;

        if (state != SoundState.UNLOADED)
        {
            // update immediately if the sound is loaded
            SoundManager.getSharedInstance().setMusicVolume(volume);
        }
    }

    public void setLooped(final boolean looped)
    {
        Log.d(TAG, "Set looped: " + looped);

        this.looped = looped;

        if (state != SoundState.UNLOADED)
        {
            // update immediately if the sound is loaded
            SoundManager.getSharedInstance().setMusicLoop(looped);
        }
    }

    public float getDuration()
    {
        Log.d(TAG, "Get duration");

        if (duration == -1 && state != SoundState.UNLOADED)
        {
            // update the duration value, if it is still the default value
            duration = SoundManager.getSharedInstance().getCurrentMusicDuration();
        }

        return duration;
    }

    public float getPosition()
    {
        Log.d(TAG, "Get position");

        if (state == SoundState.PLAYING)
        {
            // always cache the last position in case it changes state too quickly
            position = SoundManager.getSharedInstance().getCurrentMusicPosition();
        }

        return position;
    }

    public void unload()
    {
        id = -1;
        state = SoundState.UNLOADED;
        position = 0;
    }

    @Override
    public void onSoundReady(final int soundId, final long soundDurationMillis)
    {
        Log.d(TAG, "Music ready! ID: " + soundId);

        id = soundId;
        duration = soundDurationMillis;
        state = SoundState.IDLE;

        haxeMusic.call0("onMusicLoadCompleted");

        if (playAfterPreload)
        {
            playMusic();
        }
    }

    @Override
    public void onSoundComplete()
    {
        Log.d(TAG, "Music complete!");

        if (looped)
        {
            position = 0;
            // state is unaffected
        }
        else
        {
            position = (long) getDuration();
            state = SoundState.IDLE;
        }

        haxeMusic.call0("onPlaybackCompleted");
    }

    //
    // Getters
    //

    public boolean isReady()
    {
        return state != SoundState.UNLOADED;
    }

    public String getFileUrl()
    {
        return fileUrl;
    }

    public boolean isLooped()
    {
        return looped;
    }

    public float getVolume()
    {
        return volume;
    }

    public int getId()
    {
        return id;
    }

    public int getUniqueKey()
    {
        return uniqueKey;
    }

    public boolean isFromAssets()
    {
        return fromAssets;
    }
}