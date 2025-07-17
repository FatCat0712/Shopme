package com.shopme.admin.user.controller;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Objects;

@Controller
public class AccountController {
    private final UserService userService;

    @Autowired
    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/account")
    public String viewDetails(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user =userService.getByEmail(email);

        model.addAttribute("user", user);
        return "users/account_form";
    }

    @PostMapping("/account/update")
    public String updateDetails(User userInform, RedirectAttributes redirectAttributes, @RequestParam("image")MultipartFile multipartFile,@AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException, UserNotFoundException {
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

            userInform.setPhotos(fileName);
            User savedUser = userService.updateAccount(userInform);
            String uploadDir = "user-photos/" + savedUser.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        }
        else {
            if(userInform.getPhotos().isEmpty()) userInform.setPhotos(null);
            userService.updateAccount(userInform);
        }

        loggedUser.setFirstName(userInform.getFirstName());
        loggedUser.setLastName(userInform.getLastName());
        redirectAttributes.addFlashAttribute("message", "Your account details have been updated");

        return "redirect:/account";
    }


}
