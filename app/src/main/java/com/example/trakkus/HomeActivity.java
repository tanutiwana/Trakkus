//Amritpal Singh
//Shubhpreet Uppal

package com.example.trakkus;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.trakkus.Utils.Commonx;
import com.firebase.geofire.GeoLocation;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView textView, textVV;
    Animation topAnimation, bottonAnimation;
    BottomNavigationView bottomNavigationView;
    GeoLocation location;
    Button btn_alluser, btn_friendList;

    //image slider
    SliderView sliderView;
    int[] images = {R.drawable.loa, R.drawable.loct, R.drawable.locationtrackinga, R.drawable.logo, R.drawable.locatoio};

    SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottonAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //hooks for slider
        sliderView = findViewById(R.id.imageSlider);

        //Initilize SliderAdapter
        sliderAdapter = new SliderAdapter(images);
        // set Slider Adapter
        sliderView.setSliderAdapter(sliderAdapter);
        //set Indicator Animation
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        // set transformation Animaton
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        //start auto cycle
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();


        // Toolbar setup;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //bottom navigation View
        bottomNavigationView = findViewById(R.id.bottom_navigation_home);

        //method for bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home_bottom:
                        Toast.makeText(HomeActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.notification_bottom:
                        startActivity(new Intent(getApplicationContext(), FriendNotificationActivity.class));
                        break;
                    case R.id.add_member_bottom:
                        startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
                        break;
                    case R.id.user_bottom:
                        startActivity(new Intent(getApplicationContext(), UserCurrentLocationActivity.class));
                        break;


                }

                return false;
            }
        });
        //menu hooks
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        //Navigation menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //header
        View headerView = navigationView.getHeaderView(0);
        TextView txt_user_logged = headerView.findViewById(R.id.username_txt);
        txt_user_logged.setText(Commonx.loggedUser.getEmail());
    }

    //on back press method
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Toolbar intent
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_navigation, menu);
        return true;
    }

    //method for tool bar item
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        //simplification of state
        if (id == R.id.top_sos) {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:112"));

            if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(HomeActivity.this, "National help line number", Toast.LENGTH_SHORT).show();

            }
            startActivity(callIntent);
            return true;
        } else if (id == R.id.top_help) {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("917888981270") + "@s.whatsapp.net");
            startActivity(sendIntent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.friends:
                startActivity(new Intent(getApplicationContext(), AllPeopleActivity.class));
                break;
            case R.id.GeoFencing:
                startActivity(new Intent(getApplicationContext(), GeoFencingActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
            case R.id.call:
                startActivity(new Intent(getApplicationContext(), ContactActivity.class));
                break;
            case R.id.help:
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("917888981270") + "@s.whatsapp.net");
                startActivity(sendIntent);
                break;
            case R.id.shareLocation:
                Intent shareintent = new Intent();
                shareintent.setAction(Intent.ACTION_SEND);
                shareintent.putExtra(Intent.EXTRA_TEXT, "http//com.example.trakkus");
                shareintent.setType("text/plain");
                startActivity(Intent.createChooser(shareintent, "Share via"));
                Toast.makeText(this, "share our app", Toast.LENGTH_SHORT).show();

                break;
            case R.id.LogOut:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent logoutintent = new Intent(HomeActivity.this, LogoutActivity.class);
                                    logoutintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(logoutintent);
                                    finish();
                                } else {
                                    Toast.makeText(HomeActivity.this, "Failed to logout", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;


    }

    public void btn_currentLocation(View view) {
        startActivity(new Intent(getApplicationContext(), UserCurrentLocationActivity.class));
    }

    public void btn_userList(View view) {
        startActivity(new Intent(getApplicationContext(), AllPeopleActivity.class));
    }

    public void btn_friendlist(View view) {
        startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
    }
}




