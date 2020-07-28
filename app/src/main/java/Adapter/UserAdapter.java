package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techstrum.socialapp.MessageActivity;
import com.techstrum.socialapp.R;
import com.techstrum.socialapp.User;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    public UserAdapter(Context mContext,List<User> mUser){
        this.mUsers=mUser;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_list,parent,false);
        return new UserAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user=mUsers.get(position);
         holder.username.setText(user.name);
         //add profile picture here
       //holder.itemView.setOnClickListener(new View.OnClickListener() {
        //    @Override
            //public void onClick(View v) {
            //    Intent intent =new Intent(mContext, MessageActivity.class);
             //   intent.putExtra("emailid",user.email);
            //    mContext.startActivity(intent);
         //   }
      //  });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        public ViewHolder(View itemView){
            super(itemView);
            username=itemView.findViewById(R.id.userList_username);
            profile_image=itemView.findViewById(R.id.userList_profilePic);

        }
    }
}
