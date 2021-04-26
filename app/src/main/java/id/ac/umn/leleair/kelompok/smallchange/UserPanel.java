package id.ac.umn.leleair.kelompok.smallchange;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import id.ac.umn.leleair.kelompok.smallchange.Model.Data;
import id.ac.umn.leleair.kelompok.smallchange.Model.User;

public class UserPanel extends Fragment {
    private TextView username, email;
    private Button btnChangePass, btnLogout;
    private ImageView piggy, backWallet, backgroundBox;
    private ConstraintLayout frontWallet, photoContainer;
    private String stUsername;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_panel, container, false);

        //Firebase Initialization
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        String uid = mUser.getUid();

        mUsername = FirebaseDatabase.getInstance().getReference().child("Username").child(uid);

        piggy = view.findViewById(R.id.piggy);
        backWallet = view.findViewById(R.id.backWallet);
        frontWallet = view.findViewById(R.id.frontWallet);
        backgroundBox = view.findViewById(R.id.backgroundBoxUP);
        photoContainer = view.findViewById(R.id.photoContainer);
        username = view.findViewById(R.id.tvUsernameUP);
        email = view.findViewById(R.id.tvEmailUP);
        btnChangePass = view.findViewById(R.id.btnChangePass);
        btnLogout = view.findViewById(R.id.btnLogout);

        checkDatabaseUpdate();

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mAuth.signOut();
                    moveToLogin();
                } catch (Exception e) {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Logout Failed, please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void changePass() {
        //Change Password Form
        Dialog mdialog = new Dialog(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myviewm = inflater.inflate(R.layout.change_pass_form, null);
        mdialog.setContentView(myviewm);
        mdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.38);

        mdialog.getWindow().setLayout(width, height);

        EditText editNewPass = myviewm.findViewById(R.id.tvNewPass);

        Button btnChange = myviewm.findViewById(R.id.btnChangePass);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass = editNewPass.getText().toString().trim();

                if(TextUtils.isEmpty(newPass)){
                    editNewPass.setError("Required Field");
                    return;
                }
            }
        });
        mdialog.show();
    }

    private void checkDatabaseUpdate() {
        String authEmail;

        //get auth Email
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            FirebaseUser mUser = mAuth.getCurrentUser();
            authEmail = mUser.getEmail();
            email.setText(authEmail);
        }

        mUsername.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stUsername = "";

                for(DataSnapshot mSnapshot:snapshot.getChildren()){
                    User user = mSnapshot.getValue(User.class);
                    assert  user != null;
                    stUsername = user.getUsername();
                }
                username.setText(stUsername);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void playAnimOut(){
        photoContainer.animate().alpha(0).setDuration(200);
        backgroundBox.animate().translationY(300).alpha(0).setDuration(200);
        piggy.animate().translationY(450).alpha(0).setDuration(200);
        backWallet.animate().translationY(300).alpha(0).setDuration(300);
        frontWallet.animate().translationY(300).alpha(0).setDuration(300);
    }

    public void playAnimIn(){
        photoContainer.animate().alpha(1).setDuration(400);
        backgroundBox.animate().translationY(0).alpha(1).setDuration(600);
        piggy.animate().translationY(0).alpha(1).setDuration(600).setStartDelay(400);
        backWallet.animate().translationY(0).alpha(1).setDuration(600);
        frontWallet.animate().translationY(0).alpha(1).setDuration(600);
    }

    private void moveToLogin() {
        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}