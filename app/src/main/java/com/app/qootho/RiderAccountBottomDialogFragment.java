package com.app.qootho;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.ListView;

/**
 * Created by MG on 17-07-2016.
 */
public class RiderAccountBottomDialogFragment extends BottomSheetDialogFragment {

    String mString;

    private ListView mapListView;
    private BottomSheetBehavior mBehavior;

    static RiderAccountBottomDialogFragment newInstance(String string) {
        RiderAccountBottomDialogFragment f = new RiderAccountBottomDialogFragment();
        Bundle args = new Bundle();
        args.putString("string", string);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mString = getArguments().getString("string");

    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.layout_search_map, null);
        mapListView = (ListView) view.findViewById(R.id.listView);

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }


    public interface MyDialogFragmentListener {
        void onReturnValue(String foo);
    }
}
