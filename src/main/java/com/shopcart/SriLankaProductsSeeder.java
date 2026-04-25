package com.shopcart;

import com.shopcart.entity.Category;
import com.shopcart.entity.Product;
import com.shopcart.repository.CategoryRepository;
import com.shopcart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SriLankaProductsSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.findByName("Fresh Carrots (1kg)").isPresent()) {
            log.info("Sri Lankan products already seeded, skipping SriLankaProductsSeeder");
            return;
        }

        log.info("Seeding Sri Lankan essential products...");

        Category freshFood = getOrCreateCategory("Fresh Food");
        Category bakery = getOrCreateCategory("Bakery & Sweets");
        Category dairy = getOrCreateCategory("Dairy & Eggs");
        Category pantry = getOrCreateCategory("Pantry");
        Category drinks = getOrCreateCategory("Drinks");
        Category snacks = getOrCreateCategory("Snacks");

        // Fresh Food
        addProduct("Fresh Carrots (1kg)", "Freshly harvested carrots from Nuwara Eliya — Vegetables", 450.00, freshFood, "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=500&q=80");
        addProduct("Leeks (500g)", "Crisp and fresh leeks — Vegetables", 300.00, freshFood, "https://images.unsplash.com/photo-1628773822503-1234005c2a18?w=500&q=80");
        addProduct("Ambul Banana (1kg)", "Sweet local Ambul bananas — Fruits", 350.00, freshFood, "https://images.unsplash.com/photo-1571501679680-de32f1e7aad4?w=500&q=80");
        addProduct("Papaya (1kg)", "Fresh ripe papaya — Fruits", 250.00, freshFood, "https://images.unsplash.com/photo-1517282009859-f000ef1b44ea?w=500&q=80");
        addProduct("Seer Fish / Thora (500g)", "Premium cut Seer fish — Fresh Seafood", 2500.00, freshFood, "https://images.unsplash.com/photo-1615141982883-c7ad0e69fd62?w=500&q=80");
        addProduct("Sprats / Halmassa (200g)", "Dried local sprats — Fresh Seafood", 400.00, freshFood, "https://images.unsplash.com/photo-1580476262798-bddd9f4b7369?w=500&q=80");
        addProduct("Chicken Breast (1kg)", "Skinless boneless chicken breast — Meat", 1800.00, freshFood, "https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=500&q=80");
        addProduct("Mutton (500g)", "Fresh local mutton — Meat", 3000.00, freshFood, "https://images.unsplash.com/photo-1603048297172-c92544798d5e?w=500&q=80");

        // Bakery & Sweets
        addProduct("Butter Cake (500g)", "Soft and fluffy butter cake — Cakes", 800.00, bakery, "https://images.unsplash.com/photo-1557925923-33b251d5928f?w=500&q=80");
        addProduct("Chocolate Cake (1kg)", "Rich chocolate icing cake — Cakes", 2500.00, bakery, "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=500&q=80");
        addProduct("Munchee Super Cream Cracker", "Classic Sri Lankan cream crackers — Biscuits", 350.00, bakery, "https://images.unsplash.com/photo-1590080875515-ce84f74dd30f?w=500&q=80");
        addProduct("Maliban Marie Biscuits", "Perfect for tea time — Biscuits", 200.00, bakery, "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=500&q=80");
        addProduct("Roast Bread", "Traditional Sri Lankan roast paan — Bread", 120.00, bakery, "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500&q=80");
        addProduct("Sliced Sandwich Bread", "Soft white bread — Bread", 250.00, bakery, "https://images.unsplash.com/photo-1598373182133-52452f7691ef?w=500&q=80");
        addProduct("Fish Bun (Malu Paan)", "Spicy fish stuffed pastry — Pastries", 150.00, bakery, "https://images.unsplash.com/photo-1624695029645-817ab7ca17c6?w=500&q=80");
        addProduct("Sausage Bun", "Chicken sausage baked in soft dough — Pastries", 180.00, bakery, "https://images.unsplash.com/photo-1549590143-d5855148a9d5?w=500&q=80");

        // Dairy & Eggs
        addProduct("Anchor Milk Powder (400g)", "Full cream milk powder — Milk", 1200.00, dairy, "https://images.unsplash.com/photo-1563636619-e9143da7973b?w=500&q=80");
        addProduct("Highland Fresh Milk (1L)", "Locally sourced fresh milk — Milk", 450.00, dairy, "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=500&q=80");
        addProduct("Kotmale Cheese Wedges", "Creamy cheese wedges — Cheese", 850.00, dairy, "https://images.unsplash.com/photo-1486297678162-eb2a19b0a32d?w=500&q=80");
        addProduct("Happy Cow Cheese", "Classic spreadable cheese — Cheese", 1100.00, dairy, "https://images.unsplash.com/photo-1618164436241-4473940d1f5c?w=500&q=80");
        addProduct("Highland Set Yogurt", "Traditional set yogurt — Yogurt", 80.00, dairy, "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=500&q=80");
        addProduct("Kotmale Drinking Yogurt", "Strawberry flavored drinking yogurt — Yogurt", 150.00, dairy, "https://images.unsplash.com/photo-1571212515416-f28682977d24?w=500&q=80");
        addProduct("Astra Fat Spread (250g)", "Popular margarine spread — Butter", 450.00, dairy, "https://images.unsplash.com/photo-1588195538326-c5b1e9f80a1b?w=500&q=80");
        addProduct("Anchor Butter (200g)", "Pure creamery butter — Butter", 1200.00, dairy, "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=500&q=80");

        // Pantry
        addProduct("Samba Rice (5kg)", "Premium local samba rice — Grains", 1300.00, pantry, "https://images.unsplash.com/photo-1586201375761-83865001e8ac?w=500&q=80");
        addProduct("Red Kekulu Rice (5kg)", "Nutritious red raw rice — Grains", 1100.00, pantry, "https://images.unsplash.com/photo-1536304929831-ee1ca9d44906?w=500&q=80");
        addProduct("Roasted Curry Powder (100g)", "Spicy Sri Lankan curry powder — Spices", 250.00, pantry, "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=500&q=80");
        addProduct("Cinnamon Sticks (50g)", "Pure Ceylon cinnamon — Spices", 400.00, pantry, "https://images.unsplash.com/photo-1556910110-a5a63dfd393c?w=500&q=80");
        addProduct("Pure Coconut Oil (1L)", "100% natural coconut oil — Oils", 850.00, pantry, "https://images.unsplash.com/photo-1620619767323-b95a89183081?w=500&q=80");
        addProduct("Sunflower Oil (1L)", "Refined sunflower oil — Oils", 1100.00, pantry, "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500&q=80");
        addProduct("Canned Mackerel / Salmon", "Jack Mackerel in tomato sauce — Canned Goods", 650.00, pantry, "https://images.unsplash.com/photo-1534483509719-3feaee7c30da?w=500&q=80");
        addProduct("Baked Beans (400g)", "Baked beans in tomato sauce — Canned Goods", 450.00, pantry, "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?w=500&q=80");

        // Drinks
        addProduct("Harischandra Coffee (100g)", "Local pure coffee powder — Coffee", 450.00, drinks, "https://images.unsplash.com/photo-1559525839-b184a4d698c7?w=500&q=80");
        addProduct("Nescafe Classic (50g)", "Instant coffee — Coffee", 850.00, drinks, "https://images.unsplash.com/photo-1587049352847-4d4b12405451?w=500&q=80");
        addProduct("Dilmah Ceylon Tea (400g)", "Premium loose leaf black tea — Tea", 950.00, drinks, "https://images.unsplash.com/photo-1597481499750-3e6b22637e12?w=500&q=80");
        addProduct("Watawala Tea (200g)", "Strong local tea — Tea", 450.00, drinks, "https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=500&q=80");
        addProduct("Kist Nectar (1L)", "Mixed fruit nectar — Juice", 650.00, drinks, "https://images.unsplash.com/photo-1622597467836-f38240662c89?w=500&q=80");
        addProduct("MD Mixed Fruit Cordial", "Concentrated fruit cordial — Juice", 750.00, drinks, "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=500&q=80");
        inputs("Elephant House EGB (1.5L)", "Local ginger beer — Soft Drinks", 450.00, drinks, "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=500&q=80");
        inputs("Coca Cola (1.5L)", "Classic cola — Soft Drinks", 480.00, drinks, "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=500&q=80");

        // Snacks
        addProduct("Cassava Chips (100g)", "Spicy local manioc chips — Chips", 250.00, snacks, "https://images.unsplash.com/photo-1566478989037-e987b52b2207?w=500&q=80");
        addProduct("Potato Chips (50g)", "Salted potato chips — Chips", 300.00, snacks, "https://images.unsplash.com/photo-1613564834233-a0a7c53e05a8?w=500&q=80");
        addProduct("Cashew Nuts (100g)", "Roasted and salted cashews — Nuts", 1200.00, snacks, "https://images.unsplash.com/photo-1580281658626-ee379ebebedc?w=500&q=80");
        addProduct("Roasted Peanuts (100g)", "Spicy peanuts — Nuts", 200.00, snacks, "https://images.unsplash.com/photo-1597348989645-46b190ce4918?w=500&q=80");
        addProduct("Lemon Puff Biscuits", "Cream filled biscuits — Cookies", 250.00, snacks, "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=500&q=80");
        addProduct("Chocolate Chip Cookies", "Classic cookies — Cookies", 400.00, snacks, "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=500&q=80");
        addProduct("Milk Toffee (100g)", "Traditional local milk toffee — Candy", 350.00, snacks, "https://images.unsplash.com/photo-1582058091505-f87a2e55a40f?w=500&q=80");
        addProduct("Fruity Mints", "Assorted fruit mints — Candy", 150.00, snacks, "https://images.unsplash.com/photo-1582058091505-f87a2e55a40f?w=500&q=80");

        log.info("Sri Lankan essential products successfully seeded!");
    }
    
    private void inputs(String name, String desc, double price, Category category, String img) {
        addProduct(name, desc, price, category, img);
    }

    private void addProduct(String name, String desc, double price, Category category, String imageUrl) {
        if (productRepository.findByName(name).isEmpty()) {
            productRepository.save(Product.builder()
                    .name(name)
                    .description(desc)
                    .price(BigDecimal.valueOf(price))
                    .stock(50)
                    .category(category)
                    .imageUrl(imageUrl)
                    .build());
        }
    }

    private Category getOrCreateCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(Category.builder().name(name).build()));
    }
}
