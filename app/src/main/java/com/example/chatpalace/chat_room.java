package com.example.chatpalace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class chat_room extends AppCompatActivity {

    private TextView messageText;
    private EditText messageEd;
    private Button sendMsgBtn;
    DatabaseReference root;
    String temp_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        messageText = findViewById(R.id.msgTextShowID);
        messageEd = findViewById(R.id.messageEdID);
        sendMsgBtn = findViewById(R.id.sendMsgBtnID);

        Bundle bundle = getIntent().getExtras();

        String userName = bundle.getString("userName");
        String roomName = bundle.getString("room_name");

        setTitle("Room :" + roomName);

        root = FirebaseDatabase.getInstance().getReference().child(roomName);

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageEd.getText().toString();
                if (msg.isEmpty()) {
                    messageEd.setError("Please, Enter Some Message");
                    messageEd.requestFocus();
                }else{
                    Map<String, Object> uniqueKeyMap = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.updateChildren(uniqueKeyMap);
                    DatabaseReference userReference = root.child(temp_key);
                    Map<String, Object> userMessageMap = new HashMap<String, Object>();
                    userMessageMap.put("name", userName);
                    userMessageMap.put("message", msg);
                    userReference.updateChildren(userMessageMap);
                    messageEd.setText("");
                    messageEd.requestFocus();
                }
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                AppendChatConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                AppendChatConversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void AppendChatConversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {
            String ChatMessage = (String) ((DataSnapshot) i.next()).getValue();
            String ChatUserName = (String) ((DataSnapshot) i.next()).getValue();
            messageText.append(ChatUserName + ": " + ChatMessage + "\n\n");
        }


    }


}