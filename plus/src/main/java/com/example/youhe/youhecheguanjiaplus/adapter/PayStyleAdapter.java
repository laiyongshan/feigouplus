package com.example.youhe.youhecheguanjiaplus.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.Paytype;

import java.util.HashMap;
import java.util.List;

public class PayStyleAdapter extends BaseAdapter {
    private Context context;
    HashMap<String, Boolean> states = new HashMap<String, Boolean>();//用于记录每个RadioButton的状态，并保证只可选一个
    private ViewHolder holder;
    private LayoutInflater inflater;
    private Activity activity;
    private int temp = -1;
    private int selectID=0;
    private OnMyCheckChangedListener mCheckChange;

    List<Integer> iconId;
    List<String> textStr ;
    List<Paytype> paytypeList;

    public PayStyleAdapter(Context context, Activity activity,List<String> textStr,List<Integer> iconId,List<Paytype> paytypeList) {
        inflater = LayoutInflater.from(context);
        this.activity = activity;
        this.textStr=textStr;
        this.iconId=iconId;
        this.paytypeList=paytypeList;
    }

    @Override
    public int getCount() {
        return iconId.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    // 自定义的选中方法
    public void setSelectID(int position) {
        selectID = position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_pay_style, null);
            holder.pay_icon = (ImageView) convertView.findViewById(R.id.pay_icon);
            holder.pay_style_tv = (TextView) convertView.findViewById(R.id.pay_style_tv);
            holder.rdBtn = (RadioButton) convertView.findViewById(R.id.rdBtn);

            holder.rdBtn.setChecked(false);

            holder.pay_icon.setImageResource(iconId.get(position));
            holder.pay_style_tv.setText(textStr.get(position));

			holder.rdBtn.setId(position);
			holder.rdBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (temp != -1) {
						RadioButton tempButton = (RadioButton) activity.findViewById(temp);
							if (tempButton != null) {
									tempButton.setChecked(false);
                            }
						}
						temp = buttonView.getId();
				     }
					}
				});

				if (position == temp) {
					holder.rdBtn.setChecked(true);
				} else {
					holder.rdBtn.setChecked(false);
				}
//			 	if(position==selectID){
//					holder.rdBtn.setChecked(true);
//				}
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (selectID == position) {
            holder.rdBtn.setChecked(true);
        } else {
            holder.rdBtn.setChecked(false);
        }

        holder.rdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectID = position;

                /**
                 * 在MyListView中使用mListViewAdapter.setOncheckChanged
                 * 来监听RadioButton的点击事件，（当然，首先要判空）
                 * 当我们按下单选按钮时，我们把按下的item的位置赋值给selectID
                 * ，然后在上面的if语句中判断当前点击的位置与selectID的位置
                 * 是否相等，如果不相等，那么说明按下了新的位置，那么就把原来位置上的选择取消掉，
                 * 在新的位置让单选按钮显示选中状态就可以了。
                 */
                Intent intent;
                if (mCheckChange != null) {
                    mCheckChange.setSelectID(selectID);
                    intent = new Intent("com_yeohe_payActivity_paytype_action");
                    intent.putExtra("selectID", selectID);
                    activity.sendBroadcast(intent);
                }

            }
        });

        return convertView;
    }

    // 回调函数
    public void setOncheckChanged(OnMyCheckChangedListener l) {
        mCheckChange = l;
    }

    // 回調接口
    public interface OnMyCheckChangedListener {
        void setSelectID(int selectID);
    }

    private class ViewHolder {
        ImageView pay_icon;
        TextView pay_style_tv;
        RadioButton rdBtn;
    }

}
