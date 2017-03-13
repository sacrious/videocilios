package videos.domicilios.com.videocilios.Adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import videos.domicilios.com.videocilios.R;
import videos.domicilios.com.videocilios.Utils.LatoFontTextView;


/**
 * Created by Sergio on 3/10/17.
 */
@SuppressWarnings("all")
public abstract class ExpandableRecyclerAdapter<T extends ExpandableRecyclerAdapter.ListItem> extends RecyclerView.Adapter<ExpandableRecyclerAdapter.ViewHolder> {
    protected Context mContext;
    protected List<T> allItems = new ArrayList<>();
    protected List<T> visibleItems = new ArrayList<>();
    private List<Integer> indexList = new ArrayList<>();
    private SparseIntArray expandMap = new SparseIntArray();
    private int mode;

    protected static final int TYPE_HEADER = 1000;
    protected static final int TYPE_CONTENT = 1001;

    private static final int ARROW_ROTATION_DURATION = 150;

    public static final int MODE_NORMAL = 0;
    public static final int MODE_ACCORDION = 1;

    public ExpandableRecyclerAdapter(Context context) {
        mContext = context;
    }

    public static class ListItem {
        public int ItemType;

        public ListItem(int itemType) {
            ItemType = itemType;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return visibleItems == null ? 0 : visibleItems.size();
    }

    protected View inflate(int resourceID, ViewGroup viewGroup) {
        return LayoutInflater.from(mContext).inflate(resourceID, viewGroup, false);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    public class HeaderViewHolder extends ViewHolder {
        ImageView arrow;
        View viewContainer;

        public HeaderViewHolder(View view) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleExpandedItems(getLayoutPosition(), false);
                }
            });
        }

        public HeaderViewHolder(View view, final ImageView arrow) {
            super(view);
            this.arrow = arrow;
            this.viewContainer = view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClick();
                }
            });
        }

        protected void handleClick() {
            if (toggleExpandedItems(getLayoutPosition(), false))
                openArrow(arrow, viewContainer);
            else
                closeArrow(arrow, viewContainer);
        }

        public void bind(int position) {
            if (isExpanded(position))
                openArrow(arrow, viewContainer);
            else
                closeArrow(arrow, viewContainer);
        }
    }

    public boolean toggleExpandedItems(int position, boolean notify) {
        if (isExpanded(position)) {
            collapseItems(position, notify);
            return false;
        } else {
            expandItems(position, notify);

            if (mode == MODE_ACCORDION) {
                collapseAllExcept(position);
            }

            return true;
        }
    }

    public void expandItems(int position, boolean notify) {
        int count = 0;
        int index = indexList.get(position);
        int insert = position;

        for (int i = index + 1; i < allItems.size() && allItems.get(i).ItemType != TYPE_HEADER; i++) {
            insert++;
            count++;
            visibleItems.add(insert, allItems.get(i));
            indexList.add(insert, i);
        }

        notifyItemRangeInserted(position + 1, count);

        int allItemsPosition = indexList.get(position);
        expandMap.put(allItemsPosition, 1);

        if (notify) {
            notifyItemChanged(position);
        }
    }

    public void collapseItems(int position, boolean notify) {
        int count = 0;
        int index = indexList.get(position);

        for (int i = index + 1; i < allItems.size() && allItems.get(i).ItemType != TYPE_HEADER; i++) {
            count++;
            visibleItems.remove(position + 1);
            indexList.remove(position + 1);
        }

        notifyItemRangeRemoved(position + 1, count);

        int allItemsPosition = indexList.get(position);
        expandMap.delete(allItemsPosition);

        if (notify) {
            notifyItemChanged(position);
        }
    }


    protected boolean isExpanded(int position) {
        int allItemsPosition = indexList.get(position);
        return expandMap.get(allItemsPosition, -1) >= 0;
    }

    @Override
    public int getItemViewType(int position) {
        return visibleItems.get(position).ItemType;
    }

    public void setItems(List<T> items) {
        allItems = items;
        List<T> visibleItems = new ArrayList<>();
        expandMap.clear();
        indexList.clear();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).ItemType == TYPE_HEADER) {
                indexList.add(i);
                visibleItems.add(items.get(i));
            }
        }

        this.visibleItems = visibleItems;
        notifyDataSetChanged();
    }


    protected void removeItemAt(int visiblePosition) {
        int allItemsPosition = indexList.get(visiblePosition);

        allItems.remove(allItemsPosition);
        visibleItems.remove(visiblePosition);

        incrementIndexList(allItemsPosition, visiblePosition, -1);
        incrementExpandMapAfter(allItemsPosition, -1);

        notifyItemRemoved(visiblePosition);
    }

    private void incrementExpandMapAfter(int position, int direction) {
        SparseIntArray newExpandMap = new SparseIntArray();

        for (int i = 0; i < expandMap.size(); i++) {
            int index = expandMap.keyAt(i);
            newExpandMap.put(index < position ? index : index + direction, 1);
        }

        expandMap = newExpandMap;
    }

    private void incrementIndexList(int allItemsPosition, int visiblePosition, int direction) {
        List<Integer> newIndexList = new ArrayList<>();

        for (int i = 0; i < indexList.size(); i++) {
            if (i == visiblePosition) {
                if (direction > 0) {
                    newIndexList.add(allItemsPosition);
                }
            }

            int val = indexList.get(i);
            newIndexList.add(val < allItemsPosition ? val : val + direction);
        }

        indexList = newIndexList;
    }

    public void collapseAll() {
        collapseAllExcept(-1);
    }

    public void collapseAllExcept(int position) {
        for (int i = visibleItems.size() - 1; i >= 0; i--) {
            if (i != position && getItemViewType(i) == TYPE_HEADER) {
                if (isExpanded(i)) {
                    collapseItems(i, true);
                }
            }
        }
    }

    public void expandAll() {
        for (int i = visibleItems.size() - 1; i >= 0; i--) {
            if (getItemViewType(i) == TYPE_HEADER) {
                if (!isExpanded(i)) {
                    expandItems(i, true);
                }
            }
        }
    }

    public static void openArrow(View view, View viewContainer) {
        view.animate().setDuration(ARROW_ROTATION_DURATION).rotation(180);
        viewContainer.setBackgroundColor(ContextCompat.getColor(viewContainer.getContext(), R.color.selected_background));
        changeTextColor(Color.WHITE, viewContainer);
        ((ImageView) view).setColorFilter(Color.WHITE);

    }

    public static void closeArrow(View view, View viewContainer) {
        view.animate().setDuration(ARROW_ROTATION_DURATION).rotation(0);
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = viewContainer.getContext().obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        viewContainer.setBackgroundResource(backgroundResource);
        typedArray.recycle();
        changeTextColor(Color.BLACK, viewContainer);
        ((ImageView) view).setColorFilter(Color.BLACK);
    }

    private static void changeTextColor(int color, View viewContainer) {
        int viewSize = ((ViewGroup) viewContainer).getChildCount();
        for (int i = 0; i < viewSize; i++) {
            View view = ((ViewGroup) viewContainer).getChildAt(i);
            if (view instanceof LatoFontTextView) {
                LatoFontTextView textView = (LatoFontTextView) view;
                textView.setTextColor(color);
            }
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
