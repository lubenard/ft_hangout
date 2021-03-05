package com.lubenard.ft_hangouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<ContactModel> implements View.OnClickListener{

    private ArrayList<ContactModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView contactName;
        TextView contactPhoneNumber;
        ImageView contactImageView;
    }

    public CustomListAdapter(ArrayList<ContactModel> data, Context context) {
        super(context, R.layout.custom_contact_list_element, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        ContactModel dataModel=(ContactModel) object;

        switch (v.getId())
        {
            // Action
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ContactModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_contact_list_element, parent, false);
            viewHolder.contactName = (TextView) convertView.findViewById(R.id.custom_view_contactName);
            viewHolder.contactPhoneNumber = (TextView) convertView.findViewById(R.id.custom_view_contactPhoneNumber);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        /*Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
*/
        viewHolder.contactName.setText(dataModel.getName());
        viewHolder.contactPhoneNumber.setText(dataModel.getPhoneNumber());
        /*viewHolder.txtVersion.setText(dataModel.getVersion_number());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);*/
        // Return the completed view to render on screen
        return convertView;
    }
}
