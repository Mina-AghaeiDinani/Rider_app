package com.example.deliveryman;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NotificationDialogActivity extends AppCompatDialogFragment {

    TextView dialogTextView;
    NotificationDialogListener notificationDialogListener;
    String order_id;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        Bundle args = getArguments();
        order_id = args.getString("new_orderID");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();


        View view = inflater.inflate(R.layout.notification_dialog, null);

        dialogTextView = view.findViewById(R.id.dialog_textview);

        dialogTextView.setText("New course received. Accept?");

        builder.setView(view)
                .setTitle("A new course for you!")
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notificationDialogListener.declineOrder(order_id);
                    }
                })
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notificationDialogListener.acceptOrder(order_id);
                    }
                });

        return builder.create();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            notificationDialogListener = (NotificationDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+
                    "must implement NotificationDialogListener");
        }

    }


    public interface NotificationDialogListener{
        void acceptOrder(String order);
        void declineOrder(String order);
    }
}
