package com.grievancesystem.speakout.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.grievancesystem.speakout.Database.Prefs;
import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.adapter.ReplyAdapter;
import com.grievancesystem.speakout.models.Complaints;
import com.grievancesystem.speakout.models.Notification;
import com.grievancesystem.speakout.models.Reply;
import com.grievancesystem.speakout.models.User;
import com.grievancesystem.speakout.utility.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int DELETING_THE_COMPLAINT = 101;
    private static final int BLOCKING_THE_USER = 102;

    @BindView(R.id.imageViewArrowBack)
    ImageView imageViewArrowBack;
    @BindView(R.id.imageViewBlockUser)
    ImageView imageViewBlockUser;
    @BindView(R.id.imageViewDeleteConvo)
    ImageView imageViewDeleteConvo;
    @BindView(R.id.imageViewStatus)
    ImageView imageViewStatus;
    @BindView(R.id.textViewComplaintTitle)
    TextView textViewComplaintTitle;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.complaints_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.send)
    ImageView send;
    @BindView(R.id.editTextMessage)
    EditText editTextMessage;
    @BindView(R.id.progressLoader)
    TashieLoader progressLoader;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String msg = editTextMessage.getText().toString().trim();
            if (msg.isEmpty()) send.setVisibility(View.GONE);
            else send.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    Unbinder unbinder;
    Complaints complaint;
    String status, new_status;
    ReplyAdapter adapter;
    TashieLoader pdChangeStatusLoader, pdDeleteComplaintLoader;
    ArrayList<Reply> list = new ArrayList<>();
    AlertDialog alertDialogChangeStatus, alertDialogDeleteBlock;
    // RadioGroup pdChangeStatusRadioGroup;
    TextInputLayout textInputLayoutPassword;
    MaterialButton pdChangeStatusSubmitButton;
    MaterialButton pdDialogDeleteBlockSubmitButton;
    int changeStatusState = 0, deleteBlockState = 0;
    TextView pdChangeStatusTitle, pdDialogDeleteBlockTitle, pdDialogDeleteBlockMessage;
    int flag = -1;

    boolean isConnected = true;
    boolean monitoringConnectivity = false;
    View parentLayout;
    private final ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            showBackOnlineUI();
            isConnected = true;
        }

        @Override
        public void onLost(Network network) {
            showNoInternetUI();
            isConnected = false;
        }
    };

    private void showBackOnlineUI() {
        if (parentLayout != null)
            Snackbar.make(parentLayout, "Back Online", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(android.R.color.holo_green_light))
                    .setTextColor(getResources().getColor(android.R.color.white)).show();
    }

    private void showNoInternetUI() {
        if (parentLayout != null)
            Snackbar.make(parentLayout, "No Internet Connection Available", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(android.R.color.black))
                    .setTextColor(getResources().getColor(android.R.color.white)).show();
    }

    @Override
    protected void onPause() {
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
    }

    private void checkConnectivity() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (!isConnected) {
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;
        }

    }

    @Override
    protected void onStart() {
        if (!Helper.isInternetAvailable(this)) {
            showNoInternetUI();
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        parentLayout = findViewById(android.R.id.content);
        unbinder = ButterKnife.bind(this);

        complaint = (Complaints) getIntent().getSerializableExtra("complaint");
        textViewComplaintTitle.setText(complaint.getSubject());
        status = complaint.getStatus();

        imageViewArrowBack.setOnClickListener(this);
        imageViewBlockUser.setOnClickListener(this);
        imageViewDeleteConvo.setOnClickListener(this);
        if (status.equals(Helper.PENDING)) {
            imageViewStatus.setBackgroundResource(R.drawable.ic_pending);
        } else if (status.equals(Helper.IN_PROGRESS)) {
            imageViewStatus.setBackgroundResource(R.drawable.ic_pause_circle);
        } else {
            imageViewStatus.setBackgroundResource(R.drawable.ic_check_circle);
            ImageViewCompat.setImageTintList(imageViewStatus, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.resolved_color)));
        }
        imageViewStatus.setOnClickListener(this);
        send.setOnClickListener(this);

        if (Prefs.getUser(ConversationActivity.this).getUserType() == Helper.USER_STUDENT) {
            imageViewStatus.setVisibility(View.GONE);
            imageViewDeleteConvo.setVisibility(View.GONE);
            imageViewBlockUser.setVisibility(View.INVISIBLE);
        }

        initRecyclerView();

        if (Prefs.getUser(this).getUserType() != Helper.USER_ADMINISTRATOR
                && !Prefs.getUser(this).getUsername().equals(complaint.getUsername())) {
            editTextMessage.setText("Only Admin or the owner of the complaint can participate to this conversation");
            editTextMessage.setEnabled(false);
        } else editTextMessage.addTextChangedListener(textWatcher);

        checkUpdatesForReplies();
    }

    /*
    private void loadReplies() {
        Query query = FirebaseDatabase.getInstance().getReference("Reply").child(complaint.getComplaintId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists()) {
                        String replyId = ds.child("reply_id").getValue().toString();
                        String sent_from = ds.child("sent_from").getValue().toString();
                        String conversation_id = ds.child("conversation_id").getValue().toString();
                        String message = ds.child("message").getValue().toString();
                        String time = null;
                        long timeStamp = 0;
                        Map<String, Long> map = new HashMap();
                        if (ds.child("timeStampMap").child("timeStamp").exists()) {
                            timeStamp = (long) ds.child("timeStampMap").child("timeStamp").getValue();
                            DateFormat dateFormat = getDateTimeInstance();
                            Date netDate = (new Date(timeStamp));
                            time = dateFormat.format(netDate);
                            map.put("timeStamp", timeStamp);
                        }
                        list.add(new Reply(replyId, sent_from, conversation_id, message, map));
                    }
                    if (list.isEmpty()) {
                        Helper.toast(ConversationActivity.this, "No Conversation Found for this complaint");
                    }
                    if(adapter!=null && recyclerView!=null && progressLoader!=null) {
                        adapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(list.size());
                        progressLoader.setVisibility(View.GONE);
                        checkUpdatesForReplies();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
     */

    private void initRecyclerView() {
        adapter = new ReplyAdapter(list, ConversationActivity.this, complaint);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ConversationActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewArrowBack:
                onBackPressed();
                break;
            case R.id.imageViewStatus:
                changeStatusState = 0;
                showProgressDialogueChangeStatus();
                break;
            case R.id.imageViewDeleteConvo:
                deleteBlockState = 0;
                flag = DELETING_THE_COMPLAINT;
                showProgressDialogueDeleteBlock();
                break;
            case R.id.imageViewBlockUser:
                deleteBlockState = 0;
                flag = BLOCKING_THE_USER;
                showProgressDialogueDeleteBlock();
                break;
            case R.id.send:
                sendMessage();
                break;
        }
    }

    private void checkUpdatesForReplies() {
        Query query = FirebaseDatabase.getInstance().getReference("Reply").child(complaint.getComplaintId());

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
                if (ds.exists() && list != null) {
                    String replyId = ds.child("reply_id").getValue().toString();
                    String sent_from = ds.child("sent_from").getValue().toString();
                    String conversation_id = ds.child("conversation_id").getValue().toString();
                    String message = ds.child("message").getValue().toString();
                    String time = null;
                    long timeStamp = 0;
                    Map<String, Long> map = new HashMap();
                    if (ds.child("timeStampMap").child("timeStamp").exists()) {
                        timeStamp = (long) ds.child("timeStampMap").child("timeStamp").getValue();
                        map.put("timeStamp", timeStamp);
                    }
                    list.add(new Reply(replyId, sent_from, conversation_id, message, map));
                }
                if (adapter != null && recyclerView != null && progressLoader != null) {
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(list.size());
                    progressLoader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
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

    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();
        editTextMessage.setText(null);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Reply").child(complaint.getComplaintId());
        String replyId = reference.push().getKey();
        Map map = new HashMap();
        map.put("timeStamp", ServerValue.TIMESTAMP);
        Reply reply = new Reply(replyId, Prefs.getUser(this).getUsername(), complaint.getComplaintId(), message, map);

        reference.child(replyId).setValue(reply).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("Convo Activity", "onComplete: replied");
                    User user = Prefs.getUser(ConversationActivity.this);
                    if (!complaint.getUsername().equals(user.getUsername()) && user.getUserType() == Helper.USER_ADMINISTRATOR) {
                        Notification notification = new Notification("Complaint : " + complaint.getSubject(), "@" + user.getUsername() + " replied : " + message
                                , complaint.getComplaintId(), null, user.getProfileUri(), false);
                        Helper.sendNotificationToUser(complaint.getUsername(), notification);
                    }
                } else
                    Log.i("Convo Activity", "Comment Error : " + task.getException().getMessage());
            }
        });
    }

    private void showProgressDialogueChangeStatus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialogue_change_complaint_status, null);
        builder.setView(v);
        pdChangeStatusSubmitButton = v.findViewById(R.id.submitButton);
        pdChangeStatusLoader = v.findViewById(R.id.progressLoader);
        // pdChangeStatusRadioGroup = v.findViewById(R.id.radioGroup);
        pdChangeStatusTitle = v.findViewById(R.id.textViewTitle);
        RelativeLayout relativeLayout = v.findViewById(R.id.relative);
        ImageView icon1 = v.findViewById(R.id.icon1);
        ImageView icon2 = v.findViewById(R.id.icon2);
        TextView textView1 = v.findViewById(R.id.text1);
        TextView textView2 = v.findViewById(R.id.text2);

        if (status.equals(Helper.PENDING)) {
            icon1.setBackgroundResource(R.drawable.ic_pause_circle);
            icon2.setBackgroundResource(R.drawable.ic_check_circle);
        } else if (status.equals(Helper.IN_PROGRESS)) {
            icon1.setBackgroundResource(R.drawable.ic_pending);
            icon2.setBackgroundResource(R.drawable.ic_check_circle);
            textView1.setText(Helper.PENDING);
        } else {
            icon1.setBackgroundResource(R.drawable.ic_pending);
            icon2.setBackgroundResource(R.drawable.ic_pause_circle);
            textView1.setText(Helper.PENDING);
            textView2.setText(Helper.IN_PROGRESS);
        }

       /* if (complaint.getStatus().equals(Helper.IN_PROGRESS))
            pdChangeStatusRadioGroup.check(R.id.radio_in_progress);
        else if (complaint.getStatus().equals(Helper.RESOLVED))
            pdChangeStatusRadioGroup.check(R.id.radio_resolved);*/

        alertDialogChangeStatus = builder.create();
        alertDialogChangeStatus.show();

        alertDialogChangeStatus.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialogChangeStatus.getWindow().setBackgroundDrawable(null);
        alertDialogChangeStatus.getWindow().setGravity(Gravity.BOTTOM);

        icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdChangeStatusTitle.setText("Please wait we are changing the complaint status...");
                pdChangeStatusLoader.setVisibility(View.VISIBLE);
                pdChangeStatusSubmitButton.setVisibility(View.GONE);
                //pdChangeStatusRadioGroup.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.GONE);
                changeStatusState = 1;
                alertDialogChangeStatus.setCancelable(false);
                if (status.equals(Helper.PENDING)) new_status = Helper.IN_PROGRESS;
                else new_status = Helper.PENDING;
                changeStatus(new_status);
            }
        });

        icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdChangeStatusTitle.setText("Please wait we are changing the complaint status...");
                pdChangeStatusLoader.setVisibility(View.VISIBLE);
                pdChangeStatusSubmitButton.setVisibility(View.GONE);
                //pdChangeStatusRadioGroup.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.GONE);
                changeStatusState = 1;
                alertDialogChangeStatus.setCancelable(false);
                if (status.equals(Helper.RESOLVED)) new_status = Helper.IN_PROGRESS;
                else new_status = Helper.RESOLVED;
                changeStatus(new_status);
            }
        });

        pdChangeStatusSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogChangeStatus.dismiss();
            }
        });
    }

    public void showProgressDialogueDeleteBlock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialogue_delete, null);
        builder.setView(v);
        pdDialogDeleteBlockSubmitButton = v.findViewById(R.id.submitButton);
        pdDeleteComplaintLoader = v.findViewById(R.id.progressLoader);
        pdDialogDeleteBlockTitle = v.findViewById(R.id.textViewTitle);
        pdDialogDeleteBlockMessage = v.findViewById(R.id.textViewMessage);
        textInputLayoutPassword = v.findViewById(R.id.textInputPassword);

        if (flag == DELETING_THE_COMPLAINT) {
            pdDialogDeleteBlockTitle.setText("Are you sure you want to delete this complaint?");
            pdDialogDeleteBlockMessage.setText("Once deleted the following complaint cannot be restored.It will delete its whole data including it's replies, upvotes, downvotes, comments, etc.After deleting this complaint a notification will be sent to the user regarding this delete of his complaint but he will never be able to find it again from anywhere.");
        } else if (flag == BLOCKING_THE_USER) {
            pdDialogDeleteBlockTitle.setText("Are you sure you want to block this user?");
            pdDialogDeleteBlockMessage.setText("Once this user get blocked he will be logout from the logged in devices and will not be able to login again until get unblocked by the admin.");
        }
        alertDialogDeleteBlock = builder.create();
        alertDialogDeleteBlock.show();

        alertDialogDeleteBlock.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialogDeleteBlock.getWindow().setBackgroundDrawable(null);
        alertDialogDeleteBlock.getWindow().setGravity(Gravity.BOTTOM);

        pdDialogDeleteBlockSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (deleteBlockState) {
                    case 0:
                        if (flag == DELETING_THE_COMPLAINT) {
                            pdDialogDeleteBlockTitle.setText("You are deleting this Complaint Completely?\nAuthenticate First!!");
                        } else if (flag == BLOCKING_THE_USER) {
                            pdDialogDeleteBlockTitle.setText("You are blocking this User?\nAuthenticate First!!");
                        }
                        pdDialogDeleteBlockMessage.setText("For the Security reasons please retype your password for Authentication.");
                        textInputLayoutPassword.setVisibility(View.VISIBLE);
                        deleteBlockState = 1;
                        break;
                    case 1:
                        String pass = textInputLayoutPassword.getEditText().getText().toString().trim();
                        if (pass.isEmpty()) {
                            textInputLayoutPassword.setError("Password is Required");
                            textInputLayoutPassword.requestFocus();
                            return;
                        } else textInputLayoutPassword.setError(null);

                        alertDialogDeleteBlock.setCancelable(false);
                        String email = Prefs.getUser(ConversationActivity.this).getEmail();
                        if (email == null || email.isEmpty()) {
                            deleteBlockState = 3;
                            textInputLayoutPassword.setVisibility(View.GONE);
                            pdDeleteComplaintLoader.setVisibility(View.GONE);
                            pdDialogDeleteBlockMessage.setVisibility(View.GONE);
                            pdDialogDeleteBlockTitle.setText("Oops Error occurred unable to find you email. Please logout and login again.");
                            pdDialogDeleteBlockSubmitButton.setText("Dismiss");
                            return;
                        }

                        pdDialogDeleteBlockMessage.setText("Please Wait We are verifying the details...");
                        pdDeleteComplaintLoader.setVisibility(View.VISIBLE);
                        textInputLayoutPassword.setVisibility(View.GONE);
                        pdDialogDeleteBlockSubmitButton.setVisibility(View.GONE);
                        authenticate(pass, email);
                        break;
                    case 3:
                        Intent intent = new Intent(ConversationActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                }

            }
        });
    }

    private void authenticate(String pass, String email) {
        AuthCredential credentials = EmailAuthProvider.getCredential(email, pass);
        FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credentials).addOnCompleteListener(ConversationActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    alertDialogDeleteBlock.setCancelable(false);
                    if (flag == DELETING_THE_COMPLAINT) {
                        pdDialogDeleteBlockTitle.setText("Complaint Deleting Process");
                        pdDialogDeleteBlockMessage.setText("Authentication Success!!\nDeleting the Complaint...");
                    } else if (flag == BLOCKING_THE_USER) {
                        pdDialogDeleteBlockTitle.setText("User Blocking Process");
                        pdDialogDeleteBlockMessage.setText("Authentication Success!!\nBlocking the user...");
                    }
                    textInputLayoutPassword.setVisibility(View.GONE);
                    pdDialogDeleteBlockSubmitButton.setVisibility(View.GONE);
                    deleteBlockState = 3;
                    if (flag == DELETING_THE_COMPLAINT)
                        deleteUserComplaint();
                    else if (flag == BLOCKING_THE_USER)
                        blockUser();
                } else setResultsDelUI(task.getException().getMessage());
            }
        });
    }

    private void blockUser() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("StudentUsers").child(complaint.getUid());

        HashMap<String, Object> data = new HashMap<>();
        data.put("isBlocked", true);
        data.put("isLoggedIn", false);

        reference.updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    setResultsDelUI("User has been blocked Successfully.");
                    Notification notification = new Notification("SpeakOut  Account Issues", "Your SpeakOut account has been blocked by the Admin"
                            , null, null, null, true);
                    Helper.sendNotificationToUser(complaint.getUsername(), notification);
                }
                else{
                    setResultsDelUI("Error In blocking the User : " + task.getException().getMessage());
                }
            }
        });
    }

    private void deleteUserComplaint() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Complaints").child(complaint.getComplaintId());
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    setResultsDelUI("Complaint Deleted Successfully.");
                    Notification notification = new Notification("Complaint Deleted by Admin", "Your Complaint ‘" + complaint.getSubject() + "’ has been deleted. \n Please contact to your college"
                            , complaint.getComplaintId(), null, null, false);
                    Helper.sendNotificationToUser(complaint.getUsername(), notification);
                } else
                    setResultsDelUI("Error In Deleting Complaint : " + task.getException().getMessage());
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("Reply").child(complaint.getComplaintId());
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("Convo Activity", "onComplete: replies deleted");
                } else
                    Log.i("Convo Activity", "replies deleted Error : " + task.getException().getMessage());
            }
        });
    }

    private void setResultsDelUI(String message) {
        if (deleteBlockState == 1) {
            alertDialogDeleteBlock.setCancelable(true);
            pdDialogDeleteBlockSubmitButton.setVisibility(View.VISIBLE);
            textInputLayoutPassword.setVisibility(View.VISIBLE);
            pdDeleteComplaintLoader.setVisibility(View.GONE);
            pdDialogDeleteBlockMessage.setText(message);
        } else if (deleteBlockState == 3) {
            textInputLayoutPassword.setVisibility(View.GONE);
            pdDeleteComplaintLoader.setVisibility(View.GONE);
            pdDialogDeleteBlockMessage.setText(message);
            pdDialogDeleteBlockSubmitButton.setText("Dismiss");
            pdDialogDeleteBlockSubmitButton.setVisibility(View.VISIBLE);
        }
    }

    private void changeStatus(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Complaints").child(complaint.getComplaintId());
        complaint.setStatus(status);
        reference.updateChildren(Helper.getHashMap(complaint)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    setResultsUI("Complaint Status Changed Successfully");
                    Notification notification = new Notification("Complaint Status Updated", "Your Complaint ‘" + complaint.getSubject() + "’ is now " + status
                            , complaint.getComplaintId(), null, null, false);
                    Helper.sendNotificationToUser(complaint.getUsername(), notification);
                } else
                    setResultsUI("Failed to change status \n Error : " + task.getException().getMessage());
            }
        });
    }

    private void setResultsUI(String message) {
        changeStatusState = 2;
        pdChangeStatusLoader.setVisibility(View.GONE);
        pdChangeStatusSubmitButton.setVisibility(View.VISIBLE);
        pdChangeStatusTitle.setText(message);
        pdChangeStatusSubmitButton.setText("Dismiss");
    }
}