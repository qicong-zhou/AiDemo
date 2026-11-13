package org.example.aidemo;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MemoMapper {

    @Select("SELECT * FROM memo")
    List<Memo>findAll();

    @Select("SELECT * FROM memo WHERE id = #{id}")
    Memo findOne(Integer id);

    @Insert("INSERT INTO memo(title , content ,tag) VALUE(#{title},#{content},#{tag})")
    void insertOne(Memo memo);

    @Update("UPDATE memo SET title = #{title},content = #{content},tag = #{tag} WHERE id = #{id}")
    void updateOne(Memo memo);

    @Delete("DELETE FROM memo WHERE id = #{id}")
    void deleteOne(Integer id);
}
