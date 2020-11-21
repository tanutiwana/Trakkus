package com.example.trakkus;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trakkus.Interface.IFirebaseLoadDone;
import com.example.trakkus.Interface.IRecycItemListerner;
import com.example.trakkus.Model.User;
import com.example.trakkus.Services.MyLocationReceiver;
import com.example.trakkus.Utils.Commonx;
import com.example.trakkus.Viewholder.AllFriendsViewHolder;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IFirebaseLoadDone {

    //veribles
    FirebaseRecyclerAdapter<User, AllFriendsViewHolder> adapter;
    FirebaseRecyclerAdapter<User, AllFriendsViewHolder> searchAdapter;
    RecyclerView recycler_friend_list;
    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();
    IFirebaseLoadDone firebaseLoadDone;
    LocationRequest locationRequest;
    DatabaseReference publicLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView friend_list_empty;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        //hooks
        friend_list_empty = findViewById(R.id.friend_list_is_empty);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Initilize View
        searchBar = (MaterialSearchBar) findViewById(R.id.f_search_bar);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }


        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {

                    //if search close restore the default
                    if (adapter != null) {
                        recycler_friend_list.setAdapter(adapter);
                    }
                }

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });

        //Initilze the Recycler View
        recycler_friend_list = (RecyclerView) findViewById(R.id.recycler_friend_list);
        recycler_friend_list.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_friend_list.setLayoutManager(layoutManager);
        recycler_friend_list.addItemDecoration(new DividerItemDecoration(this, ((LinearLayoutManager) layoutManager).getOrientation()));


        //bottom Nvigation
        //bottom navigation View
        bottomNavigationView = findViewById(R.id.bottom_navigation_home);

        //method for bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home_bottom:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));

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
        //navigationView

        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.friendsdrawer);
        //Navigation menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_navigation_drawer,
                R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //header
        View headerView = navigationView.getHeaderView(0);
        TextView txt_user_logged = headerView.findViewById(R.id.username_txt);
        txt_user_logged.setText(Commonx.loggedUser.getEmail());

        //Update Location
        publicLocation = FirebaseDatabase.getInstance().getReference(Commonx.PUBLIC_LOCATION);
        updateLocation();

        firebaseLoadDone = this;
        loadFriendList();
        loadSerchData();


    }

    private void loadSerchData() {
        final List<String> lsUserEmail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance()
                .getReference(Commonx.USER_INFORMATION)
                .child(Commonx.loggedUser.getUid())
                .child(Commonx.ACCEPT_LIST);

        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                    User user = userSnapShot.getValue(User.class);
                    lsUserEmail.add(user.getEmail());
                }
                firebaseLoadDone.onFirebaseLoadUserNameDone(lsUserEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());

            }
        });


    }

    private void loadFriendList() {

        Query query = FirebaseDatabase.getInstance()
                .getReference(Commonx.USER_INFORMATION)
                .child(Commonx.loggedUser.getUid())
                .child(Commonx.ACCEPT_LIST);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, AllFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final AllFriendsViewHolder allFriendsViewHolder, int i, @NonNull final User user) {
                allFriendsViewHolder.all_friends_txt_user_email.setText(new StringBuilder(user.getEmail()));


                DatabaseReference referencet = FirebaseDatabase.getInstance().getReference().child(Commonx.USER_INFORMATION);

                Query newt = referencet.orderByChild("uid").equalTo(user.getUid());


                newt.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        if (getItemCount() > 0) {
                            friend_list_empty.setText(R.string.click);
                        }


                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userx : dataSnapshot.getChildren()) {

                                if (userx.child("email").exists()) {
                                    // Picasso.get().load(userx.child("email").getValue().toString()).into((Target) allFriendsViewHolder.all_friends_txt_user_email);
                                }


                            }


                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });


                allFriendsViewHolder.setiRecycItemListerner(new IRecycItemListerner() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        //show tracking
                        Commonx.trackingUser = user;
                        startActivity(new Intent(getApplicationContext(), TrackingActivity.class));

                    }
                });

            }

            @NonNull
            @Override
            public AllFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_friends_list, parent, false);

                return new AllFriendsViewHolder(itemView);

            }

            @NonNull
            @Override
            public User getItem(int position) {
                return super.getItem(position);
            }
        };

        adapter.startListening();
        recycler_friend_list.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

   /* // delete the user from friend list
    private void Delete_user(final boolean b) {

        DatabaseReference friendRequest = FirebaseDatabase.getInstance()
                .getReference(Commonx.USER_INFORMATION)
                .child(Commonx.loggedUser.getUid())
                .child(Commonx.ACCEPT_LIST);
        friendRequest.child(Commonx.loggedUser.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (b) {
                            Toast.makeText(FriendsActivity.this, "Delete!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    */

    private void updateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());


    }

    // pending method
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(FriendsActivity.this, MyLocationReceiver.class);
        intent.setAction(MyLocationReceiver.ACTION);

        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }


    //Build loaction method
    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setFastestInterval(3000);
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }

    // start seacrh method
    private void startSearch(String search_value) {
        Query query = FirebaseDatabase.getInstance()
                .getReference(Commonx.USER_INFORMATION)
                .child(Commonx.loggedUser.getUid())
                .child(Commonx.ACCEPT_LIST)
                .orderByChild("email")
                .startAt(search_value);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<User, AllFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllFriendsViewHolder allFriendsViewHolder, int i, @NonNull final User user) {
                allFriendsViewHolder.all_friends_txt_user_email.setText(new StringBuilder(user.getEmail()));
                allFriendsViewHolder.setiRecycItemListerner(new IRecycItemListerner() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        //show tracking
                        Commonx.trackingUser = user;
                        startActivity(new Intent(getApplicationContext(), TrackingActivity.class));

                    }
                });

            }

            @NonNull
            @Override
            public AllFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_friends_list, parent, false);

                return new AllFriendsViewHolder(itemView);

            }
        };

        searchAdapter.startListening();
        recycler_friend_list.setAdapter(searchAdapter);


    }

    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
        if (searchAdapter != null)
            searchAdapter.startListening();

    }


    @Override
    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        if (searchAdapter != null)
            searchAdapter.stopListening();
        super.onStop();
    }


    //firebase data occur method
    @Override
    public void onFirebaseLoadUserNameDone(List<String> lstEmail) {
        searchBar.setLastSuggestions(lstEmail);
    }

    //firebase data denied method
    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

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

            if (ActivityCompat.checkSelfPermission(FriendsActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(FriendsActivity.this, "National help line number", Toast.LENGTH_SHORT).show();

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

    // methid for navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {
            case R.id.GeoFencing:
                startActivity(new Intent(getApplicationContext(), GeoFencingActivity.class));
                break;
            case R.id.friends:
                startActivity(new Intent(getApplicationContext(), AllPeopleActivity.class));
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
                                    Intent logoutintent = new Intent(FriendsActivity.this, LogoutActivity.class);
                                    logoutintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(logoutintent);
                                    finish();

                                } else {
                                    Toast.makeText(FriendsActivity.this, "Failed to logout", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

