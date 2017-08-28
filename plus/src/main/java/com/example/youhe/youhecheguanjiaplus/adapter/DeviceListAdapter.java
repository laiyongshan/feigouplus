package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dynamicode.p27.companyyh.bluetooth4.DcBleDevice;
import com.example.youhe.youhecheguanjiaplus.R;
import java.util.List;

public class DeviceListAdapter extends BaseAdapter{

	private List<DcBleDevice> list;
	private Context mContext;
	private ViewHolder holder;
	
	public DeviceListAdapter(List<DcBleDevice> list, Context mContext) {
		this.list = list;
		this.mContext = mContext;
	}
	
	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null || convertView.getTag() == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.devicemanager_list_adapter, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.devicemanager_list_adapter_name);
//			holder.address = (TextView) convertView.findViewById(R.id.devicemanager_list_adapter_address);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(list.get(position).getDeviceName());
		return convertView;
	}

	class ViewHolder{
		private TextView name;
	}
}
