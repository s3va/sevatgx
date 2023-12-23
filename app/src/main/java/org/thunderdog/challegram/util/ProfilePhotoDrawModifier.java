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
 * File created on 12/11/2023, 15:38.
 */

package org.thunderdog.challegram.util;

import android.graphics.Canvas;
import android.view.View;

import org.drinkless.tdlib.TdApi;
import org.thunderdog.challegram.loader.AvatarReceiver;
import org.thunderdog.challegram.loader.ComplexReceiver;
import org.thunderdog.challegram.loader.ComplexReceiverProvider;
import org.thunderdog.challegram.telegram.Tdlib;
import org.thunderdog.challegram.tool.Screen;

import me.vkryl.td.Td;

public class ProfilePhotoDrawModifier implements DrawModifier {
  @Override
  public void afterDraw (View view, Canvas c) {
    ComplexReceiver complexReceiver = view instanceof ComplexReceiverProvider ? ((ComplexReceiverProvider) view).getComplexReceiver() : null;
    if (complexReceiver == null) return;

    AvatarReceiver avatarReceiver = complexReceiver.getAvatarReceiver(0);
    if (avatarReceiver.isEmpty()) return;

    int size = Screen.dp(48);
    int x = view.getMeasuredWidth() - size - Screen.dp(20);
    int y = Screen.dp(8);

    avatarReceiver.setBounds(x, y, x + size, y + size);
    if (avatarReceiver.needPlaceholder()) {
      avatarReceiver.drawPlaceholder(c);
    }
    avatarReceiver.draw(c);
  }

  public ProfilePhotoDrawModifier requestFiles (ComplexReceiver complexReceiver, Tdlib tdlib) {
    AvatarReceiver avatarReceiver = complexReceiver.getAvatarReceiver(0);

    TdApi.UserFullInfo info = tdlib.myUserFull();
    if (info != null && info.publicPhoto != null && info.publicPhoto.sizes != null && info.publicPhoto.sizes.length > 0) {
      TdApi.ChatPhotoInfo chatPhotoInfo = new TdApi.ChatPhotoInfo(
        Td.findSmallest(info.publicPhoto.sizes).photo,
        Td.findBiggest(info.publicPhoto.sizes).photo,
        info.publicPhoto.minithumbnail, info.publicPhoto.animation != null, false);
      avatarReceiver.requestSpecific(tdlib, chatPhotoInfo, AvatarReceiver.Options.NO_UPDATES);
    } else {
      avatarReceiver.clear();
    }

    return this;
  }

  @Override
  public int getWidth () {
    return Screen.dp(48);
  }
}
