package org.example.aidemo.service;

import org.apache.ibatis.annotations.*;
import org.example.aidemo.entity.Memo;
import org.example.aidemo.entity.TagCount;

import java.util.List;

public interface MemoService {

    List<Memo> getAll();
    Memo getOne(Integer id);
    void add(Memo memo) throws Exception;
    void update(Integer id,Memo memo)throws Exception;
    void delete(Integer id);
    List<Memo>searchByTag(String keyword);
    List<TagCount>getCount();
    List<Memo>findByPage(Integer page,Integer size);
}
