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
 */
package org.thunderdog.challegram.telegram;

import androidx.annotation.Nullable;

import org.drinkless.td.libcore.telegram.TdApi;
import org.thunderdog.challegram.component.chat.MessagesManager;
import org.thunderdog.challegram.data.AvatarPlaceholder;
import org.thunderdog.challegram.data.TD;
import org.thunderdog.challegram.loader.ImageFile;
import org.thunderdog.challegram.util.text.Letters;

import me.vkryl.core.StringUtils;
import me.vkryl.core.BitwiseUtils;
import me.vkryl.td.ChatId;
import me.vkryl.td.Td;
import me.vkryl.td.TdConstants;

public class TdlibSender {
  private static final int FLAG_BOT = 1;
  private static final int FLAG_SERVICE_ACCOUNT = 1 << 1;
  private static final int FLAG_SCAM = 1 << 2;
  private static final int FLAG_FAKE = 1 << 3;

  private final Tdlib tdlib;
  private final long inChatId;
  private final TdApi.MessageSender sender;

  private final String name, nameShort, username;
  private final TdApi.ChatPhotoInfo photo;
  private final Letters letters;
  private final AvatarPlaceholder.Metadata placeholderMetadata;
  private final int flags;

  public TdlibSender (Tdlib tdlib, long inChatId, TdApi.MessageSender sender) {
    this(tdlib, inChatId, sender, null, false);
  }

  public TdlibSender (Tdlib tdlib, long inChatId, TdApi.MessageSender sender, @Nullable MessagesManager manager, boolean isDemo) {
    this.tdlib = tdlib;
    this.inChatId = inChatId;
    this.sender = sender;

    int flags = 0;
    switch (sender.getConstructor()) {
      case TdApi.MessageSenderChat.CONSTRUCTOR: {
        final long chatId = ((TdApi.MessageSenderChat) sender).chatId;
        TdApi.Chat chat = tdlib.chat(chatId);

        this.name = tdlib.chatTitle(chat, false);
        this.nameShort = tdlib.chatTitle(chat, false, true);
        this.username = tdlib.chatUsername(chat);
        this.photo = chat != null ? chat.photo : null;
        this.letters = tdlib.chatLetters(chat);
        this.placeholderMetadata = tdlib.chatPlaceholderMetadata(chatId, chat, false);

        flags = BitwiseUtils.setFlag(flags, FLAG_BOT, tdlib.isBotChat(chat));
        flags = BitwiseUtils.setFlag(flags, FLAG_SERVICE_ACCOUNT, tdlib.isServiceNotificationsChat(chatId));
        flags = BitwiseUtils.setFlag(flags, FLAG_SCAM, tdlib.chatScam(chat));
        flags = BitwiseUtils.setFlag(flags, FLAG_FAKE, tdlib.chatFake(chat));

        break;
      }
      case TdApi.MessageSenderUser.CONSTRUCTOR: {
        final long userId = ((TdApi.MessageSenderUser) sender).userId;
        TdApi.User user = isDemo && manager != null ? manager.demoParticipant(userId) : tdlib.cache().user(userId);
        TdApi.ProfilePhoto profilePhoto = user != null ? user.profilePhoto : null;

        this.name = TD.getUserName(userId, user);
        this.nameShort = TD.getUserSingleName(userId, user);
        this.username = user != null && !StringUtils.isEmpty(user.username) ? user.username : null;
        this.photo = profilePhoto != null ? new TdApi.ChatPhotoInfo(profilePhoto.small, profilePhoto.big, profilePhoto.minithumbnail, profilePhoto.hasAnimation) : null;
        this.letters = TD.getLetters(user);
        this.placeholderMetadata = tdlib.cache().userPlaceholderMetadata(userId, user, false);

        flags = BitwiseUtils.setFlag(flags, FLAG_BOT, TD.isBot(user));
        flags = BitwiseUtils.setFlag(flags, FLAG_SERVICE_ACCOUNT, tdlib.isServiceNotificationsChat(ChatId.fromUserId(userId)));
        flags = BitwiseUtils.setFlag(flags, FLAG_SCAM, user != null && user.isScam);
        flags = BitwiseUtils.setFlag(flags, FLAG_FAKE, user != null && user.isFake);

        break;
      }
      default: {
        throw new UnsupportedOperationException(sender.toString());
      }
    }
    this.flags = flags;
  }

  public boolean isUser () {
    return sender.getConstructor() == TdApi.MessageSenderUser.CONSTRUCTOR;
  }

  public boolean isChat () {
    return sender.getConstructor() == TdApi.MessageSenderChat.CONSTRUCTOR;
  }

  public boolean isAnonymousGroupAdmin () {
    return isChat() && inChatId == getChatId() && !tdlib.isChannel(getChatId());
  }

  public boolean isSameSender (@Nullable TdlibSender sender) {
    return sender != null && Td.equalsTo(this.sender, sender.sender);
  }

  public boolean isSelf () {
    return getUserId() == tdlib.myUserId();
  }

  public boolean isChannel () {
    return isChat() && tdlib.isChannel(getChatId());
  }

  public long getUserId () {
    return isUser() ? ((TdApi.MessageSenderUser) sender).userId : 0;
  }

  public long getChatId () {
    return isChat() ? ((TdApi.MessageSenderChat) sender).chatId : 0;
  }

  public String getName () {
    return name;
  }

  public String getNameShort () {
    return nameShort;
  }

  public String getUsername () {
    return username;
  }

  public TdApi.ChatPhotoInfo getPhoto () {
    return photo;
  }

  public Letters getLetters () {
    return letters;
  }

  public AvatarPlaceholder.Metadata getPlaceholderMetadata () {
    return placeholderMetadata;
  }

  public int getAvatarColorId () {
    return placeholderMetadata.colorId;
  }

  public int getNameColorId () {
    return TD.getNameColorId(getAvatarColorId());
  }

  public ImageFile getAvatar () {
    switch (sender.getConstructor()) {
      case TdApi.MessageSenderChat.CONSTRUCTOR:
        return tdlib.chatAvatar(((TdApi.MessageSenderChat) sender).chatId);
      case TdApi.MessageSenderUser.CONSTRUCTOR:
        return tdlib.cache().userAvatar(((TdApi.MessageSenderUser) sender).userId);
    }
    throw new AssertionError();
  }

  // flags

  public boolean isServiceChannelBot () {
    return getUserId() == TdConstants.TELEGRAM_CHANNEL_BOT_ACCOUNT_ID;
  }

  public boolean isBot () {
    return BitwiseUtils.getFlag(flags, FLAG_BOT);
  }

  public boolean isServiceAccount () {
    return BitwiseUtils.getFlag(flags, FLAG_SERVICE_ACCOUNT);
  }

  public boolean isScam () {
    return BitwiseUtils.getFlag(flags, FLAG_SCAM);
  }

  public boolean isFake () {
    return BitwiseUtils.getFlag(flags, FLAG_FAKE);
  }

  public boolean hasChatMark () {
    return isScam() || isFake();
  }

  // Creators

  public static TdlibSender[] valueOfUserIds (Tdlib tdlib, long inChatId, long[] userIds) {
    TdlibSender[] senders = new TdlibSender[userIds.length];
    for (int i = 0; i < userIds.length; i++) {
      long userId = userIds[i];
      senders[i] = new TdlibSender(tdlib, inChatId, new TdApi.MessageSenderUser(userId));
    }
    return senders;
  }
}
