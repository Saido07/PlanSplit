package com.example.plansplit.Controllers;

import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.R;
import com.google.gson.Gson;

public class MyGroupActivity extends AppCompatActivity {
    //NavigationView navigationView;
    Database db = new Database();
    boolean ctrlType=false;             //eğer friend'den geliyorsa true, gruptan geliyorsa false

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(MyGroupActivity.this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.mygroup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.mygroup_group_options)
                    Toast.makeText(MyGroupActivity.this, "Grup Ayarları Seçildi", Toast.LENGTH_SHORT).show();
                if(menuItem.getItemId() == R.id.mygroup_table_export)
                    Toast.makeText(MyGroupActivity.this, "Tablo Olarak Çıkar Seçildi", Toast.LENGTH_SHORT).show();
                if(menuItem.getItemId() == R.id.mygroup_quick_add)
                    Toast.makeText(MyGroupActivity.this, "Hızlı Ekle Seçildi", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        popup.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygroup);

        TextView groupnameTv = findViewById(R.id.group_title_mygroupTv);
        final TextView list_titleTv = findViewById(R.id.list_buttonTv);
        final TextView events_titleTv = findViewById(R.id.events_buttonTv);
        final TextView group_op_titletV = findViewById(R.id.group_op_buttonTv);
        final TextView remove_txt = findViewById(R.id.remove_friend);
        //BottomNavigationView navView = findViewById(R.id.nav_view_mygroup);
        LinearLayout l = findViewById(R.id.remove_friend_linear);
        ImageView groupPhotoIv = findViewById(R.id.group_pictureIv);
        ImageButton listBttn = (ImageButton) findViewById(R.id.task_listButton);
        ImageButton eventsBttn = (ImageButton) findViewById(R.id.eventsButton);
        ImageButton groupOpBttn = (ImageButton) findViewById(R.id.groupOpButton);
        ImageButton backBttn = (ImageButton) findViewById(R.id.mygroup_back_button);
        ImageButton removeFriendBttn = findViewById(R.id.removeFriendButton);
        ImageButton menu = findViewById(R.id.mygroup_menuline_button);

        final NavController navController = Navigation.findNavController(this, R.id.fragment_place_mygroup);

        String group_title = "Group title";

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.keySet().contains("group_title")){
            group_title = extras.getString("group_title");
            int resid = extras.getInt("group_image");
            groupPhotoIv.setImageResource(resid);
            System.out.println("grouppp");
            groupOpBttn.setVisibility(View.VISIBLE);
            removeFriendBttn.setVisibility(View.INVISIBLE);
            l.setVisibility(View.INVISIBLE);
            menu.setVisibility(View.VISIBLE);

            ctrlType=false;
        }else if(extras != null && extras.keySet().contains("friend")){
            Gson gson = new Gson();
            String json = extras.getString("friend");
            Friend friend = gson.fromJson(json, Friend.class);
            groupPhotoIv.setImageResource(friend.getPerson_image());
            group_title = friend.getName();
            System.out.println("friend" + friend.getName());
            groupOpBttn.setVisibility(View.INVISIBLE);
            removeFriendBttn.setVisibility(View.VISIBLE);
            l.setVisibility(View.VISIBLE);
            menu.setVisibility(View.INVISIBLE);
            ctrlType=true;
        }

        list_titleTv.setVisibility(View.GONE);
        events_titleTv.setVisibility(View.VISIBLE);
        groupnameTv.setText(group_title);
        group_op_titletV.setVisibility(View.GONE);
        remove_txt.setVisibility(View.GONE);


        listBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.navi_todo_list);
                list_titleTv.setVisibility(View.VISIBLE);
                events_titleTv.setVisibility(View.GONE);
                if(!ctrlType)
                    group_op_titletV.setVisibility(View.GONE);
            }
        });

        eventsBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.navi_events);
                events_titleTv.setVisibility(View.VISIBLE);
                list_titleTv.setVisibility(View.GONE);
                if(!ctrlType)
                    group_op_titletV.setVisibility(View.GONE);
            }
        });

        if(!ctrlType) {
            groupOpBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navController.navigate(R.id.navi_operation);
                    group_op_titletV.setVisibility(View.VISIBLE);
                    events_titleTv.setVisibility(View.GONE);
                    list_titleTv.setVisibility(View.GONE);
                }
            });
        }else{
            removeFriendBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.removeFriend();
                }
            });
        }



        backBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

/*
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_personal, R.id.navigation_friends, R.id.navigation_groups, R.id.navigation_notifications)

                .build();
        NavController navController = Navigation.findNavController(MyGroup.this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(MyGroup.this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navigationView=findViewById(R.id.nav_draw_view);
*/
    }


}