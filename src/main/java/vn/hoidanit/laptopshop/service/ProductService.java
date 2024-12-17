package vn.hoidanit.laptopshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartDetailRepository cartDetailRepository;
    private final CartRepository cartRepository;
    private final UserService userService;

    public ProductService(ProductRepository productRepository, CartDetailRepository cartDetailRepository,
            CartRepository cartRepository, UserService userService) {
        this.productRepository = productRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.cartRepository = cartRepository;
        this.userService = userService;
    }

    public Product handleSaveProduct(Product newProduct) {
        return this.productRepository.save(newProduct);
    }

    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    public Product getProductById(long id) {
        return this.productRepository.findById(id);
    }

    public String getImageById(long id) {
        Product product = getProductById(id);
        return product.getImage();
    }

    public void deleteAProduct(long id) {
        this.productRepository.deleteById(id);
    }

    public void handleAddProductToCart(long id, String email, HttpSession session) {
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            Cart cart = this.cartRepository.findByUser(user);
            if (cart == null) {
                // Create new cart
                Cart otherCart = new Cart();
                otherCart.setUser(user);
                otherCart.setSum(0);

                cart = this.cartRepository.save(otherCart);
            }
            // save cart_detail
            Product product = this.productRepository.findById(id);
            // check card detail Exist product
            CartDetail cartOld = this.cartDetailRepository.findByCartAndProduct(cart, product);
            if (cartOld == null) {
                CartDetail cartDetail = new CartDetail();
                cartDetail.setCart(cart);
                cartDetail.setPrice(product.getPrice());
                cartDetail.setProduct(product);
                cartDetail.setQuantity(1);

                this.cartDetailRepository.save(cartDetail);

                // update sum of Cart (sum distinct Product)
                int sum = cart.getSum() + 1;
                cart.setSum(sum);
                session.setAttribute("sum", sum);
                cart = this.cartRepository.save(cart);
            } else {
                cartOld.setQuantity(cartOld.getQuantity() + 1);
                this.cartDetailRepository.save(cartOld);
            }

        }

    }

    public void handleRemoveCardDetails(long id, HttpSession session) {
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(id);
        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();
            Cart curCart = cartDetail.getCart();
            this.cartDetailRepository.deleteById(id);

            if (curCart.getSum() > 1) {
                int s = curCart.getSum() - 1;
                curCart.setSum(s);
                session.setAttribute("sum", s);
            } else {
                this.cartRepository.deleteById(curCart.getId());
                session.setAttribute("sum", 0);
            }
        }
    }

    public void handleCheckOut(List<Long> id, List<Long> quantity) {
        for (int i = 0; i < id.size(); i++) {
            Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(id.get(i));
            if (cartDetailOptional.isPresent()) {
                CartDetail cartDetail = cartDetailOptional.get();
                cartDetail.setQuantity(quantity.get(i));
                this.cartDetailRepository.save(cartDetail);
            }
        }
    }
}
