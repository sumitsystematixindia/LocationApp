package com.location.app.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.location.app.R;
import com.location.app.model.ModelTechnique;

import java.util.ArrayList;
import java.util.List;

public class TechniqueAdapter extends  RecyclerView.Adapter<TechniqueAdapter.ViewHolder> {
    List<ModelTechnique> techniqueList=new ArrayList();
    Context context;

    public TechniqueAdapter(List<ModelTechnique> techniqueList, Context context)
    {
        this.techniqueList=techniqueList;
        this.context=context;
    }

    @NonNull
    @Override
    public TechniqueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_technique, parent, false);

        return new TechniqueAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TechniqueAdapter.ViewHolder holder, int position) {
        holder.tv_technique_name.setText(techniqueList.get(position).getTechnique_name);
    }

    @Override
    public int getItemCount() {
        return techniqueList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_technique_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_technique_name=itemView.findViewById(R.id.tv_technique_name);
        }
    }
}
