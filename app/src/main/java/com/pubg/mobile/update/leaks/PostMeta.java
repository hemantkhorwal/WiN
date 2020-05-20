package com.pubg.mobile.update.leaks;

public class PostMeta {
    String headline , imgUrl , metaTitle , metaDesc ,postTypeImgUrl, postUrl,id , type;
    PostMeta(String headline,String imgUrl,String postTypeImgUrl, String metaTitle, String metaDesc, String postUrl,String id,String type){
        this.headline=headline;
        this.imgUrl=imgUrl;
        this.postTypeImgUrl=postTypeImgUrl;
        this.metaTitle=metaTitle;
        this.metaDesc=metaDesc;
        this.postUrl = postUrl;
        this.id = id;
        this.type = type;
    }

    public String getHeadline() {
        return headline;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getMetaDesc() {
        return metaDesc;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public String getPostTypeImgUrl() {
        return postTypeImgUrl;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
