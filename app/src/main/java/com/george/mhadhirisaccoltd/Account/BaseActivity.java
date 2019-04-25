package com.george.mhadhirisaccoltd.Account;

import android.content.Context;
import android.view.View;

import com.hanks.htextview.base.AnimationListener;
import com.hanks.htextview.base.HTextView;

/**
 * Created by George on 3/12/2019.
 */

public class BaseActivity {

    String[] sentences = {"Password link will be sent to your provided email,",
            "please make sure the email is active and valid"};
    int index;

    public class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v instanceof HTextView) {
                if (index + 1 >= sentences.length) {
                    index = 0;
                }
                ((HTextView) v).animateText(sentences[index++]);
            }
        }
    }

    static class SimpleAnimationListener implements AnimationListener {

        private Context context;

        public SimpleAnimationListener(Context context) {
            this.context = context;
        }
        @Override
        public void onAnimationEnd(HTextView hTextView) {
        }
    }
}
