package videos.domicilios.com.videocilios.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by proximate on 3/11/17.
 */

public interface IRowSelected {
    void onRowClick(RecyclerView.ViewHolder viewHolder, Object object);

    void onRowLongClick(RecyclerView.ViewHolder viewHolder, Object object);
}
