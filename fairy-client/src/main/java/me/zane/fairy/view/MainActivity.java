package me.zane.fairy.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.zane.fairy.MySharedPre;
import me.zane.fairy.R;


public class MainActivity extends AppCompatActivity {
    private static final String NULL_VALUE = "command_null";
    private static final int NULL_POSITION = -1;//不用刷新item
    private RecyclerView recycleView;
    private LinearLayout root;
    private MyAdapter adapter;

    private int currentPosition = NULL_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        adapter.setOnClickListener(position -> {
            currentPosition = position;
            Intent intent = new Intent(MainActivity.this, LogcatActivity.class);
            intent.putExtra(LogcatActivity.INDEX_KEY, position);
            startActivity(intent);
        });

        MySharedPre.getInstance().putOptions(99, null);
        String value = MySharedPre.getInstance().getOptions(99, "nullllll");
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentPosition != NULL_POSITION) {
            ItemBean bean = adapter.get(currentPosition);
            bean.setCommand(String.format("logcat %s %s", MySharedPre.getInstance().getOptions(currentPosition, ""),
                    MySharedPre.getInstance().getFilter(currentPosition, "")));
            adapter.notifyItemChanged(currentPosition);
            currentPosition = NULL_POSITION;
        }
    }

    private void initView() {
        recycleView = findViewById(R.id.recycle_main);
        root = findViewById(R.id.root);
        adapter = new MyAdapter(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MyItemTouchCallback(this,
                                                                                             adapter,
                                                                                             ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                                                                                             ItemTouchHelper.LEFT | ItemTouchHelper.END));


        List<ItemBean> beans = new ArrayList<>();
        int i = 0;
        while (true) {
            String options = MySharedPre.getInstance().getOptions(i, NULL_VALUE);
            String filter = MySharedPre.getInstance().getFilter(i, NULL_VALUE);
            if (options.equals(NULL_VALUE) || filter.equals(NULL_VALUE)) {
                break;
            }
            ItemBean bean = new ItemBean();
            bean.setCommand(String.format("logcat %s %s", options, filter));
            beans.add(bean);
            i++;
        }

        adapter.addAll(beans);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycleView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(recycleView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_main_bar:
                ItemBean itemBean = new ItemBean();
                itemBean.setCommand("logcat");
                int index = adapter.getItemCount();
                adapter.add(itemBean);
                adapter.notifyItemInserted(index);

                MySharedPre.getInstance().putFilter(index, "");
                MySharedPre.getInstance().putOptions(index, "");
                Toast.makeText(this, adapter.getItemCount() + "", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}