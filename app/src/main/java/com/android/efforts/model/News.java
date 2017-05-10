package com.android.efforts.model;

/**
 * Created by Jonathan Simananda on 09/05/2017.
 */

import java.util.Date;

public class News {

    //variables all here
    private String newsTitle;
    private String newaContent;
    private Date newsTimestamp;
    private String newsID;

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewaContent() {
        return newaContent;
    }

    public void setNewaContent(String newaContent) {
        this.newaContent = newaContent;
    }

    public Date getNewsTimestamp() {
        return newsTimestamp;
    }

    public void setNewsTimestamp(Date newsTimestamp) {
        this.newsTimestamp = newsTimestamp;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }
}

