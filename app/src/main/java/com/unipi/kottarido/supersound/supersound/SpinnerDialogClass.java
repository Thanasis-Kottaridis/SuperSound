package com.unipi.kottarido.supersound.supersound;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

public class SpinnerDialogClass extends AppCompatDialogFragment {
    private Spinner spinner;
    private String Title;
    private long songID;
    private List<String> musicPreferences;
    private DialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Title = getArguments().getString(AddSongsActivity.BUNDLE_CODE_TITLE);
        musicPreferences = getArguments().getStringArrayList(AddSongsActivity.BUNDLE_CODE_PREFERENCES_NAME);
        songID = getArguments().getLong(AddSongsActivity.BUNDLE_CODE_SONG_ID);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_spinner_dialog,null);
        spinner = view.findViewById(R.id.SongKindSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,musicPreferences);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



        builder.setView(view)
                .setTitle(Title)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String songKind = spinner.getSelectedItem().toString();
                        listener.applyText(new String []{songKind}, songID);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            //throw new ClassCastException(context.toString() + "Must implement ExampleDialogListener");
        }
    }


    public void setTitle(String title){
        Title = title;
    }

    public void setMusicPreferences(List<String> musicPreferences) {
        this.musicPreferences = musicPreferences;
    }

    //ftiaxnw ena functional interface
    public interface DialogListener{
        void applyText(String [] Answers , long songID);
    }
}
