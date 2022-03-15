package com.location.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.location.app.R;
import com.location.app.activity.MyPathActivity;
import com.location.app.model.ModelMyPath;

import java.util.ArrayList;

public class MyPathAdapter extends RecyclerView.Adapter<MyPathAdapter.ViewHolderItem> {
    ArrayList<ModelMyPath> list;
    Context context;
    MyPathActivity activity;

    public MyPathAdapter(ArrayList<ModelMyPath> list, Context context, MyPathActivity activity) {
        this.list = list;
        this.context = context;
        this.activity =activity;
    }

    @NonNull
    @Override
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_path, parent, false);
        return new ViewHolderItem(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolderItem holder, int position) {
        holder.date_time.setText(list.get(position).date_time);

        holder.rl_path_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.callFragment();
            }
        });
        //    holder.txtName.setText(list.get(position).getName());
        //   holder.textPhone.setText(list.get(position).getPhone());
//        holder.txtRecommen.setText(list.get(position).getTxtRecommen());
        // holder.txtName.setOnClickListener(new View.OnClickListener() {
        //      @Override
        //   public void onClick(View view) {
        //  list.get(position).setAlreadyInCall(true);

//                Intent i = new Intent(context, VoiceActivity.class);
//                Bundle b = new Bundle();
//
//                i.putExtra("name", list.get(position).getName());
//                i.putExtra("mobile", list.get(position).getPhone());
//                i.putExtra("profile", list.get(position).getProfilePic());
//                i.putExtra("calleeItem", list.get(position));
//                i.putExtra("contacts", list);
//                i.putExtras(b);
//                context.startActivity(i);
        //      }
        //  });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolderItem extends RecyclerView.ViewHolder {
        TextView date_time;
        RelativeLayout rl_path_detail;


        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            date_time = itemView.findViewById(R.id.tv_date_time);
            rl_path_detail = itemView.findViewById(R.id.rl_path_detail);
//            txtName = itemView.findViewById(R.id.txtName);
//            textPhone = itemView.findViewById(R.id.textPhone);
//            imgOne = itemView.findViewById(R.id.imgOne);

        }
    }
}
