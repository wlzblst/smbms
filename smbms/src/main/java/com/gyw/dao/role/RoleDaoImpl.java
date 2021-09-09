package com.gyw.dao.role;

import com.gyw.dao.BaseDao;
import com.gyw.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao {
    public List<Role> getRoleList(Connection connection) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet re = null;
        ArrayList<Role> roleList = new ArrayList<Role>();

        if(connection!=null){
            String sql = "SELECT * FROM smbms_role";
            Object[] params = {};
            re = BaseDao.searchData(connection,sql,pstm,re,params);

            while(re.next()){
                Role role = new Role();
                role.setId(re.getInt("id"));
                role.setRoleCode(re.getString("roleCode"));
                role.setRoleName(re.getString("roleName"));
                roleList.add(role);
            }
            BaseDao.closeResource(connection,pstm,re);
        }
        return roleList;
    }
}
