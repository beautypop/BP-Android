package com.beautypop.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.viewmodel.InstagramVM;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * http://blog.sqisland.com/2014/12/recyclerview-grid-with-header.html
 * https://github.com/chiuki/android-recyclerview
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.FeedViewHolder> {

	private static final int ITEM_VIEW_TYPE_HEADER = 0;
	private static final int ITEM_VIEW_TYPE_ITEM = 1;

	private View headerView;

	private SparseBooleanArray selectedItems = new SparseBooleanArray();

	private Activity activity;

	private List<InstagramVM> items;


	public ImageAdapter(Activity activity, List<InstagramVM> items) {
		this.activity = activity;
		this.items = items;
	}


	public boolean hasHeader() {
		return headerView != null;
	}

	public boolean isHeader(int position) {
		if (hasHeader()) {
			return position == 0;
		}
		return false;
	}

	public InstagramVM getItem(int position) {
		if (hasHeader()) {
			return items.get(position - 1);
		}
		return items.get(position);
	}

	@Override
	public int getItemCount() {
		if (hasHeader()) {
			return items.size() + 1;
		}
		return items.size();
	}

	@Override
	public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == ITEM_VIEW_TYPE_HEADER) {
			return new FeedViewHolder(headerView);  // Dummy!! This will not be binded!!
		}

		View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view_item, parent, false);
		FeedViewHolder holder = new FeedViewHolder(layoutView);
		return holder;
	}

	@Override
	public void onBindViewHolder(final FeedViewHolder holder, final int position) {

		final InstagramVM item = getItem(position);

		Picasso.with(activity).load(item.getUrl()).into(holder.image);
		holder.caption.setText(item.getCaption());

		holder.itemLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (selectedItems.get(position, false)) {
					selectedItems.delete(position);
					holder.tickImage.setVisibility(View.GONE);


				} else {
					selectedItems.put(position, true);
					holder.tickImage.setVisibility(View.VISIBLE);
				}

				for(int i=0; i<selectedItems.size(); i++){

					System.out.println("item position :: "+i);
					System.out.println("item value :: "+selectedItems.get(i));
				}
			}
		});

	}

	@Override
	public int getItemViewType(int position) {
		return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
	}


	/**
	 * View item.
	 */
	class FeedViewHolder extends RecyclerView.ViewHolder {
		ImageView image, tickImage;
		TextView caption;
		LinearLayout itemLayout;

		public FeedViewHolder(View holder) {
			super(holder);

			itemLayout = (LinearLayout) holder.findViewById(R.id.itemLayout);
			image = (ImageView) holder.findViewById(R.id.image);
			tickImage = (ImageView) holder.findViewById(R.id.tickImage);
			caption = (TextView) holder.findViewById(R.id.captionText);
		}
	}

	public SparseBooleanArray getSelectedIds() {
		return selectedItems;
	}

}