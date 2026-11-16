package org.example.aidemo.mapper;

import org.apache.ibatis.annotations.*;
import org.example.aidemo.entity.Memo;
import org.example.aidemo.entity.TagCount;

import java.util.List;

@Mapper
public interface MemoMapper {

    @Select("SELECT * FROM memo ORDER BY id DESC")
    List<Memo>findAll();

    @Select("SELECT * FROM memo WHERE id = #{id}")
    Memo findOne(Integer id);

    @Insert("INSERT INTO memo(title , content ,tag) VALUES (#{title},#{content},#{tag})")
    void insertOne(Memo memo);

    @Update("UPDATE memo SET title = #{title},content = #{content},tag = #{tag} WHERE id = #{id}")
    void updateOne(Memo memo);

    @Delete("DELETE FROM memo WHERE id = #{id}")
    void deleteOne(Integer id);

    @Select("SELECT * FROM memo WHERE title LIKE CONCAT('%',#{keyword},'%') OR content LIKE CONCAT('%',#{keyword},'%') OR tag LIKE CONCAT('%',#{keyword},'%')")
    List<Memo>findByTag(@Param("keyword") String keyword);

    @Select("SELECT tag,COUNT(*) AS count FROM memo WHERE tag != '' AND tag IS NOT NULL GROUP BY tag")
    List<TagCount>countTags();

    @Select("SELECT * FROM memo ORDER BY id DESC LIMIT #{offset},#{size}")
    List<Memo>findByPage(@Param("offset")Integer offset,@Param("size")Integer size);
}
