package org.example.aidemo.controller;

import org.example.aidemo.entity.Memo;
import org.example.aidemo.mapper.MemoMapper;
import org.example.aidemo.entity.TagCount;
import org.example.aidemo.service.MemoService;
import org.example.aidemo.utils.AiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/memos")
public class MemoController {

    @Autowired
    MemoService memoService;

    @GetMapping
    public List<Memo> getAll(){
        return memoService.getAll();
    }
    @GetMapping("/{id}")
    public Memo getOne(@PathVariable Integer id){
        return memoService.getOne(id);
    }
    @PostMapping
    public String add(@RequestBody Memo memo) throws Exception
    {
        memoService.add(memo);
        return "SUCCESS";
    }
    @PutMapping("/{id}")
    public String update(@PathVariable Integer id,@RequestBody Memo memo) throws Exception
    {
        memoService.update(id,memo);
        return "SUCCESS";
    }
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id)
    {
        memoService.delete(id);
        return "SUCCESS";
    }
    @GetMapping("/search")
    public List<Memo> searchByTag(@RequestParam("keyword") String keyword)
    {
        return memoService.searchByTag(keyword);
    }
    @GetMapping("/tags")
    public List<TagCount> getCount()
    {
        return memoService.getCount();
    }
    @GetMapping("/page")
    public List<Memo> getByPage(@RequestParam(value = "page",defaultValue = "1")Integer page,@RequestParam(value = "size",defaultValue = "20")Integer size){
        return memoService.findByPage(page,size);
    }

}
