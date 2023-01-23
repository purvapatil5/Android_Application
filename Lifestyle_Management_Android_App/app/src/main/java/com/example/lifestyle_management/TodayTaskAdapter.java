package com.example.lifestyle_management;

        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;
        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.google.android.material.snackbar.Snackbar;

        import java.util.ArrayList;

public class TodayTaskAdapter extends RecyclerView.Adapter<TodayTaskAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Task_Storage_Model> Task_Storage_ModelArrayList;

    // Constructor
    public TodayTaskAdapter(Context context, ArrayList<Task_Storage_Model> Task_Storage_ModelArrayList) {
        this.context = context;
        this.Task_Storage_ModelArrayList = Task_Storage_ModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_for_today, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        Task_Storage_Model model = Task_Storage_ModelArrayList.get(position);
        holder.BreakNameTV.setText(model.getTask_name());
        holder.BreakTimeTV.setText(model.getTask_time());
    }


    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return Task_Storage_ModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView BreakNameTV;
        private final TextView BreakTimeTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();


                }
            });
            BreakNameTV = itemView.findViewById(R.id.idTaskName);
            BreakTimeTV = itemView.findViewById(R.id.idTaskTime);

        }
    }
}
