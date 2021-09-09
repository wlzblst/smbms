package com.gyw.service.role;

import com.gyw.dao.BaseDao;
import com.gyw.dao.role.RoleDao;
import com.gyw.dao.role.RoleDaoImpl;
import com.gyw.pojo.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RoleServiceImpl implements RoleService{

    private RoleDao roleDao;
    public RoleServiceImpl(){
        roleDao = new RoleDaoImpl();
    }
    public List<Role> getRoleList(){
        Connection connection = null;
        List<Role> roleList = null;

        try{
            connection = BaseDao.getConnection();
            roleList = roleDao.getRoleList(connection);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return roleList;
    }
}
