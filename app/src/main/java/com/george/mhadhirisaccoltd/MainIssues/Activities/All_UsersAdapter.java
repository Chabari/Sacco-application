package com.george.mhadhirisaccoltd.MainIssues.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.george.mhadhirisaccoltd.Account.RequestsAdapter;
import com.george.mhadhirisaccoltd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by George on 3/14/2019.
 */

public class All_UsersAdapter extends RecyclerView.Adapter<All_UsersAdapter.ViewHolder> {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private Context context;
    private List<All_Users_List> all_users_lists;

    public All_UsersAdapter(List<All_Users_List> all_users_lists) {
        this.all_users_lists = all_users_lists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_display,parent,false);
        context = parent.getContext();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final String user_id = all_users_lists.get(position).getUser_id();
        firebaseFirestore.collection("Membership").document("Members").collection("Approval").document(user_id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        final String name = task.getResult().getString("fname")+" "+task.getResult().getString("lname");

                        firebaseFirestore.collection("ProfileImages").document(user_id).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            if (task.getResult().exists()){
                                                String imageUrl = task.getResult().getString("imageUrl");
                                                holder.setDetails(name,imageUrl);
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setIcon(R.drawable.ic_action_name)
                        .setTitle("Rights Enabling")
                        .setMessage("Are sure you want to enable admin rights to this user?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //updatingthe value
                                Map<String, Object> stringObjectMap = new HashMap<>();
                                stringObjectMap.put("rights","admin");
                                firebaseFirestore.collection("Users").document(user_id).update(stringObjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            holder.sendNotification(user_id,auth.getCurrentUser().getUid());
                                            Toast.makeText(context, "Admin right successfully updated...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return all_users_lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView Fullname;
        private CircleImageView ImageProfile;
        private View view;
        private LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            linearLayout = (LinearLayout)view.findViewById(R.id.user_item_linear);

        }

        public void setDetails(String fullname, String imageUrl){
            Fullname = (TextView)view.findViewById(R.id.tv_user_name);
            ImageProfile = (CircleImageView)view.findViewById(R.id.user_image_profile);

            Fullname.setText(fullname);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(ImageProfile);

        }

        public void sendNotification(String UserId, String MyUserId){
            Map<String ,String > stringStringMap = new HashMap<>();
            stringStringMap.put("from",MyUserId);
            stringStringMap.put("type","admin_rights");
            firebaseFirestore.collection("Notifications").document(UserId).collection("MyNotifications").add(stringStringMap);
        }
    }
}
