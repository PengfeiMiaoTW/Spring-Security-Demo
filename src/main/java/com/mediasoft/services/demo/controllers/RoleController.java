package com.mediasoft.services.demo.controllers;

import com.mediasoft.services.demo.entities.Role;
import com.mediasoft.services.demo.services.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/roles")
public class RoleController {
    @Autowired
    private IRoleService service;

    // hasRole('USER')
    @GetMapping
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Iterable<Role> getRoles() {
        return service.getAllRoles();
    }

}
