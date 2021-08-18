package org.thunderdog.challegram.support;

import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;

import org.thunderdog.challegram.theme.Theme;
import org.thunderdog.challegram.theme.ThemeColorId;
import org.thunderdog.challegram.util.ColorChangeAcceptorDelegate;

import me.vkryl.core.ColorUtils;

/**
 * Date: 26/01/2017
 * Author: default
 */

public abstract class SimpleShapeDrawable extends Drawable implements ColorChangeAcceptorDelegate {
  public static final boolean USE_SOFTWARE_SHADOW = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;

  private @ThemeColorId
  int colorId;
  private float changeFactor;
  private @ThemeColorId
  int toColorId;

  protected final float size;
  private final boolean isPressed;

  public SimpleShapeDrawable (int colorId, float size, boolean isPressed) {
    this.colorId = colorId;
    this.size = size;
    this.isPressed = isPressed;
  }

  @Override
  public void applyColor (@ThemeColorId int fromColorId, @ThemeColorId int toColorId, float factor) {
    if (colorId != fromColorId || changeFactor != factor || (this.toColorId != toColorId && factor > 0f)) {
      this.colorId = fromColorId;
      this.changeFactor = factor;
      this.toColorId = toColorId;
    }
  }

  @Override
  public final int getDrawColor () {
    final int color;
    if (changeFactor == 0f) {
      color = colorId != 0 ? Theme.getColor(colorId) : 0;
    } else if (changeFactor == 1f) {
      color = toColorId != 0 ? Theme.getColor(toColorId) : 0;
    } else {
      color = ColorUtils.fromToArgb(colorId != 0 ? Theme.getColor(colorId) : 0, toColorId != 0 ? Theme.getColor(toColorId) : 0, changeFactor);
    }
    return isPressed ? ColorUtils.compositeColor(color, 0x40a0a0a0) : color;
  }

  @Override
  public void setAlpha (int alpha) { }

  @Override
  public void setColorFilter (ColorFilter colorFilter) { }

  @Override
  public int getOpacity () {
    return PixelFormat.UNKNOWN;
  }
}
