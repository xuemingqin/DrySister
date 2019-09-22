package com.example.drysister;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.drysister.Bean.API.SisterApi;
import com.example.drysister.Bean.Sister;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button showBtn;
    private Button refreshbtn;
    private ImageView showImg;

    private ArrayList<Sister> data;
    private int curPos = 0;
    private int page = 1;
    private PictureLoader loader;
    private SisterApi sisterApi;
    private SisterTask sisterTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sisterApi = new SisterApi();
        loader = new PictureLoader();
        initData();
        initUI();

    }

    private void initUI() {
        showBtn = findViewById(R.id.btn_sister);
        showImg = findViewById(R.id.image_sister);
        refreshbtn = findViewById(R.id.btn_refresh);
        showBtn.setOnClickListener(this);
        refreshbtn.setOnClickListener(this);
    }

    private void initData() {
        data = new ArrayList<Sister>();
        sisterTask = new SisterTask();
        sisterTask.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sister:
                if (data != null && !data.isEmpty()) {
                    if (curPos > 9) {
                        curPos = 0;
                    }
                  loader.load(showImg,data.get(curPos).getUrl());
                    curPos++;
                }
                break;
            case R.id.btn_refresh:
                sisterTask = new SisterTask();
                sisterTask.execute();
                curPos=0;
                break;
        }

    }
    class SisterTask extends AsyncTask<Void,Void, ArrayList<Sister>>{
        public SisterTask(){
        }


        @Override
        protected ArrayList<Sister> doInBackground(Void... voids) {
            return sisterApi.fetchSister(10,page);
        }

        @Override
        protected void onPostExecute(ArrayList<Sister> sisters) {
            super.onPostExecute(sisters);
            data.clear();
            data.addAll(sisters);
            page++;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            sisterTask=null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sisterTask.cancel(true);
    }
}