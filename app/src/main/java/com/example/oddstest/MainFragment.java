package com.example.oddstest;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.navigation.Navigation;

public class MainFragment extends Fragment {
    private EditText mColumn;
    private EditText mRow;

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mColumn = view.findViewById(R.id.column);
        mRow = view.findViewById(R.id.row);
        Button mBtn = view.findViewById(R.id.button);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptShowLayout();
            }
        });
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    private void attemptShowLayout() {
        mColumn.setError(null);
        mRow.setError(null);

        String strColumn = mColumn.getText().toString();
        String strRow = mRow.getText().toString();

        if (TextUtils.isEmpty(strColumn) || !isColumnValid(strColumn)) {
            mColumn.setError(getString(R.string.error_invalid_column));
            mColumn.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(strRow) || !isRowValid(strRow)) {
            mRow.setError(getString(R.string.error_invalid_row));
            mRow.requestFocus();
            return;
        }

        int column = Integer.parseInt(strColumn);
        int row = Integer.parseInt(strRow);
        Navigation.findNavController(this.getView()).navigate(R.id.action_page_odds, OddsFragment.setBundle(column, row));


    }

    private boolean isColumnValid(String strColumn) {
        try {
            int column = Integer.parseInt(strColumn);
            return column > 0 && column < 11;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    private boolean isRowValid(String strRow) {
        try {
            int row = Integer.parseInt(strRow);
            return row > 0 && row < 6;
        } catch (NumberFormatException e) {
            return false;
        }

    }
}
