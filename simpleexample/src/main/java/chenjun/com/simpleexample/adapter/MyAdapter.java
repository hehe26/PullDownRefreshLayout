package chenjun.com.simpleexample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import chenjun.com.simpleexample.R;

/**
 * Created by chenjun on 16-1-15.
 */
public class MyAdapter extends BaseAdapter {
    private List<String> m_list;
    private Context m_context;
     public MyAdapter(List<String> list,Context context){
         m_list = list;
         m_context = context;
     }
    @Override
    public int getCount() {
        return m_list.size();
    }

    @Override
    public Object getItem(int i) {
        return m_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View m_view = LayoutInflater.from(m_context).inflate(R.layout.item_layout, null);
        TextView tv = (TextView) m_view.findViewById(R.id.tv);
        tv.setText(m_list.get(i));
        return m_view;
    }
}
