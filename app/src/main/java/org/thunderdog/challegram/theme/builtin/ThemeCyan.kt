@file:JvmName("ThemeCyan")

package org.thunderdog.challegram.theme.builtin

import androidx.annotation.ColorInt
import me.vkryl.annotation.Autogenerated
import org.thunderdog.challegram.R
import org.thunderdog.challegram.theme.ThemeId
import org.thunderdog.challegram.theme.ThemeColorIdTinted
import org.thunderdog.challegram.theme.ThemeProperty

@Autogenerated class ThemeCyan : ThemeDefault(ThemeId.CYAN) {
  override fun getProperty (@ThemeProperty propertyId: Int): Float = when (propertyId) {
    ThemeProperty.PARENT_THEME ->
      1.0f
    else -> super.getProperty(propertyId)
  }

  @ColorInt override fun getColor (@ThemeColorIdTinted colorId: Int): Int = when (colorId) {
    R.id.theme_color_badge, R.id.theme_color_bubbleOut_inlineOutline, R.id.theme_color_chatListAction, R.id.theme_color_chatListVerify, R.id.theme_color_circleButtonActive, R.id.theme_color_circleButtonRegular, R.id.theme_color_circleButtonTheme, R.id.theme_color_headerBarCallActive, R.id.theme_color_inlineOutline, R.id.theme_color_inputActive, R.id.theme_color_profileSectionActiveContent, R.id.theme_color_seekDone, R.id.theme_color_sliderActive, R.id.theme_color_snackbarUpdate ->
      -14235942 // 0xff26c6da
    R.id.theme_color_bubbleIn_progress, R.id.theme_color_bubbleIn_textLink, R.id.theme_color_bubbleOut_chatCorrectChosenFilling, R.id.theme_color_bubbleOut_chatCorrectFilling, R.id.theme_color_bubbleOut_chatVerticalLine, R.id.theme_color_bubbleOut_inlineIcon, R.id.theme_color_bubbleOut_inlineText, R.id.theme_color_bubbleOut_messageAuthor, R.id.theme_color_bubbleOut_textLink, R.id.theme_color_bubbleOut_ticks, R.id.theme_color_bubbleOut_ticksRead, R.id.theme_color_bubbleOut_waveformActive, R.id.theme_color_checkActive, R.id.theme_color_iconActive, R.id.theme_color_inlineIcon, R.id.theme_color_inlineText, R.id.theme_color_iv_textLink, R.id.theme_color_iv_textMarkedLink, R.id.theme_color_messageAuthor, R.id.theme_color_messageCorrectChosenFilling, R.id.theme_color_messageCorrectFilling, R.id.theme_color_messageSwipeBackground, R.id.theme_color_messageVerticalLine, R.id.theme_color_progress, R.id.theme_color_textLink, R.id.theme_color_textNeutral, R.id.theme_color_ticks, R.id.theme_color_ticksRead, R.id.theme_color_togglerActive, R.id.theme_color_unreadText, R.id.theme_color_waveformActive ->
      -16728876 // 0xff00bcd4
    R.id.theme_color_bubbleIn_textLinkPressHighlight, R.id.theme_color_bubbleOut_textLinkPressHighlight, R.id.theme_color_iv_textLinkPressHighlight, R.id.theme_color_iv_textMarkedLinkPressHighlight, R.id.theme_color_textLinkPressHighlight ->
      1073790164 // 0x4000bcd4
    R.id.theme_color_bubbleOut_background, R.id.theme_color_textSelectionHighlight, R.id.theme_color_unread ->
      -2033670 // 0xffe0f7fa
    R.id.theme_color_bubbleOut_file, R.id.theme_color_controlActive, R.id.theme_color_file, R.id.theme_color_playerButtonActive, R.id.theme_color_profileSectionActive, R.id.theme_color_promo, R.id.theme_color_textSearchQueryHighlight ->
      -11677471 // 0xff4dd0e1
    R.id.theme_color_bubbleOut_waveformInactive, R.id.theme_color_togglerActiveBackground, R.id.theme_color_waveformInactive ->
      -5051406 // 0xffb2ebf2
    R.id.theme_color_chatSendButton, R.id.theme_color_online ->
      -16732991 // 0xff00acc1
    R.id.theme_color_headerBackground, R.id.theme_color_notification, R.id.theme_color_notificationLink, R.id.theme_color_notificationPlayer, R.id.theme_color_passcode ->
      -16738393 // 0xff0097a7
    R.id.theme_color_messageSelection ->
      273535201 // 0x104dd0e1
    R.id.theme_color_seekReady ->
      -3542795 // 0xffc9f0f5
    else -> super.getColor(colorId)
  }
}