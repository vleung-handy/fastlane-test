package com.handy.portal.clients.ui.adapter;

import android.content.Context;
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
        RecyclerView.Adapter<ClientListRecyclerViewAdapter.ClientItemViewHolder> {

    private Context mContext;
    private List<Client> mClientList;

    public ClientListRecyclerViewAdapter(Context context, List<Client> clientList) {
        mContext = context;
        mClientList = clientList;
    }

    @Override
    public ClientItemViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new ClientItemViewHolder(LayoutInflater
                .from(mContext)
                .inflate(R.layout.layout_client_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ClientItemViewHolder holder, final int position) {
        holder.bind(mClientList.get(position));
    }

    @Override
    public int getItemCount() {
        return mClientList == null ? 0 : mClientList.size();
    }

    public class ClientItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.client_list_item_img_view)
        ImageView mImageView;
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

        public void bind(Client client) {
            Context context = ClientListRecyclerViewAdapter.this.mContext;
            Picasso.with(context)
                    .load(client.getProfileImageUrl())
                    .placeholder(R.drawable.img_pro_placeholder)
                    .noFade()
                    .into(mImageView);

            mNameTextView.setText(context.getString(
                    R.string.client_list_item_name,
                    client.getFirstName(),
                    client.getLastName().substring(0, 1)));

            mCityTextView.setText(client.getAddress().getCityState());

            Client.Context clientContext = client.getContext();

            if (clientContext == null) {
                mDescriptionTextView.setText("");
                mGreenDotImageView.setVisibility(View.GONE);
            }
            else {
                mDescriptionTextView.setText(clientContext.getDescription());
                mGreenDotImageView.setVisibility(
                        clientContext.getContextType() == Client.ContextType.UpcomingBooking
                        ? View.VISIBLE : View.GONE);
            }
        }
    }
}
