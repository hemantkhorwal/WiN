package com.pubg.mobile.update.leaks;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.ArrayList;
import java.util.List;


public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Object> postMetas;
    private static final int POST_TYPE = 0;
    private static final int AD_TYPE = 1;
    Context context;

    public Adapter(List<Object> postMetas, Context context) {
        this.postMetas = postMetas;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case AD_TYPE:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.native_ad,
                        parent, false);
                return new NativeAdViewHolder(unifiedNativeLayoutView);
            case POST_TYPE:
                //Go to default which is post Holder;
            default:
                View postView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.list_item, parent, false);
                return new PostHolder(postView);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final int viewType = getItemViewType(position);

        switch (viewType) {
            case AD_TYPE:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) postMetas.get(position);
                populateNativeAdView(nativeAd, ((NativeAdViewHolder) holder).getAdView());
                break;
            case POST_TYPE:
                //Always go to default which is post holder.
            default:
                PostHolder postHolder = (PostHolder) holder;
                PostMeta p = (PostMeta) postMetas.get(position);

                postHolder.headline.setText(p.getHeadline());
                postHolder.metaDesc.setText(p.getMetaDesc());
                postHolder.metaTitle.setText(p.getMetaTitle());
                Glide.with(context).load(p.getImgUrl()).into(postHolder.headImage);
                Glide.with(context).load(p.getPostTypeImgUrl()).into(postHolder.typeImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewType==POST_TYPE){
                    Intent openPost = new Intent(context,Post.class);
                    openPost.putExtra("URL",((PostMeta) postMetas.get(position)).getPostUrl());
                    openPost.putExtra("TITLE",((PostMeta) postMetas.get(position)).getMetaTitle());
                    openPost.putExtra("TYPE",((PostMeta) postMetas.get(position)).getType());
                    context.startActivity(openPost);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItem = postMetas.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return AD_TYPE;
        }
        return POST_TYPE;
    }

    @Override
    public int getItemCount() {
        return postMetas.size();

    }

    public class PostHolder extends RecyclerView.ViewHolder {
        TextView headline, metaTitle, metaDesc;
        ImageView headImage, typeImage;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            headline = itemView.findViewById(R.id.headline_text);
            metaTitle = itemView.findViewById(R.id.post_meta_title);
            metaDesc = itemView.findViewById(R.id.post_meta_desc);
            headImage = itemView.findViewById(R.id.post_head_img);
            typeImage = itemView.findViewById(R.id.post_type_logo);
        }

    }


    public void addNewData(List<Object> p) {
        int intialSize = postMetas.size();
        postMetas.addAll(p);
        Log.d("AdapterLog","initial Size ="+intialSize);
        notifyItemRangeChanged(intialSize, p.size());
    }

    public void refresh(){
        notifyDataSetChanged();
    }


    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        //((CardView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }
}
