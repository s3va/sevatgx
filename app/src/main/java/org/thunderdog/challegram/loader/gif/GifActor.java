/*
 * This file is a part of Telegram X
 * Copyright © 2014-2022 (tgx-android@pm.me)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * File created on 01/03/2016 at 12:16
 */
package org.thunderdog.challegram.loader.gif;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;
import org.thunderdog.challegram.Log;
import org.thunderdog.challegram.N;
import org.thunderdog.challegram.U;
import org.thunderdog.challegram.config.Config;
import org.thunderdog.challegram.data.TD;
import org.thunderdog.challegram.data.TGMessageSticker;
import org.thunderdog.challegram.emoji.Emoji;
import org.thunderdog.challegram.player.TGPlayerController;
import org.thunderdog.challegram.telegram.Tdlib;
import org.thunderdog.challegram.telegram.TdlibManager;
import org.thunderdog.challegram.tool.Screen;
import org.thunderdog.challegram.ui.EmojiMediaListController;
import org.thunderdog.challegram.ui.StickersListController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.vkryl.android.ViewUtils;
import me.vkryl.core.StringUtils;
import me.vkryl.core.reference.ReferenceList;
import me.vkryl.td.Td;

@SuppressWarnings ("JniMissingFunction")
public class GifActor implements GifState.Callback, TGPlayerController.TrackChangeListener {
  private static final int FLAG_CANCELLED = 0x01;
  private static final int FLAG_LOADING_FILE = 0x02;
  private static final int FLAG_AWAITING = 0x04;

  private static final int LOTTIE_CACHE_NONE = 0;
  private static final int LOTTIE_CACHE_CREATING = 1;
  private static final int LOTTIE_CACHE_CREATED = 2;
  private static final int LOTTIE_CACHE_ERROR = 3;

  private volatile int flags;
  private volatile long nativePtr;
  private final GifFile file;
  private final Object gifLock = new Object();
  private @Nullable GifState gif;
  private final int[] metadata;
  private final double[] lottieMetadata;
  private final GifThread thread;
  private final boolean isLottie;
  private int lottieCacheState = LOTTIE_CACHE_NONE;

  public boolean isLottie () {
    return isLottie;
  }

  private final Client.ResultHandler remoteFileHandler;
  private final Client.ResultHandler fileLoadHandler;

  private final double maxFrameRate;
  private static final int DEFAULT_MAX_STICKER_RESOLUTION = 384;
  private static final int REDUCED_MAX_STICKER_RESOLUTION = 160;

  private final boolean isPlayOnce;

