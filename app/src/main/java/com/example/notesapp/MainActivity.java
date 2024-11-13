package com.example.notesapp;

import static androidx.core.content.IntentCompat.getSerializableExtra;

import static java.util.Locale.filter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notesapp.Adapter.NoteListAdapter;
import com.example.notesapp.Database.RoomDb;
import com.example.notesapp.Interface.NotesClickListener;
import com.example.notesapp.Model.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    RecyclerView recyclerView;
    NoteListAdapter noteListAdapter;
    RoomDb database;
    List<Notes> notes = new ArrayList<>();
    FloatingActionButton fbtn;
    SearchView searchView;
    Notes selectedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.noteRv);
        fbtn = findViewById(R.id.addBtn);
        searchView = findViewById(R.id.searchView);
        database = RoomDb.getInstance(this);
        notes = database.mainDAO().getAll();
        updateRecycle(notes);
        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NotesTakeActivity.class);
                startActivityForResult(intent,101);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String newText){
        List<Notes> filterlist = new ArrayList<>();
        for(Notes singlenote : notes){
            if (singlenote.getTitle().toLowerCase().contains(newText.toLowerCase())
                    || singlenote.getNotes().toLowerCase().contains(newText.toLowerCase())){
                filterlist.add(singlenote);
            }
        }
        noteListAdapter.filterList(filterlist);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101){
            if (resultCode == Activity.RESULT_OK){
                Notes new_note = (Notes) data.getSerializableExtra("note");
                database.mainDAO().insert(new_note);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                noteListAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == 102){
            if (resultCode == Activity.RESULT_OK){
                Notes new_note = (Notes) data.getSerializableExtra("note");
                database.mainDAO().update(new_note.getId(),new_note.getTitle(),new_note.getNotes());
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                noteListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateRecycle(List<Notes> notes){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        noteListAdapter = new NoteListAdapter(MainActivity.this,notes,notesClickListener);
        recyclerView.setAdapter(noteListAdapter);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this,NotesTakeActivity.class);
            intent.putExtra("old_notes",notes);
            startActivityForResult(intent,102);
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote = new Notes();
            selectedNote = notes;
            showPop(cardView);
        }
    };

    private void showPop(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this,cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.pin1){
            if (selectedNote.getPinned()){
                database.mainDAO().pin(selectedNote.getId(),false);
                selectedNote.setPinned(false);
                Toast.makeText(this, "UnPinned", Toast.LENGTH_SHORT).show();
            }else {
                database.mainDAO().pin(selectedNote.getId(),true);
                selectedNote.setPinned(true);
                Toast.makeText(this, "Pinned", Toast.LENGTH_SHORT).show();
            }
            notes.clear();
            notes.addAll(database.mainDAO().getAll());
            noteListAdapter.notifyDataSetChanged();
            return true;
        } else if (item.getItemId() == R.id.delete) {
            database.mainDAO().delete(selectedNote);
            notes.remove(selectedNote);
            noteListAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Note is Deleted", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}