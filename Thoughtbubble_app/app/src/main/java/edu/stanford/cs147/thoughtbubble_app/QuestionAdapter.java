package edu.stanford.cs147.thoughtbubble_app;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuestionAdapter extends ArrayAdapter<Question> {

    private String TAG = "QuestionAdapter";

    private Context context;
    int layoutResourceId;
    ArrayList<Question> data = null;

    private DatabaseHelper DBH;
    private StorageHelper storageHelper;


    public QuestionAdapter(Context context, int layoutResourceId, ArrayList<Question> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        QuestionHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new QuestionHolder();
            holder.questionContent = (TextView)row.findViewById(R.id.questionContent);
            holder.answerContent = (TextView)row.findViewById(R.id.answerContent);
            holder.profileImage = (ImageView)row.findViewById(R.id.profile_image);
            row.setTag(holder);
        }
        else
        {
            holder = (QuestionHolder)row.getTag();
        }

        System.out.println(position);
        System.out.println(data.toString());
        Question question = data.get(position);
        holder.questionContent.setText(question.questionText);
        holder.answerContent.setText(question.answerText);

        if (holder.profileImage != null) {
            final String answererID = question.answererID;
            final ImageView profileImage = holder.profileImage;
            DBH = DatabaseHelper.getInstance();
            storageHelper = storageHelper.getInstance();
            DatabaseReference answererHasProfile = DBH.users.child(answererID).child("hasProfile");
            answererHasProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean hasProfile = dataSnapshot.getValue(Boolean.class);
                    getProfileImage(hasProfile, answererID, profileImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        return row;
    }

    static class QuestionHolder
    {
        ImageView profileImage;
        TextView questionContent;
        TextView answerContent;
    }


    private void getProfileImage(Boolean hasProfile, String answererID, ImageView mImageView) {
        Log.d(TAG, "answererID " + answererID);
        // Load the image using Glide
        if (hasProfile) {
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(storageHelper.getProfileImageRef(answererID))
                    .asBitmap()
                    .into(mImageView);
        } else {
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(storageHelper.getProfileImageRef("blank-user"))
                    .asBitmap()
                    .into(mImageView);
        }

    }

}