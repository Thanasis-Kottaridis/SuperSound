package com.unipi.kottarido.supersound.supersound;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class TextViewDialogClass extends AppCompatDialogFragment {

    private EditText CustomDialogText;
    private String Title;
    private DialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //kali ton builder gia na ftia3ei ena alertDialog
        //kai tou pernaei san context to activity apo to opoio klithike
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //kanei inflate to view pou ftia3ame gia to custom dialog
        //to inflate ginete me 2 tropous
        //1 opos ginete se kathe oncreate activity pou pernei to activity to layout tou xml tou
        //2 opos to kanoume twra pou ftiaxnoume ena view kai tou leme tha paris to layout tou sigkekrimenou xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_text_view_dialog,null);

        //vriskei to textbox sto view pou ftia3ame
        //to opoio view kanei inflate to layout tou custom dialog
        CustomDialogText = view.findViewById(R.id.CustomDialogText);

        //vazei ston bulder to view tou custom dialog
        //orizei ton titlo tou
        //kai ti tha kanei sto patima kathe koumpio tou (Send = Positive) (Cancel = negative)
        builder.setView(view).
                setTitle(Title)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //den tha kanei tipota apla klinei to dialog
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String UserText = CustomDialogText.getText().toString();
                        listener.applyText(UserText);
                    }
                });

        return builder.create();
    }

    //elenxos gia to an i class pou kalei tin auti ti class exei kanei implement to interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement ExampleDialogListener");
        }
    }

    public void setTitle(String title){
        Title = title;
    }

    public interface DialogListener{
        void applyText(String Answer);
    }

}
