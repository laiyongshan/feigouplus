package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.IllegalCode;

import java.util.List;

/**
 * Created by Administrator on 2016/9/7 0007.
 * 违章代码
 */
public class IllegalCodeAdapter extends BaseAdapter {
    public LayoutInflater inflater;
    private ViewHolder holder;
    private List<IllegalCode> codelist;

    public IllegalCodeAdapter(Context context, List<IllegalCode> codelist) {
        inflater = LayoutInflater.from(context);
        this.codelist = codelist;
    }

    @Override
    public int getCount() {
        return codelist.size();
    }

    @Override
    public Object getItem(int i) {
        return codelist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.item_illegalcode, null);
        }

        TextView code_tv = (TextView) view.findViewById(R.id.code_tv);
        code_tv.setText("代码：" + codelist.get(i).getIllegalCode());
        TextView koufen_tv = (TextView) view.findViewById(R.id.koufen_tv);
        koufen_tv.setText("扣分:" + codelist.get(i).getScop());
        TextView fakuan_tv = (TextView) view.findViewById(R.id.fakuan_tv);
        fakuan_tv.setText("罚款:" + codelist.get(i).getFakuan());
        TextView content_tv = (TextView) view.findViewById(R.id.content_tv);
        content_tv.setText(codelist.get(i).getIllegalDetail());

        return view;
    }

    public class ViewHolder {
        TextView code_tv;
        TextView koufen_tv;
        TextView fakuan_tv;
        TextView content_tv;
    }

    /**
     * 添加数据列表项
     */
    public void addNewsItem(IllegalCode codeItem) {
        codelist.add(codeItem);
    }

}
