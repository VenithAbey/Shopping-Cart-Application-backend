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
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${ADMIN_EMAIL:#{null}}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:#{null}}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        // Wipe if old mismatched categories exist
        boolean needsWipe = categoryRepository.findAll().stream()
                .anyMatch(c -> c.getName().equals("Dairy & Essentials") || c.getName().equals("Biscuits & Snacks")
                        || c.getName().equals("Beverages") || c.getName().equals("Australian Cavendish"));

        if (needsWipe) {
            log.warn("Old category schema detected! Wiping and re-seeding full Sri Lankan catalog...");
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
            jdbcTemplate.execute("TRUNCATE TABLE order_items;");
            jdbcTemplate.execute("TRUNCATE TABLE orders;");
            jdbcTemplate.execute("TRUNCATE TABLE products;");
            jdbcTemplate.execute("TRUNCATE TABLE categories;");
            jdbcTemplate.execute("DELETE FROM users WHERE role = 'admin';");
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
            log.warn("Wipe complete!");
        } else if (categoryRepository.count() > 0 && userRepository.countByRole(User.Role.admin) > 0) {
            log.info("Full Sri Lankan catalog already seeded. Skipping.");
            return;
        }

        seedCategories();
        seedProducts();
        seedMasterAdmin();
    }

    private void seedMasterAdmin() {
        if (userRepository.countByRole(User.Role.admin) == 0) {
            if (adminEmail != null && adminPassword != null) {
                log.info("Seeding Master Admin from environment variables: {}", adminEmail);
                userRepository.save(User.builder()
                        .name("Master Administrator")
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(User.Role.admin)
                        .build());
            } else {
                log.error("CRITICAL: ADMIN_EMAIL/ADMIN_PASSWORD env vars missing. No admin seeded.");
            }
        }
    }

    private Category cat(String name) {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    private void p(Category c, String name, String desc, double price, int stock, String img) {
        productRepository.save(Product.builder()
                .category(c).name(name).description(desc)
                .price(new BigDecimal(String.format("%.2f", price)))
                .stock(stock).imageUrl(img).build());
    }

    private void seedCategories() {
        categoryRepository.save(Category.builder().name("Fresh Food").description("Vegetables, Fruits, Seafood & Meat").build());
        categoryRepository.save(Category.builder().name("Bakery & Sweets").description("Cakes, Biscuits, Bread & Pastries").build());
        categoryRepository.save(Category.builder().name("Dairy & Eggs").description("Milk, Cheese, Yogurt & Butter").build());
        categoryRepository.save(Category.builder().name("Pantry").description("Grains, Spices, Oils & Canned Goods").build());
        categoryRepository.save(Category.builder().name("Drinks").description("Coffee, Tea, Juice & Soft Drinks").build());
        categoryRepository.save(Category.builder().name("Snacks").description("Chips, Nuts, Cookies & Candy").build());
        log.info("Seeded 6 categories aligned with frontend.");
    }

    private void seedProducts() {
        Category fresh   = cat("Fresh Food");
        Category bakery  = cat("Bakery & Sweets");
        Category dairy   = cat("Dairy & Eggs");
        Category pantry  = cat("Pantry");
        Category drinks  = cat("Drinks");
        Category snacks  = cat("Snacks");

        // =====================================================================
        // FRESH FOOD
        // =====================================================================

        // --- Vegetables (keyword "vegetables" in description) ---
        p(fresh, "Farm Fresh Green Beans 500g",      "Crisp locally grown vegetables from Nuwara Eliya",    190, 150, "https://images.unsplash.com/photo-1567375698348-5d9d5ae99de0?w=400&auto=format&fit=crop");
        p(fresh, "Organic Gotukola Bunch",            "Nutritious leafy green vegetables, great for sambol",  80, 200, "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400&auto=format&fit=crop");
        p(fresh, "Fresh Murunga (Drumstick) 250g",   "Tender drumstick vegetables packed with nutrients",   120, 100, "https://images.unsplash.com/photo-1628556720838-8ba07c0cc028?w=400&auto=format&fit=crop");
        p(fresh, "Ceylon Karawila (Bitter Gourd) 3pc","Bitter gourd vegetables ideal for diabetic diets",   140, 120, "https://images.unsplash.com/photo-1601648764658-cf37e8c89b70?w=400&auto=format&fit=crop");
        p(fresh, "Fresh Leeks Bundle 500g",           "Tender Sri Lankan vegetables perfect for soups",      160, 180, "https://images.unsplash.com/photo-1518977956812-cd3dbadaaf31?w=400&auto=format&fit=crop");
        p(fresh, "Red Onions 1kg",                    "Essential cooking vegetables from Dambulla farms",    280, 300, "https://images.unsplash.com/photo-1508747703725-719777637510?w=400&auto=format&fit=crop");

        // --- Fruits (keyword "fruits") ---
        p(fresh, "King Coconut Thambili (each)",      "Refreshing king coconut — Sri Lanka's favourite natural fruits drink source", 120, 200, "https://images.unsplash.com/photo-1620768524985-a6a98e1e6e42?w=400&auto=format&fit=crop");
        p(fresh, "Ripe Papaya Medium",                "Sweet tropical fruits — naturally ripened in Sri Lanka",    350, 80,  "https://images.unsplash.com/photo-1526318472351-c75fcf070305?w=400&auto=format&fit=crop");
        p(fresh, "Ceylon Sweet Mango 1kg",            "Juicy seasonal tropical fruits at peak ripeness",           500, 100, "https://images.unsplash.com/photo-1601493700631-2b16ec4b4716?w=400&auto=format&fit=crop");
        p(fresh, "Ambul Banana Bunch (~12 pcs)",      "Small but sweet local fruits from southern Sri Lanka",      350, 150, "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=400&auto=format&fit=crop");
        p(fresh, "Fresh Woodapple (Beli) 500g",       "Exotic tropical fruits, excellent for beli juice",          300, 60,  "https://images.unsplash.com/photo-1531349434580-3d1ec2e93a11?w=400&auto=format&fit=crop");
        p(fresh, "Pineapple (Whole)",                 "Sweet Hawaiian-variety fruits grown in Sri Lanka",          450, 90,  "https://images.unsplash.com/photo-1550258987-190a2d41a8ba?w=400&auto=format&fit=crop");

        // --- Fresh Seafood (keyword "seafood") ---
        p(fresh, "Fresh Tuna (Thalapath) 1kg",        "Premium ocean fresh seafood caught off the Sri Lankan coast",      1100, 50, "https://images.unsplash.com/photo-1510130387422-82bed34b37e9?w=400&auto=format&fit=crop");
        p(fresh, "Tiger Prawns 500g",                 "Succulent jumbo seafood prawns — perfect for devilled dishes",      950, 60, "https://images.unsplash.com/photo-1565680018093-ebb6b9ab5460?w=400&auto=format&fit=crop");
        p(fresh, "Fresh Cuttlefish 500g",             "Tender cuttlefish seafood excellent for black curry",               700, 70, "https://images.unsplash.com/photo-1565680018434-b8f8cf5b1d6c?w=400&auto=format&fit=crop");
        p(fresh, "Seer Fish (Thora) Fillet 500g",     "Premium seafood fillet, Sri Lanka's most beloved table fish",       900, 45, "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=400&auto=format&fit=crop");
        p(fresh, "Fresh Sardines (Hurulla) 1kg",      "Small silver seafood fish ideal for spicy Sri Lankan curry",        600, 80, "https://images.unsplash.com/photo-1580822184713-fc5400e7fe10?w=400&auto=format&fit=crop");

        // --- Meat (keyword "meat") ---
        p(fresh, "Keells Krest Fresh Chicken 1kg",    "Farm-raised fresh chicken meat — antibiotic free",     900, 100, "https://images.unsplash.com/photo-1604503468506-a8da13d11bea?w=400&auto=format&fit=crop");
        p(fresh, "Fresh Beef (Boneless) 500g",        "Premium local beef meat cuts for curries and stews",   850, 60,  "https://images.unsplash.com/photo-1588168333986-5078d3ae3976?w=400&auto=format&fit=crop");
        p(fresh, "Pork Belly Strips 500g",            "Succulent pork meat strips ideal for black pork curry", 780, 50, "https://images.unsplash.com/photo-1558502676-9e4e2b43b7e8?w=400&auto=format&fit=crop");
        p(fresh, "Fresh Mutton Curry Cut 500g",       "Tender goat meat pieces, ideal for traditional curry",  950, 40, "https://images.unsplash.com/photo-1547496502-affa22e38b82?w=400&auto=format&fit=crop");
        p(fresh, "Chicken Drumsticks 6pc Pack",       "Juicy chicken meat drumsticks — great for grilling",    700, 80, "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?w=400&auto=format&fit=crop");

        // =====================================================================
        // BAKERY & SWEETS
        // =====================================================================

        // --- Cakes (keyword "cake") ---
        p(bakery, "Elephant House Black Forest Cake 500g", "Classic chocolate cake layered with cream and cherries",          1450, 30, "https://images.unsplash.com/photo-1558636508-e0db3814bd1d?w=400&auto=format&fit=crop");
        p(bakery, "Traditional Love Cake (Kalu Dodol Cake) 250g", "Rich Sri Lankan spiced love cake with cashews and rose water", 950, 40, "https://images.unsplash.com/photo-1586985289688-ca3cf47d3e6e?w=400&auto=format&fit=crop");
        p(bakery, "Kothmale Vanilla Swiss Roll Cake",      "Light and fluffy cream-filled sponge cake",                        350, 80, "https://images.unsplash.com/photo-1571115764595-644a1f56a55c?w=400&auto=format&fit=crop");
        p(bakery, "Watalappam Ceylon Coconut Cake 400g",   "Traditional Sri Lankan jaggery and coconut milk cake",             650, 50, "https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=400&auto=format&fit=crop");
        p(bakery, "Ribbon Cake Slice 200g",                "Colourful layered almond cake — a Sri Lankan party favourite",     420, 60, "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400&auto=format&fit=crop");

        // --- Biscuits (keyword "biscuits") ---
        p(bakery, "Munchee Super Cream Cracker 500g",  "Sri Lanka's #1 salted cream biscuits",                   350, 400, "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=400&auto=format&fit=crop");
        p(bakery, "Maliban Lemon Puff Biscuits 200g",  "Tangy citrus cream-filled crunchy biscuits",             150, 300, "https://images.unsplash.com/photo-1553909489-cd47e0907980?w=400&auto=format&fit=crop");
        p(bakery, "Munchee Tikiri Marie Biscuits 150g","Classic thin plain marie-style biscuits",                 80, 350, "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=400&auto=format&fit=crop");
        p(bakery, "Maliban Nice Biscuits 200g",         "Light coconut-flavoured biscuits, ideal for tea time",  120, 250, "https://images.unsplash.com/photo-1548365328-8c6db3220e4c?w=400&auto=format&fit=crop");
        p(bakery, "Munchee Butter Puff Biscuits 180g", "Buttery flaky layered puff biscuits",                   130, 280, "https://images.unsplash.com/photo-1486297678162-eb2a19b0a826?w=400&auto=format&fit=crop");

        // --- Bread (keyword "bread") ---
        p(bakery, "Gardenia Sandwich Loaf Bread 400g",  "Soft sliced sandwich bread, freshly baked daily",        250, 150, "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=400&auto=format&fit=crop");
        p(bakery, "Cargills Milk Bread 350g",            "Classic sweet milk bread roll — Sri Lankan breakfast staple", 220, 200, "https://images.unsplash.com/photo-1586444248902-2f64eddc13df?w=400&auto=format&fit=crop");
        p(bakery, "Whole Wheat Bread Loaf 400g",         "Nutritious wholegrain bread with added fibre",           280, 120, "https://images.unsplash.com/photo-1608198093002-ad4e005484ec?w=400&auto=format&fit=crop");
        p(bakery, "Ceylon Roti (Pol Roti) 6pc",          "Traditional Sri Lankan coconut flatbread — homestyle",   180, 100, "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400&auto=format&fit=crop");
        p(bakery, "Kade Brand Stuffed Bun (Egg Bread) 2pc", "Soft filled egg bread rolls — a local street food favourite", 160, 80, "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=400&auto=format&fit=crop");

        // --- Pastries (keyword "pastries") ---
        p(bakery, "Chicken Patties Pastries 2pc",        "Flaky golden pastries stuffed with spiced chicken mince", 250, 80, "https://images.unsplash.com/photo-1607908329629-2adefe8ebe66?w=400&auto=format&fit=crop");
        p(bakery, "Chinese Rolls Pastries 3pc",          "Crispy fried pastries with vegetable noodle filling",     220, 70, "https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=400&auto=format&fit=crop");
        p(bakery, "Keells Cutlet Pastries 4pc",          "Deep fried potato cutlet pastries with local spices",     280, 60, "https://images.unsplash.com/photo-1493770348161-369560ae357d?w=400&auto=format&fit=crop");
        p(bakery, "Cream Puff Pastries 3pc",             "Light choux pastries filled with fresh whipped cream",    320, 50, "https://images.unsplash.com/photo-1603532648955-039310d9ed75?w=400&auto=format&fit=crop");
        p(bakery, "Beef Short Eats Pastries 4pc",        "Savoury stuffed short eats pastries — Sri Lankan style",  300, 65, "https://images.unsplash.com/photo-1548369937-47519962c11a?w=400&auto=format&fit=crop");

        // =====================================================================
        // DAIRY & EGGS
        // =====================================================================

        // --- Milk (keyword "milk") ---
        p(dairy, "Anchor Full Cream Milk Powder 400g",   "Premium milk powder from New Zealand — Sri Lanka's favourite", 1000, 300, "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=400&auto=format&fit=crop");
        p(dairy, "Kotmale Fresh Pasteurized Milk 1L",    "100% locally sourced fresh chilled milk",                        450, 200, "https://images.unsplash.com/photo-1563636619-e9143da7973b?w=400&auto=format&fit=crop");
        p(dairy, "Nestomalt Malt Milk Powder 400g",      "Energy-boosting nutritious malt milk drink powder",              950, 150, "https://images.unsplash.com/photo-1515706886582-54c73c5eaf41?w=400&auto=format&fit=crop");
        p(dairy, "Ratthi Enriched Milk Powder 200g",     "Fortified milk powder with vitamins and minerals",               600, 180, "https://images.unsplash.com/photo-1628557044797-f21a177c37ec?w=400&auto=format&fit=crop");
        p(dairy, "Harischandra Soy Milk Powder 200g",    "Dairy-free soy milk alternative for lactose intolerance",        550, 100, "https://images.unsplash.com/photo-1600718374662-0483d2b9da44?w=400&auto=format&fit=crop");

        // --- Cheese (keyword "cheese") ---
        p(dairy, "Kotmale Processed Cheese Slices 200g", "Meltable processed cheese slices for sandwiches and burgers",    650, 100, "https://images.unsplash.com/photo-1618888007540-2bdead974bbb?w=400&auto=format&fit=crop");
        p(dairy, "Anchor Cheddar Cheese Block 250g",     "Mature cheddar cheese — great for cooking and snacking",         850, 80,  "https://images.unsplash.com/photo-1452195100486-9cc805987862?w=400&auto=format&fit=crop");
        p(dairy, "President Cheese Spread 140g",         "Creamy spreadable cheese for toast and crackers",                480, 120, "https://images.unsplash.com/photo-1588365036509-1a2f7e08ac75?w=400&auto=format&fit=crop");
        p(dairy, "Milco Mozzarella Cheese 200g",         "Stretchy mozzarella cheese perfect for Sri Lankan pizza lovers",  750, 60, "https://images.unsplash.com/photo-1609590981063-d495a4282e73?w=400&auto=format&fit=crop");
        p(dairy, "Danish Blue Cheese 150g",              "Bold and creamy imported blue cheese for gourmet palates",        950, 40, "https://images.unsplash.com/photo-1559561853-08451507cbe7?w=400&auto=format&fit=crop");

        // --- Yogurt (keyword "yogurt") ---
        p(dairy, "Kotmale Plain Yogurt 200ml",            "Fresh creamy Sri Lankan yogurt — natural and unsweetened",       180, 150, "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=400&auto=format&fit=crop");
        p(dairy, "Ambewela Strawberry Yogurt 100ml",      "Fruity flavoured yogurt from Ambewela Highland Farm",            120, 200, "https://images.unsplash.com/photo-1571212515416-fef01fc43dff?w=400&auto=format&fit=crop");
        p(dairy, "Cargills Diva Set Yogurt 200g",         "Thick set yogurt with rich creamy texture",                      200, 130, "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400&auto=format&fit=crop");
        p(dairy, "Nestle Milo Flavoured Yogurt 150ml",    "Chocolate malt yogurt — kids love this creamy treat",            160, 100, "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=400&auto=format&fit=crop");
        p(dairy, "Kotmale Mango Yogurt Drink 180ml",      "Refreshing mango-flavoured yogurt drink for on the go",          150, 120, "https://images.unsplash.com/photo-1548345680-f5475ea5df84?w=400&auto=format&fit=crop");

        // --- Butter (keyword "butter") ---
        p(dairy, "Anchor Unsalted Butter 227g",           "Premium New Zealand butter — ideal for baking",                  700, 120, "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=400&auto=format&fit=crop");
        p(dairy, "Keells Salted Butter 200g",             "Sri Lankan made butter with a hint of salt",                     580, 100, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&auto=format&fit=crop");
        p(dairy, "Astra Margarine Butter Spread 500g",   "Light vegetable-based butter spread — great for toast",           750, 150, "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=400&auto=format&fit=crop");
        p(dairy, "Lurpak Slightly Salted Butter 200g",   "Imported Danish butter — premium quality for discerning cooks",   950, 60, "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=400&auto=format&fit=crop");
        p(dairy, "Milco Ghee (Clarified Butter) 200g",   "Pure clarified butter ghee — traditional Sri Lankan cooking fat", 650, 80, "https://images.unsplash.com/photo-1483137140003-ae073b395549?w=400&auto=format&fit=crop");

        // =====================================================================
        // PANTRY
        // =====================================================================

        // --- Grains (keyword "grains") ---
        p(pantry, "Keeri Samba Rice 5kg",                "Long-grain premium rice — Sri Lanka's finest grains",            1200, 100, "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400&auto=format&fit=crop");
        p(pantry, "Nadu Rice Grains 5kg",                 "Traditional short-grain Sri Lankan white rice grains",           950, 120, "https://images.unsplash.com/photo-1536304993881-ff6e9eefa2a6?w=400&auto=format&fit=crop");
        p(pantry, "Harischandra Red Raw Rice 5kg",        "Healthy unpolished red rice grains — rich in fibre",            1050, 80,  "https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400&auto=format&fit=crop");
        p(pantry, "Harischandra Wheat Flour 1kg",         "All-purpose wheat flour grains for baking and cooking",          280, 200, "https://images.unsplash.com/photo-1556800572-1b8aeef2c54f?w=400&auto=format&fit=crop");
        p(pantry, "Kurakkan Flour 500g",                  "Traditional Sri Lankan finger millet grains flour",              320, 100, "https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400&auto=format&fit=crop");

        // --- Spices (keyword "spices") ---
        p(pantry, "Wijaya Roasted Curry Powder 100g",    "Bold blend of roasted Sri Lankan spices",                        180, 400, "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=400&auto=format&fit=crop");
        p(pantry, "Ceylon True Cinnamon Sticks 50g",     "Authentic Ceylon cinnamon — the finest cooking spices",          250, 200, "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=400&auto=format&fit=crop");
        p(pantry, "Turmeric Powder Spices 100g",         "Vibrant golden Sri Lankan turmeric spices for curries",          120, 300, "https://images.unsplash.com/photo-1571680322279-a226e6a4cc2a?w=400&auto=format&fit=crop");
        p(pantry, "Black Pepper Spices 50g",             "Fiery whole black peppercorns — essential kitchen spices",       180, 250, "https://images.unsplash.com/photo-1506368249639-73a05d6f6488?w=400&auto=format&fit=crop");
        p(pantry, "Cardamom Green Spices 25g",           "Aromatic green cardamom pods — luxury cooking spices",           350, 150, "https://images.unsplash.com/photo-1584473457406-6240486418e9?w=400&auto=format&fit=crop");

        // --- Oils (keyword "oil") ---
        p(pantry, "Coconut Oil Cold Pressed 1L",         "Pure virgin coconut oil — Sri Lanka's traditional cooking oil",  850, 150, "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=400&auto=format&fit=crop");
        p(pantry, "Sunflower Cooking Oil 1L",            "Light and healthy sunflower cooking oil for everyday use",        780, 200, "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=400&auto=format&fit=crop");
        p(pantry, "Soya Vegetable Oil 1L",               "Refined soya vegetable oil — affordable multipurpose oil",        650, 180, "https://images.unsplash.com/photo-1562426752-6bd7f0fb348a?w=400&auto=format&fit=crop");
        p(pantry, "Sesame Gingelly Oil 200ml",           "Traditional Sri Lankan sesame oil with nutty aroma",              580, 80,  "https://images.unsplash.com/photo-1543581251-8a0e4ea4e05e?w=400&auto=format&fit=crop");
        p(pantry, "Extra Virgin Olive Oil 500ml",        "Premium imported extra virgin olive oil for dressings",          1450, 60,  "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=400&auto=format&fit=crop");

        // --- Canned Goods (keyword "canned") ---
        p(pantry, "Kist Canned Tomato Sauce 400g",       "Sweet and tangy canned tomato sauce — a kitchen staple",         500, 250, "https://images.unsplash.com/photo-1472476443507-c7a5948772fc?w=400&auto=format&fit=crop");
        p(pantry, "VA Canned Fish in Brine 425g",        "Healthy mackerel canned fish in brine — high protein",            520, 200, "https://images.unsplash.com/photo-1607116667981-ff2b67879ea0?w=400&auto=format&fit=crop");
        p(pantry, "MD Canned Fruit Cocktail 560g",       "Tropical mixed fruit canned in light syrup for desserts",         480, 150, "https://images.unsplash.com/photo-1567306226416-28f0efdc88ce?w=400&auto=format&fit=crop");
        p(pantry, "Maggi Canned Peas & Carrots 400g",    "Ready-to-use canned mixed vegetables — convenient pantry staple", 380, 180, "https://images.unsplash.com/photo-1515543237350-b3eea1ec8082?w=400&auto=format&fit=crop");
        p(pantry, "MD Canned Corned Beef 340g",          "Tender seasoned canned beef — popular Sri Lankan breakfast item",  680, 120, "https://images.unsplash.com/photo-1518779578993-ec3579fee39f?w=400&auto=format&fit=crop");

        // =====================================================================
        // DRINKS
        // =====================================================================

        // --- Coffee (keyword "coffee") ---
        p(drinks, "Nescafe Classic Instant Coffee 200g", "Classic bold instant coffee — Sri Lanka's favourite morning brew", 1050, 200, "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=400&auto=format&fit=crop");
        p(drinks, "Bru Gold Instant Coffee 200g",        "Smooth South Asian instant coffee blend",                          850, 150, "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400&auto=format&fit=crop");
        p(drinks, "Moccona Freeze Dried Coffee 100g",    "Premium freeze-dried coffee for rich aromatic brew",              1250, 80,  "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400&auto=format&fit=crop");
        p(drinks, "Kotmale Gold Coffee 3in1 Sachets 20s","Convenient 3-in-1 coffee sachets with milk and sugar",             650, 120, "https://images.unsplash.com/photo-1498804103079-a6351b050096?w=400&auto=format&fit=crop");
        p(drinks, "Lavazza Espresso Beans Coffee 250g",  "Authentic Italian espresso coffee beans for barista-style brew",  1850, 50,  "https://images.unsplash.com/photo-1447933601403-0c6688de566e?w=400&auto=format&fit=crop");

        // --- Tea (keyword "tea") ---
        p(drinks, "Dilmah Premium Ceylon Tea 100 Bags",  "World-famous 100% pure Ceylon tea bags",                          750, 300, "https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=400&auto=format&fit=crop");
        p(drinks, "Lipton Yellow Label Tea 100 Bags",    "Classic Lipton yellow label blended tea bags",                    650, 250, "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400&auto=format&fit=crop");
        p(drinks, "Mlesna Earl Grey Loose Leaf Tea 100g","Luxury bergamot-infused Earl Grey loose leaf Ceylon tea",          850, 100, "https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=400&auto=format&fit=crop");
        p(drinks, "Link Samahan Herbal Tea 10 Sachet",   "Ayurvedic herbal tea blend — Sri Lanka's renowned cold remedy",   200, 400, "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=400&auto=format&fit=crop");
        p(drinks, "Basilur White Magic Green Tea 75g",   "Delicate white and green tea from Ceylon highlands",              680, 80,  "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400&auto=format&fit=crop");

        // --- Juice (keyword "juice") ---
        p(drinks, "Elephant House Passion Fruit Juice 1L","Tropical passion fruit juice — made in Sri Lanka",               380, 150, "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400&auto=format&fit=crop");
        p(drinks, "Kist Orange Juice Drink 500ml",        "Refreshing citrus orange juice drink for the whole family",      250, 200, "https://images.unsplash.com/photo-1621506289937-a8e4df240d0b?w=400&auto=format&fit=crop");
        p(drinks, "Richlife Mango Juice 1L",              "Pure Sri Lankan mango juice — smooth, sweet and tropical",       420, 120, "https://images.unsplash.com/photo-1546173159-315724a31696?w=400&auto=format&fit=crop");
        p(drinks, "Del Monte Pineapple Juice Tetra 250ml","100% pineapple juice with no added sugar",                       180, 180, "https://images.unsplash.com/photo-1563746098251-d35aef196e83?w=400&auto=format&fit=crop");
        p(drinks, "Kist Mixed Fruit Juice Drink 200ml",   "Tropical mixed fruit juice blend — refreshing and affordable",  120, 250, "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400&auto=format&fit=crop");

        // --- Soft Drinks (keyword "soft drinks") ---
        p(drinks, "Elephant House EGB Ginger Beer 1.5L",  "Iconic Sri Lankan extra ginger beer soft drinks",                300, 300, "https://images.unsplash.com/photo-1554866585-cd94860890b7?w=400&auto=format&fit=crop");
        p(drinks, "Coca-Cola Soft Drinks Can 330ml",       "Classic Cola soft drinks — always refreshing",                  150, 400, "https://images.unsplash.com/photo-1619472776361-1e2b05e2bc7d?w=400&auto=format&fit=crop");
        p(drinks, "Fanta Orange Soft Drinks 1.5L",         "Fun and fizzy orange flavoured soft drinks",                    220, 250, "https://images.unsplash.com/photo-1625772299848-391b6a87d7b3?w=400&auto=format&fit=crop");
        p(drinks, "Sprite Lemon Soft Drinks 1.5L",         "Crisp lemon-lime carbonated soft drinks",                       220, 220, "https://images.unsplash.com/photo-1631679706909-1844bbd07221?w=400&auto=format&fit=crop");
        p(drinks, "Elephant House Cream Soda Soft Drinks 1.5L","Pink sweet cream soda soft drinks — a Sri Lankan classic",  220, 180, "https://images.unsplash.com/photo-1624517787804-1bb8b276f30c?w=400&auto=format&fit=crop");

        // =====================================================================
        // SNACKS
        // =====================================================================

        // --- Chips (keyword "chips") ---
        p(snacks, "Rancrisp Cassava Chips 100g",          "Crispy Sri Lankan tapioca chips — light and crunchy",            300, 200, "https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=400&auto=format&fit=crop");
        p(snacks, "Lays Classic Salted Chips 90g",         "International favourite — lightly salted potato chips",          280, 300, "https://images.unsplash.com/photo-1599490659213-e2b9527bd087?w=400&auto=format&fit=crop");
        p(snacks, "Prawn Crackers Chips 100g",             "Airy crunchy prawn-flavoured chips — Sri Lankan party snack",    200, 150, "https://images.unsplash.com/photo-1560717789-0ac7c58ac3e8?w=400&auto=format&fit=crop");
        p(snacks, "Cheetos Crunchy Cheese Chips 80g",      "Cheesy puffed corn chips loved by kids and adults alike",       280, 180, "https://images.unsplash.com/photo-1612204103590-b910d49bd46d?w=400&auto=format&fit=crop");
        p(snacks, "Munchee Chick Bites Spicy Chips 90g",  "Spicy fried corn-based chips — a Sri Lankan office snack",       180, 200, "https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=400&auto=format&fit=crop");

        // --- Nuts (keyword "nuts") ---
        p(snacks, "Roasted Cashew Nuts 200g",              "Premium Sri Lankan cashew nuts — lightly roasted and salted",    850, 150, "https://images.unsplash.com/photo-1563208771-bcc3d8cda038?w=400&auto=format&fit=crop");
        p(snacks, "Mixed Nuts Party Pack 200g",            "Premium assorted nuts — cashews, almonds and peanuts blend",    950, 100, "https://images.unsplash.com/photo-1548247416-ec66f4900b2e?w=400&auto=format&fit=crop");
        p(snacks, "Roasted Peanuts (Kadala) 300g",         "Crunchy dry-roasted groundnut peanuts nuts — local favourite",  250, 300, "https://images.unsplash.com/photo-1567306226416-28f0efdc88ce?w=400&auto=format&fit=crop");
        p(snacks, "Pistachios Roasted Nuts 150g",          "Imported premium pistachios nuts — great for snacking",         1150, 60,  "https://images.unsplash.com/photo-1574184864703-3487b13f0edd?w=400&auto=format&fit=crop");
        p(snacks, "Almonds Raw Nuts 200g",                 "Nutritious whole natural almonds nuts — healthy energy boost",   980, 80,  "https://images.unsplash.com/photo-1508061253366-f7da158b6d46?w=400&auto=format&fit=crop");

        // --- Cookies (keyword "cookies") ---
        p(snacks, "Oreo Chocolate Cream Cookies 137g",    "Iconic sandwich cookies with sweet cream filling",                380, 250, "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=400&auto=format&fit=crop");
        p(snacks, "Maliban Ginger Snap Cookies 200g",     "Spicy ginger-flavoured snap cookies — a Sri Lankan classic",     150, 200, "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=400&auto=format&fit=crop");
        p(snacks, "Munchee Chocolate Cookies 150g",       "Rich chocolate chip flavoured crispy cookies",                   180, 220, "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=400&auto=format&fit=crop");
        p(snacks, "Digestive Biscuit Cookies 250g",       "Fibre-rich wholemeal digestive cookies for healthy snacking",    320, 180, "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&auto=format&fit=crop");
        p(snacks, "Bourbon Cream Cookies 150g",           "Classic cocoa biscuit cookies sandwiched with chocolate cream",  200, 160, "https://images.unsplash.com/photo-1553909489-cd47e0907980?w=400&auto=format&fit=crop");

        // --- Candy (keyword "candy") ---
        p(snacks, "Eclairs Toffee Candy Assorted 200g",   "Creamy caramel toffee candy — smooth and chewy treats",          280, 200, "https://images.unsplash.com/photo-1582058091597-bc9812a2b7e5?w=400&auto=format&fit=crop");
        p(snacks, "Mi Land White Milk Candy 150g",         "Sri Lankan favourite milky white candy squares",                 200, 250, "https://images.unsplash.com/photo-1594979879697-9ab905e33e3f?w=400&auto=format&fit=crop");
        p(snacks, "Halls Mentholyptus Candy 9pc",          "Cooling menthol-eucalyptus candy drops for throat relief",        80, 400, "https://images.unsplash.com/photo-1553909489-cd47e0907980?w=400&auto=format&fit=crop");
        p(snacks, "Polo Mint Candy Roll",                  "Classic minty polo ring candy — refreshing after meals",          50, 500, "https://images.unsplash.com/photo-1582058091597-bc9812a2b7e5?w=400&auto=format&fit=crop");
        p(snacks, "Harpo's Fizzy Pop Candy Bag 100g",     "Fun fizzy popping candy assortment for kids",                    180, 150, "https://images.unsplash.com/photo-1519340333755-56e9c1d04579?w=400&auto=format&fit=crop");

        log.info("Seeded 100+ authentic Sri Lankan products across 6 categories and 24 subcategories!");
    }
}
