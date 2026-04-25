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
        log.info("Deleting the 48 seeded Sri Lankan products...");

        String[] seededProducts = {
            "Fresh Carrots (1kg)", "Leeks (500g)", "Ambul Banana (1kg)", "Papaya (1kg)", 
            "Seer Fish / Thora (500g)", "Sprats / Halmassa (200g)", "Chicken Breast (1kg)", "Mutton (500g)",
            "Butter Cake (500g)", "Chocolate Cake (1kg)", "Munchee Super Cream Cracker", "Maliban Marie Biscuits", 
            "Roast Bread", "Sliced Sandwich Bread", "Fish Bun (Malu Paan)", "Sausage Bun",
            "Anchor Milk Powder (400g)", "Highland Fresh Milk (1L)", "Kotmale Cheese Wedges", "Happy Cow Cheese", 
            "Highland Set Yogurt", "Kotmale Drinking Yogurt", "Astra Fat Spread (250g)", "Anchor Butter (200g)",
            "Samba Rice (5kg)", "Red Kekulu Rice (5kg)", "Roasted Curry Powder (100g)", "Cinnamon Sticks (50g)", 
            "Pure Coconut Oil (1L)", "Sunflower Oil (1L)", "Canned Mackerel / Salmon", "Baked Beans (400g)",
            "Harischandra Coffee (100g)", "Nescafe Classic (50g)", "Dilmah Ceylon Tea (400g)", "Watawala Tea (200g)", 
            "Kist Nectar (1L)", "MD Mixed Fruit Cordial", "Elephant House EGB (1.5L)", "Coca Cola (1.5L)",
            "Cassava Chips (100g)", "Potato Chips (50g)", "Cashew Nuts (100g)", "Roasted Peanuts (100g)", 
            "Lemon Puff Biscuits", "Chocolate Chip Cookies", "Milk Toffee (100g)", "Fruity Mints"
        };

        for (String name : seededProducts) {
            productRepository.findByName(name).ifPresent(productRepository::delete);
        }

        log.info("Successfully deleted all 48 seeded products!");
    }

}