  public GifActor (final GifFile file, GifThread thread) {
    this.isPlayOnce = file.isPlayOnce();
    file.setVibrationPattern(Emoji.VIBRATION_PATTERN_NONE);
    this.maxFrameRate = file.needOptimize() ? REDUCED_MAX_FRAME_RATE : DEFAULT_MAX_FRAME_RATE;
    this.isLottie = file.getGifType() == GifFile.TYPE_TG_LOTTIE;
    this.metadata = new int[4];
    this.lottieMetadata = new double[3];
    this.thread = thread;
    this.file = file;
    this.isPlaybackFrozen = isFrozen(file);

    this.remoteFileHandler = new Client.ResultHandler() {
      @Override
      public void onResult (TdApi.Object object) {
        switch (object.getConstructor()) {
          case TdApi.File.CONSTRUCTOR: {
            TdApi.File resultFile = (TdApi.File) object;
            Td.copyTo(resultFile, file.getFile());
            if (resultFile.local.isDownloadingCompleted) {
              dispatchFileLoaded();
            } else {
              flags |= FLAG_LOADING_FILE;
              if (!resultFile.local.isDownloadingActive) {
                if (!Config.DEBUG_DISABLE_DOWNLOAD) {
                  file.tdlib().client().send(new TdApi.DownloadFile(resultFile.id, 1, 0, 0, false), fileLoadHandler);
                }
              }
            }
            break;
          }
          case TdApi.Error.CONSTRUCTOR: {
            Log.e(Log.TAG_GIF_LOADER, "GetFileRemote failed: %s", TD.toErrorString(object));
            break;
          }
        }
      }
    };
    this.fileLoadHandler = object -> {
      switch (object.getConstructor()) {
        case TdApi.File.CONSTRUCTOR: {
          TdApi.File resultFile = (TdApi.File) object;
          Td.copyTo(resultFile, file.getFile());
          if (resultFile.local.isDownloadingCompleted) {
            dispatchFileLoaded();
          } else if (!resultFile.local.isDownloadingActive) {
            Log.e(Log.TAG_GIF_LOADER, "DownloadFile ignored: %s", resultFile);
          }
          break;
        }
        case TdApi.Ok.CONSTRUCTOR: {
          break;
        }
        case TdApi.Error.CONSTRUCTOR: {
          Log.e(Log.TAG_GIF_LOADER, "DownloadFile failed: %s", TD.toErrorString(object));
          break;
        }
      }
    };

    if (file.isRoundVideo()) {
      isPlayingRoundVideo = TdlibManager.instance().player().isPlayingRoundVideo();
      TdlibManager.instance().player().addTrackChangeListener(this);
    }

    addFreezeCallback(this);
  }

  private void setPlaybackFrozen (boolean isFrozen) {
    if (this.isPlaybackFrozen != isFrozen) {
      this.isPlaybackFrozen = isFrozen;
      if (!isFrozen) {
        onDrawNextFrame();
      }
    }
  }

  private volatile boolean isPlayingRoundVideo, isPlaybackFrozen, seekToStart;

  @Override
  public void onTrackChanged (Tdlib tdlib, @Nullable TdApi.Message newTrack, int fileId, int state, float progress, boolean byUser) {
    boolean newIsFrozen = state != TGPlayerController.STATE_NONE;
    if (isPlayingRoundVideo != newIsFrozen || newIsFrozen) {
      isPlayingRoundVideo = newIsFrozen;
      if (gif != null) {
        gif.setFrozen(isPlayingRoundVideo && lastTimeStamp == 0);
      }
      onDrawNextFrame();
    }
  }

  // GifBridge thread
  // called only once
  public void act () {
    TdApi.File file = this.file.getFile();

    if (TD.isFileLoadedAndExists(file)) {
      onLoad(file);
      return;
    }

    if (this.file instanceof GifFileRemote) {
      this.file.tdlib().client().send(new TdApi.GetRemoteFile(file.remote.id, new TdApi.FileTypeAnimation()), remoteFileHandler);
    } else {
      flags |= FLAG_LOADING_FILE;
      if (!Config.DEBUG_DISABLE_DOWNLOAD) {
        this.file.tdlib().client().send(new TdApi.DownloadFile(file.id, 1, 0, 0, false), fileLoadHandler);
      }
    }
  }

  // GifBridge thread
  // called only once
  public void cancel () {
    synchronized (this) {
      flags |= FLAG_CANCELLED;
      if ((flags & FLAG_LOADING_FILE) != 0) {
        file.tdlib().client().send(new TdApi.CancelDownloadFile(file.getFileId(), false), fileLoadHandler);
        flags &= ~FLAG_LOADING_FILE;
      } else {
        thread.onDestroy(this);
      }
    }
  }

  // GifBridge thread
  public void watcherJoined (GifWatcherReference reference) {
    if (lastProgress != 0f && (flags & FLAG_LOADING_FILE) != 0) {
      reference.gifProgress(file, lastProgress);
    } else if (gif != null) {
      reference.gifLoaded(file, gif);
    }
  }

  private float lastProgress;
  // TG or HTTP reader thread
  public void cacheProgress (float progress) {
    if (progress != 0f) {
      lastProgress = progress;
    }
  }

