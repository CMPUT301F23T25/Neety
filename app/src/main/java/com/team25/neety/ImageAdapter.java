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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<String> imageUrls;
    private Context context;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    /*
     * this is the constructor for the image adapter
     * @param context
     * @param imageUrls
     */
    public ImageAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        Set<String> uniqueImageUrls = new HashSet<>(imageUrls);
        this.imageUrls = new ArrayList<>(uniqueImageUrls);
    }

    /*
     * this creates the view holder
     * @param parent
     * @param viewType
     * @return ImageViewHolder
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(itemView);
    }
    /*
     * this binds the view holder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        Glide.with(context)
                .load(imageUrl)
                .apply(new RequestOptions().override(0, 0))
                .into(holder.imageView);

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
                        .setIcon(R.drawable.alert)
                        .show();
            }
        });
    }

    /*
     * this gets the number of items in the list
     * @return int
     */
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