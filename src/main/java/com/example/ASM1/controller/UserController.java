package com.example.ASM1.controller;

import com.example.ASM1.Entity.User;
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

    // Phương thức POST để lưu người dùng (thêm hoặc cập nhật)
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, @RequestParam("role") String role) {
        // Lưu người dùng vào cơ sở dữ liệu
        user.setRole(role);
        // Nếu user đã tồn tại (update), không mã hóa lại mật khẩu nếu không thay đổi
        if (user.getUserId() != null) { // Kiểm tra nếu là chỉnh sửa
            User existingUser = userSevice.findById(user.getUserId());
            if (existingUser != null && !user.getPassword().equals(existingUser.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                user.setPassword(existingUser.getPassword()); // Giữ mật khẩu cũ
            }
        } else { // Nếu là thêm mới, mã hóa mật khẩu
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

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

    // Phương thức GET để hiển thị form chỉnh sửa người dùng
    @GetMapping("/editUser/{userId}")
    public String editUser(@PathVariable Integer userId, Model model) {
        User user = userSevice.findById(userId);
        if (user != null) {
            model.addAttribute("user", user);
            List<User> users = userSevice.findAllUser();
            model.addAttribute("users", users);
            return "quanLyUser"; // Sử dụng cùng template quanLyUser
        }
        return "redirect:/user/findAllUser"; // Nếu không tìm thấy, quay lại danh sách
    }

    // Phương thức GET để xóa người dùng
    @GetMapping("/deleteUser/{userId}")
    public String deleteUser(@PathVariable Integer userId) {
        userSevice.deleteById(userId);
        return "redirect:/user/findAllUser"; // Quay lại danh sách sau khi xóa
    }
}