  // GifBridge thread
  public void onLoad (TdApi.File file) {
    synchronized (this) {
      flags &= ~FLAG_LOADING_FILE;
    }

    TdApi.File localFile = this.file.getFile();
    Td.copyTo(file, localFile);

    if ((flags & FLAG_CANCELLED) == 0) {
      thread.startDecoding(this, file.local.path);
    }
  }

  // TG thread
  // CancelDownloadFile or DownloadFile

  private void dispatchFileLoaded () {
    GifBridge.instance().getBaseThread().post(() -> {
      if (!isCancelled()) {
        onLoad(file.getFile());
      }
    }, 0);
  }

  /*@Override
  public void onResult (TdApi.Object object) {
    if (isCancelled()) {
      return;
    }
    switch (object.getConstructor()) {
      case TdApi.File.CONSTRUCTOR: {
        TdApi.File resultFile = (TdApi.File) object;


        TD.copyFileData(, file.getFile());
        GifBridge.instance().getBaseThread().post(new Runnable() {
          @Override
          public void run () {
            if (!isCancelled()) {
              if (TD.isFileLoadedAndExists(file.getFile())) {
                onLoad(file.getFile());
              } else {
                flags |= FLAG_LOADING_FILE;
                TG.getClientInstance().send(new TdApi.DownloadFile(file.getFileId(), 1), GifActor.this);
              }
            }
          }
        }, 0);
        break;
      }
    }
  }*/

  private boolean isCancelled () {
    return (flags & FLAG_CANCELLED) != 0;
  }

  // Decoding stuff

  private final Object nativeSync = new Object();

  private void destroyDecoder () {
    if (nativePtr != 0) {
      if (isLottie) {
        N.cancelLottieDecoder(nativePtr);
        synchronized (nativeSync) {
          N.destroyLottieDecoder(nativePtr);
          nativePtr = 0;
        }
        if (lottieCacheFile != null) {
          LottieCache.instance().checkFile(file, lottieCacheFile, file.needOptimize(), lottieCacheFileSize, file.getFitzpatrickType());
        }
      } else {
        N.destroyDecoder(nativePtr);
        nativePtr = 0;
      }
    }
  }

  // Decoder thread
  public void startDecoding (String path) {
    synchronized (this) {
      if (isCancelled()) {
        return;
      }
    }
    int width, height;
    boolean error;
    if (isLottie) {
      String json = U.gzipFileToString(path);
      if (StringUtils.isEmpty(json))
        return;
      nativePtr = N.createLottieDecoder(path, json, lottieMetadata, file.getFitzpatrickType());
      totalFrameCount = (long) lottieMetadata[0];
      file.setTotalFrameCount(totalFrameCount);
      frameRate = lottieMetadata[1];
      double durationSeconds = lottieMetadata[2];
      width = height = file.needOptimize() ? Math.min(Math.max(EmojiMediaListController.getEstimateColumnResolution(), StickersListController.getEstimateColumnResolution()), REDUCED_MAX_STICKER_RESOLUTION) : Math.min(Screen.dp(TGMessageSticker.MAX_STICKER_SIZE), DEFAULT_MAX_STICKER_RESOLUTION);
      error = totalFrameCount <= 0 || frameRate <= 0 || durationSeconds <= 0; // || durationSeconds > 3.0 || frameRate % 30.0 != 0;
      if (totalFrameCount == 1) {
        file.setIsStill(true);
      }
    } else {
      nativePtr = N.createDecoder(path, metadata);
      width = metadata[0];
      height = metadata[1];
      error = (width <= 0 || height <= 0);
    }
    if (error) {
      destroyDecoder();
    }
    if (nativePtr == 0) {
      return;
    }
    int rotation = 0;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && !isLottie) {
      MediaMetadataRetriever retriever = null;
      try {
        retriever = U.openRetriever(path);
        String rotationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        rotation = StringUtils.parseInt(rotationString);
      } catch (Throwable ignored) { }
      U.closeRetriever(retriever);
    }
    int queueSize = file.isStill() ? 1 : isLottie ? 2 : GifState.DEFAULT_QUEUE_SIZE;
    GifState gif = new GifState(width, height, rotation, this, queueSize);
    gif.setFrozen(isPlayingRoundVideo);
    boolean success = false;
    try {
      success = gif.init(frame -> {
        if (isLottie) {
          long startFrame = file.needDecodeLastFrame() || file.hasLooped() ? totalFrameCount - 1 : 0;
          synchronized (nativeSync) {
            if (nativePtr != 0 && N.getLottieFrame(nativePtr, frame.bitmap, lastFrameNo = startFrame)) {
              frame.no = startFrame;
              return true;
            }
          }
          return false;
        } else {
          int ret = N.getVideoFrame(nativePtr, frame.bitmap, metadata);
          frame.no = lastTimeStamp = metadata[3];
          return ret == 1 && !N.isVideoBroken(nativePtr);
        }
      }, 1, Bitmap.Config.ARGB_8888);
    } catch (OutOfMemoryError e) {
      Log.w(Log.TAG_GIF_LOADER, "Cannot start decoding gif", e);
    }

