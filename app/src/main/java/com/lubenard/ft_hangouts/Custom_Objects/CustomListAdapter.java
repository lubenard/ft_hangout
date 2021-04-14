package com.lubenard.ft_hangouts.Custom_Objects;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.lubenard.ft_hangouts.UI.MessageFragment;
import com.lubenard.ft_hangouts.R;
import com.lubenard.ft_hangouts.Utils.Utils;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<ContactModel> {

    private ArrayList<ContactModel> dataSet;
    private Context mContext;
    private FragmentActivity activity;

    // View lookup cache
    private static class ViewHolder {
        TextView contactName;
        TextView contactPhoneNumberEmail;
        ImageView contactImageView;
        ImageButton callButton;
        ImageButton messageButton;
    }

    public CustomListAdapter(ArrayList<ContactModel> data, Context context, FragmentActivity activity) {
        super(context, R.layout.custom_contact_list_element, data);
        this.dataSet = data;
        this.mContext = context;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ContactModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_contact_list_element, parent, false);
            viewHolder.contactName = convertView.findViewById(R.id.custom_view_contactName);
            viewHolder.contactPhoneNumberEmail = convertView.findViewById(R.id.custom_view_contactPhoneNumber);
            viewHolder.callButton = convertView.findViewById(R.id.call_contact);
            viewHolder.messageButton = convertView.findViewById(R.id.message_contact);
            viewHolder.contactImageView = convertView.findViewById(R.id.custom_view_contactImage);

            if (dataModel.getIsFavourite())
                viewHolder.contactName.setTextColor(getContext().getResources().getColor(R.color.yellow));
            viewHolder.contactName.setText(dataModel.getName());

            // If no phoneNumber is supplied, print the email, else show a text saying the contact is empty
            if (!dataModel.getPhoneNumber().isEmpty()) {
                viewHolder.contactPhoneNumberEmail.setText(dataModel.getPhoneNumber());
                viewHolder.callButton.setOnClickListener(view -> {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + dataModel.getPhoneNumber()));
                        getContext().startActivity(intent);
                });
            } else if (!dataModel.getEmail().isEmpty()) {
                viewHolder.contactPhoneNumberEmail.setText(dataModel.getEmail());
                viewHolder.callButton.setImageResource(R.drawable.outline_email_24);
                viewHolder.callButton.setOnClickListener(view -> {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", dataModel.getEmail(), null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getContext().getString(R.string.contact_app));
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                    getContext().startActivity(Intent.createChooser(emailIntent, getContext().getString(R.string.send_mail)));
                });
            } else {
                viewHolder.contactPhoneNumberEmail.setText(getContext().getString(R.string.no_info_provided));
                viewHolder.callButton.setOnClickListener(view -> {
                    Toast.makeText(mContext, R.string.impossible_call_no_phone_number, Toast.LENGTH_LONG).show();
                });
            }

            viewHolder.messageButton.setOnClickListener(view -> {
                if (Utils.checkExistantPhoneNumnber(dataModel.getPhoneNumber())) {
                    MessageFragment fragment = new MessageFragment();
                    Bundle args = new Bundle();
                    args.putInt("contactId", dataModel.getId());
                    fragment.setArguments(args);
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(android.R.id.content, fragment, null)
                            .addToBackStack(null).commit();
                } else
                    Toast.makeText(mContext, R.string.impossible_message_no_phone_number, Toast.LENGTH_LONG).show();
            });

            viewHolder.contactName.setSelected(true);
            viewHolder.contactPhoneNumberEmail.setSelected(true);

            if (dataModel.getContactImage() != null)
                viewHolder.contactImageView.setImageDrawable(Drawable.createFromPath(dataModel.getContactImage()));
            else
                viewHolder.contactImageView.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.ic_menu_help));
        }
        return convertView;
    }
}
