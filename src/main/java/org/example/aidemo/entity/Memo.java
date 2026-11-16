package org.example.aidemo.entity;

import java.time.LocalDateTime;

public class Memo {
    private Integer id;
    private String title;
    private String content;
    private String tag;
    private LocalDateTime create_time;
    private  LocalDateTime update_time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public LocalDateTime getCreateTime() {
        return create_time;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.create_time = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return update_time;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.update_time = updateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
