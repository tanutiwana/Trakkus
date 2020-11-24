//Parul
package com.example.trakkus;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trakkus.Interface.IFirebaseLoadDone;
import com.example.trakkus.Interface.IRecycItemListerner;
import com.example.trakkus.Model.MyResponse;
import com.example.trakkus.Model.Request;
import com.example.trakkus.Model.User;
import com.example.trakkus.Remote.IFCMService;
import com.example.trakkus.Utils.Commonx;
import com.example.trakkus.Viewholder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AllPeopleActivity extends AppCompatActivity implements IFirebaseLoadDone {
    //Global veriables
    FirebaseRecyclerAdapter<User, UserViewHolder> adapter, searchAdapter;
    RecyclerView recycler_all_user;
    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();
    IFirebaseLoadDone firebaseLoadDone;
    IFCMService ifcmService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_people);

        //Initilize  Api
        ifcmService = Commonx.getFCMService();
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

        recycler_all_user.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_all_user.setLayoutManager(layoutManager);
        recycler_all_user.addItemDecoration(new DividerItemDecoration(this, ((LinearLayoutManager) layoutManager).getOrientation()));

        firebaseLoadDone = this;
        loadUserList();
        loadSearchData();
    }

    //load user List
    private void loadUserList() {

        Query query = FirebaseDatabase.getInstance().getReference().child(Commonx.USER_INFORMATION);
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i, final User user) {
                if (user.getUid().equals(Commonx.loggedUser.getUid())) {

                    if (user.getImage() != null) {
                        Picasso.get().load(Commonx.loggedUser.getImage()).into(userViewHolder.recycler_profile_image);
                    }
                    userViewHolder.txt_user_email.setText(new StringBuilder(user.getEmail()).append(" (ME)"));
                    userViewHolder.itemView.setClickable(false);
                    userViewHolder.txt_user_email.setTypeface(userViewHolder.txt_user_email.getTypeface(), Typeface.ITALIC);
                } else {
                    if (user.getImage() != null) {
                        Picasso.get().load(user.getImage()).into(userViewHolder.recycler_profile_image);
                    }
                    userViewHolder.txt_user_email.setText(new StringBuilder(user.getEmail()));
                }

                userViewHolder.setiRecycItemListerner(new IRecycItemListerner() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        showDialogRequest(user);
                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_user, parent, false);
                return new UserViewHolder(itemView);
            }
        };
        adapter.startListening();
        recycler_all_user.setAdapter(adapter);
    }

    //load search data in the form of suggestio
    private void loadSearchData() {
        final List<String> lsUserEmail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance()
                .getReference(Commonx.USER_INFORMATION);
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

    //start searching from the list
    private void startSearch(String txt_search) {

        Query query = FirebaseDatabase.getInstance()
                .getReference(Commonx.USER_INFORMATION)
                .orderByChild("email")
                .startAt(txt_search);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i, @NonNull final User user) {
                if (user.getEmail().equals(Commonx.loggedUser.getEmail())) {


                    if (user.getImage() != null) {
                        Picasso.get().load(Commonx.loggedUser.getImage()).into(userViewHolder.recycler_profile_image);
                    }
                    userViewHolder.txt_user_email.setText(new StringBuilder(user.getEmail()).append(" (ME)"));
                    userViewHolder.txt_user_email.setTypeface(userViewHolder.txt_user_email.getTypeface(), Typeface.ITALIC);
                } else {

                    if (user.getImage() != null) {
                        Picasso.get().load(user.getImage()).into(userViewHolder.recycler_profile_image);
                    }

                    userViewHolder.txt_user_email.setText(new StringBuilder(user.getEmail()));


                }

                userViewHolder.setiRecycItemListerner(new IRecycItemListerner() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        showDialogRequest(user);

                    }
                });

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_user, parent, false);

                return new UserViewHolder(itemView);
            }
        };

        searchAdapter.startListening();
        recycler_all_user.setAdapter(searchAdapter);

    }

    //show the Dialog when tap for sending friend request
    private void showDialogRequest(final User user) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.DialogRequest);


        alertDialog.setTitle("Send friend request");
        alertDialog.setMessage("Send friend request to " + user.getEmail() + "?");
        alertDialog.setIcon(R.drawable.ic_username);

        alertDialog.setNegativeButton("Cancel friend request", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });

        alertDialog.setPositiveButton(" Send ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //adding to accepted list
                DatabaseReference acceptList = FirebaseDatabase.getInstance()
                        .getReference(Commonx.USER_INFORMATION)
                        .child(Commonx.loggedUser.getUid())
                        .child(Commonx.ACCEPT_LIST);

                acceptList.orderByKey().equalTo(user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.getValue() == null) {
                                    sendFriendRequest(user); //already not added
                                } else
                                    Toast.makeText(AllPeopleActivity.this, "You and" + user.getEmail() + "are already friends ", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }

        });

        alertDialog.show();

    }

    //send friend request for tracking
    private void sendFriendRequest(final User user) {

        //get user
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Commonx.TOKENS);

        tokens.orderByKey().equalTo(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null)
                            Toast.makeText(AllPeopleActivity.this, "Token error", Toast.LENGTH_SHORT).show(); //cancel
                        else {
                            //create req
                            Request request = new Request();
                            //create data
                            final Map<String, String> dataSend = new HashMap<>();
                            dataSend.put(Commonx.FROM_UID, Commonx.loggedUser.getUid());
                            dataSend.put(Commonx.FROM_NAME, Commonx.loggedUser.getEmail());
                            dataSend.put(Commonx.TO_UID, user.getUid());
                            dataSend.put(Commonx.TO_NAME, user.getEmail());

                            request.setTo(dataSnapshot.child(user.getUid()).getValue(String.class));
                            request.setData(dataSend);

                            //send
                            compositeDisposable.add(ifcmService.sendFriendRequestToUser(request)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<MyResponse>() {
                                        @Override
                                        public void accept(MyResponse myResponse) throws Exception {
                                            if (myResponse.success == 1) {
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Commonx.USER_INFORMATION)
                                                        .child(dataSend.get(Commonx.TO_UID)).child(Commonx.FRIEND_REQUEST);
                                                User userp = new User();
                                                userp.setUid(dataSend.get(Commonx.FROM_UID));
                                                userp.setEmail(dataSend.get(Commonx.FROM_NAME));

                                                databaseReference.child(userp.getUid()).setValue(userp);


                                                Toast.makeText(AllPeopleActivity.this, "Request sent!",
                                                        Toast.LENGTH_SHORT).show();

                                            }


                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Toast.makeText(AllPeopleActivity.this, throwable.getMessage(),
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }));


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


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

        compositeDisposable.clear();
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
}
