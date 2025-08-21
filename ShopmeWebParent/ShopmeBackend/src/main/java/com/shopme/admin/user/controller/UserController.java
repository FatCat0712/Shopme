package com.shopme.admin.user.controller;

import com.shopme.admin.SupabaseS3Util;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {
    private final UserService userService;
    private final String defaultURL = "redirect:/users/page/1?sortField=firstName&sortDir=asc";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String listFirstPage() {
       return defaultURL;
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listUsers", moduleURL = "/users") PagingAndSortingHelper helper,
             @PathVariable(name = "pageNum") Integer pageNum
    ) {

        userService.listByPage(pageNum, helper);
        return "users/users";

    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        List<Role> listRoles = userService.listRoles();
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("pageTitle", "Create New User");
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        return "users/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        redirectAttributes.addFlashAttribute("message", String.format("The user has been %s", user.getId() == null ? "created" : "updated"));
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            user.setPhotos(fileName);
             User savedUser =   userService.save(user);
            String uploadDir = "user-photos/" + savedUser.getId();
            SupabaseS3Util.removeFolder(uploadDir);
            SupabaseS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
        }
        else {
            if(user.getPhotos().isEmpty()) {
                user.setPhotos(null);
            }
            userService.save(user);
        }
        
       return getRedirectURLtoAffectedUser(user);
    }

    public String getRedirectURLtoAffectedUser(User user ){
        String firstPartOfEmail  = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.get(id);
            List<Role> listRoles = userService.listRoles();
            model.addAttribute("listRoles", listRoles);
            model.addAttribute("pageTitle", String.format("Edit User (ID: %d)", id));
            model.addAttribute("user", user);
            return "users/user_form";
        }catch (UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return defaultURL;
        }
    }


    @RequestMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name ="id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            String userPhotoDir = "user-photos/" + id;
            SupabaseS3Util.removeFolder(userPhotoDir);
            redirectAttributes.addFlashAttribute("message", String.format("The user ID %d has been deleted successfully", id));
        } catch (UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return defaultURL;
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateEnabledStatus(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean enabled, RedirectAttributes redirectAttributes) {
        userService.updateUserEnabledStatus(id, enabled);
        redirectAttributes.addFlashAttribute("message", String.format("The user ID %d has been %s", id, enabled ? "enabled" : "disabled"));
        return defaultURL;
    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAll();
        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(listUsers, response);
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException{
        List<User> listUsers = userService.listAll();
        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(listUsers, response);
    }

    @GetMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAll();
        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(listUsers, response);
    }






}
