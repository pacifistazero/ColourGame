package com.example.colourgame.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.colourgame.model.CardDataModel;
import com.example.colourgame.model.ScoreDataContract;
import com.example.colourgame.util.DatabaseHandler;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    private static int ROW_COUNT = -1;
    private static int COL_COUNT = -1;
    private Drawable backImage;
    private int[][] cards;
    private List<Drawable> images;
    private CardDataModel firstCard;
    private CardDataModel secondCard;
    private ButtonListener buttonListener;

    private Intent starterIntent;

    private static Object lock = new Object();

    private LinearLayout mainView;
    private TextView matchTextView;
    private TextView numberOfTryTextView;

    private UpdateCardsHandler handler;
    private DatabaseHandler db;

    private static int MAXTRY = 4;
    private static final int MAXSCORE = 800;
    private int score = 0;
    private int numberOfFailed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        starterIntent = getIntent();

        handler = new UpdateCardsHandler();

        loadImages();

        backImage = getResources().getDrawable(R.drawable.card_bg);

        buttonListener = new ButtonListener();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mainView = (LinearLayout) findViewById(R.id.GameLayout);

        matchTextView = (TextView) findViewById(R.id.matchTextView);
        numberOfTryTextView = (TextView)findViewById(R.id.numberOfTryTextView);

        db = new DatabaseHandler(this);

        //Construct a game
        newGame(4, 4);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_restart:
                recreate();
                return true;
            case R.id.action_score:
                Intent k = new Intent(this, ScoreActivity.class);
                startActivity(k);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void newGame(int c, int r) {
        ROW_COUNT = r;
        COL_COUNT = c;

        cards = new int[ROW_COUNT][COL_COUNT];

        matchTextView.setText("SCORE : " + score);
        numberOfTryTextView.setText("TRY : " + MAXTRY);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for (int y = 0; y < ROW_COUNT; y++) {
            linearLayout.addView(createView(y));
        }
        mainView.addView(linearLayout);

        firstCard = null;
        loadCards();
    }

    private void loadImages() {
        images = new ArrayList<Drawable>();

        images.add(getResources().getDrawable(R.drawable.colour1));
        images.add(getResources().getDrawable(R.drawable.colour2));
        images.add(getResources().getDrawable(R.drawable.colour3));
        images.add(getResources().getDrawable(R.drawable.colour4));
        images.add(getResources().getDrawable(R.drawable.colour5));
        images.add(getResources().getDrawable(R.drawable.colour6));
        images.add(getResources().getDrawable(R.drawable.colour7));
        images.add(getResources().getDrawable(R.drawable.colour8));
    }

    private void loadCards() {
        try {
            int size = ROW_COUNT * COL_COUNT;
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < size; i++) {
                list.add(i);
            }

            // put card randomly
            Random r = new Random();
            for (int i = size - 1; i >= 0; i--) {
                int t = 0;
                if (i > 0) {
                    t = r.nextInt(i);
                }

                t = list.remove(t);
                cards[i % COL_COUNT][i / COL_COUNT] = t % (size / 2);
            }
        } catch (Exception e) {
            Log.e("loadCards()", e + "");
        }
    }

    private View createView(int y) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        for (int x = 0; x < COL_COUNT; x++) {
            linearLayout.addView(createImageButton(x, y));
        }

        return linearLayout;
    }

    private View createImageButton(int x, int y) {
        Button button = new Button(this);
        button.setBackgroundDrawable(backImage);
        button.setId(100 * x + y);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        button.setOnClickListener(buttonListener);
        return button;
    }

    class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            synchronized (lock) {
                if (firstCard != null && secondCard != null) {
                    return;
                }
                int id = v.getId();
                int x = id / 100;
                int y = id % 100;
                turnCard((Button) v, x, y);
            }
        }

        private void turnCard(Button button, int x, int y) {
            button.setBackgroundDrawable(images.get(cards[x][y]));

            if (firstCard == null) {
                firstCard = new CardDataModel(button, x, y);
            } else {

                // Check if user pick the same card
                if (firstCard.x == x && firstCard.y == y) {
                    return;
                }

                secondCard = new CardDataModel(button, x, y);
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            synchronized (lock) {
                                handler.sendEmptyMessage(0);
                            }
                        } catch (Exception e) {
                            Log.e("E1", e.getMessage());
                        }
                    }
                };

                // Delay to see the card
                Timer t = new Timer(false);
                t.schedule(timerTask, 1000);
            }
        }
    }

    class UpdateCardsHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            synchronized (lock) {
                checkCards();
            }
        }

        public void checkCards() {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            final EditText inputName = new EditText(MainActivity.this);
            alertDialogBuilder.setView(inputName);

            if (cards[secondCard.x][secondCard.y] == cards[firstCard.x][firstCard.y]) {
                firstCard.button.setVisibility(View.INVISIBLE);
                secondCard.button.setVisibility(View.INVISIBLE);
                score += 100;
                matchTextView.setText("SCORE : " + score);

                // remove card from array
                cards[firstCard.x][firstCard.y] = -1;
                if (score == MAXSCORE) {
                    // set title
                    alertDialogBuilder.setTitle("Congratulation! You Won!");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Please Enter Your Name")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Random r = new Random();
                                    // Store it into database
                                    db.addScore(new ScoreDataContract(r.nextInt(), inputName.getText().toString(), score));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            } else {
                secondCard.button.setBackgroundDrawable(backImage);
                firstCard.button.setBackgroundDrawable(backImage);

                numberOfFailed+=1;
                score -= 100;
                matchTextView.setText("SCORE : " + score);
                numberOfTryTextView.setText("TRY : " + (MAXTRY - numberOfFailed));
                if(numberOfFailed == MAXTRY){
                    // set title
                    alertDialogBuilder.setTitle("Sorry! You Lose!");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Please Enter Your Name")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Random r = new Random();
                                    // Store it into database
                                    db.addScore(new ScoreDataContract(r.nextInt(), inputName.getText().toString(), score));
                                    recreate();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            }

            firstCard = null;
            secondCard = null;
        }
    }
}
