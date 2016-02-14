package com.moxun.flowlayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moxun.flowlayoutlib.FlowItemAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by moxun on 16/2/14.
 */
public class MyAdapter extends FlowItemAdapter {
    private List<String> dataSet = new ArrayList<>();

    public MyAdapter() {
        dataSet.clear();
        for (int i = 0; i < 10; i++) {
            dataSet.add("/" + new Random().nextInt());
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public View getView(Context context, ViewGroup parent, int position) {
        TextView view = (TextView) LayoutInflater.from(context).inflate(R.layout.item_flowlayout, parent, false);
        view.setText("Item" + position + getItem(position));
        return view;
    }

    public void randomRemove() {
        int index = Math.abs(new Random().nextInt()) % dataSet.size();
        dataSet.remove(index);
        notifyItemRemoved(index);
    }

    public void randomInsert() {
        int index = Math.abs(new Random().nextInt()) % dataSet.size();
        dataSet.add(index,"/" + new Random().nextInt());
        notifyItemInserted(index);
    }

    public void append() {
        dataSet.add("/" + new Random().nextInt());
        notifyItemInserted(dataSet.size() - 1);
    }

    public void reset() {
        int random = new Random().nextInt();
        int index =  Math.abs(new Random().nextInt()) % dataSet.size();
        if (random > 0) {
            dataSet.add(index, "/" + random);
        } else {
            dataSet.remove(index);
        }
        notifyDataSetChanged();
    }
}
