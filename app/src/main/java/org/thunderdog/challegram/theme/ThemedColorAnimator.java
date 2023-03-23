/*
 * This file is a part of Telegram X
 * Copyright © 2014 (tgx-android@pm.me)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * File created on 23/03/2023
 */
package org.thunderdog.challegram.theme;

import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.Nullable;

import me.vkryl.android.animator.ColorAnimator;

public class ThemedColorAnimator extends ColorAnimator {
  private static FutureColor futureForColorId (@ThemeColorId int colorId, @Nullable ThemeDelegate forcedTheme) {
    if (colorId == ThemeColorId.NONE)
      throw new IllegalArgumentException();
    if (forcedTheme != null) {
      return () ->
        forcedTheme.getColor(colorId);
    } else {
      return () ->
        Theme.getColor(colorId);
    }
  }

  public ThemedColorAnimator (View target, Interpolator interpolator, long duration, @ThemeColorId int startColorId) {
    this((animator, newValue) -> target.invalidate(), interpolator, duration, startColorId, null);
  }

  public ThemedColorAnimator (Target<FutureColor> target, Interpolator interpolator, long duration, @ThemeColorId int startColorId) {
    this(target, interpolator, duration, startColorId, null);
  }

  private @ThemeColorId int currentColorId;
  private @Nullable ThemeDelegate forcedTheme;

  public ThemedColorAnimator (Target<FutureColor> target, Interpolator interpolator, long duration, @ThemeColorId int startColorId, @Nullable ThemeDelegate forcedTheme) {
    super(target, interpolator, duration, futureForColorId(startColorId, forcedTheme));
    this.currentColorId = startColorId;
    this.forcedTheme = forcedTheme;
  }

  public final void setValue (@ThemeColorId int colorId, boolean animated) {
    setValue(colorId, null, animated);
  }

  public final void setValue (@ThemeColorId int colorId, @Nullable ThemeDelegate forcedTheme, boolean animated) {
    if (this.currentColorId != colorId || this.forcedTheme != forcedTheme) {
      this.currentColorId = colorId;
      this.forcedTheme = forcedTheme;
      setValue(futureForColorId(colorId, forcedTheme), animated);
    } else if (!animated) {
      applyCurrentValue(true);
    }
  }

  @ThemeColorId
  public int getColorIdValue () {
    return currentColorId;
  }
}
