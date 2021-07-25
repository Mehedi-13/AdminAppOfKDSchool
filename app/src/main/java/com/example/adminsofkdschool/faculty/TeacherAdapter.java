package com.example.adminsofkdschool.faculty;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminsofkdschool.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewAdapter> {

    private List<TeacherData> list;
    private Context context;
    private String category;

    public TeacherAdapter(List<TeacherData> list, Context context, String category) {
        this.list = list;
        this.context = context;
        this.category= category;

    }

    @NonNull
    @NotNull
    @Override
    public TeacherViewAdapter onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.faculty_item_layout,parent,false);
        return  new TeacherViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TeacherAdapter.TeacherViewAdapter holder, int position) {

        TeacherData item= list.get(position);
        holder.name.setText(item.getName());
        holder.email.setText(item.getEmail());
        holder.post.setText(item.getPost());
        holder.phoneNumber.setText(item.getPhoneNumber());
        holder.shift.setText(item.getShift());
        try {
            Picasso.get().load(item.getImage()).into(holder.image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context,UpdateTeacherActivity.class);
                intent.putExtra("name",item.getName());
                intent.putExtra("email",item.getEmail());
                intent.putExtra("post",item.getPost());
                intent.putExtra("phoneNumber",item.getPhoneNumber());
                intent.putExtra("shift",item.getShift());
                intent.putExtra("image",item.getImage());
                intent.putExtra("key",item.getKey());
                intent.putExtra("category",item.getCategory());

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TeacherViewAdapter extends RecyclerView.ViewHolder {

        private TextView name,post,email,phoneNumber,shift;
        private Button update;
        private ImageView image;

        public TeacherViewAdapter(@NonNull @NotNull View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.teacherName);
            post= itemView.findViewById(R.id.teacherPost);
            email= itemView.findViewById(R.id.teacherEmail);
            phoneNumber= itemView.findViewById(R.id.teacherPhoneNumber);
            shift= itemView.findViewById(R.id.teacherShift);
            update= itemView.findViewById(R.id.teacherUpdate);
            image= itemView.findViewById(R.id.teacherImage);

        }
    }
}
