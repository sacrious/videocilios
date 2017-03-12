package videos.domicilios.com.videocilios.Utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by proximate on 3/11/17.
 */

public interface IRowSelected {
    void onRowSelected(Object object, ImageView moviePicture, TextView movieTitle, TextView ratingNumber, TextView dateRelease);
}
