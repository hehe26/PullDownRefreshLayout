package chenjun.com.simpleexample;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import chenjun.com.simpleexample.adapter.MyAdapter;
import chenjun.com.simpleexample.listener.OnRefreshListener;
import chenjun.com.simpleexample.views.RefreshLayout;
import chenjun.com.simpleexample.views.RefreshView;

public class MainActivity extends AppCompatActivity {
    public static final int START_REFRESH = 0;
    private RefreshLayout m_refreshlayout;
    private RefreshView m_refeshView;
    private List<String> list = new ArrayList<String>();
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case START_REFRESH:
                    m_refreshlayout.stopRefreshing();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_refreshlayout= (RefreshLayout) findViewById(R.id.refresh_layout);
        m_refeshView=m_refreshlayout.getRefreshView();
        initData();
        m_refeshView.setAdapter(new MyAdapter(list, this));
        m_refreshlayout.addOnRefreshLister(new OnRefreshListener() {

            @Override
            public void onRefreshing() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            myHandler.sendEmptyMessage(START_REFRESH);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .start();
            }
        });
    }

    private void initData() {
        for (int i= 0;i<30;i++){
            list.add("这是第"+i+"条数据");
        }
    }
}
