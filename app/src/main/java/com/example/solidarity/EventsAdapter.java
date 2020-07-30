package com.example.solidarity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.solidarity.fragments.EventsFragment;
import com.parse.Parse;
import com.parse.ParseFile;

import org.parceler.Parcels;


import java.util.List;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private final int REQUEST_CODE = 20;
    private Context context;
    private List<Event> events;
    private Fragment fragment;

    public EventsAdapter(Context context, List<Event> events, Fragment fragment) {
        this.context = context;
        this.events = events;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }


    @Override
    public int getItemCount() {
        return events.size();
    }

    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Event> list) {
        events.addAll(list);
        notifyDataSetChanged();
    }

    public void addEvent(Event event) {
        events.add(event);
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private ImageView ivImage;
        private TextView tvUsername;
        private TextView tvDescription;
        private TextView tvLocation;
        private TextView tvDate;
        private CardView container;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(final Event event) {
            tvTitle.setText(event.getTitle());
            tvUsername.setText(event.getAuthor().getUsername());
            tvDescription.setText(event.getDescription());
            tvLocation.setText(event.getLocation());
            tvDate.setText(event.getEventDate().toString());

            ParseFile image = event.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, EventDetailsActivity.class);
                    intent.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));

                    fragment.startActivityForResult(intent, REQUEST_CODE);
                }
            });

        }


    }


}
