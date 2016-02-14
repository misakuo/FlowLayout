package com.moxun.flowlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.moxun.flowlayoutlib.FlowItemAdapter;
import com.moxun.flowlayoutlib.FlowLayout;

public class MainActivity extends AppCompatActivity {

    private FlowLayout flowLayout;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flowLayout = (FlowLayout) findViewById(R.id.flowlayout);
        adapter = new MyAdapter();
        flowLayout.setAdapter(adapter);
        flowLayout.setOnItemClickListener(new FlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Item" + position + " clicked.", Toast.LENGTH_SHORT).show();
            }
        });
        flowLayout.setItemInsertAnimation(FlowLayout.DEFAULT_INSERT_ANIMATION);
        flowLayout.setItemRemoveAnimation(FlowLayout.DEFAULT_REMOVE_ANIMATION);

        findViewById(R.id.append).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.append();
            }
        });

        findViewById(R.id.insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.randomInsert();
            }
        });

        findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.randomRemove();
            }
        });

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert or remove item without animation
                adapter.reset();
            }
        });
    }
}
