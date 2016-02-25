package com.zgs.api.appwidget.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.zgs.api.R;

/**
 * Created by simon on 15-12-2.
 */
public class MYGridView extends MYListView {
    public MYGridView(Context context) {
        this(context, null, 0);
    }

    public MYGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MYGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        int columns = 1, verticalSpace = 0, horizontalSpace = 0;

        if (attrs != null) {
            TypedArray types = getContext().obtainStyledAttributes(attrs, R.styleable.MYGridView);

            for (int n = 0; n < types.getIndexCount(); n++) {
                int attr = types.getIndex(n);

                if (attr == R.styleable.MYGridView_columns) {
                    //默认2行
                    columns = types.getInteger(attr, 2);
                } else if (attr == R.styleable.MYGridView_verticalSpace) {
                    //默认1像素垂直分割线
                    verticalSpace = (int) types.getDimension(attr, 1);
                } else if (attr == R.styleable.MYGridView_horizontalSpace) {
                    //默认1像素水平分割线
                    horizontalSpace = (int) types.getDimension(attr, 1);
                }
            }

            types.recycle();
        }

        return new MYGridManager(this, columns, verticalSpace, horizontalSpace);
    }

    class MYGridManager extends GridLayoutManager {
        private MYGridView listView;
        /**
         * columns of gridview
         */
        private int columns = 2;
        /**
         * vertical space of gridview
         */
        private int verticalSpace = 1;
        /**
         * horizontal space of gridview
         */
        private int horizontalSpace = 1;

        public MYGridManager(MYGridView list, int columns, int verticalSpace, int horizontalSpace) {
            super(list.getContext(), columns);

            this.listView = list;
            this.columns = columns;
            this.verticalSpace = verticalSpace;
            this.horizontalSpace = horizontalSpace;

            setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (adapter != null) {
                        if (adapter.getItemViewType(position) != MYViewHolderTypes.TYPE_CONTENT) {
                            return MYGridManager.this.columns;
                        }

                        if (adapter.getContentSpanSize(position) != 0) {
                            return adapter.getContentSpanSize(position);
                        }
                    }

                    return 1;
                }
            });

            listView.addItemDecoration(new DividerItemDecoration());
        }

        class DividerItemDecoration extends RecyclerView.ItemDecoration {

            @Override
            public void getItemOffsets(Rect outRect, View view,
                                       RecyclerView parent, RecyclerView.State state) {

                int index = parent.getChildLayoutPosition(view);
                int headerCount = adapter != null ? adapter.getHeaderCount() : 0;
                int contentCount = adapter != null ? adapter.getContentCount() : 0;
                int lines = adapter != null ? adapter.getContentCount() / columns + (adapter.getContentCount() % columns == 0 ? 0 : 1) : 0;

                if (index >= headerCount && index < headerCount + contentCount) {
                    outRect.left = horizontalSpace >> 1;
                    outRect.right = horizontalSpace >> 1;
                    outRect.top = verticalSpace >> 1;
                    outRect.bottom = verticalSpace >> 1;

                    if (headerCount == 0 || columns > 2) {
                        return;
                    }

                    index -= headerCount;

                    //最上方
                    if (index < columns) {
                        outRect.top = verticalSpace;
                    }

                    //最右边
                    if ((index + 1) % columns == 0) {
                        outRect.right = horizontalSpace;
                    }

                    //最下边
                    if ((index + 1) / columns + ((index + 1) % columns == 0 ? 0 : 1) == lines) {
                        outRect.bottom = verticalSpace;
                    }

                    //最左边
                    if (index % columns == 0) {
                        outRect.left = horizontalSpace;
                    }
                }
            }
        }
    }
}
