package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.UserRepository;
import vn.hoidanit.laptopshop.service.UserService;

@Controller
public class UserController {

    // DI : dependency injection
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String getHomePage(Model model) {
        List<User> arrUsers = this.userService.getAllUsersByEmail("anhdeptrai1809@gmail.com");
        System.out.println(arrUsers);
        model.addAttribute("eric", "test");
        model.addAttribute("hoidanit", "from controller with model");
        return "hello";
    }

    @RequestMapping("/admin/user")
    public String getUserPage(Model model) {
        List<User> users = this.userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/user/show";
    }

    @RequestMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        User userdetail = this.userService.getUsersById(id);
        // if userdetail is LIST -> use foreach in jsp file
        model.addAttribute("userdetail", userdetail);
        return "admin/user/detail";
    }

    @RequestMapping("/admin/user/create")
    public String getUserCreatePage(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    @RequestMapping(value = "/admin/user/create", method = RequestMethod.POST)
    public String getUserCreateForm(@ModelAttribute("newUser") User hoidanit) {
        this.userService.HandleSaveUser(hoidanit);
        // if return /admin/user -> data null
        return "redirect:/admin/user";
    }

    @RequestMapping("/admin/user/update/{id}")
    public String getUserUpdatePage(Model model, @PathVariable long id) {
        User curUser = this.userService.getUsersById(id);
        model.addAttribute("newUser", curUser);
        return "admin/user/update";
    }

    @PostMapping("/admin/user/update") // = @RequestMapping(value = "/admin/user/update", method = RequestMethod.POST)
    public String postUserUpdate(Model model, @ModelAttribute("newUser") User user) {
        User curUser = this.userService.getUsersById(user.getId());
        if (curUser != null) {
            curUser.setAddress(user.getAddress());
            curUser.setPhone(user.getPhone());
            curUser.setFullName(user.getFullName());
            this.userService.HandleSaveUser(curUser);
        }
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String getUserDeletePage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        model.addAttribute("newUser", new User());
        return "admin/user/delete";
    }

    @PostMapping("/admin/user/delete")
    public String postUserDelete(Model model, @ModelAttribute("newUser") User user) {
        this.userService.deleteAUser(user.getId());
        return "redirect:/admin/user";
    }
}
