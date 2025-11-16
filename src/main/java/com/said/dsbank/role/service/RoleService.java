package com.said.dsbank.role.service;


import com.said.dsbank.res.Response;
import com.said.dsbank.role.entity.Role;

import java.util.List;

public interface RoleService {

    Response<Role> createRole(Role roleRequest);

    Response<Role> updateRole(Role roleRequest);

    Response<List<Role>> getAllRoles();

    Response<?> deleteRole(Long id);


}
