package com.zxwl.szga.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.szga.R;
import com.zxwl.szga.util.DateUtils;
import com.zxwl.szga.bean.MeetingInfo;

import java.util.Date;
import java.util.List;

/**
 * 会议列表适配器
 */
public class MeetingListAdapter extends BaseQuickAdapter<MeetingInfo, BaseViewHolder> {

    public MeetingListAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MeetingInfo item) {

        helper.setText(R.id.meeting_title, item.getConfName());

        Date begintime = DateUtils.stringToDate(item.getBeginTime(), DateUtils.DATE_TO_STRING_DETAIAL_PATTERN);
        Date endtime = DateUtils.stringToDate(item.getEndTime(), DateUtils.DATE_TO_STRING_DETAIAL_PATTERN);

        String dur = DateUtils.timeDur(begintime, endtime);
        if ("Not Single Day".equals(dur)) {
            helper.setText(R.id.meeting_time, "起:" + item.getBeginTime() + "\n" + "止:" + item.getEndTime());
        } else {
            helper.setText(R.id.meeting_time, dur);
        }
        helper.setText(R.id.meeting_create, item.getCreatorName());
        if (item.getConfStatus().equals(MeetingInfo.STATE_MEETING)) {//召开中的会议
            helper.setImageResource(R.id.ic_meeting_item, R.drawable.ic_meetrunning);
            helper.setVisible(R.id.meeting_join, true);
//            helper.addOnClickListener(R.id.meeting_join);
            helper.addOnClickListener(R.id.rl_content);
        } else if (item.getConfStatus().equals(MeetingInfo.STATE_ORDER)) {//预约会议
            helper.setImageResource(R.id.ic_meeting_item, R.drawable.ic_meetorder);
            helper.setGone(R.id.meeting_join, false);
            helper.addOnClickListener(R.id.rl_content);
        }
    }
}
