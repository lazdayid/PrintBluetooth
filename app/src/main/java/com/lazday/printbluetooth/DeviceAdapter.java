package com.lazday.printbluetooth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private final String TAG = "DeviceAdapter";

    private ArrayList<String> results;
    private Context context;
    private OnAdapterListener listener;

    public DeviceAdapter(Context context, ArrayList<String> results, OnAdapterListener listener) {
        this.results    = results ;
        this.context    = context ;
        this.listener   = listener ;
    }

    @NonNull
    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_device,
                        parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceAdapter.ViewHolder viewHolder, int i) {
        final String result = results.get(i);
        viewHolder.textDevice.setText( result);
        viewHolder.textDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnClick( result );
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDevice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDevice = itemView.findViewById(R.id.text_device);
        }
    }

    public void setData(ArrayList<String> data){
        results.clear();
        results.addAll(data);
        notifyDataSetChanged();
    }

    public interface OnAdapterListener {
        void OnClick(String device);
    }
}
