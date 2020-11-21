package com.example.trakkus;

import android.Manifest;
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
import com.example.trakkus.Model.User;
import com.example.trakkus.Utils.Commonx;
import com.example.trakkus.Viewholder.FriendRequestViewHolder;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class FriendNotificationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IFirebaseLoadDone {
    //veriables
    FirebaseRecyclerAdapter<User, FriendRequestViewHolder> adapter, searchAdapter;
    IFirebaseLoadDone firebaseLoadDone;
    RecyclerView recycler_all_user;
    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_notification);

        // toolsbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Hooks
        recycler_all_user = (RecyclerView) findViewById(R.id.recycler_all_people);
        //Initilize View
        searchBar = (MaterialSearchBar) findViewById(R.id.m_search_bar);
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
                        recycler_all_user.setAdapter(adapter);
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

                return true;
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


        recycler_all_user.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_all_user.setLayoutManager(layoutManager);
        recycler_all_user.addItemDecoration(new DividerItemDecoration(this, ((LinearLayoutManager) layoutManager).getOrientation()));

        firebaseLoadDone = this;
        loadFriendRequestList();
        loadSearchData();
    }

    private void startSearch(String search_v) {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(Commonx.USER_INFORMATION)
                .child(Commonx.loggedUser.getUid())
                .child(Commonx.FRIEND_REQUEST)
                .orderByChild("email")
                .startAt(search_v);


        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<User, FriendRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendRequestViewHolder friendRequestViewHolder, int i, @NonNull final User user) {

                friendRequestViewHolder.txt_friend_email.setText(user.getEmail());
                friendRequestViewHolder.btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteFriendRequest(user, false);
                        toAddAcceptList(user);
                        addUserToFriendContact(user);


                    }
                });

                //for cancle friend request
                friendRequestViewHolder.btn_decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //
                        deleteFriendRequest(user, true);


                    }
                });

            }

            @NonNull
            @Override
            public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_friends_notification, viewGroup, false);
                return new FriendRequestViewHolder(itemView);
            }
        };


        searchAdapter.startListening();
        recycler_all_user.setAdapter(searchAdapter);


    }

    private void loadFriendRequestList() {
        Query query = FirebaseDatabase.getInstance().getReference().child(Commonx.USER_INFORMATION)
                .child(Commonx.loggedUser.getUid())
                .child(Commonx.FRIEND_REQUEST);
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, FriendRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendRequestViewHolder friendRequestViewHolder, int i, @NonNull final User user) {

                friendRequestViewHolder.txt_friend_email.setText(user.getEmail());
                friendRequestViewHolder.btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteFriendRequest(user, false);
                        toAddAcceptList(user);
                        addUserToFriendContact(user);


                    }
                });

                //for cancle friend request
                friendRequestViewHolder.btn_decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //
                        deleteFriendRequest(user, true);


                    }
                });

            }

            @NonNull
            @Override
            public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_friends_notification, viewGroup, false);
                return new FriendRequestViewHolder(itemView);
            }
        };


        adapter.startListening();
        recycler_all_user.setAdapter(adapter);

    }

    //Friend add user
    private void addUserToFriendContact(User user) {
        DatabaseReference acceptList = FirebaseDatabase.getInstance()
                .getReference(Commonx.USER_INFORMATION)
                .child(user.getUid())
                .child(Commonx.ACCEPT_LIST);

        acceptList.child(user.getUid()).setValue(Commonx.loggedUser);

    }

    //add friend by user
    private void toAddAcceptList(User user) {
        DatabaseReference acceptList = FirebaseDatabase.getInstance()
                .getReference(Commonx.USER_INFORMATION)
                .child(Commonx.loggedUser.getUid())
                .child(Commonx.ACCEPT_LIST);
        acceptList.child(user.getUid()).setValue(user);
    }

    private void deleteFriendRequest(final User user, final boolean b) {
        DatabaseReference friendRequest = FirebaseDatabase.getInstance()
                .getReference(Commonx.USER_INFORMATION)
                .child(Commonx.loggedUser.getUid())
                .child(Commonx.FRIEND_REQUEST);

        friendRequest.child(user.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (b) {
                            Toast.makeText(FriendNotificationActivity.this, "Delete!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    @Override
    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        if (searchAdapter != null)
            searchAdapter.stopListening();
        super.onStop();
    }

    private void loadSearchData() {
        final List<String> lsUserEmail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance()
                .getReference()
                .child(Commonx.USER_INFORMATION)
                .child(Commonx.loggedUser.getUid())
                .child(Commonx.FRIEND_REQUEST);

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

            if (ActivityCompat.checkSelfPermission(FriendNotificationActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(FriendNotificationActivity.this, "National help line number", Toast.LENGTH_SHORT).show();

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
                                    Intent logoutintent = new Intent(FriendNotificationActivity.this, LogoutActivity.class);
                                    logoutintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(logoutintent);
                                    finish();
                                } else {
                                    Toast.makeText(FriendNotificationActivity.this, "Failed to logout", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;


        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}
