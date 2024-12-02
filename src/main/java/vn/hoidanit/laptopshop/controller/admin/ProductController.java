package vn.hoidanit.laptopshop.controller.admin;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UploadService;
import vn.hoidanit.laptopshop.service.UserService;

@Controller
public class ProductController {
    private final ProductService productService;
    private final UploadService uploadService;

    public ProductController(ProductService productService, UploadService uploadService) {
        this.productService = productService;
        this.uploadService = uploadService;
    }

    @GetMapping("/admin/product")
    public String getProductPage(Model model) {
        List<Product> products = this.productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin/product/show";
    }

    @GetMapping("/admin/product/create")
    public String getProductCreatePage(Model model) {
        model.addAttribute("newProduct", new Product());
        return "admin/product/create";
    }

    @PostMapping("/admin/product/create")
    public String postProductCreateForm(@ModelAttribute("newProduct") @Valid Product newProduct,
            BindingResult newProductBindingResult,
            @RequestParam("uploadFile") MultipartFile file) {
        // validate
        List<FieldError> errors = newProductBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(error.getField() + " - " + error.getDefaultMessage());
        }
        if (newProductBindingResult.hasErrors()) {
            return "admin/product/create";
        }
        //
        String filename = this.uploadService.handleSaveUploadFile(file, "product");
        newProduct.setImage(filename);
        this.productService.handleSaveProduct(newProduct);
        // if return /admin/user -> data null
        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/{id}")
    public String getProductDetailPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        Product productdetail = this.productService.getProductById(id);
        // if userdetail is LIST -> use foreach in jsp file
        model.addAttribute("productdetail", productdetail);
        return "admin/product/detail";
    }

    @GetMapping("/admin/product/update/{id}")
    public String getProductUpdatePage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        Product productDetail = this.productService.getProductById(id);
        // if userdetail is LIST -> use foreach in jsp file
        model.addAttribute("newProduct", productDetail);
        return "admin/product/update";
    }

    @PostMapping("/admin/product/update")
    public String postProductUpdatePage(@ModelAttribute("newProduct") Product newProduct,
            BindingResult newProductBindingResult,
            @RequestParam("uploadFile") MultipartFile file) {

        if (newProductBindingResult.hasErrors()) {
            return "admin/product/update";
        }

        Product originProduct = this.productService.getProductById(newProduct.getId());
        if (originProduct != null) {
            if (!file.isEmpty()) {
                String img = this.uploadService.handleSaveUploadFile(file, "product");
                originProduct.setImage(img);
            }
            originProduct.setName(newProduct.getName());
            originProduct.setDetailDesc(newProduct.getDetailDesc());
            originProduct.setFactory(newProduct.getFactory());
            originProduct.setPrice(newProduct.getPrice());
            originProduct.setQuantity(newProduct.getQuantity());
            originProduct.setShortDesc(newProduct.getShortDesc());
            originProduct.setTarget(newProduct.getTarget());
            this.productService.handleSaveProduct(originProduct);

        }
        return "redirect:/admin/product";
    }

}
