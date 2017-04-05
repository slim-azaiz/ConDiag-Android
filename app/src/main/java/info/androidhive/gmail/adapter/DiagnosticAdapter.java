package info.androidhive.gmail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import info.androidhive.gmail.R;
import info.androidhive.gmail.model.Diagnostic;

public class DiagnosticAdapter extends RecyclerView.Adapter<DiagnosticAdapter.MyViewHolder> {
    private Context mContext;
    private List<Diagnostic> mDiagnostics;



    public class MyViewHolder extends RecyclerView.ViewHolder  {
        public TextView parameter, value;
        public LinearLayout ServerContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;
        public MyViewHolder(View view) {
            super(view);
            parameter = (TextView) view.findViewById(R.id.ipAddress);
            value = (TextView) view.findViewById(R.id.txt_primary);
            iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);
            ServerContainer = (LinearLayout) view.findViewById(R.id.server_container);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);
        }
    }


    public DiagnosticAdapter(Context mContext, List<Diagnostic> diagnostics) {
        this.mContext = mContext;
        this.mDiagnostics = diagnostics;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.server_list_row, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Diagnostic diagnostic = mDiagnostics.get(position);

        // displaying text view data
        holder.parameter.setText(diagnostic.getParameter());
        holder.value.setText(diagnostic.getValue());


    }

    @Override
    public int getItemCount() {
        return mDiagnostics.size();
    }
}