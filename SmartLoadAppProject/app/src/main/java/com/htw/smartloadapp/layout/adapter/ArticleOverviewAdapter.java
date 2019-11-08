package com.htw.smartloadapp.layout.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.htw.smartloadapp.R;
import com.htw.smartloadapp.webservice.restservice.model.ArticleOverviewModel;

import java.util.List;

public class ArticleOverviewAdapter extends ArrayAdapter {
    private static final String TAG = "ArticleOverviewAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<ArticleOverviewModel> articleOverviewModelList;

    public ArticleOverviewAdapter(Context context, int resource, List<ArticleOverviewModel> articleOverviewModelList) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.articleOverviewModelList = articleOverviewModelList;
    }

    @Override
    public ArticleOverviewModel getItem(int position) {
        return articleOverviewModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return articleOverviewModelList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null){
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            // set tags
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.txt_itemArticleDetail, viewHolder.txtArticleDetail);
            convertView.setTag(R.id.cb_itemArticleDetail, viewHolder.cbArticleDetail);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.cbArticleDetail.setTag(position);

        ArticleOverviewModel currentArticleOverview = articleOverviewModelList.get(position);

        viewHolder.txtArticleDetail.setText(currentArticleOverview.toString());
        viewHolder.cbArticleDetail.setChecked(currentArticleOverview.isChecked());


        viewHolder.cbArticleDetail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                articleOverviewModelList.get(position).setChecked(isChecked);
            }
        });

        viewHolder.txtArticleDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // implementation here, in case text needs to be clickable
                Log.d(TAG, "onClick: " + position);
            }
        });

        return convertView;
    }


    private class ViewHolder {
        final TextView txtArticleDetail;
        final CheckBox cbArticleDetail;

        ViewHolder(View v){
            this.txtArticleDetail = (TextView) v.findViewById(R.id.txt_itemArticleDetail);
            this.cbArticleDetail = (CheckBox) v.findViewById(R.id.cb_itemArticleDetail);
        }
    }
}
