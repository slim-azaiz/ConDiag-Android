package info.androidhive.gmail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.gmail.R;
import info.androidhive.gmail.model.Diagnostic;


public class DiagnosticAdapter extends RecyclerView.Adapter<DiagnosticAdapter.ViewHolder> {
    private ArrayList<Diagnostic> diagnostics;

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