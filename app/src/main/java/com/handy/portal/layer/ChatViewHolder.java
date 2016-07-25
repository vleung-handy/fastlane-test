package com.handy.portal.layer;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    public TextView mTextMessage;
    public TextView mTextStatus;
    public CardView mMainContainer;

    private int mDefaultMargin;

    public ChatViewHolder(View itemView, int defaultMargin) {
        super(itemView);
        mTextMessage = (TextView)itemView.findViewById(R.id.text_message);
        mTextStatus = (TextView)itemView.findViewById(R.id.text_status);
        mMainContainer = (CardView) itemView.findViewById(R.id.main_container);
        mDefaultMargin = defaultMargin;
    }

    public void updateState(ChatItem item){
        mMainContainer.setCardBackgroundColor(item.getBgColor());
        mTextMessage.setTextColor(item.getTextColor());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mTextMessage.setText(item.getMessage().getMessage());
        mTextStatus.setText(item.getMessage().getStatus());
        params.gravity = item.getGravity();
        if (item.getGravity() == Gravity.RIGHT) {
            params.setMargins(mDefaultMargin *4, mDefaultMargin/2, mDefaultMargin, mDefaultMargin/2);
        } else {
            params.setMargins(mDefaultMargin , mDefaultMargin/2, mDefaultMargin *4 , mDefaultMargin/2);
        }
        mMainContainer.setLayoutParams(params);
    }
}
