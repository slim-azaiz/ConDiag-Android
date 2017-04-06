package info.androidhive.gmail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import info.androidhive.gmail.R;
import info.androidhive.gmail.model.Diagnostic;

public class DiagnosticAdapter extends RecyclerView.Adapter<DiagnosticAdapter.DiagnosticHolder> {
    private Context mContext;
    private List<Diagnostic> mDiagnostics;



    public class DiagnosticHolder extends RecyclerView.ViewHolder  {
        public TextView parameter, value;
        public LinearLayout diagnosticContainer;
        public DiagnosticHolder(View view) {
            super(view);
            parameter = (TextView) view.findViewById(R.id.parameter);
            value = (TextView) view.findViewById(R.id.value);
            diagnosticContainer = (LinearLayout) view.findViewById(R.id.diagnostic_container);
        }
    }


    public DiagnosticAdapter(Context mContext, List<Diagnostic> diagnostics) {
        this.mContext = mContext;
        this.mDiagnostics = diagnostics;

    }

    @Override
    public DiagnosticHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.diagnostic_list_row, parent, false);

        return new DiagnosticHolder(itemView);
    }



    @Override
    public void onBindViewHolder(final DiagnosticHolder holder, final int position) {
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