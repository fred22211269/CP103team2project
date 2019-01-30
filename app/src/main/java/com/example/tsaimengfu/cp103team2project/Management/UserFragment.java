package com.example.tsaimengfu.cp103team2project.Management;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.tsaimengfu.cp103team2project.R;
import com.example.tsaimengfu.cp103team2project.task.Common;
import com.example.tsaimengfu.cp103team2project.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static com.example.tsaimengfu.cp103team2project.task.Common.networkConnected;


public class UserFragment extends Fragment {

    public UserFragment(){

    }

    Activity activity;
    private CommonTask userTask;
    private RecyclerView rvUsers;
    private SwipeRefreshLayout srUsers;
    List<User> users = null;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        if (getActivity() != null) {
            activity = getActivity();
        }
        View view = inflater.inflate(R.layout.fragment_managers, container, false);
        handleViews(view);

        srUsers = view.findViewById(R.id.srUsers);
        srUsers.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srUsers.setRefreshing(true);
                getUsers();
                srUsers.setRefreshing(false);
            }
        });
        return view;
    }

    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
        Context context;
        List<User> users;

        public UserAdapter(Context context, List<User> users) {
            this.context = context;
            this.users = users;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.managers_list_view, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
            final User user = users.get(i);
            myViewHolder.tvUserAccount.setText(user.getUserAccount());
            myViewHolder.tvUserName.setText(user.getUserName());
            myViewHolder.swPriority.setChecked(false);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvUserAccount, tvUserName;
            Switch swPriority;

            public MyViewHolder(View itemView) {
                super(itemView);
                tvUserAccount = itemView.findViewById(R.id.tvUserAccount);
                tvUserName = itemView.findViewById(R.id.tvUserName);
                swPriority = itemView.findViewById(R.id.swPriority);
                swPriority.setChecked(false);
            }
        }

    }

    private void handleViews(View view) {
        rvUsers = view.findViewById(R.id.recycleView);
        rvUsers.setLayoutManager(new LinearLayoutManager(activity));
        getUsers();
    }
// 假資料
private void getUsers() {
    if (networkConnected(activity)) {
        String url = Common.URL + "/UserServlet";
//           List<User> users = null;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getAll");
//        jsonObject.addProperty("id", 2);
        userTask = new CommonTask(url, jsonObject.toString());

        if (networkConnected(activity)) {
            try {
                String jsonIn = userTask.execute().get();
                Type listType = new TypeToken<List<User>>() {
                }
                        .getType();
                users = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
//                        Log.e(TAG, e.toString());
            }
            if (users == null || users.isEmpty())
            {
                Common.showToast(activity, R.string.text_NoReturn);
            } else {
                rvUsers.setAdapter(new UserFragment.UserAdapter(activity, users));
            }
        }
    } else {
        Common.showToast(activity, R.string.text_NoNetwork);
    }
}


}
