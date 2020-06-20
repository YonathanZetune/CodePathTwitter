package com.codepath.apps.restclienttemplate;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends DialogFragment implements TextView.OnEditorActionListener {

    public final static int MAX_TWEET_LENGTH = 280;
    public final static String TAG = "ComposeFragment";


    EditText composeET;
    Button tweetButton;
    TwitterClient client;
    TextView charCountTV;

    public interface ComposeFragmentListener {
        void onFinishEditDialog(String inputText);
    }

    public ComposeFragment() {

    }

    public static ComposeFragment newInstance(String title, boolean isReply) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();

        args.putString("handle", title);
        args.putBoolean("isReply", isReply);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.compose_fragment, container);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        composeET = view.findViewById(R.id.composeET);
        tweetButton = view.findViewById(R.id.buttonTweet);
        client = new TwitterClient(getContext());
        charCountTV = view.findViewById(R.id.charCountTV);
        charCountTV.setText(String.valueOf(MAX_TWEET_LENGTH));
        composeET.setOnEditorActionListener(this);
        composeET.setText(getArguments().getString("handle"));

        composeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i(TAG, "Text Changed On");
                int bodyLength = charSequence.length();
                int newCount = MAX_TWEET_LENGTH - bodyLength;
                charCountTV.setText(String.valueOf(newCount));
                if (newCount < 0) {
                    charCountTV.setTextColor(getResources().getColor(R.color.medium_red));
                } else if (newCount < 280 && newCount >= 0) {
                    charCountTV.setTextColor(getResources().getColor(R.color.black));
                    tweetButton.setTextColor(getResources().getColor(R.color.white));

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditorAction(composeET, EditorInfo.IME_ACTION_DONE, null);
                dismiss();
                //make api call to send tweet

            }
        });

        // Fetch arguments from bundle and set title
        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.MATCH_PARENT);
    }


    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            final ComposeFragmentListener listener = (ComposeFragmentListener) getActivity();


            // Close the dialog and return back to the parent activity
            listener.onFinishEditDialog(composeET.getText().toString());

            return true;
        }
        return false;
    }
}
