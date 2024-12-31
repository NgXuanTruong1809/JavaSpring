package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UploadService;

@Controller
public class ProductController {
    private final ProductService productService;
    private final UploadService uploadService;

    public ProductController(ProductService productService, UploadService uploadService) {
        this.productService = productService;
        this.uploadService = uploadService;
    }

    @GetMapping("/admin/product")
    public String getProductPage(Model model, @RequestParam(value = "page", required = false) String page) {
        // client: page . limit
        // database: offset + limit
        int pageInt = 1;
        try {
            pageInt = Integer.parseInt(page);
        } catch (Exception exception) {

        }
        Pageable pageable = PageRequest.of(pageInt - 1, 5); // page index start = 0, limit
        Page<Product> products = this.productService.getAllProducts(pageable);
        List<Product> listProducts = products.getContent();
        model.addAttribute("products", listProducts);
        model.addAttribute("curPage", pageInt);
        model.addAttribute("totalPages", products.getTotalPages());

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
        // List<FieldError> errors = newProductBindingResult.getFieldErrors();
        // for (FieldError error : errors) {
        // System.out.println(error.getField() + " - " + error.getDefaultMessage());
        // }
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
    public String postProductUpdatePage(@ModelAttribute("newProduct") @Valid Product newProduct,
            BindingResult newProductBindingResult,
            @RequestParam("uploadFile") MultipartFile file) {
        // VALIDATE
        // List<FieldError> errors = newProductBindingResult.getFieldErrors();
        // for (FieldError error : errors) {
        // System.out.println(error.getField() + " - " + error.getDefaultMessage());
        // }
        if (newProductBindingResult.hasErrors()) {
            String imgOrigin = this.productService.getImageById(newProduct.getId());
            newProduct.setImage(imgOrigin);

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

    @GetMapping("/admin/product/delete/{id}")
    public String getProductDeletePage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        model.addAttribute("newProduct", new Product());
        return "admin/product/delete";
    }

    @PostMapping("/admin/product/delete")
    public String postProductDeletePage(Model model, @ModelAttribute("newProduct") Product newProduct) {
        this.productService.deleteAProduct(newProduct.getId());
        return "redirect:/admin/product";
    }
}
