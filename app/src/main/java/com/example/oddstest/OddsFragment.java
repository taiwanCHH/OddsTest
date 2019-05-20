package com.example.oddstest;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class OddsFragment extends Fragment {
    private static final String ARG_COLUMN = "column";
    private static final String ARG_ROW = "row";
    private static final long PERIOD = 10000L;
    private LinearLayout mainLayout;
    private int mColumn;
    private int mRow;
    private int displayWidth, displayHeight;
    private int boardLength = 0;

    public static Bundle setBundle(int column, int row) {
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN, column);
        args.putInt(ARG_ROW, row);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumn = getArguments().getInt(ARG_COLUMN);
            mRow = getArguments().getInt(ARG_ROW);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_odds, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        mainLayout = view.findViewById(R.id.layout);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                displayHeight = mainLayout.getHeight();
                displayWidth = mainLayout.getWidth();
                initBox();

                initView();
            }
        });
    }


    private void initBox() {
        int boxW = displayWidth / mColumn - dip2px(12);
        int boxH = displayHeight / (mRow + 1) - dip2px(12);
        boardLength = Math.min(boxH, boxW);
    }

    private void initView() {
        mainLayout.removeAllViewsInLayout();

        for (int i = 0; i < mColumn; i++) {
            LinearLayout columnLayout = new LinearLayout(getActivity());
            columnLayout.setTag("column_" + i);
            columnLayout.setEnabled(false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(boardLength + dip2px(4), LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(dip2px(2), dip2px(2), dip2px(2), dip2px(2));
            columnLayout.setLayoutParams(layoutParams);
            columnLayout.setOrientation(LinearLayout.VERTICAL);
            columnLayout.setBackgroundResource(R.drawable.column_bg);
            mainLayout.addView(columnLayout);

            for (int j = 0; j < mRow; j++) {
                TextView item = new TextView(getContext());
                item.setTag("tag_" + i + "_" + j);
                LinearLayout.LayoutParams layoutParamsText = new LinearLayout.LayoutParams(boardLength, boardLength);
                layoutParamsText.gravity = Gravity.CENTER;
                layoutParamsText.setMargins(dip2px(2), dip2px(2), dip2px(2), dip2px(2));
                item.setLayoutParams(layoutParamsText);
                item.setGravity(Gravity.CENTER);
                item.setBackgroundResource(R.drawable.btn_bg);
                item.setTextColor(Color.BLACK);
                columnLayout.addView(item);
            }

            TextView item = new TextView(getContext());
            item.setTag("btn_" + i);
            LinearLayout.LayoutParams layoutParamsText = new LinearLayout.LayoutParams(boardLength, boardLength);
            layoutParamsText.gravity = Gravity.CENTER;
            layoutParams.setMargins(dip2px(2), dip2px(2), dip2px(2), dip2px(2));
            item.setLayoutParams(layoutParamsText);
            item.setGravity(Gravity.CENTER);
            item.setText(getString(R.string.btn_text));
            item.setBackgroundResource(R.drawable.btn_bg);
            item.setTextColor(Color.BLACK);
            initTextSize(item);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.isSelected()) {
                        initView();
                    }
                }
            });
            columnLayout.addView(item);
        }


    }

    private int dip2px(float dpValue) {
        final float scale = this.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void initTextSize(TextView text) {
        while (true) {
            float textWidth = text.getPaint().measureText(text.getText().toString());
            if (textWidth > boardLength) {
                int textSize = (int) text.getTextSize();
                textSize = textSize - 2;
                text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            } else {
                break;
            }
        }
    }

    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag) {
        ArrayList<View> views = new ArrayList<>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setTarget(random(mColumn), random(mRow));

            handler.postDelayed(this, PERIOD);
        }
    };

    private int random(int range) {
        return (int) (Math.random() * range);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, PERIOD);
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    private void setTarget(int selectColumn, int selectRow) {
        initView();
        //column
        ArrayList<View> columnList = getViewsByTag(mainLayout, "column_" + selectColumn);
        for (View item : columnList) {
            item.setEnabled(true);
        }
        //btn
        ArrayList<View> btnList = getViewsByTag(mainLayout, "btn_" + selectColumn);
        for (View item : btnList) {
            item.setSelected(true);
        }
        //text
        ArrayList<View> itemList = getViewsByTag(mainLayout, "tag_" + selectColumn + "_" + selectRow);
        for (View item : itemList) {
            TextView tvItem = ((TextView) item);
            tvItem.setText(getString(R.string.odds_text));
            initTextSize(tvItem);
            item.setSelected(true);
        }

    }
}
