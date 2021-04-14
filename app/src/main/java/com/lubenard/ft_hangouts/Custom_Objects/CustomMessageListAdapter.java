package com.lubenard.ft_hangouts.Custom_Objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lubenard.ft_hangouts.R;

import java.util.ArrayList;

public class CustomMessageListAdapter extends ArrayAdapter<MessageModel> {

    // View lookup cache
    private static class ViewHolder {
        TextView messageContent;
    }

    public CustomMessageListAdapter(ArrayList<MessageModel> data, Context context) {
        super(context, R.layout.custom_contact_list_element, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MessageModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            if (dataModel.getDirection() == 0)
                convertView = inflater.inflate(R.layout.contact_message, parent, false);
            else
                convertView = inflater.inflate(R.layout.my_messages, parent, false);

            viewHolder.messageContent = convertView.findViewById(R.id.messageTextView);
            viewHolder.messageContent.setText(dataModel.getContent());

        }
        return convertView;
    }
}
