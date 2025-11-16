package com.said.dsbank.role.controller;

import com.said.dsbank.res.Response;
import com.said.dsbank.role.entity.Role;

import com.said.dsbank.role.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.List;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasAuthority('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<Response<Role>> createRole(@RequestBody Role roleRequest){

        return ResponseEntity.ok(roleService.createRole(roleRequest));

    }

    @PutMapping
    public ResponseEntity<Response<Role>> updateRole(@RequestBody Role roleRequest){

        return ResponseEntity.ok(roleService.updateRole(roleRequest));

    }

    @GetMapping
    public ResponseEntity<Response<List<Role>>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteRole(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.deleteRole(id));
    }






}
