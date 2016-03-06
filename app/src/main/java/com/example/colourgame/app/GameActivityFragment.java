package com.example.colourgame.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.colourgame.model.Card;

import java.util.*;

public class GameActivityFragment extends Fragment {
    private static int ROW_COUNT = -1;
    private static int COL_COUNT = -1;
    private Context context;
    private Drawable backImage;
    private int[][] cards;
    private List<Drawable> images;
    private Card firstCard;
    private Card secondCard;
    private ButtonListener buttonListener;

    private static Object lock = new Object();

    private RelativeLayout mainView;
    private TextView matchTextView;
    private UpdateCardsHandler handler;
    private int matchCount;

    private ArrayList scoreList;

    public GameActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        handler = new UpdateCardsHandler();
        loadImages();

        backImage = getResources().getDrawable(R.drawable.card_bg);

        buttonListener = new ButtonListener();

        mainView = (RelativeLayout) getView().findViewById(R.id.GameLayout);
        context = mainView.getContext();

        //matchTextView = (TextView) getView().findViewById(R.id.matchTextView);

        scoreList = new ArrayList();
        //Construct a game
        newGame(4, 4);
    }

    private void newGame(int c, int r) {
        ROW_COUNT = r;
        COL_COUNT = c;

        cards = new int[ROW_COUNT][COL_COUNT];

        matchCount = 0;
        //matchTextView.setText("Match Card : " + 0);

        LinearLayout linearLayout = new LinearLayout(getContext());
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
                list.add(new Integer(i));
            }

            // put card randomly
            Random r = new Random();
            for (int i = size - 1; i >= 0; i--) {
                int t = 0;
                if (i > 0) {
                    t = r.nextInt(i);
                }

                t = list.remove(t).intValue();
                cards[i % COL_COUNT][i / COL_COUNT] = t % (size / 2);
            }
        } catch (Exception e) {
            Log.e("loadCards()", e + "");
        }
    }

    private View createView(int y){
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        for (int x = 0; x < COL_COUNT; x++) {
            linearLayout.addView(createImageButton(x, y));
        }

        return linearLayout;
    }

    private View createImageButton(int x, int y) {
        Button button = new Button(context);
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
                firstCard = new Card(button, x, y);
            } else {

                // Check if user pick the same card
                if (firstCard.x == x && firstCard.y == y) {
                    return;
                }

                secondCard = new Card(button, x, y);
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
            if (cards[secondCard.x][secondCard.y] == cards[firstCard.x][firstCard.y]) {
                firstCard.button.setVisibility(View.INVISIBLE);
                secondCard.button.setVisibility(View.INVISIBLE);
                matchCount += 1;
                //matchTextView.setText("Match Card : " + matchCount);

                // remove card from array
                cards[firstCard.x][firstCard.y] = -1;
                if (matchCount == 8) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    final EditText inputName = new EditText(context);
                    alertDialogBuilder.setView(inputName);

                    // set title
                    alertDialogBuilder.setTitle("Congratulation! You Won!");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Please Enter Your Name")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    scoreList.add(inputName.getText().toString());
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
            }

            firstCard = null;
            secondCard = null;
        }
    }
}
