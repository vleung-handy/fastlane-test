package com.handy.portal.clients.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.clients.model.Client;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sng on 7/18/17.
 */

public class ClientListRecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_CLIENT = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    // flag for footer ProgressBar (i.e. last item of list)
    private boolean mIsLoadingAdded = false;

    private Context mContext;
    private List<Client> mClientList;
    private OnItemClickListener mOnItemClickListener;

    public ClientListRecyclerViewAdapter(@NonNull Context context, @NonNull List<Client> clientList) {
        mContext = context;
        mClientList = clientList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        switch (viewType) {
            case VIEW_TYPE_CLIENT:
                return new ClientItemViewHolder(LayoutInflater
                        .from(mContext)
                        .inflate(R.layout.layout_client_list_item, parent, false));
            case VIEW_TYPE_LOADING:
                return new LoadingViewHolder(LayoutInflater
                        .from(mContext)
                        .inflate(R.layout.layout_progress_spinner, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ClientItemViewHolder) {
            ((ClientItemViewHolder) holder).bind(mClientList.get(position));
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return (position == mClientList.size() && mIsLoadingAdded)
                ? VIEW_TYPE_LOADING : VIEW_TYPE_CLIENT;
    }

    @Override
    public int getItemCount() {
        return mClientList == null ? 0 : mClientList.size() + getFooterItemCount();
    }

    private int getFooterItemCount() {
        return  mIsLoadingAdded ? 1 : 0;
    }

    @Nullable
    public String getLastClientId() {
        if (mClientList == null || mClientList.size() == 0) { return null; }

        return String.valueOf(mClientList.get(mClientList.size() - 1).getId());
    }

    //Helper methods
    public void add(Client client) {
        mClientList.add(client);
        notifyItemInserted(mClientList.size() - 1);
    }

    public void addAll(List<Client> clientList) {
        for (Client client : clientList) {
            add(client);
        }
    }

    public void remove(Client client) {
        int position = mClientList.indexOf(client);
        if (position > -1) {
            mClientList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mIsLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        mIsLoadingAdded = true;
        //add footer is automatic in the view card holder
        notifyItemInserted(mClientList.size());
    }

    public void removeLoadingFooter() {
        mIsLoadingAdded = false;

        int position = mClientList.size();
        notifyItemRemoved(mClientList.size());
    }

    public Client getItem(int position) {
        if (position < 0) { return null; }

        return mClientList.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(final View itemView) {
            super(itemView);
        }
    }


    public class ClientItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.client_list_item_layout)
        View mListItemLayout;
        @BindView(R.id.client_list_item_img_view)
        ImageView mImageView;
        @BindView(R.id.client_list_item_initials_layout)
        ViewGroup mInitialsLayout;
        @BindView(R.id.client_list_item_initials)
        TextView mInitialsTextView;
        @BindView(R.id.client_list_item_name)
        TextView mNameTextView;
        @BindView(R.id.client_list_item_city)
        TextView mCityTextView;
        @BindView(R.id.client_list_item_green_dot)
        ImageView mGreenDotImageView;
        @BindView(R.id.client_list_item_description)
        TextView mDescriptionTextView;

        public ClientItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final Client client) {
            Context context = ClientListRecyclerViewAdapter.this.mContext;

            if(mOnItemClickListener != null) {
                mListItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mOnItemClickListener.onItemClick(client);
                    }
                });
            }

            //If there's no profile url then just display initials
            if (android.text.TextUtils.isEmpty(client.getProfileImageUrl())) {
                mImageView.setVisibility(View.GONE);
                mInitialsLayout.setVisibility(View.VISIBLE);
                mInitialsTextView.setText(client.getFirstName().substring(0, 1) +
                                          client.getLastName().substring(0, 1));
            }
            else {
                mImageView.setVisibility(View.VISIBLE);
                mInitialsLayout.setVisibility(View.GONE);
                Picasso.with(context)
                        .load(client.getProfileImageUrl())
                        .placeholder(R.drawable.img_pro_placeholder)
                        .noFade()
                        .into(mImageView);
            }

            mNameTextView.setText(context.getString(
                    R.string.client_list_item_name,
                    client.getFirstName(),
                    client.getLastName().substring(0, 1)));

            mCityTextView.setText(client.getAddress().getCityState());

            Client.Context clientContext = client.getContext();

            //If there's no client context then don't show a green dot.
            if (clientContext == null) {
                mDescriptionTextView.setText("");
                mGreenDotImageView.setVisibility(View.GONE);
            }
            else {
                mDescriptionTextView.setText(clientContext.getDescription());
                //Green dot only displays for upcoming bookings
                mGreenDotImageView.setVisibility(
                        clientContext.getContextType() == Client.ContextType.UpcomingBooking
                                ? View.VISIBLE : View.GONE);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Client client);
    }

}
