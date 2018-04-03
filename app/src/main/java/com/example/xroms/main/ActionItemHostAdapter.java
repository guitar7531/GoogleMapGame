package com.example.xroms.main;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter to bind a ToDoItem List to a view
 */
public class ActionItemHostAdapter extends ArrayAdapter<ToDoItem> {

    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public ActionItemHostAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    /**
     * Returns the view for a specific item on the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final ToDoItem currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);
        final TextView textView = (TextView) row.findViewById(R.id.hostAdapterActionItem);
        textView.setText(currentItem.getId());
        textView.setEnabled(true);

        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //if (textView.isPressed()) {
                    textView.setEnabled(false);
                    if (mContext instanceof HostActivity) {
                        HostActivity activity = (HostActivity) mContext;
                        activity.checkItem(currentItem);
                    }
                //}
            }
        });

        return row;
    }

}