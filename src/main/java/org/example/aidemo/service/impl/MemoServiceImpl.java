package org.example.aidemo.service.impl;

import org.example.aidemo.entity.Memo;
import org.example.aidemo.entity.TagCount;
import org.example.aidemo.mapper.MemoMapper;
import org.example.aidemo.service.MemoService;
import org.example.aidemo.utils.AiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemoServiceImpl implements MemoService {
    @Autowired
    private MemoMapper memoMapper;
    @Autowired
    private AiUtils aiUtils;

    @Override
    public List<Memo> getAll() {
        return memoMapper.findAll();
    }

    @Override
    public Memo getOne(Integer id) {
        return memoMapper.findOne(id);
    }

    @Override
    public void add(Memo memo) throws Exception {
        String autoTag = aiUtils.aiGetTag(memo.getTitle(),memo.getContent());
        memo.setTag(autoTag);
        memoMapper.insertOne(memo);
    }

    @Override
    public void update(Integer id,Memo memo) throws Exception{
        memo.setId(id);
        String autoTag = aiUtils.aiGetTag(memo.getTitle(),memo.getContent());
        memo.setTag(autoTag);
        memoMapper.updateOne(memo);
    }

    @Override
    public void delete(Integer id) {
        memoMapper.deleteOne(id);
    }

    @Override
    public List<Memo> searchByTag(String keyword) {
        return memoMapper.findByTag(keyword);
    }

    @Override
    public List<TagCount> getCount() {
        return memoMapper.countTags();
    }

    @Override
    public List<Memo> findByPage(Integer page, Integer size) {
        int offset = (page - 1) * size;
        return memoMapper.findByPage(offset,size);
    }
}
