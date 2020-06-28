package com.lvlv.gorilla.cat.mapper;

import com.lvlv.gorilla.cat.entity.sql.User;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 定义 Results map, 可通过 @ResultMap("userMap") 使用
     * @return
     */
    @Results(id="userMap", value={
            @Result(column="uid", property="uid"),
            @Result(column="name", property="name"),
            @Result(column="nickname", property="nickname"),
            @Result(column="gender", property="gender"),
            @Result(column="birthday", property="birthday"),
            @Result(column="mobile", property="mobile"),
            @Result(column="email", property="email"),
            @Result(column="password", property="password"),
            @Result(column="password", property="salt"),
            @Result(column="avatar", property="avatar"),
            @Result(column="profile", property="profile"),
            @Result(column="created_at", property="createdAt"),
            @Result(column="updated_at", property="updatedAt"),
            @Result(column="status", property="status")
    })

    /**
     * 总用户数
     */
    @Select("select count(uid) from user")
    int conntAll();

    /**
     * 有效用户数
     */
    @Select("select count(uid) from user where status < 10")
    int conntValid();

    /**
     * 无效用户数
     */
    @Select("select count(uid) from user where status > 10")
    int conntInvalid();

    /**
     * 在线用户数
     */
    @Select("select count(uid) from user where status = 0")
    int conntOnline();

    /**
     * 离线用户数
     */
    @Select("select count(uid) from user where status = 1")
    int conntOffline();

    /**
     * 同名用户数
     */
    @Select("select count(name) from user where name = #{name}")
    int countByName(@Param("name") String name);

    /**
     * 检查昵称是否重复
     * @param uid
     * @param nickname
     * @return
     */
    @Select("select count(nickname) from user where uid != #{uid} and nickname = #{nickname}")
    int countByNickname(@Param("uid") long uid, @Param("nickname") String nickname);

    /**
     * 设定手机号记录数
     */
    @Select("select count(mobile) from user where mobile = #{mobile}")
    int countByMobile(@Param("mobile") String mobile);

    /**
     * 新增，参数是一个bean
     */
    @Insert("insert into user       "
            + "(uid, name, nickname, gender, birthday, mobile, email, password, salt, avatar, profile, created_at, updated_at, status)    "
            + "values                   "
            + "(#{uid}, #{name}, #{nickname}, #{gender}, #{birthday}, #{mobile}, #{email}, #{password}, #{salt}, #{avatar}, #{profile}, #{createdAt}, #{updatedAt}, #{status}) ")
    int insertUser(User bean);

    /**
     * 根据 uid 查询用户
     */
    @ResultMap("userMap")
    @Select("select uid, name, nickname, gender, birthday, mobile, email, password, salt, avatar, profile, created_at, updated_at, status  "
            + "from user "
            + "where uid = #{uid} ")
    User findUserByUid(@Param("uid") long uid);

    /**
     * 根据 电话号码 查询用户
     * @param mobile
     * @return
     */
    @ResultMap("userMap")
    @Select("select uid, name, nickname, gender, birthday, mobile, email, password, salt, avatar, profile, created_at, updated_at, status  "
            + "from user "
            + "where mobile = #{mobile} ")
    User findUserByMobile(@Param("mobile") String mobile);

    /**
     * 查询所有用户
     */
    @ResultMap("userMap")
    @Select("select uid, name, nickname, gender, birthday, mobile, email, password, salt, avatar, profile, created_at, updated_at, status  "
            + "from user ")
    List<User> findAll();

    /**
     * 修改用户记录
     */
    @Update("update user set  "
            + "name = #{name},  "
            + "nickname  = #{nickname},    "
            + "gender  = #{gender},    "
            + "birthday  = #{birthday},    "
            + "mobile  = #{mobile},    "
            + "email  = #{email},    "
            + "password  = #{password},    "
            + "salt  = #{salt},    "
            + "avatar  = #{avatar},    "
            + "profile  = #{profile},    "
            + "created_at  = #{createdAt},    "
            + "updated_at  = #{updatedAt},    "
            + "status  = #{status}    "
            + "where uid = #{uid} ")
    int updateUser(User bean);

    /**
     * 修改密码
     */
    @Update("update user set  "
            + "password  = #{password},    "
            + "salt  = #{salt},    "
            + "updated_at = #{updatedAt}"
            + "where uid = #{uid} ")
    int updatePassword(@Param("uid") long uid,
                       @Param("password") String password,
                       @Param("salt") String salt,
                       @Param("updatedAt")Date updatedAt);

    /**
     * 修改 昵称
     * @param uid
     * @param nickname
     * @param updatedAt
     * @return
     */
    @Update("update user set  "
            + "nickname  = #{nickname},    "
            + "updated_at = #{updatedAt}"
            + "where uid = #{uid} ")
    int updateNickname(@Param("uid") long uid,
                       @Param("nickname") String nickname,
                       @Param("updatedAt")Date updatedAt);

    /**
     * 修改出生日期
     * @param uid
     * @param birthday
     * @param updatedAt
     * @return
     */
    @Update("update user set  "
            + "birthday  = #{birthday},    "
            + "updated_at = #{updatedAt}"
            + "where uid = #{uid} ")
    int updateBirthday(@Param("uid") long uid,
                       @Param("birthday") String birthday,
                       @Param("updatedAt")Date updatedAt);

    /**
     * 修改 个性签名
     * @param uid
     * @param profile
     * @param updatedAt
     * @return
     */
    @Update("update user set  "
            + "profile  = #{profile},    "
            + "updated_at = #{updatedAt}"
            + "where uid = #{uid} ")
    int updateProfile(@Param("uid") long uid,
                      @Param("profile") String profile,
                      @Param("updatedAt")Date updatedAt);

    /**
     * 修改 性别
     * @param uid
     * @param gender
     * @param updatedAt
     * @return
     */
    @Update("update user set  "
            + "gender  = #{gender},    "
            + "updated_at = #{updatedAt}"
            + "where uid = #{uid} ")
    int updateGender(@Param("uid") long uid,
                     @Param("gender") int gender,
                     @Param("updatedAt")Date updatedAt);

    /**
     * 删除
     */
    @Delete("delete from user  "
            + "where uid = #{uid}  ")
    int deleteUserByUid(@Param("uid") long uid);

}
