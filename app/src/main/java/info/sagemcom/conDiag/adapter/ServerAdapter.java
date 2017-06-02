package info.sagemcom.conDiag.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import info.sagemcom.conDiag.R;
import info.sagemcom.conDiag.history.HistoryActivity;
import info.sagemcom.conDiag.helper.CircleTransform;
import info.sagemcom.conDiag.helper.FlipAnimator;
import info.sagemcom.conDiag.login.Login;
import info.sagemcom.conDiag.model.Server;

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.MyViewHolder> implements Filterable {
    private Context mContext;
    private List<Server> mServers;
    private List<Server> mFilteredServerList;

    public static ServerAdapterListener listener;
    private SparseBooleanArray selectedItems;


    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView ipAddress, friendlyName, modelName, iconText, timestamp;
        public ImageView iconImp, imgServer, wakeOnLan;
        public LinearLayout ServerContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;
        public MyViewHolder(View view) {
            super(view);
            ipAddress = (TextView) view.findViewById(R.id.ipAddress);
            friendlyName = (TextView) view.findViewById(R.id.txt_primary);
            modelName = (TextView) view.findViewById(R.id.txt_secondary);
            iconText = (TextView) view.findViewById(R.id.icon_text);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);
            iconImp = (ImageView) view.findViewById(R.id.icon_star);
            wakeOnLan = (ImageView) view.findViewById(R.id.wake_on_lan_icon);
            imgServer = (ImageView) view.findViewById(R.id.icon_profile);
            ServerContainer = (LinearLayout) view.findViewById(R.id.server_container);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredServerList = mServers;
                } else {
                    List<Server> filteredList = new ArrayList<>();

                    for (Server server : mServers) {

                        if (server.getFriendlyName().toLowerCase().contains(charString) || server.getIpAddress().toLowerCase().contains(charString) || server.getModel().toLowerCase().contains(charString)) {

                            filteredList.add(server);
                        }
                    }

                    mFilteredServerList = filteredList;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredServerList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredServerList = (ArrayList<Server>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public ServerAdapter(Context mContext, List<Server> servers, ServerAdapterListener listener) {
        this.mContext = mContext;
        this.mServers = servers;
        this.mFilteredServerList = servers;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.server_list_row, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Server server = mFilteredServerList.get(position);

        // displaying text view data
        holder.ipAddress.setText(server.getIpAddress());
        holder.friendlyName.setText(server.getFriendlyName());
        holder.modelName.setText(server.getModel());
        holder.timestamp.setText(server.getTimestamp());

        // displaying the first letter of From in icon text
        holder.iconText.setText(server.getIpAddress().substring(0, 1));

        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));

        // change the font style depending on modelName read status
        applyReadStatus(holder, server);

        // handle modelName star
        applyImportant(holder, server);

        // handle icon animation
        applyIconAnimation(holder, position);

        // display profile image
        applyProfilePicture(holder, server);

        // apply click events
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(final MyViewHolder holder, final int position) {
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        holder.iconImp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconImportantClicked(position);
            }
        });
        holder.wakeOnLan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWakeOnLanClicked(position);
            }
        });

        holder.ServerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onServerRowClicked(position);

                Intent intent = new Intent(mContext, Login.class);
                intent.putExtra("IpAddress",holder.ipAddress.getText());

                mContext.startActivity(intent);

            }
        });

        holder.ServerContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }
    public Drawable scaleImage (Drawable image, float scaleFactor) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

        image = new BitmapDrawable(this.mContext.getResources(), bitmapResized);

        return image;

    }

    private void applyProfilePicture(MyViewHolder holder, Server server) {
        if (!TextUtils.isEmpty(server.getPicture())) {

            Glide.with(mContext).load(R.mipmap.fourk)
                    .thumbnail(0.5f)
                    .crossFade()
                    .override(600,600)
                    .transform(new CircleTransform(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgServer);
            //holder.imgServer.setImageResource(R.mipmap.stb);
            holder.imgServer.setColorFilter(null);
            holder.iconText.setVisibility(View.GONE);
        } else {
            holder.imgServer.setImageResource(R.drawable.bg_circle);
            holder.imgServer.setColorFilter(server.getColor());
            holder.iconText.setVisibility(View.VISIBLE);
        }
    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }


    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    @Override
    public long getItemId(int position) {
        return mFilteredServerList.get(position).getId();
    }

    private void applyImportant(MyViewHolder holder, Server server) {
        holder.wakeOnLan.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.wake_on_lan));
        if (server.isImportant()) {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_black_24dp));
            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_selected));
            HistoryActivity.db.updateIsImportant(server.getId(), 0);

        } else {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_border_black_24dp));
            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_normal));
            HistoryActivity.db.updateIsImportant(server.getId(), 1);
        }
    }

    private void applyReadStatus(MyViewHolder holder, Server server) {
        if (server.isRead()) {
            holder.ipAddress.setTypeface(null, Typeface.NORMAL);
            holder.friendlyName.setTypeface(null, Typeface.NORMAL);
            holder.ipAddress.setTextColor(ContextCompat.getColor(mContext, R.color.friendlyName));
            holder.friendlyName.setTextColor(ContextCompat.getColor(mContext, R.color.model));
        } else {
            holder.ipAddress.setTypeface(null, Typeface.BOLD);
            holder.friendlyName.setTypeface(null, Typeface.BOLD);
            holder.ipAddress.setTextColor(ContextCompat.getColor(mContext, R.color.ipAddress));
            holder.friendlyName.setTextColor(ContextCompat.getColor(mContext, R.color.friendlyName));
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredServerList.size();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        mServers.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface ServerAdapterListener {
        void onIconClicked(int position);

        void onWakeOnLanClicked(int positoin);

        void onIconImportantClicked(int position);

        void onServerRowClicked(int position);

        void onRowLongClicked(int position);
    }
}