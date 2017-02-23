package com.sdaacademy.jawny.daniel.httpdropbox2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String DROP_BOX = "DropBox";

    @BindView(R.id.id)
    EditText mId;

    @BindView(R.id.first_name)
    TextView mFirstName;

    @BindView(R.id.last_name)
    TextView mLastName;

    @BindView(R.id.email)
    TextView mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mId.setText("dbid:AAA2pzGeWQFSaru1XugrFWamf8YMWDTO314");
    }

    @OnClick(R.id.get_response)
    public void getResponse() {
        if (!mId.getText().toString().isEmpty()) {
            new dropBoxCommunicationTask().execute();
        }
    }

    public class dropBoxCommunicationTask extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... params) {
            String response = "null";
            try {
                response = sentPost();
            } catch (IOException e) {
                Log.i(DROP_BOX, "Bląd komunikacji dropBox");
            } catch (JSONException e) {
                Log.i(DROP_BOX, "Zle id");
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i(DROP_BOX, s);

            try {
                displayResponse(s);
            } catch (JSONException e) {
                Log.i(DROP_BOX, "Problem z zamianą ze stringa na jasona");
            }
        }

        private void displayResponse(String s) throws JSONException {
            JSONObject json = new JSONObject(s);
            mFirstName.setText(json.getJSONObject("name").getString("given_name"));
            mLastName.setText(json.getJSONObject("name").getString("surname"));
            mEmail.setText(json.getString("email"));
        }

        private String sentPost() throws IOException, JSONException {
            MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            String url = "https://api.dropboxapi.com/2/users/get_account";
            JSONObject json = new JSONObject();
            json.put("account_id", mId.getText().toString());
            String jsonStr = json.toString();

            RequestBody body = RequestBody.create(jsonMediaType, jsonStr);
            Request request = new Request.Builder()
                    .addHeader("Authorization", "Bearer 3TS3KjVdr6AAAAAAAAAAE2Lr1QU0h9tZMtJwYpyDaxF_jzFdvrvHjjSz7OGmz9UL")
                    .addHeader("Content-Type", "application/json")
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }
}
