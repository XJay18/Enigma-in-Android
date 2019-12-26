package com.android.xjay.enigma;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * A helper class for jumping between input frames.
 *
 * @author Junyi Cao
 */
public class JumpTextWatcher implements TextWatcher {

    private EditText mThisView;
    private View mNextView = null;
    private Context mContext;

    JumpTextWatcher(Context c, EditText vThis, View vNext) {
        super();
        mContext = c;
        mThisView = vThis;
        if (vNext != null) {
            mNextView = vNext;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
        if (str.contains("\r") || str.contains("\n")) {
            mThisView.setText(str.replace(
                    "\r", "").replace("\n", ""));
            if (mNextView != null) {
                mNextView.requestFocus();
                if (mNextView instanceof EditText) {
                    EditText et = (EditText) mNextView;
                    et.setSelection(et.getText().length());
                } else {
                    InputMethodManager imm = (InputMethodManager)
                            mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mThisView.getWindowToken(), 0);
                }
            }
        }
    }
}

