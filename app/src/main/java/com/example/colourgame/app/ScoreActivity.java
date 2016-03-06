package com.example.colourgame.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.example.colourgame.util.ScoreArrayAdapter;

public class ScoreActivity extends Activity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // Set adapter
        ScoreArrayAdapter adapter = new ScoreArrayAdapter(this);
        listView = (ListView)findViewById(R.id.scoreListView);
        listView.setAdapter(adapter);
    }
}
