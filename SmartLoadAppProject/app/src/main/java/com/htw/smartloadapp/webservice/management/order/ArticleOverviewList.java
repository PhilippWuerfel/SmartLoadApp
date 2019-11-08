package com.htw.smartloadapp.webservice.management.order;

import com.htw.smartloadapp.webservice.restservice.model.ArticleOverviewModel;

import java.util.ArrayList;

public class ArticleOverviewList {

    private static ArticleOverviewList articleOverview_instance = null;
    private ArrayList<ArticleOverviewModel> articleOverview;

    private ArticleOverviewList() {
    }

    public static ArticleOverviewList getInstance(){
        if(articleOverview_instance == null){
            articleOverview_instance = new ArticleOverviewList();
        }
        return articleOverview_instance;
    }

    public void setArticleOverview(ArrayList<ArticleOverviewModel> articleOverview) {
        this.articleOverview = articleOverview;
    }

    public ArrayList<ArticleOverviewModel> getArticleOverview() {
        return articleOverview;
    }
}
