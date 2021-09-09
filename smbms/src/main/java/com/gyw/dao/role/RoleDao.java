package com.gyw.dao.role;

import com.gyw.pojo.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface RoleDao {
    //查询角色列表
    public List<Role> getRoleList(Connection connection)throws SQLException;
}
