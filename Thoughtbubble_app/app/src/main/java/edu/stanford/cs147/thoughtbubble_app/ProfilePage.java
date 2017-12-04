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
import android.widget.TextSwitcher;
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

public class ProfilePage extends AppCompatActivity {

    private String TAG = "ProfilePage";

    // profile image
    private ImageView mImageView;
    private ArrayList<String> topics;

    private DatabaseHelper DBH;
    private AuthenticationHelper authHelper;
    private StorageHelper storageHelper;

    private ValueEventListener currUserListener;
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // board page
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(this));
        viewPager.setPageMargin(64);
    }

    public void EnableSave(View view) {

        TextSwitcher switcher = (TextSwitcher) findViewById(R.id.save_edit_switcher);
        SwitchEditSave(view);
        // TODO: Save these things to the database

        // This is a new name.
        EditText et=(EditText)findViewById(R.id.hidden_edit_view);
        String newName = et.getText().toString();
        TextView myTV = (TextView) findViewById(R.id.profile_name);
        myTV.setText(newName);


        // Save button disabled, change to edit button
        TextView switcher_cur_view = (TextView) switcher.getCurrentView();
        switcher.showNext();
        ViewSwitcher switcher2 = (ViewSwitcher) findViewById(R.id.my_switcher);
        switcher2.showNext();
    }

    private void SwitchEditSave(View view){
        TextSwitcher switcher = (TextSwitcher) findViewById(R.id.save_edit_switcher);
        TextView switcher_cur_view = (TextView) switcher.getCurrentView();
        String switch_string = switcher_cur_view.getText().toString().replaceAll("\\s+","");
        System.out.println(switch_string);
        if (switch_string.equals("EditProfile")){
            System.out.println("I AM HERE");
            switcher.showNext();
        }
    }

    public void EditProfile(View view) {
        SwitchEditSave(view);
        EditName(view);
    }


    public void EditName(View view) {
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.my_switcher);
        switcher.showNext(); //or switcher.showPrevious();
        TextView myTV = (TextView) switcher.findViewById(R.id.profile_name);
        EditText et=(EditText)findViewById(R.id.hidden_edit_view);
        String newName = et.getText().toString();
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
        startActivity(fullview);
    }

    // for the board view
    public enum CustomPagerEnum {

        // TODO: change this to reflect the backend

        RED(0, R.layout.view_red),
        BLUE(1, R.layout.view_blue);

        private int mTitleResId;
        private int mLayoutResId;

        CustomPagerEnum(int titleResId, int layoutResId) {
            mTitleResId = titleResId;
            mLayoutResId = layoutResId;
        }

        public int getTitleResId() {
            return mTitleResId;
        }

        public int getLayoutResId() {
            return mLayoutResId;
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
                    loadProfileText();
                    loadProfileImage();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        currUserRef.addListenerForSingleValueEvent(currUserListener);
    }

    private void loadProfileText() {
        String name = currUser.getFirstName() + " " + currUser.getLastName();

        TextView nameField = (TextView) findViewById(R.id.profile_name);
        nameField.setText(name);

        LinearLayout profilelayout = (LinearLayout) findViewById(R.id.profilelayout);

        for (int i = 0; i < topics.size(); i++) {

            Button btn = new Button(this);
            //btn.setWidth();
            //btn.setHeight(20);
            btn.setText(topics.get(i));

            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    FragmentManager fm = getFragmentManager();
                    DialogFragmentProfile dialogFragment = new DialogFragmentProfile ();
                    Bundle args = new Bundle();
                    dialogFragment.setArguments(args);
                    dialogFragment.show(fm, "Do you want to delete this topic?");
                    System.out.println(dialogFragment.getActivity());
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
        // TODO: load images from database
        mImageView = (ImageView) findViewById(R.id.profile_image);
        Log.d(TAG, "userID " + authHelper.thisUserID);
        // Load the image using Glide
        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(storageHelper.getProfileImageRef(authHelper.thisUserID))
                .asBitmap()
                .into(mImageView);
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
            CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(customPagerEnum.getLayoutResId(), collection, false);
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return CustomPagerEnum.values().length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
            return mContext.getString(customPagerEnum.getTitleResId());
        }

        public void addResult(String inputText) {
            String result = inputText;
            topics.add(result);
        }

        public void removeResult() {

        }

    }

}