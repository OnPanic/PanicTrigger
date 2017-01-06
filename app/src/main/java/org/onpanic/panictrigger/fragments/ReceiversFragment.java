package org.onpanic.panictrigger.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.onpanic.panictrigger.R;
import org.onpanic.panictrigger.interfaces.RequestConnection;
import org.onpanic.panictrigger.ui.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import info.guardianproject.panic.PanicTrigger;


public class ReceiversFragment extends Fragment {

    private String responders[];
    private Set<String> enabledResponders;
    private Set<String> respondersThatCanConnect;
    private Set<String> connectedResponders;
    private ArrayList<CharSequence> appLabelList;
    private ArrayList<Drawable> iconList;
    private Context mContext;
    private RequestConnection requestConnection;

    public ReceiversFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_receivers, container, false);

        enabledResponders = PanicTrigger.getEnabledResponders(mContext);
        respondersThatCanConnect = PanicTrigger.getRespondersThatCanConnect(mContext);
        connectedResponders = PanicTrigger.getConnectedResponders(mContext);

        // sort enabled first, then disabled
        LinkedHashSet<String> a = new LinkedHashSet<String>(enabledResponders);
        LinkedHashSet<String> b = new LinkedHashSet<String>(PanicTrigger.getAllResponders(mContext));
        b.removeAll(enabledResponders);
        a.addAll(b);
        responders = a.toArray(new String[a.size()]);

        appLabelList = new ArrayList<CharSequence>(responders.length);
        iconList = new ArrayList<Drawable>(responders.length);

        PackageManager pm = mContext.getPackageManager();

        for (String packageName : responders) {
            try {
                appLabelList.add(pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)));
                iconList.add(pm.getApplicationIcon(packageName));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(mContext));
        recyclerView.setHasFixedSize(true); // does not change, except in onResume()
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(new RecyclerView.Adapter<AppRowHolder>() {
            @Override
            public AppRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return (new AppRowHolder(inflater.inflate(R.layout.row, parent, false)));
            }

            @Override
            public void onBindViewHolder(AppRowHolder holder, int position) {
                String packageName = responders[position];
                boolean mustConnect = respondersThatCanConnect.contains(packageName);
                holder.setupForApp(
                        packageName,
                        iconList.get(position),
                        appLabelList.get(position),
                        mustConnect);
            }

            @Override
            public int getItemCount() {
                return appLabelList.size();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (mContext instanceof RequestConnection) {
            requestConnection = (RequestConnection) mContext;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RequestConnection");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        requestConnection = null;
    }

    class AppRowHolder extends RecyclerView.ViewHolder {

        private final SwitchCompat onSwitch;
        private final TextView editableLabel;
        private final ImageView iconView;
        private final TextView appLabelView;
        private String rowPackageName;

        private boolean needsConnection;

        AppRowHolder(final View row) {
            super(row);

            iconView = (ImageView) row.findViewById(R.id.iconView);
            appLabelView = (TextView) row.findViewById(R.id.appLabel);
            editableLabel = (TextView) row.findViewById(R.id.editableLabel);
            onSwitch = (SwitchCompat) row.findViewById(R.id.on_switch);

            onSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean enabled) {
                    setEnabled(enabled);
                    if (enabled) {
                        if (needsConnection) {
                            requestConnection.connectToApp(rowPackageName, true);
                        } else {
                            PanicTrigger.enableResponder(mContext, rowPackageName);
                        }
                    } else {
                        if (connectedResponders.contains(rowPackageName)) {
                            requestConnection.connectToApp(rowPackageName, false);
                        } else {
                            PanicTrigger.disableResponder(mContext, rowPackageName);
                        }
                    }
                }
            });
        }

        void setEnabled(boolean enabled) {
            if (enabled) {
                appLabelView.setEnabled(true);
                iconView.setEnabled(true);
                iconView.setColorFilter(null);
            } else {
                appLabelView.setEnabled(false);
                iconView.setEnabled(false);
                // grey out app icon when disabled
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                iconView.setColorFilter(filter);
            }
        }

        void setupForApp(String packageName, Drawable icon, CharSequence appLabel, boolean mustConnect) {
            boolean enabled;

            needsConnection = mustConnect && !connectedResponders.contains(packageName);

            this.rowPackageName = packageName;
            iconView.setImageDrawable(icon);
            appLabelView.setText(appLabel);

            if (mustConnect) {

                enabled = !needsConnection &&
                        enabledResponders.contains(packageName);

                if (enabled) {
                    editableLabel.setText(R.string.click_for_setup);
                } else {
                    editableLabel.setText(R.string.explicit_connection);
                }

            } else {
                editableLabel.setText(R.string.app_hides);
                editableLabel.setTypeface(null, Typeface.NORMAL);

                enabled = enabledResponders.contains(packageName);

                if (Build.VERSION.SDK_INT >= 14)
                    editableLabel.setAllCaps(false);
            }

            onSwitch.setChecked(enabled);
            setEnabled(enabled);
        }
    }
}
