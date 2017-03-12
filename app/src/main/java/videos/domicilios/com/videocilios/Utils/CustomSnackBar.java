package videos.domicilios.com.videocilios.Utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import videos.domicilios.com.videocilios.R;

/**
 * Created by proximate on 3/10/17.
 */

public class CustomSnackBar {
    public static void show(String mMessage, int mIcon, int backGroundColor, View mParentLayout, Context context, final ISnackBarHidding listener) {
        SpannableStringBuilder mSnackBarText = new SpannableStringBuilder();
        mSnackBarText.append("Videocilios\n");
        int titleStart = mSnackBarText.length();
        mSnackBarText.setSpan(new RelativeSizeSpan(1.8f), 0, titleStart, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSnackBarText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, titleStart, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSnackBarText.append(mMessage);
        final Snackbar mSnackbar = Snackbar.make(mParentLayout, mSnackBarText, Snackbar.LENGTH_LONG);
        View mSnackbarView = mSnackbar.getView();
        TextView textView = (TextView) mSnackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(mIcon, 0, 0, 0);
        textView.setCompoundDrawablePadding(context.getResources().getDimensionPixelOffset(R.dimen.snackbar_icon_padding));
        mSnackbarView.setBackgroundColor(backGroundColor);
        FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) mSnackbarView.getLayoutParams();
        mLayoutParams.gravity = Gravity.TOP;
        mSnackbarView.setLayoutParams(mLayoutParams);
        mSnackbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSnackbar.dismiss();
            }
        });
        mSnackbarView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {

            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                if (listener != null)
                    listener.snackBarHiding();
            }
        });
        mSnackbar.show();
    }
}
