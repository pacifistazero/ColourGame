package com.example.colourgame.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.colourgame.app.R;
import com.example.colourgame.model.ScoreDataContract;

public class ScoreArrayAdapter extends ArrayAdapter<String> {
    private final Context context;

    private DatabaseHandler databaseHandler;

    static  class ViewHolder {
        public TextView nameTextView;
        public TextView scoreTextView;
    }

    public ScoreArrayAdapter(Context context) {
        super(context, R.layout.activity_score);
        this.context = context;

        databaseHandler = new DatabaseHandler(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.nameTextView = (TextView) rowView.findViewById(R.id.name);
        viewHolder.scoreTextView = (TextView) rowView.findViewById(R.id.score);
        for (ScoreDataContract score : databaseHandler.getAllScores()) {
            viewHolder.nameTextView.setText(score.getName());
            viewHolder.scoreTextView.setText("SCORE : " + score.getScore());
        }

        return rowView;
    }
}
