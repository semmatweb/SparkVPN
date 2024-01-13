package com.bycomsolutions.bycomvpn.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bycomsolutions.bycomvpn.BuildConfig;
import com.bycomsolutions.bycomvpn.Preference;
import com.bycomsolutions.bycomvpn.R;
import com.bycomsolutions.bycomvpn.dialog.CountryData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.bycomsolutions.bycomvpn.utils.BillConfig.PRIMIUM_STATE;

import unified.vpn.sdk.Country;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {

    public Context context;
    private Preference preference;
    private List<CountryData> regions;
    private RegionListAdapterInterface listAdapterInterface;

    public LocationListAdapter(RegionListAdapterInterface listAdapterInterface, Activity cntec) {
        this.listAdapterInterface = listAdapterInterface;
        this.context = cntec;
        preference = new Preference(this.context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_list_free, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final CountryData datanew = this.regions.get(holder.getAdapterPosition());
        final Country data = datanew.getCountryvalue();
        Locale locale = new Locale("", data.getCountry());

        if (position == 0) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/earthspeed", null, context.getPackageName()));
            holder.app_name.setText(R.string.best_performance_server);
            holder.limit.setVisibility(View.GONE);
            holder.ll_server_type.setVisibility(View.GONE);
        } else {

            ImageView imageView = holder.flag;
            Resources resources = context.getResources();
            String sb = "drawable/" + data.getCountry().toLowerCase();
            imageView.setImageResource(resources.getIdentifier(sb, null, context.getPackageName()));
            holder.app_name.setText(locale.getDisplayCountry());
            holder.limit.setVisibility(View.VISIBLE);
            holder.ll_server_type.setVisibility(View.VISIBLE);

        }

        if (datanew.isPro()) {
            holder.pro.setVisibility(View.VISIBLE);
            holder.tv_server_type.setText(R.string.pro_server);
            holder.tv_latency.setText((new Random().nextInt(28) + 21)+"ms");
        } else {
            holder.pro.setVisibility(View.GONE);
            holder.tv_server_type.setText(R.string.free_server);
            holder.tv_latency.setText((new Random().nextInt(98) + 91)+"ms");
        }
        holder.itemView.setOnClickListener(view -> listAdapterInterface.onCountrySelected(regions.get(holder.getAbsoluteAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return regions != null ? regions.size() : 0;
    }

    public void setRegions(List<Country> list) {
        regions = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CountryData newData = new CountryData();
            newData.setCountryvalue(list.get(i));

            if(!BuildConfig.USE_IN_APP_PURCHASE) newData.setPro(false);
            else if (preference.isBooleenPreference(PRIMIUM_STATE)) newData.setPro(true);
            else newData.setPro(i >= 6);

            regions.add(newData);

           /* if (i < 6) {
                newData.setPro(false);
                regions.add(newData);
            } else {
                if (list.get(i).getServers() > 0) {
                    if (BuildConfig.USE_IN_APP_PURCHASE) {
                        if (preference.isBooleenPreference(PRIMIUM_STATE)) {
                            newData.setPro(false);
                        } else {
                            newData.setPro(true);
                        }
                    } else {
                        newData.setPro(false);
                    }
                    regions.add(newData);
                } else {
                    newData.setPro(false);
                    regions.add(newData);
                }
            }*/
        }



        notifyDataSetChanged();
    }

    public interface RegionListAdapterInterface {
        void onCountrySelected(CountryData item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView app_name,tv_server_type,tv_latency;
        ImageView flag, pro;
        ImageView limit;

        LinearLayout ll_server_type;

        ViewHolder(View v) {
            super(v);
            this.app_name = itemView.findViewById(R.id.region_title);
            this.limit = itemView.findViewById(R.id.region_limit);
            this.flag = itemView.findViewById(R.id.country_flag);
            this.pro = itemView.findViewById(R.id.pro);
            this.tv_server_type = itemView.findViewById(R.id.tv_server_type);
            this.tv_latency = itemView.findViewById(R.id.tv_latency);
            this.ll_server_type = itemView.findViewById(R.id.ll_server_type);
        }
    }
}
