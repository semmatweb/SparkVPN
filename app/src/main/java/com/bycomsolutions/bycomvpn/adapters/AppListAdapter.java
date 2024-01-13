package com.bycomsolutions.bycomvpn.adapters;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bycomsolutions.bycomvpn.R;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.List;
import java.util.Map;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private final List<ResolveInfo> resolveInfoList;

    private final Map<String, String> excludedAppMap;

    private final PackageManager packageManager;

    private final Activity activity;


    public AppListAdapter(List<ResolveInfo> resolveInfoList,Map<String, String> ExcludedAppList,PackageManager packageManager,Activity activity) {
        this.resolveInfoList = resolveInfoList;
        this.excludedAppMap = ExcludedAppList;
        this.packageManager = packageManager;
        this.activity = activity;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.applist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResolveInfo resolveInfo = resolveInfoList.get(position);

        if(resolveInfo.activityInfo.packageName.equals(activity.getPackageName())) return;


        new Thread(() -> {
            Drawable drawable = resolveInfo.loadIcon(packageManager);
            activity.runOnUiThread(() -> holder.appIcon.setImageDrawable(drawable));
        }).start();


        holder.appTitle.setText(resolveInfo.loadLabel(packageManager));
        holder.appPackageName.setText(resolveInfo.activityInfo.packageName);

        if(position == 0)
            holder.tv_description.setVisibility(View.VISIBLE);
        else holder.tv_description.setVisibility(View.GONE);

        holder.materialSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
                excludedAppMap.put(resolveInfoList.get(holder.getAbsoluteAdapterPosition()).activityInfo.packageName,"");
            else excludedAppMap.remove(resolveInfoList.get(holder.getAbsoluteAdapterPosition()).activityInfo.packageName);
        });

        holder.materialSwitch.setChecked(excludedAppMap.containsKey(resolveInfo.activityInfo.packageName));

    }

    public Map<String, String> getExcludedAppMap(){
        return excludedAppMap;
    }

    @Override
    public int getItemCount() {
        return resolveInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appTitle,appPackageName,tv_description;

        MaterialSwitch materialSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appTitle = itemView.findViewById(R.id.app_title);
            appPackageName = itemView.findViewById(R.id.app_package_name);
            tv_description = itemView.findViewById(R.id.tv_description);
            materialSwitch = itemView.findViewById(R.id.mSwitch);

        }
    }
}
