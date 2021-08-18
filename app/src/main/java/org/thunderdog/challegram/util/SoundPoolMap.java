package org.thunderdog.challegram.util;

import android.media.SoundPool;
import android.os.Build;
import android.util.SparseIntArray;

import androidx.annotation.RawRes;

import org.thunderdog.challegram.core.BaseThread;
import org.thunderdog.challegram.tool.UI;

/**
 * Date: 5/25/17
 * Author: default
 */

public class SoundPoolMap {
  private final SparseIntArray sounds;
  private SoundPool soundPool;

  private final BaseThread pool;

  private final int stream;

  public SoundPoolMap (int stream) {
    this.stream = stream;
    sounds = new SparseIntArray(8);
    pool = new BaseThread("SoundPoolMap");
  }

  public void prepare (@RawRes int... resources) {
    for (int res : resources) {
      get(res);
    }
  }

  public int get (@RawRes int res) {
    if (soundPool == null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && false) {
        android.media.AudioAttributes attributes = new android.media.AudioAttributes.Builder()
          .setUsage(android.media.AudioAttributes.USAGE_VOICE_COMMUNICATION)
          .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
          .build();
        soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(attributes).build();
      } else {
        //noinspection deprecation
        soundPool = new SoundPool(1, stream, 0);
      }
    }
    int sound = sounds.get(res);
    if (sound == 0) {
      sound = soundPool.load(UI.getAppContext(), res, 1);
      sounds.put(res, sound);
    }
    return sound;
  }

  public void stop (int streamID) {
    soundPool.stop(streamID);
  }

  private int lastStreamID, lastStreamResID, lastStreamLoop;

  private int play (@RawRes int res) {
    return play(res, 1, 1, 0, -1, 1);
  }

  private int play (@RawRes int res, float leftVolume, float rightVolume, int priority, int loop, float rate) {
    int soundId = get(res);
    lastStreamResID = res;
    return lastStreamID = soundPool.play(soundId, leftVolume, rightVolume, priority, lastStreamLoop = loop, rate);
  }

  public void playUnique (final @RawRes int res, final float leftVolume, final float rightVolume, final int priority, final int loop, final float rate) {
    pool.post(() -> {
      if (lastStreamID == 0 || lastStreamResID != res || lastStreamLoop != loop) {
        if (lastStreamID != 0 && lastStreamLoop != 0) {
          stop(lastStreamID);
        }
        play(res, leftVolume, rightVolume, priority, loop, rate);
      }
    }, 0);
  }

  public void stopLastSound () {
    pool.post(() -> {
      if (lastStreamID != 0 && lastStreamLoop != 0) {
        stop(lastStreamID);
        lastStreamID = 0;
      }
    }, 0);
  }

  public boolean isProbablyPlaying () {
    return lastStreamID != 0;
  }

  public void release () {
    pool.post(() -> {
      if (soundPool != null) {
        sounds.clear();
        soundPool.release();
        soundPool = null;
      }
    }, 0);
  }


}
