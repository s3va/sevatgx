/**
 * File created on 07/02/16 at 15:58
 * Copyright Vyacheslav Krylov, 2014
 */
package org.thunderdog.challegram.widget;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import org.thunderdog.challegram.R;
import org.thunderdog.challegram.core.Lang;
import org.thunderdog.challegram.theme.Theme;
import org.thunderdog.challegram.tool.UI;
import org.thunderdog.challegram.tool.Views;
import org.thunderdog.challegram.util.CustomTypefaceSpan;

public class PrefixEditText extends EditText implements InputFilter, View.OnLongClickListener {
  private String prefix;
  private int minLength;
  private boolean forceEdit;
  private boolean editable;

  public PrefixEditText (Context context) {
    super(context);
    editable = true;
    setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    setFilters(new InputFilter[] {this});
    setCustomSelectionActionModeCallback(new ActionMode.Callback() {
      @Override
      public boolean onCreateActionMode (ActionMode mode, Menu menu) {
        return false;
      }

      @Override
      public boolean onPrepareActionMode (ActionMode mode, Menu menu) {
        return false;
      }

      @Override
      public boolean onActionItemClicked (ActionMode mode, MenuItem item) {
        return false;
      }

      @Override
      public void onDestroyActionMode (ActionMode mode) {

      }
    });
  }

  public void setPrefix (@StringRes int resId) {
    setPrefix(Lang.getString(resId));
  }

  public void setPrefix (String prefix) {
    this.prefix = prefix;
    Spannable spannable = new SpannableString(prefix);
    if (prefix.length() > 0) {
      spannable.setSpan(new CustomTypefaceSpan(null, R.id.theme_color_textPlaceholder), 0, prefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    minLength = spannable.length();
    forceText(spannable);
    Views.setSelection(this, minLength);
  }

  /**
   * aka setText
   * @param text text
   */
  public void setSuffix (String text) {
    Spannable spannable = new SpannableString(prefix + text);
    if (prefix.length() > 0) {
      spannable.setSpan(new ForegroundColorSpan(Theme.textPlaceholderColor()), 0, prefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    forceText(spannable);
    Views.setSelection(this, spannable.length());
  }

  public String getSuffix () {
    String str = getText().toString();
    return str.length() <= minLength ? "" : str.substring(minLength);
  }

  public void setEditable (boolean editable) {
    if (this.editable != editable) {
      this.editable = editable;
      setClickable(editable);
      setFocusable(editable);
      setFocusableInTouchMode(editable);
      setOnLongClickListener(editable ? null : this);
    }
  }

  public void forceText (Spannable s) {
    forceEdit = true;
    setText(s, TextView.BufferType.SPANNABLE);
    forceEdit = false;
  }

  @Override
  public boolean onLongClick (View v) {
    UI.copyText(getText().toString(), R.string.CopiedLink);
    return true;
  }

  @Override
  @SuppressWarnings(value = "SpellCheckingInspection")
  public CharSequence filter (CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
    return forceEdit ? null : !editable ? (source.length() < 1 ? dest.subSequence(dstart, dend) : "") : (prefix != null && dstart < minLength ? dest.subSequence(dstart, dend) : null);
  }

  @Override
  protected void onSelectionChanged (int selStart, int selEnd) {
    if (editable && selStart < minLength && getText().length() >= minLength) {
      Views.setSelection(this, minLength);
    } else {
      super.onSelectionChanged(selStart, selEnd);
    }
  }
}
