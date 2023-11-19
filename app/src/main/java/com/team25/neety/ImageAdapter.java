package com.team25.neety;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<String> imageUrls;
    private Context context;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public ImageAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        Set<String> uniqueImageUrls = new HashSet<>(imageUrls);
        this.imageUrls = new ArrayList<>(uniqueImageUrls);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;

        Picasso.get()
                .load(imageUrl)
                .resize(width, 0)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Image loaded successfully
                    }

                    @Override
                    public void onError(Exception e) {
                        // Error occurred while loading image
                        Log.e("ImageAdapter", "Error loading image", e);
                    }
                });

        holder.imageDelButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Image")
                        .setMessage("Are you sure you want to delete this image?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Continue with delete operation
                            StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
                            imageRef.delete().addOnSuccessListener(aVoid -> {
                                // Image deleted successfully
                                imageUrls.remove(position);
                                notifyItemRemoved(position);
                            }).addOnFailureListener(e -> {
                                // Error occurred while deleting image
                                Log.e("ImageAdapter", "Error deleting image", e);
                            });
                        })
                        .setNegativeButton("No", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton imageDelButton;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.imageDelButton = itemView.findViewById(R.id.image_del_button);
        }
    }
}