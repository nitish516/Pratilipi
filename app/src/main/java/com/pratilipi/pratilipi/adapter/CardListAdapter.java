package com.pratilipi.pratilipi.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pratilipi.pratilipi.AppController;
import com.pratilipi.pratilipi.DataFiles.Metadata;
import com.pratilipi.pratilipi.DetailPageActivity;
import com.pratilipi.pratilipi.R;
import com.pratilipi.pratilipi.ReadActivity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by MOHIT KHAITAN on 17-06-2015.
 */
public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.DataViewHolder> {

    List<Metadata> metadata;
    ViewGroup mViewGroup;

    public CardListAdapter(List<Metadata> metadata){
        this.metadata = metadata;
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        this.mViewGroup = viewGroup;
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        DataViewHolder mDataViewHolder = new DataViewHolder(v);

        return mDataViewHolder;
    }

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {

        final Context context = mViewGroup.getContext();
        String lan = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("selectedLanguage", "");
        Long lanId = null;
        if(lan.equalsIgnoreCase("hi"))
            lanId = 5130467284090880l;
        else if(lan.equalsIgnoreCase("ta"))
            lanId = 6319546696728576l;
        else if(lan.equalsIgnoreCase("gu"))
            lanId = 5965057007550464l;
        Typeface typeFace = null;
        if(lan.equalsIgnoreCase("hi"))
            typeFace= Typeface.createFromAsset(context.getAssets(), "fonts/devanagari.ttf");
        else if(lan.equalsIgnoreCase("ta"))
            typeFace= Typeface.createFromAsset(context.getAssets(), "fonts/tamil.ttf");
        else if(lan.equalsIgnoreCase("gu"))
            typeFace= Typeface.createFromAsset(context.getAssets(), "fonts/gujarati.ttf");

        holder.bookTitle.setTypeface(typeFace);
        holder.authorName.setTypeface(typeFace);
        final Metadata metadataObj = metadata.get(position);
        holder.bookTitle.setText(metadataObj.get_title());
        holder.authorName.setText(metadataObj.get_authorFullName());
        holder.bookCover.setImageUrl("http:" + metadataObj.get_coverImageUrl(), imageLoader);
        if (metadataObj.get_ratingCount() > 0) {

            float val = (float) metadataObj.get_starCount() / metadataObj.get_ratingCount();
            if (val != 0.0) {
                holder.mRatingBar.setRating(val);

                NumberFormat numberformatter = NumberFormat.getNumberInstance();
                numberformatter.setMaximumFractionDigits(1);
                numberformatter.setMinimumFractionDigits(1);
                String rating = numberformatter.format(val);

//                holder.ratingCount.setText(String.valueOf("("+metadataObj.get_ratingCount() + " ratings)"));
                holder.avgeragerating.setText("FREE!");

            }else{
                Log.d("Val is Null", "");
            }
        }

        holder.mCardVeiw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DetailPageActivity.class);
                i.putExtra(DetailPageActivity.METADATA, (Serializable) metadataObj);
                context.startActivity(i);
            }
        });
        holder.imgOverflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(context,v);
                try {
                    Field[] fields = menu.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(menu);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                menu.getMenuInflater().inflate(R.menu.popup_menu, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popupaction1:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setCancelable(true);
                                builder.setMessage(R.string.remove_from_shelf_dialog);
                                builder.setTitle(R.string.remove_from_shelf_title);
                                builder.setPositiveButton(R.string.remove_from_shelf_positive, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(context, "Removed from Shelf !", Toast.LENGTH_LONG).show();
                                    }
                                });
                                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                });
                                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    public void onCancel(DialogInterface dialog) {
                                        return;
                                    }
                                });

                                builder.show();
                                break;
                            case R.id.addasShortcut:
                                Intent shortcutIntent = new Intent(context, ReadActivity.class);
                                shortcutIntent.setAction(Intent.ACTION_MAIN);
                                Intent intent = new Intent();
                                intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Pratilipi"); //Add Book Name here
                                intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.logo)); //Add Book Image Here
                                intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                context.sendBroadcast(intent);
                                break;
                            default:
                                return false;
                        }
                        return true;
                    }
                });
                menu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return metadata.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder{

        CardView mCardVeiw;
        TextView bookTitle;
        TextView authorName;
        NetworkImageView bookCover;
        RatingBar mRatingBar;
        TextView ratingCount;
        TextView avgeragerating;
        ImageView imgOverflowButton ;

        public DataViewHolder(View itemView) {
            super(itemView);

            mCardVeiw = (CardView)itemView.findViewById(R.id.more_card_feature);
            bookTitle = (TextView)itemView.findViewById(R.id.titleTextViewMoreFeatured);
            authorName = (TextView)itemView.findViewById(R.id.authorTextViewMoreFeatured);
            bookCover = (NetworkImageView)itemView.findViewById(R.id.detail_image);
            mRatingBar = (RatingBar)itemView.findViewById(R.id.averageRatingBarFeatured);
            ratingCount = (TextView)itemView.findViewById(R.id.featuredPageRatingNumber);
            avgeragerating = (TextView)itemView.findViewById(R.id.averageRatingTextView);
            imgOverflowButton = (ImageView) itemView.findViewById(R.id.overflow_cardlist);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
