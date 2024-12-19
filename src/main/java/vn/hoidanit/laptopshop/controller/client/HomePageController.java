package vn.hoidanit.laptopshop.controller.client;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.dto.RegisterDTO;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class HomePageController {

    private final ProductService productService;
    private final UserService userService;
    private final PasswordEncoder bCryptPasswordEncoder;

    public HomePageController(ProductService productService, UserService userService,
            PasswordEncoder bCryptPasswordEncoder) {
        this.productService = productService;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<Product> products = this.productService.getAllProducts();
        model.addAttribute("products", products);
        return "client/homepage/show";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registerUser", new RegisterDTO());
        return "client/authentication/register";
    }

    @PostMapping("/register")
    public String postRegisterPage(@ModelAttribute("registerUser") @Valid RegisterDTO registerDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "client/authentication/register";
        }

        User user = this.userService.registerDTOToUser(registerDTO);
        String hashPassword = this.bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        user.setRole(this.userService.getRoleByName("USER"));
        this.userService.handleSaveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "client/authentication/login";
    }

    @GetMapping("/cart")
    public String getCartPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long userID = (long) session.getAttribute("id");
        User user = this.userService.getUsersById(userID);
        if (user.getCart() == null) {
            model.addAttribute("cardDetailNull", "Không có sản phẩm nào");
        } else {
            if (user.getCart().getCartDetails().isEmpty()) {
                model.addAttribute("cardDetailNull", "Không có sản phẩm nào");
            }
        }

        model.addAttribute("user", user);
        return "client/cart/show";
    }

    @PostMapping("/cart/delete/{id}")
    public String postDeleteCartDetail(@PathVariable long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        this.productService.handleRemoveCardDetails(id, session);
        return "redirect:/cart";
    }

    @PostMapping("/check-out")
    public String postCheckOut(@RequestParam(value = "cartDetailId", required = false) List<Long> cartDetailId,
            @RequestParam(value = "cartDetailQuantity", required = false) List<Long> cartDetailQuantity) {
        if (cartDetailId == null) {
            return "redirect:/cart";
        }
        this.productService.handleCheckOut(cartDetailId, cartDetailQuantity);
        return "redirect:/check-out";
    }

    @GetMapping("/check-out")
    public String getCheckOut(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long userID = (long) session.getAttribute("id");
        User user = this.userService.getUsersById(userID);
        if (user.getCart() == null) {
            model.addAttribute("cardDetailNull", "Không có sản phẩm nào");
        } else {
            if (user.getCart().getCartDetails().isEmpty()) {
                model.addAttribute("cardDetailNull", "Không có sản phẩm nào");
            }
        }

        model.addAttribute("user", user);
        return "client/cart/checkout";
    }

    @PostMapping("/order")
    public String postMethodName(@RequestParam("receiverName") String receiverName,
            @RequestParam("receiverAddress") String receiverAddress,
            @RequestParam("receiverPhone") String receiverPhone,
            @RequestParam("totalAmount") String totalAmount,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long userID = (long) session.getAttribute("id");
        User user = this.userService.getUsersById(userID);

        this.productService.handleOrder(user, session, receiverName, receiverAddress, receiverPhone, totalAmount);
        return "client/cart/orderSuss";
    }

}
