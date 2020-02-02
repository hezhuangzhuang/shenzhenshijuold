package com.zxwl.szga.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.szga.R;
import com.zxwl.szga.bean.SiteStatus;

import java.util.List;

public class MeetingControlListAdapter extends BaseQuickAdapter<SiteStatus, BaseViewHolder> {

    public MeetingControlListAdapter(int layoutResId, @Nullable List<SiteStatus> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SiteStatus item) {
        helper.setText(R.id.name, item.getSiteName());

        if (SiteStatus.STATE_ONLINE.equals(item.getSiteStatus())) {
            helper.setImageResource(R.id.call, R.drawable.ic_hangup);
            if (SiteStatus.STATE_MUTE.equals(item.getMicrophoneStatus())) {
                helper.setImageResource(R.id.mute, R.drawable.ic_cancelmute);
            } else {
                helper.setImageResource(R.id.mute, R.drawable.ic_mute);
            }
        } else {
            helper.setImageResource(R.id.call, R.drawable.ic_call);
            helper.setImageResource(R.id.mute, R.drawable.ic_mute_disable);
        }

        helper.addOnClickListener(R.id.call);
        helper.addOnClickListener(R.id.mute);
        helper.addOnClickListener(R.id.delete);

    }
}
