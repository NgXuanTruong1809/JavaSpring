package vn.hoidanit.laptopshop.controller.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.Product_;
import vn.hoidanit.laptopshop.domain.dto.ProductCriteriaDTO;
import vn.hoidanit.laptopshop.service.ProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ItemController {

    private final ProductService productService;

    public ItemController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product/{id}")
    public String getProductPage(Model model, @PathVariable long id) {
        Product product = this.productService.getProductById(id);
        model.addAttribute("product", product);
        return "client/product/detail";
    }

    @PostMapping("/add-product-to-cart/{id}")
    public String postAddProductToCart(@PathVariable long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        this.productService.handleAddProductToCart(id, email, session);
        return "redirect:/";
    }

    @PostMapping("/add-product-to-cart-from-detail/{id}")
    public String postAddProductToCartFromDetail(@PathVariable long id, HttpServletRequest request,
            @RequestParam("quantity") Long quantity) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        this.productService.handleAddProductToCartFromDetail(id, email, session, quantity);
        return "redirect:/product/" + id;
    }

    @GetMapping("/products")
    public String getProductPage(Model model, ProductCriteriaDTO productCriteriaDTO, HttpServletRequest request) {
        int page = 1;
        try {
            if (productCriteriaDTO.getPage().isPresent()) {
                // convert from String to int
                page = Integer.parseInt(productCriteriaDTO.getPage().get());
            }
        } catch (Exception e) {
        }
        Pageable pageable = PageRequest.of(page - 1, 3);
        // check sort price
        if (productCriteriaDTO.getSort() != null && productCriteriaDTO.getSort().isPresent()) {
            String sort = productCriteriaDTO.getSort().get();
            if (sort.equals("gia-giam-dan")) {
                pageable = PageRequest.of(page - 1, 3, Sort.by(Product_.PRICE).descending());
            } else if (sort.equals("gia-tang-dan")) {
                pageable = PageRequest.of(page - 1, 3, Sort.by(Product_.PRICE).ascending());
            } else {
                pageable = PageRequest.of(page - 1, 3);
            }
        }
        // Page<Product> prs = this.productService.getAllProducts(pageable);
        Page<Product> prs = this.productService.getAllProductsSpecification(pageable, productCriteriaDTO);

        // case 1
        // double min = minOptional.isPresent() ? Double.parseDouble(minOptional.get())
        // : 0;
        // Page<Product> prs = this.productService.getAllProductsSpecification(pageable,
        // min);
        // case 2
        // double max = maxOptional.isPresent() ? Double.parseDouble(maxOptional.get())
        // : 0;
        // Page<Product> prs = this.productService.getAllProductsSpecification(pageable,
        // max);
        // case 3
        // String factory = factoryOptional.isPresent() ? factoryOptional.get() : "";
        // Page<Product> prs = this.productService.getAllProductsSpecification(pageable,
        // factory);
        // case 4
        // List<String> factory = Arrays.asList(factoryOptional.get().split(","));
        // Page<Product> prs = this.productService.getAllProductsSpecification(pageable,
        // factory);
        // case 5
        // String price = priceOptional.isPresent() ? priceOptional.get() : "";
        // Page<Product> prs = this.productService.getAllProductsSpecification(pageable,
        // price);
        // case 6
        // List<String> price = Arrays.asList(priceOptional.get().split(","));
        // Page<Product> prs = this.productService.getAllProductsSpecification(pageable,
        // price);
        List<Product> products = prs.getContent().size() > 0 ? prs.getContent()
                : new ArrayList<Product>();
        String qs = request.getQueryString();
        if (qs != null && !qs.isBlank()) {
            // remove page
            qs = qs.replace("page=" + page, "");
        }

        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", prs.getTotalPages());
        model.addAttribute("queryString", qs);
        return "client/product/show";
    }

}
