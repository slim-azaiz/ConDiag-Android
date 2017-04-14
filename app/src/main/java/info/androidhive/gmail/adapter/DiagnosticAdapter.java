package info.androidhive.gmail.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.gmail.R;
import info.androidhive.gmail.model.Diagnostic;
import info.androidhive.gmail.model.Server;


public class DiagnosticAdapter extends RecyclerView.Adapter<DiagnosticAdapter.ViewHolder> implements Filterable {
    public static ArrayList<Diagnostic> diagnostics;
    private ArrayList<Diagnostic> mFilteredDiagnosticArray;


    public DiagnosticAdapter(ArrayList<Diagnostic> diagnostics) {
        this.diagnostics = diagnostics;
    }

    @Override
    public DiagnosticAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiagnosticAdapter.ViewHolder viewHolder, int i) {

        viewHolder.parameter.setText(diagnostics.get(i).getParameter());
        viewHolder.value.setText(diagnostics.get(i).getValue());
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
        public ViewHolder(View view) {
            super(view);
            parameter = (TextView)view.findViewById(R.id.parameter);
            value = (TextView)view.findViewById(R.id.value);
        }
    }
}