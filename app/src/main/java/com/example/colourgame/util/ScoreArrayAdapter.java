package com.example.colourgame.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.colourgame.app.R;
import com.example.colourgame.model.ScoreDataContract;

import java.util.Comparator;
import java.util.List;

public class ScoreArrayAdapter extends ArrayAdapter<ScoreDataContract> {
    private final Context context;

    private List<ScoreDataContract> data;
    private ViewHolder viewHolder;

    static class ViewHolder {
        public TextView idTextView;
        public TextView nameTextView;
        public TextView scoreTextView;
    }

    public ScoreArrayAdapter(Context context, List<ScoreDataContract> data) {
        super(context, R.layout.activity_score, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        viewHolder = new ViewHolder();

        viewHolder.idTextView = (TextView)  rowView.findViewById(R.id.id);
        viewHolder.nameTextView = (TextView) rowView.findViewById(R.id.name);
        viewHolder.scoreTextView = (TextView) rowView.findViewById(R.id.score);

        viewHolder.idTextView.setText(String.valueOf(position+1));
        viewHolder.nameTextView.setText(data.get(position).getName());
        viewHolder.scoreTextView.setText(String.valueOf(data.get(position).getScore()));
        rowView.setTag(viewHolder);

        return rowView;
    }

    @Override
    public void notifyDataSetChanged() {
        this.setNotifyOnChange(false);

        this.sort(new Comparator<ScoreDataContract>() {
            @Override
            public int compare(ScoreDataContract data1, ScoreDataContract data2) {
                return Double.compare(data2.getScore(), data1.getScore());
            }
        });

        this.setNotifyOnChange(true);
    }
}
