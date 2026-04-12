package com.shopcart;

import com.shopcart.entity.Category;
import com.shopcart.entity.Product;
import com.shopcart.entity.User;
import com.shopcart.repository.CategoryRepository;
import com.shopcart.repository.ProductRepository;
import com.shopcart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedCategories();
        seedProducts();
        seedUsers();
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) {
            log.info("Database already seeded. Skipping initialization.");
            return;
        }

        categoryRepository.save(Category.builder().name("Fresh Food").description("Vegetables, Fruits, and fresh produce").build());
        categoryRepository.save(Category.builder().name("Bakery & Sweets").description("Breads, Cakes, and Pastries").build());
        categoryRepository.save(Category.builder().name("Dairy & Eggs").description("Milk, Cheese, Butter, and Eggs").build());
        categoryRepository.save(Category.builder().name("Pantry").description("Grains, Spices, and Canned Goods").build());
        categoryRepository.save(Category.builder().name("Drinks").description("Coffee, Tea, and Soft Drinks").build());
        categoryRepository.save(Category.builder().name("Snacks").description("Chips, Nuts, and Candy").build());

        log.info("Seeded 6 comprehensive categories");
    }

    private void seedProducts() {
        if (productRepository.count() > 0) return;

        Category freshFood = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Fresh Food")).findFirst().orElse(null);
        Category bakery = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Bakery & Sweets")).findFirst().orElse(null);
        Category dairy = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Dairy & Eggs")).findFirst().orElse(null);
        Category pantry = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Pantry")).findFirst().orElse(null);
        Category drinks = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Drinks")).findFirst().orElse(null);
        Category snacks = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Snacks")).findFirst().orElse(null);

        // --- FRESH FOOD ---
        if (freshFood != null) {
            productRepository.save(Product.builder().category(freshFood)
                    .name("Australian Cavendish Bananas").description("Sweet and fresh everyday bananas").price(new BigDecimal("1050.00")).stock(200)
                    .imageUrl("https://images.unsplash.com/photo-1571501679680-de32f1e7aad4?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(freshFood)
                    .name("Continental Cucumbers").description("Crisp and refreshing").price(new BigDecimal("690.00")).stock(150)
                    .imageUrl("https://images.unsplash.com/photo-1604977042946-1eeccf895fb8?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(freshFood)
                    .name("Hass Avocados Prepacked 1kg").description("Perfect for toast or guacamole").price(new BigDecimal("1450.00")).stock(80)
                    .imageUrl("https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(freshFood)
                    .name("Fuji Apples 1kg Pack").description("Juicy and crisp red apples").price(new BigDecimal("1150.00")).stock(120)
                    .imageUrl("https://images.unsplash.com/photo-1560806887-1e4cd0b6faa6?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(freshFood)
                    .name("Lemons Prepacked 500g").description("Tangy and fresh lemons").price(new BigDecimal("1100.00")).stock(100)
                    .imageUrl("https://images.unsplash.com/photo-1590502593747-42a996111139?q=80&w=400&auto=format&fit=crop").build());
        }

        // --- BAKERY & SWEETS ---
        if (bakery != null) {
            productRepository.save(Product.builder().category(bakery)
                    .name("Wholemeal Sliced Bread 750g").description("Soft, daily baked bread").price(new BigDecimal("850.00")).stock(60)
                    .imageUrl("https://images.unsplash.com/photo-1509440159596-0249088772ff?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(bakery)
                    .name("Butter Croissants 4 Pack").description("Flaky, buttery French-style croissants").price(new BigDecimal("1350.00")).stock(40)
                    .imageUrl("https://images.unsplash.com/photo-1549903072-7e6e0bedb7fb?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(bakery)
                    .name("Rich Chocolate Mud Cake").description("Decadent chocolate cake perfect for birthdays").price(new BigDecimal("4500.00")).stock(15)
                    .imageUrl("https://images.unsplash.com/photo-1578985545062-69928b1d9587?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(bakery)
                    .name("Choc Chip Cookies 400g").description("Crispy cookies baked fresh").price(new BigDecimal("1050.00")).stock(100)
                    .imageUrl("https://images.unsplash.com/photo-1499636136210-6f4ee915583e?q=80&w=400&auto=format&fit=crop").build());
        }

        // --- DAIRY & EGGS ---
        if (dairy != null) {
            productRepository.save(Product.builder().category(dairy)
                    .name("Full Cream Milk 2L").description("Rich and creamy fresh milk").price(new BigDecimal("950.00")).stock(150)
                    .imageUrl("https://images.unsplash.com/photo-1563636619-e9143da7973b?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(dairy)
                    .name("Free Range Eggs 12 Pack").description("Large, cage-free farm eggs").price(new BigDecimal("1650.00")).stock(90)
                    .imageUrl("https://images.unsplash.com/photo-1587486913049-53fc88980cfc?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(dairy)
                    .name("Cheddar Cheese Block 500g").description("Aged tasty cheddar cheese").price(new BigDecimal("2250.00")).stock(70)
                    .imageUrl("https://images.unsplash.com/photo-1618164436241-4473940d1f5c?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(dairy)
                    .name("Greek Style Yogurt 1kg").description("Thick, unsweetened natural yogurt").price(new BigDecimal("1800.00")).stock(60)
                    .imageUrl("https://images.unsplash.com/photo-1488477181946-6428a0291777?q=80&w=400&auto=format&fit=crop").build());
        }

        // --- PANTRY ---
        if (pantry != null) {
            productRepository.save(Product.builder().category(pantry)
                    .name("Jasmine Rice 5kg").description("Premium fragrant white rice").price(new BigDecimal("3600.00")).stock(50)
                    .imageUrl("https://images.unsplash.com/photo-1586201375761-83865001e8ac?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(pantry)
                    .name("Olive Oil Extra Virgin 500ml").description("First cold pressed olive oil").price(new BigDecimal("2550.00")).stock(100)
                    .imageUrl("https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(pantry)
                    .name("Pasta Spaghetti 500g").description("Classic durum wheat spaghetti").price(new BigDecimal("360.00")).stock(300)
                    .imageUrl("https://images.unsplash.com/photo-1551183053-bf91a1d81141?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(pantry)
                    .name("Tomato Pasta Sauce 500g").description("Rich napoletana style sauce").price(new BigDecimal("900.00")).stock(200)
                    .imageUrl("https://images.unsplash.com/photo-1601000938259-9e92002320b2?q=80&w=400&auto=format&fit=crop").build());
        }

        // --- DRINKS ---
        if (drinks != null) {
            productRepository.save(Product.builder().category(drinks)
                    .name("Roasted Coffee Beans 1kg").description("Medium dark roast espresso beans").price(new BigDecimal("6600.00")).stock(40)
                    .imageUrl("https://images.unsplash.com/photo-1559525839-b184a4d698c7?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(drinks)
                    .name("Orange Juice 2L").description("100% natural pulpy orange juice").price(new BigDecimal("1350.00")).stock(80)
                    .imageUrl("https://images.unsplash.com/photo-1622483767028-3f66f32aef97?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(drinks)
                    .name("Sparkling Mineral Water 1.25L").description("Crisp, bubbly natural water").price(new BigDecimal("450.00")).stock(250)
                    .imageUrl("https://images.unsplash.com/photo-1549465220-1a8b9238cd48?q=80&w=400&auto=format&fit=crop").build());
        }

        // --- SNACKS ---
        if (snacks != null) {
            productRepository.save(Product.builder().category(snacks)
                    .name("Potato Chips Original 175g").description("Thin, crispy, and lightly salted").price(new BigDecimal("1050.00")).stock(120)
                    .imageUrl("https://images.unsplash.com/photo-1566478989037-eade3f7ceabe?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(snacks)
                    .name("Roasted Almonds 400g").description("Crunchy, unsalted dry roasted nuts").price(new BigDecimal("2970.00")).stock(60)
                    .imageUrl("https://images.unsplash.com/photo-1508061253366-f7da158b6d46?q=80&w=400&auto=format&fit=crop").build());
            productRepository.save(Product.builder().category(snacks)
                    .name("Milk Chocolate Block 200g").description("Smooth and creamy dairy milk chocolate").price(new BigDecimal("1500.00")).stock(150)
                    .imageUrl("https://images.unsplash.com/photo-1548842698-c116d9972b22?q=80&w=400&auto=format&fit=crop").build());
        }

        log.info("Seeded 20+ comprehensive supermarket products");
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;

        userRepository.save(User.builder()
                .name("Demo User")
                .email("demo@example.com")
                .password(passwordEncoder.encode("demo123"))
                .role(User.Role.user)
                .build());

        userRepository.save(User.builder()
                .name("Admin User")
                .email("admin@example.com")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.admin)
                .build());

        log.info("Seeded demo users: demo@example.com / demo123, admin@example.com / admin123");
    }
}
