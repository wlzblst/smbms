package com.gyw.dao.user;

import com.gyw.pojo.Role;
import com.gyw.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {

    //得到登陆用户
    public User getLoginUser(Connection connection ,String userCode ,String passwprd);

    //修改当前用户密码
    public int update(Connection connection, int id, String password) throws SQLException;

    //查询用户总数
    public int getUserCount(Connection connection, String userName, int userRole) throws SQLException;

    //查询用户列表
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws Exception;

    //增加用户
    public int add(Connection connection, User user) throws Exception;

    //删除用户
    public int deleteUserById(Connection connection, Integer delId) throws Exception;

    //修改用户信息
    public int modify(Connection connection, User user) throws Exception;

    //通过userId获取user
    public User getUserById(Connection connection, String id) throws Exception;
}
