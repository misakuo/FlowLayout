package com.moxun.flowlayoutlib;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by moxun on 16/2/14.
 */
public abstract class FlowItemAdapter {
    private FlowLayoutDataSetListener dataSetListener;

    protected interface FlowLayoutDataSetListener {
        void onItemInserted(int position);

        void onItemRemoved(int position);

        void onItemChanged(int position);

        void onDataSetChanged();
    }

    protected void setDataSetListener(FlowLayoutDataSetListener listener) {
        dataSetListener = listener;
    }

    public final void notifyItemInserted(int position) {
        dataSetListener.onItemInserted(position);
    }

    public final void notifyItemRemoved(int position) {
        dataSetListener.onItemRemoved(position);
    }

    public final void notifyItemChanged(int position) {
        dataSetListener.onItemChanged(position);
    }

    public final void notifyDataSetChanged() {
        dataSetListener.onDataSetChanged();
    }

    public abstract int getItemCount();
    public abstract Object getItem(int position);
    public abstract View getView(Context context, ViewGroup parent, int position);
}
