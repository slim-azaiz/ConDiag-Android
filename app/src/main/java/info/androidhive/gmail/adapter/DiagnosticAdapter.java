package info.androidhive.gmail.adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.gmail.R;
import info.androidhive.gmail.control_diagnostic.diagnostic.ChangeParameterDialog;
import info.androidhive.gmail.login.ChangePasswordDialog;
import info.androidhive.gmail.login.Login;
import info.androidhive.gmail.model.Diagnostic;
import info.androidhive.gmail.model.Server;




public class DiagnosticAdapter extends RecyclerView.Adapter<DiagnosticAdapter.ViewHolder> implements Filterable {
    public static ArrayList<Diagnostic> diagnostics = new ArrayList<>();
    private ArrayList<Diagnostic> mFilteredDiagnosticArray;
    public static DiagnosticAdapterListener listener  = new DiagnosticAdapterListener() {
        @Override
        public void onDiagnosticRowClicked(int position) {


        }
    };
    private  Context mContext;
    private FragmentManager fm;



    public DiagnosticAdapter(ArrayList<Diagnostic> diagnostics, Context mContext, FragmentManager fm) {
        this.diagnostics.addAll(diagnostics);
        this.mContext = mContext;
        this.fm = fm;
    }

    public void clearData() {
        this.diagnostics.clear();
        notifyDataSetChanged();
    }
    public void setData(List<Diagnostic> data) {
        clearData();
        this.diagnostics.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public DiagnosticAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiagnosticAdapter.ViewHolder viewHolder, int position) {

        viewHolder.parameter.setText(diagnostics.get(position).getParameter());
        viewHolder.value.setText(diagnostics.get(position).getValue());
        applyClickEvents(viewHolder, position);

    }

    private void applyClickEvents(final ViewHolder holder, final int position) {


        holder.diagnosticContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDiagnosticRowClicked(position);
                ChangeParameterDialog fragment = new ChangeParameterDialog(diagnostics.get(position).getParameter(),diagnostics.get(position).getValue());
//                mTvOldParameter = (TextView) v.findViewById(R.id.et_old_parameter);


                fragment.show(fm, ChangeParameterDialog.TAG);

            }
        });

    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredDiagnosticArray = diagnostics;
                } else {
                    ArrayList<Diagnostic> filteredList = new ArrayList<>();

                    for (Diagnostic diagnostic : diagnostics) {

                        if (diagnostic.getParameter().toLowerCase().contains(charString) || diagnostic.getValue().toLowerCase().contains(charString)) {

                            filteredList.add(diagnostic);
                        }
                    }

                    mFilteredDiagnosticArray = filteredList;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredDiagnosticArray;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredDiagnosticArray = (ArrayList<Diagnostic>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    @Override
    public int getItemCount() {
        return diagnostics.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView parameter, value;
        private LinearLayout diagnosticContainer;
        public ViewHolder(View view) {
            super(view);
            parameter = (TextView)view.findViewById(R.id.parameter);
            value = (TextView)view.findViewById(R.id.value);
            diagnosticContainer = (LinearLayout) view.findViewById(R.id.diagnostic_container);

        }
    }
    public interface DiagnosticAdapterListener {


        void onDiagnosticRowClicked(int position);

    }
}