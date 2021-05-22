package com.example.bedalground;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    private View view;
    private Context context;

    private String Uid;
    private ArrayList<String> Tid_list, Tstr_list;

    private RecyclerView rv_chatList;
    private LinearLayoutManager linearLayoutManager;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    ArrayList<ChatListItem> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        context = getActivity();
        view = inflater.inflate(R.layout.activity_chat_fragment, container, false);

        getCurrentUser();
        init();
        makeList();

        return view;
    }

    private void makeList() {
        Tid_list = new ArrayList<String>();
        Tstr_list = new ArrayList<String>();
        rv_chatList = view.findViewById(R.id.rv_chatList);
        items.clear();

        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        databaseReference.child("users").child(Uid).child("chatList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    Tid_list.add(ds.getKey().toString());
                }

                for(int i=0; i<Tid_list.size(); i++){
                    int finalI = i;
                    databaseReference.child("Posting").orderByKey().equalTo(Tid_list.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds: snapshot.getChildren()){
                                Tstr_list.add(ds.child("title").getValue().toString());

                                databaseReference.child("Posting").child(Tid_list.get(finalI)).child("chat").child("message_list").orderByChild("realTime").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds:snapshot.getChildren()){
                                            items.add(new ChatListItem(Tstr_list.get(finalI), ds.child("message").getValue().toString(), ds.child("showTime").getValue().toString(), Tid_list.get(finalI)));

                                            rv_chatList.setLayoutManager(linearLayoutManager);
                                            rv_chatList.setItemAnimator(new DefaultItemAnimator());

                                            ChatListAdapter chatListAdapter = new ChatListAdapter(items, getFragmentManager(), ChatFragment.this);
                                            rv_chatList.setAdapter(chatListAdapter);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    private void init() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void getCurrentUser() {
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        Uid = user.getUid();
    }

}