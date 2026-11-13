package org.example.aidemo;

import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/memos")
public class MemoController {

    @Autowired
    MemoMapper memoMapper;

    @Autowired
    AiUtils aiUtils;

    @GetMapping
    public List<Memo> getAll(){
        return memoMapper.findAll();
    }
    @GetMapping("/{id}")
    public Memo getOne(@PathVariable Integer id){
        return memoMapper.findOne(id);
    }
    @PostMapping
    public String add(@RequestBody Memo memo) throws Exception
    {
        String autoTag = aiUtils.aiGetTag(memo.getTitle(),memo.getContent());
        memo.setTag(autoTag);
        memoMapper.insertOne(memo);
        return "SUCCESS";
    }
    @PutMapping("/{id}")
    public String update(@PathVariable Integer id,@RequestBody Memo memo)
    {
        memo.setId(id);
        memoMapper.updateOne(memo);
        return "SUCCESS";
    }
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id)
    {

        memoMapper.deleteOne(id);
        return "SUCCESS";
    }



}
