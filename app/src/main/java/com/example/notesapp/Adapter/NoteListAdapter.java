package com.example.notesapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.Interface.NotesClickListener;
import com.example.notesapp.Model.Notes;
import com.example.notesapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoteListAdapter extends RecyclerView.Adapter<NotesViewHolder> {

    Context context;
    List<Notes> notes;
    NotesClickListener listener;

    public NoteListAdapter(Context context, List<Notes> notes, NotesClickListener listener) {
        this.context = context;
        this.notes = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.title.setText(notes.get(position).getTitle());
        holder.note.setText(notes.get(position).getNotes());
        holder.date.setText(notes.get(position).getDate());
        holder.date.setSelected(true);
        if (notes.get(position).isPinned()){
            holder.pin.setImageResource(R.drawable.pin);
        }else {
            holder.pin.setImageResource(0);
        }
        int color_code = getRandomColor();
        holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(color_code));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(notes.get(holder.getAdapterPosition()));
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(notes.get(holder.getAdapterPosition()),holder.cardView);
                return true;
            }
        });

    }

    private int getRandomColor(){
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.color1);
        colorCode.add(R.color.color2);
        colorCode.add(R.color.color3);
        colorCode.add(R.color.color4);
        colorCode.add(R.color.color5);
        colorCode.add(R.color.color6);
        colorCode.add(R.color.color7);
        colorCode.add(R.color.color8);
        colorCode.add(R.color.color9);

        Random random = new Random();
        int random_color = random.nextInt(colorCode.size());
        return colorCode.get(random_color);

    }
    @Override
    public int getItemCount() {
        return notes.size();
    }
    public void filterList(List<Notes> filterlist){
        notes = filterlist;
        notifyDataSetChanged();
    }
}

class NotesViewHolder extends RecyclerView.ViewHolder {

    CardView cardView;
    TextView note,title,date;
    ImageView pin;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.note_containter);
        note = itemView.findViewById(R.id.notestxt);
        title = itemView.findViewById(R.id.titletxt);
        date = itemView.findViewById(R.id.datetxt);
        pin = itemView.findViewById(R.id.pin);

    }
}