    if (!success) {
      gif.recycle();
      destroyDecoder();
      return;
    }

    if (isPlayingRoundVideo) {
      lastTimeStamp = 0;
    }
    GifBridge.instance().onGifLoaded(file, gif);
  }

  private volatile int lastTimeStamp;
  private volatile long lastFrameNo;
  private long totalFrameCount;
  private double frameRate;

  // GifStage thread
  public void onGifLoaded (GifState gif) {
    synchronized (gifLock) {
      this.gif = gif;
    }
    if (!isPlaybackFrozen) {
      thread.prepareNextFrame(this);
      scheduleNext(false);
    } else {
      GifBridge.instance().dispatchGifFrameChanged(file, gif);
    }
  }

  public void seekToStart () {
    if (!seekToStart && lastTimeStamp != 0) {
      seekToStart = true;
      onDrawNextFrame();
    }
  }

  // Decoder thread
  public void prepareStartFrame () {
    if (gif == null) {
      return;
    }
    boolean res = N.seekVideoToStart(nativePtr);
    if (res) {
      prepareNextFrame();
    }
  }

  private static final double DEFAULT_MAX_FRAME_RATE = /*BuildConfig.DEBUG ? 60.0 :*/ 30.0;
  private static final double REDUCED_MAX_FRAME_RATE = 30.0;

  private File lottieCacheFile;
  private int lottieCacheFileSize;

  private long frameDelta () {
    return Math.max(1, (long) (frameRate / maxFrameRate));
  }

  // Decoder thread
  public void prepareNextFrame () {

    GifState gif;
    synchronized (gifLock) {
      gif = this.gif;
    }
    if (gif == null) {
      return;
    }
    boolean success = false;
    boolean async = false;
    final GifState.Frame free = gif.takeFree();
    if (free != null) {
      long desiredNextFrameNo;
      if (isLottie) {
        long frameDelta = frameDelta();
        desiredNextFrameNo = lastFrameNo + frameDelta;
        if (desiredNextFrameNo >= totalFrameCount) {
          file.onLoop();
          desiredNextFrameNo = 0;
          file.onFrameChange(0, 0);
        } else {
          if (desiredNextFrameNo + frameDelta >= totalFrameCount && isPlayOnce) {
            file.setLooped(true);
          }
          file.onFrameChange(desiredNextFrameNo, frameDelta);
        }
      } else {
        desiredNextFrameNo = 0;
      }
      boolean gifRestarted = false;
      if (seekToStart && !isLottie) {
        gif.clearBusy();
        seekToStart = false;
        N.seekVideoToStart(nativePtr);
        desiredNextFrameNo = 0;
        gifRestarted = true;
      }
      final long nextFrameNo = desiredNextFrameNo;

      if (isLottie) {
        // lottieCacheState = LOTTIE_CACHE_ERROR;
        switch (lottieCacheState) {
          case LOTTIE_CACHE_NONE: {
            lottieCacheFile = LottieCache.getCacheFile(file, file.needOptimize(), lottieCacheFileSize = Math.max(free.getWidth(), free.getHeight()), file.getFitzpatrickType(), TimeUnit.MINUTES.toMillis(15), 8);
            int status;
            synchronized (nativeSync) {
              status = nativePtr == 0 ? 3 : lottieCacheFile == null ? 2 : N.createLottieCache(nativePtr, lottieCacheFile.getPath(), gif.getBitmap(false), free.bitmap, false, (file.needOptimize() ? REDUCED_MAX_FRAME_RATE : DEFAULT_MAX_FRAME_RATE) == 30.0);
            }
            switch (status) {
              case 0: {
                lottieCacheState = LOTTIE_CACHE_CREATED;
                synchronized (nativeSync) {
                  if (nativePtr != 0) {
                    N.getLottieFrame(nativePtr, free.bitmap, free.no = lastFrameNo = nextFrameNo);
                    success = true;
                  }
                }
                break;
              }
              case 1: {
                lottieCacheState = LOTTIE_CACHE_CREATING;
                async = true;
                LottieCache.instance().thread(file.needOptimize()).post(() -> {
                  int newStatus;
                  synchronized (nativeSync) {
                    if (nativePtr == 0)
                      return;
                    newStatus = N.createLottieCache(nativePtr, lottieCacheFile.getPath(), gif.getBitmap(false), free.bitmap, true, (file.needOptimize() ? REDUCED_MAX_FRAME_RATE : DEFAULT_MAX_FRAME_RATE) == 30.0);
                  }
                  if (newStatus == 0) {
                    free.no = lastFrameNo = totalFrameCount - frameDelta();
                    lottieCacheState = LOTTIE_CACHE_CREATED;
                    if (free.no != nextFrameNo) {
                      synchronized (nativeSync) {
                        if (nativePtr == 0)
                          return;
                        N.getLottieFrame(nativePtr, free.bitmap, free.no = lastFrameNo = nextFrameNo);
                      }
                    }
                    gif.addBusy(free);
                    GifBridge.instance().nextFrameReady(this);
                  } else {
                    gif.addFree(free);
                  }
                }, 0);
                break;
              }
              case 3:
              default: {
                lottieCacheState = LOTTIE_CACHE_ERROR;
                break;
              }
            }
            break;
          }
          case LOTTIE_CACHE_CREATED:
          case LOTTIE_CACHE_ERROR: {
            synchronized (nativeSync) {
              if (nativePtr != 0) {
                N.getLottieFrame(nativePtr, free.bitmap, free.no = lastFrameNo = nextFrameNo);
                success = true;
              }
            }
            break;
          }
        }
      } else {
        int ret = N.getVideoFrame(nativePtr, free.bitmap, metadata);
        free.no = metadata[3];
        success = true;
        if (ret == 2 && isPlayOnce) {
          file.setLooped(true);
        }
      }
      if (!async) {
        if (success) {
          gif.addBusy(free);
        } else {
          gif.addFree(free);
        }
      }
    }
    if (success) {
      GifBridge.instance().nextFrameReady(this);
    }
  }

  // GifStage thread
  public void nextFrameReady () {
    synchronized (this) {
      if ((flags & FLAG_AWAITING) != 0) {
        flags &= ~FLAG_AWAITING;
        onNextFrame(false);
      }
    }
  }

  // GifStage thread
  private void scheduleNext (boolean force) {
    int frameTime;
    int lastTimeStamp;

    if (isLottie) {
      frameTime = Math.max(/*BuildConfig.DEBUG ? 2 :*/ 5, (int) (1000.0 / Math.min(maxFrameRate, frameRate) - 17)); // 16; // Math.max(5, (int) (1000.0 / frameRate) - 20);
      lastTimeStamp = 0;
    } else {
      lastTimeStamp = this.lastTimeStamp;
      if (metadata[3] < lastTimeStamp) {
        lastTimeStamp = 0;
      }

      if (metadata[3] - lastTimeStamp != 0) {
        frameTime = metadata[3] - lastTimeStamp;
      } else {
        frameTime = 50;
      }
      lastTimeStamp = metadata[3];
      frameTime = Math.max(5, frameTime - 17);
    }

    synchronized (this) {
      if ((flags & FLAG_CANCELLED) == 0) {
        if (GifBridge.instance().scheduleNextFrame(this, file.getFileId(), force ? 0 : frameTime, force)) {
          if (gif == null || !gif.isFrozen()) {
            this.lastTimeStamp = lastTimeStamp;
          }
        }
      }
    }
  }

  // GifStage thread
  public void onNextFrame (boolean allowAwait) {
    synchronized (this) {
      if ((flags & FLAG_CANCELLED) == 0 && gif != null) {
        if (gif.hasNext()) {
          GifBridge.instance().dispatchGifFrameChanged(file, gif);
        } else if (allowAwait) {
          flags |= FLAG_AWAITING;
        }
      }
    }
  }

  private boolean awaitingResume;

  @Override
  public boolean onDraw () {
    if (awaitingResume && !file.hasLooped()) {
      awaitingResume = false;
      return true;
    }
    return false;
  }

  private static final int VIBRATE_MAIN = 3;
  private static final int VIBRATE_SIMPLE = 2;
  private static final int VIBRATE_NONE = 0;

  @Override
  public void onApplyNextFrame (long no) {
    if (isLottie) {
      int vibrationPattern = file.getVibrationPattern();
      if (vibrationPattern == Emoji.VIBRATION_PATTERN_NONE)
        return;
      double ms = (no % frameRate) / frameRate;
      double seconds = (int) (no / frameRate);
      int vibrateMode = VIBRATE_NONE;
      switch (vibrationPattern) {
        case Emoji.VIBRATION_PATTERN_HEARTBEAT: {
          vibrateMode = ms == 0 || ms == .2 ? VIBRATE_SIMPLE : VIBRATE_NONE;
          break;
        }
        case Emoji.VIBRATION_PATTERN_HEART_CUPID: {
          if (seconds == 1) {
            vibrateMode = ms == .2 ? 5 : ms == .8 || ms == .6 ? VIBRATE_SIMPLE : VIBRATE_NONE;
          }
          break;
        }
        case Emoji.VIBRATION_PATTERN_BROKEN_HEART: {
          if (seconds == 0) {
            vibrateMode = ms == .8 ? VIBRATE_SIMPLE : VIBRATE_NONE;
          } else if (seconds == 1) {
            vibrateMode = ms == .6 ? VIBRATE_MAIN : VIBRATE_NONE;
          }
          break;
        }
        case Emoji.VIBRATION_PATTERN_CAT_IN_LOVE: {
          vibrateMode = (seconds == 0 && (ms == .2 || ms == .4)) || (seconds == 1 && (ms == 0 || ms == .2 || ms == .8)) || (seconds == 2 && ms == 0) ? VIBRATE_SIMPLE : VIBRATE_NONE;
          break;
        }
      }
      if (vibrateMode != VIBRATE_NONE) {
        View view = GifBridge.instance().findAnyView(file);;
        if (view != null) {
          ViewUtils.hapticVibrate(view, vibrateMode == VIBRATE_MAIN, true);
        }
      }
    }
  }

  @UiThread
  @Override
  public void onDrawNextFrame () {
    synchronized (this) {
      if ((flags & FLAG_CANCELLED) == 0) {
        if (seekToStart && lastTimeStamp == 0) {
          seekToStart = false;
        }
        if (isPlaybackFrozen && !seekToStart) {
          return;
        }
        if (isPlayOnce && file.hasLooped()) {
          awaitingResume = true;
          return;
        }
        if (isPlayingRoundVideo) {
          if (TdlibManager.instance().player().isPlayingMessage(file.getChatId(), file.getMessageId())) {
            thread.prepareStartFrame(this);
            if (lastTimeStamp != 0) {
              scheduleNext(true);
            }
          }
          return;
        }
        if (GifBridge.instance().canScheduleNextFrame(this, file.getFileId())) {
          thread.prepareNextFrame(this);
          scheduleNext(false);
        }
      }
    }
  }

  // Decoder thread
  public void onDestroy () {
    destroyDecoder();
    if (file != null && file.isRoundVideo()) {
      TdlibManager.instance().player().removeTrackChangeListener(this);
    }
    synchronized (gifLock) {
      if (gif != null) {
        gif.recycle();
        gif = null;
      }
    }
    removeFreezeCallback(this);
  }

  private static int mFreezeReasonCount;

  private static ReferenceList<GifActor> activeActors;

  private static void addFreezeCallback (GifActor actor) {
    if (activeActors == null) {
      synchronized (GifActor.class) {
        if (activeActors == null) {
          activeActors = new ReferenceList<>(true);
        }
      }
    }
    activeActors.add(actor);
  }

  private static void removeFreezeCallback (GifActor actor) {
    if (activeActors == null) {
      synchronized (GifActor.class) {
        if (activeActors == null) {
          return;
        }
      }
    }
    activeActors.remove(actor);
  }

  private static List<GifFile> freezeWhiteList;

  public static void addFreezeReason (GifFile file, boolean add) { // Specific reason to lock all animated stuff, except this one
    synchronized (GifActor.class) {
      boolean notify;
      if (add) {
        if (freezeWhiteList == null)
          freezeWhiteList = new ArrayList<>();
        notify = !isWhiteListed(freezeWhiteList, file);
        freezeWhiteList.add(file);
      } else {
        if (freezeWhiteList == null)
          return;
        notify = freezeWhiteList.remove(file);
      }
      if (notify) {
        mFreezeReasonCount += add ? 1 : -1;
        checkFrozenActors();
      }
    }
  }

  private static void checkFrozenActors () {
    if (activeActors != null) {
      for (GifActor actor : activeActors) {
        actor.setPlaybackFrozen(isFrozenImpl(actor.file));
      }
    }
  }

  public static void addFreezeReason (int delta) {
    synchronized (GifActor.class) {
      boolean oldFrozen = mFreezeReasonCount != 0;
      mFreezeReasonCount += delta;
      boolean isFrozen = mFreezeReasonCount != 0;
      if (oldFrozen != isFrozen) {
        checkFrozenActors();
      }
    }
  }

  private static boolean isFrozen (GifFile file) {
    synchronized (GifActor.class) {
      return isFrozenImpl(file);
    }
  }

  private static boolean isFrozenImpl (GifFile file) {
    return file.isStill() || (mFreezeReasonCount != 0 && !isWhiteListed(freezeWhiteList, file));
  }

  private static boolean isWhiteListed (List<GifFile> whiteList, GifFile file) {
    if (whiteList == null || whiteList.isEmpty())
      return false;
    return whiteList.contains(file);
  }

  public static void restartGif (@NonNull GifFile gifFile) {
    if (gifFile.isStill() || gifFile.isRoundVideo() || gifFile.isLottie()) {
      return;
    }
    if (activeActors == null) {
      synchronized (GifActor.class) {
        if (activeActors == null) {
          return;
        }
      }
    }
    String key = gifFile.toString();
    for (GifActor actor : activeActors) {
      if (actor.file.toString().equals(key)) {
        actor.seekToStart();
      }
    }
  }
}
