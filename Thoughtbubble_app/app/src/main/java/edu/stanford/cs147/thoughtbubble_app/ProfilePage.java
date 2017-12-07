package edu.stanford.cs147.thoughtbubble_app;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfilePage extends AppCompatActivity {

    private String TAG = "ProfilePage";

    // profile image
    private ImageView mImageView;
    private ArrayList<String> topics;

    // user board
    CustomPagerEnum customBoard;

    private DatabaseHelper DBH;
    private AuthenticationHelper authHelper;
    private StorageHelper storageHelper;

    private ValueEventListener currUserListener;
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Makes status bar black and hides action bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ask_write);
        setContentView(R.layout.activity_profile_page);

        DBH = DatabaseHelper.getInstance();
        authHelper = AuthenticationHelper.getInstance();
        storageHelper = StorageHelper.getInstance();
        topics = new ArrayList<String>();
        loadUserFromDatabase();

        // Setting the color of the top bar -- pretty hacky -- do not touch this block//
        int unselected = Color.parseColor("#00cca3");
        int selected = Color.parseColor("#016d57");
        TextView profile = (TextView) findViewById(R.id.Profile);
        TextView ask = (TextView) findViewById(R.id.Ask);
        TextView discover = (TextView) findViewById(R.id.Discover);
        TextView answer = (TextView) findViewById(R.id.Answer);
        profile.setBackgroundColor(selected);
        ask.setBackgroundColor(unselected);
        answer.setBackgroundColor(unselected);
        discover.setBackgroundColor(unselected);
        // Setting the color of the top bar -- pretty hacky -- do not touch this block//

        // name edit box
        LinearLayout editName = (LinearLayout) findViewById(R.id.editNameBox);
        editName.setVisibility(View.GONE);

    }

    public void createBoardPage(HashMap<String, String> boards) {
        Log.d(TAG, "createBoardPage");
        customBoard = new CustomPagerEnum();
        final CustomPagerAdapter boardAdapter = new CustomPagerAdapter(this);

        for (Map.Entry<String, String> entry : boards.entrySet()) {
            DatabaseReference ref = DBH.boards.child(entry.getValue());
            ValueEventListener boardListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Board currBoard = dataSnapshot.getValue(Board.class);
                    customBoard.addBoard(currBoard.getName());
                    boardAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            ref.addListenerForSingleValueEvent(boardListener);

        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(boardAdapter);
        viewPager.setPageMargin(64);

    }

    public void EnableSave(View view) {

        // edit -> save

        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.my_switcher);
        SwitchEditSave(view);

        // This is a new name.
        EditText firstName=(EditText)findViewById(R.id.first_name_view);
        String newFirstName = firstName.getText().toString();
        EditText lastName=(EditText)findViewById(R.id.last_name_view);
        String newLastName = lastName.getText().toString();
        String newName = newFirstName + " " + newLastName;
        TextView myTV = (TextView) findViewById(R.id.profile_name);
        myTV.setText(newName);

        //Update currUser and db
        currUser.setFirstName(newFirstName);
        currUser.setLastName(newLastName);
        DBH.writeFirstName(authHelper.thisUserID, newFirstName);
        DBH.writeLastName(authHelper.thisUserID, newLastName);

        LinearLayout editName = (LinearLayout) findViewById(R.id.editNameBox);
        editName.setVisibility(View.GONE);
        TextView name = (TextView) findViewById(R.id.profile_name);
        name.setVisibility(View.VISIBLE);



        // Save button disabled, change to edit button
        TextView switcher_cur_view = (TextView) switcher.getCurrentView();
        switcher.showNext();
        //ViewSwitcher switcher2 = (ViewSwitcher) findViewById(R.id.my_switcher);
        //switcher2.showNext();
    }

    private void SwitchEditSave(View view){
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.my_switcher);
        TextView switcher_cur_view = (TextView) switcher.getCurrentView();
        String switch_string = switcher_cur_view.getText().toString().replaceAll("\\s+","");
        if (switch_string.equals("EditProfile")){
            switcher.showNext();
        }
    }

    public void EditProfile(View view) {
        SwitchEditSave(view);
        EditName(view);
        LinearLayout editName = (LinearLayout) findViewById(R.id.editNameBox);
        editName.setVisibility(View.VISIBLE);
        TextView name = (TextView) findViewById(R.id.profile_name);
        name.setVisibility(View.GONE);
    }


    public void EditName(View view) {
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.my_switcher);
        switcher.showNext(); //or switcher.showPrevious();
        // This is a new name.
        EditText firstName=(EditText)findViewById(R.id.first_name_view);
        String newFirstName = firstName.getText().toString();
        EditText lastName=(EditText)findViewById(R.id.last_name_view);
        String newLastName = lastName.getText().toString();
        String newName = newFirstName + " " + newLastName;
        TextView myTV = (TextView) findViewById(R.id.profile_name);
        LinearLayout editName = (LinearLayout) findViewById(R.id.editNameBox);
        editName.setVisibility(View.VISIBLE);
        TextView name = (TextView) findViewById(R.id.profile_name);
        name.setVisibility(View.GONE);
        myTV.setText(newName);
        SwitchEditSave(view);

    }

    public void changeimage(View view) {

        // ImageView in your Activity
        ImageView profile = (ImageView) findViewById(R.id.profile_image);

        Bitmap bitmap =((BitmapDrawable)profile.getDrawable()).getBitmap();
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);

        Intent intent=new Intent(view.getContext(),AndroidSelectImage.class);
        intent.putExtra("byteArray", bs.toByteArray());
        startActivity(intent);



        //Intent intent = new Intent(view.getContext(), AndroidSelectImage.class);

        //startActivity(intent);

    }

    public void ViewFullList(View view) {
        Intent fullview = new Intent(this, BoardFullView.class);
        fullview.putExtra("context", "view");
        fullview.putExtra("origin", "ProfilePage");
        startActivity(fullview);
    }

    // for the board view
    public class CustomPagerEnum {

        // TODO: change this to reflect the backend

        private int mTitleResId;
        private int mLayoutResId;

        private ArrayList<String> boardString;

        public CustomPagerEnum(){
            boardString = new ArrayList<String>();
        }

        public void addBoard(String boardName){
            boardString.add(boardName);
        }

        public ArrayList<String> getArray(){
            return boardString;
        }

        //public int getTitleResId() {
        //    return mTitleResId;
        //}

        public int getLayoutResId() {
            return R.layout.view_blue;
        }

    }

    private void loadUserFromDatabase() {
        Log.d(TAG, "loadUserFromDatabase");
        final DatabaseReference currUserRef = DBH.users.child(authHelper.thisUserID);
        if (currUserListener == null) {
            currUserListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currUser = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "getting currUser");
                    topics = currUser.getTopics();
                    HashMap<String, String> boards = currUser.getBoards();
                    if (boards != null) {
                        createBoardPage(boards);
                    }
                    loadProfileText();
                    loadProfileImage();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        currUserRef.addValueEventListener(currUserListener);
    }

    private void loadProfileText() {
        String name = currUser.getFirstName() + " " + currUser.getLastName();

        TextView nameField = (TextView) findViewById(R.id.profile_name);
        nameField.setText(name);

        LinearLayout profilelayout = (LinearLayout) findViewById(R.id.profilelayout);
        profilelayout.removeAllViews();

        if (topics == null) {
            topics = new ArrayList<String>();
        }
        for (int i = 0; i < topics.size(); i++) {
            Button btn = new Button(this);
            btn.setText(topics.get(i));

            final int finalI = i;
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    FragmentManager fm = getFragmentManager();
                    DialogFragmentProfile dialogFragment = new DialogFragmentProfile ();
                    Bundle args = new Bundle();
                    args.putInt("num", finalI);
                    dialogFragment.setArguments(args);
                    dialogFragment.show(fm, "Do you want to delete this topic?");
                }
            });

            profilelayout.addView(btn);
        }
        Button btn = new Button(this);
        btn.setText("+");
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                FragmentManager fm = getFragmentManager();
                AddNewInterestDialogFragment dialogFragment = new AddNewInterestDialogFragment ();
                //EditText editText = (EditText) getDialog().findViewById(R.id.project_name);
                dialogFragment.show(fm, null);
                System.out.println(dialogFragment.getActivity());
            }
        });
        profilelayout.addView(btn);

    }

    private void loadProfileImage(){
        mImageView = (ImageView) findViewById(R.id.profile_image);
        Log.d(TAG, "userID " + authHelper.thisUserID);

        // Load the image using Glide
        if (currUser.getHasProfileImage()) {
            Glide.with(this /* context */)
                    .using(new FirebaseImageLoader())
                    .load(storageHelper.getProfileImageRef(authHelper.thisUserID))
                    .asBitmap()
                    .into(mImageView);
        } else {
            Glide.with(this /* context */)
                    .using(new FirebaseImageLoader())
                    .load(storageHelper.getProfileImageRef("blank-user"))
                    .asBitmap()
                    .into(mImageView);
        }

    }

    public void ProfilePage(View view) {
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }

    public void AnswerPage(View view) {
        Intent intent = new Intent(this, AnswerListActivity.class);
        startActivity(intent);
    }

    public void AskPage(View view) {
        Intent intent = new Intent(this, AskWriteActivity.class);
        startActivity(intent);
    }

    public void DiscoverPage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            final String boardString = customBoard.getArray().get(position);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View layout = (View) inflater.inflate(customBoard.getLayoutResId(), collection, false);
            TextView tv = (TextView) layout.findViewById(R.id.textView);
            tv.setText(boardString);
            collection.addView(layout);

            layout.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    //this will log the page number that was click

                    Intent intent = new Intent(getBaseContext(), IndivBoardView.class);

                    intent.putExtra("CURRENT_BOARD", boardString);
                    startActivity(intent);

                    //Log.i("TAG", "This page was clicked: " + boardString);
                }
            });


            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return customBoard.getArray().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
            //return mContext.getString(customPagerEnum.getTitleResId());
            return customBoard.getArray().get(position);
        }

        public void addResult(String inputText) {
            String result = inputText;
            topics.add(result);
        }

        //public void removeResult() {

        //}

    }

}