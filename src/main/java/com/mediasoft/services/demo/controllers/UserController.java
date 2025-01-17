package com.mediasoft.services.demo.controllers;

import com.mediasoft.services.demo.Exception.CustomeFieldValidationException;
import com.mediasoft.services.demo.dto.ChangePasswordForm;
import com.mediasoft.services.demo.entities.Role;
import com.mediasoft.services.demo.entities.User;
import com.mediasoft.services.demo.repositories.IRole;
import com.mediasoft.services.demo.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class UserController {
    @Autowired
    private IUserService service;

    @Autowired
    private IRole roleRepo;

    @GetMapping( "/login")
    public String index() {
        return "index";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        Role userRole = roleRepo.findByName("ROLE_USER");
        List<Role> roles = Collections.singletonList(userRole);

        model.addAttribute("signup", true);
        model.addAttribute("userForm", new User());
        model.addAttribute("roles", roles);
        return "user-form/user-signup";
    }

    @PostMapping("/signup")
    public String signupAction(@Valid @ModelAttribute("userForm") User user, BindingResult result, ModelMap model) {
        Role userRole = roleRepo.findByName("ROLE_USER");
        List<Role> roles = Collections.singletonList(userRole);
        model.addAttribute("userForm", user);
        model.addAttribute("roles", roles);
        model.addAttribute("signup", true);

        if (result.hasErrors()) {
            return "user-form/user-signup";
        } else {
            try {
                service.createUser(user);
            } catch (CustomeFieldValidationException cfve) {
                result.rejectValue(cfve.getFieldName(), "", cfve.getMessage());
            } catch (Exception e) {
                model.addAttribute("formErrorMessage", e.getMessage());
            }
        }
        return "index";
    }

    @GetMapping("/userForm")
    public String userForm(Model model) {
        model.addAttribute("userForm", new User());
        model.addAttribute("userList", service.getAllUsers());
        model.addAttribute("roles", roleRepo.findAll());
        model.addAttribute("listTab", "active");
        return "user-form/user-view";
    }

    @PostMapping("/userForm")
    public String createUser(@Valid @ModelAttribute("userForm") User user, BindingResult result, ModelMap model) {
        user.setStatus(true);
        if (result.hasErrors()) {
            model.addAttribute("userForm", user);
            model.addAttribute("formTab", "active");
        } else {
            try {
                service.createUser(user);
                model.addAttribute("userForm", new User());
                model.addAttribute("listTab", "active");
            } catch (Exception e) {
                model.addAttribute("formErrorMessage", e.getMessage());
                model.addAttribute("userForm", user);
                model.addAttribute("formTab", "active");
                model.addAttribute("userList", service.getAllUsers());
                model.addAttribute("roles", roleRepo.findAll());
                //e.printStackTrace();
            }
        }
        model.addAttribute("userList", service.getAllUsers());
        model.addAttribute("roles", roleRepo.findAll());
        return "user-form/user-view";
    }

    @GetMapping("/editUser/{id}")
    public String getEditUserForm(Model model, @PathVariable(name = "id") Long id) throws Exception {
        User userToEdit = service.findById(id);
        model.addAttribute("userForm", userToEdit);
        model.addAttribute("formTab", "active");
        model.addAttribute("userList", service.getAllUsers());
        model.addAttribute("roles", roleRepo.findAll());

        model.addAttribute("editMode", "true");
        model.addAttribute("passwordForm", new ChangePasswordForm(id));
        return "user-form/user-view";
    }

    @PostMapping("/updateUser")
    public String postEditUserForm(@Valid @ModelAttribute("userForm") User user, BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            model.addAttribute("userForm", user);
            model.addAttribute("formTab", "active");
            model.addAttribute("editMode", "true");
            model.addAttribute("passwordForm", new ChangePasswordForm(user.getId()));
        } else {
            try {
                service.updateUser(user);
                model.addAttribute("userForm", new User());
                model.addAttribute("listTab", "active");
                model.addAttribute("editMode", "false");
            } catch (Exception e) {
                model.addAttribute("formErrorMessage", e.getMessage());
                model.addAttribute("userForm", user);
                model.addAttribute("formTab", "active");
                model.addAttribute("userList", service.getAllUsers());
                model.addAttribute("roles", roleRepo.findAll());

                model.addAttribute("editMode", "true");
                model.addAttribute("passwordForm", new ChangePasswordForm(user.getId()));
                //e.printStackTrace();
            }
        }

        model.addAttribute("userList", service.getAllUsers());
        model.addAttribute("roles", roleRepo.findAll());
        return "user-form/user-view";
    }

    @GetMapping("/userForm/cancel")
    public String cancelEditUser(ModelMap modelMap) {
        System.out.println(modelMap);
        return "redirect:/userForm";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(Model model, @PathVariable(name = "id") Long id) {
        try {
            service.deleteUser(id);
        } catch (Exception e) {
            model.addAttribute("listErrorMessage", e.getMessage());
        }

        return userForm(model);
    }

    @PostMapping("/editUser/changePassword")
    public ResponseEntity<?> postEditUseChangePassword(@Valid @RequestBody ChangePasswordForm form, Errors errors) {
        try {
            if (errors.hasErrors()) {
                String result = errors.getAllErrors()
                        .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(""));

                throw new Exception(result);
            }
            service.changePassword(form);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Success");
    }
}
