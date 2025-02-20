package com.example.ASM1.controller;

import com.example.ASM1.Entity.User;
import com.example.ASM1.repository.UserRepository;
import com.example.ASM1.service.UserSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserSevice userSevice;

    // Phương thức GET để hiển thị trang quanLyUser với form nhập người dùng
    @GetMapping("/quanLyUser")
    public String showUserForm(Model model) {
        List<User> users = userSevice.findAllUser();
        model.addAttribute("users", users);
        model.addAttribute("user", new User());  // Chỉ cần thêm 1 lần
        return "quanLyUser";
    }

    // Phương thức POST để lưu người dùng vào cơ sở dữ liệu
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user,@RequestParam("role") String role) {
        // Lưu người dùng vào cơ sở dữ liệu
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userSevice.save(user);

        // Sau khi lưu thành công, chuyển hướng về trang findAllUser
        return "redirect:/user/findAllUser";  // Chuyển hướng về trang danh sách người dùng
    }

    // Phương thức GET để lấy tất cả người dùng và hiển thị trên trang quanLyUser
    @GetMapping("/findAllUser")
    public String findAllUser(Model model) {
        List<User> users = userSevice.findAllUser();
        model.addAttribute("users", users);
        model.addAttribute("user", new User());  // Thêm đối tượng user cho form
        return "quanLyUser";
    }
}
