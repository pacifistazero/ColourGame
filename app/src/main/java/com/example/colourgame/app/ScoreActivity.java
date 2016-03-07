package com.example.colourgame.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import com.example.colourgame.util.DatabaseHandler;
import com.example.colourgame.util.ScoreArrayAdapter;

public class ScoreActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        setTitle("Score List");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_trophy);

        databaseHandler = new DatabaseHandler(getApplicationContext());

        ScoreArrayAdapter adapter = new ScoreArrayAdapter(this, databaseHandler.getAllScores());
        listView = (ListView)findViewById(R.id.scoreListView);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
