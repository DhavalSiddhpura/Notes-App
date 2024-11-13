package com.example.notesapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notesapp.Model.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesTakeActivity extends AppCompatActivity {

    EditText title,note;
    ImageView savebtn;
    Notes notes;
    boolean isOldNotes = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes_take);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        title = findViewById(R.id.title_edt);
        note = findViewById(R.id.note_edt);
        savebtn = findViewById(R.id.savebtn);
        notes = new Notes();
        try {
           notes = (Notes) getIntent().getSerializableExtra("old_notes");
           title.setText(notes.getTitle());
           note.setText(notes.getNotes());
           isOldNotes = true;
        }catch (Exception e){
            e.printStackTrace();
        }

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isOldNotes) {
                    notes = new Notes();
                }
                String titletxt = title.getText().toString();
                String notetxt = note.getText().toString();
                if(titletxt.isEmpty() || notetxt.isEmpty()){
                    Toast.makeText(NotesTakeActivity.this, "Please enter some data", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                Date date = new Date();

                notes.setTitle(titletxt);
                notes.setNotes(notetxt);
                notes.setDate(formatter.format(date));

                Intent i = new Intent();
                i.putExtra("note",notes);
                setResult(Activity.RESULT_OK,i);
                finish();
            }
        });
    }
}