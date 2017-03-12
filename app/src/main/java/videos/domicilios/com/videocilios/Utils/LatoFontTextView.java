package videos.domicilios.com.videocilios.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import videos.domicilios.com.videocilios.R;

/**
 * Created by proximate on 3/11/17.
 */

public class LatoFontTextView extends TextView {

    String customFont;

    public LatoFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        style(context, attrs);
    }

    public LatoFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        style(context, attrs);

    }

    private void style(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.LatoFontTextView);
        int cf = a.getInteger(R.styleable.LatoFontTextView_fontName, 0);
        int fontName;
        switch (cf) {
            case 1:
                fontName = R.string.Lato_Black;
                break;
            case 2:
                fontName = R.string.Lato_BlackItalic;
                break;
            case 3:
                fontName = R.string.Lato_Bold;
                break;
            case 4:
                fontName = R.string.Lato_BoldItalic;
                break;
            case 5:
                fontName = R.string.Lato_Hairline;
                break;
            case 6:
                fontName = R.string.Lato_HairlineItalic;
                break;
            case 7:
                fontName = R.string.Lato_Italic;
                break;
            case 8:
                fontName = R.string.Lato_Light;
                break;
            case 9:
                fontName = R.string.Lato_LightItalic;
                break;
            case 10:
                fontName = R.string.Lato_Regular;
                break;
            default:
                fontName = R.string.Lato_Light;
                break;
        }

        customFont = getResources().getString(fontName);

        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/" + customFont + ".ttf");
        setTypeface(tf);
        a.recycle();
    }

}
