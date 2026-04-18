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

    // loremflickr returns relevant images by keyword — no hotlink protection!
    private static String img(String keyword) {
        return "https://loremflickr.com/400/400/" + keyword.replace(" ", ",");
    }

    @Override
    public void run(String... args) {
        boolean needsWipe = categoryRepository.findAll().stream()
                .anyMatch(c -> c.getName().equals("Dairy & Essentials")
                        || c.getName().equals("Biscuits & Snacks")
                        || c.getName().equals("Beverages"));

        if (needsWipe) {
            log.warn("Old category schema detected! Wiping and re-seeding...");
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
                log.info("Seeding Master Admin: {}", adminEmail);
                userRepository.save(User.builder()
                        .name("Master Administrator").email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(User.Role.admin).build());
            } else {
                log.error("CRITICAL: ADMIN_EMAIL/ADMIN_PASSWORD env vars missing!");
            }
        }
    }

    private Category cat(String name) {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    private void p(Category c, String name, String desc, double price, int stock, String imgKeyword) {
        productRepository.save(Product.builder()
                .category(c).name(name).description(desc)
                .price(new BigDecimal(String.format("%.2f", price)))
                .stock(stock).imageUrl(img(imgKeyword)).build());
    }

    private void seedCategories() {
        categoryRepository.save(Category.builder().name("Fresh Food").description("Vegetables, Fruits, Seafood & Meat").build());
        categoryRepository.save(Category.builder().name("Bakery & Sweets").description("Cakes, Biscuits, Bread & Pastries").build());
        categoryRepository.save(Category.builder().name("Dairy & Eggs").description("Milk, Cheese, Yogurt & Butter").build());
        categoryRepository.save(Category.builder().name("Pantry").description("Grains, Spices, Oils & Canned Goods").build());
        categoryRepository.save(Category.builder().name("Drinks").description("Coffee, Tea, Juice & Soft Drinks").build());
        categoryRepository.save(Category.builder().name("Snacks").description("Chips, Nuts, Cookies & Candy").build());
        log.info("Seeded 6 categories.");
    }

    private void seedProducts() {
        Category fresh  = cat("Fresh Food");
        Category bakery = cat("Bakery & Sweets");
        Category dairy  = cat("Dairy & Eggs");
        Category pantry = cat("Pantry");
        Category drinks = cat("Drinks");
        Category snacks = cat("Snacks");

        // ── FRESH FOOD ─ Vegetables ────────────────────────────────────────
        p(fresh, "Farm Fresh Green Beans 500g",         "Crisp locally grown vegetables from Nuwara Eliya",     190, 150, "green,beans,vegetable");
        p(fresh, "Organic Gotukola Bunch",              "Nutritious leafy green vegetables for sambol",           80, 200, "spinach,leaf,vegetable");
        p(fresh, "Fresh Murunga (Drumstick) 250g",      "Tender drumstick vegetables packed with nutrients",     120, 100, "drumstick,vegetable,food");
        p(fresh, "Ceylon Karawila (Bitter Gourd) 3pc",  "Bitter gourd vegetables ideal for diabetic diets",     140, 120, "bittergourd,vegetable,food");
        p(fresh, "Fresh Leeks Bundle 500g",             "Tender Sri Lankan vegetables perfect for soups",        160, 180, "leek,vegetable,food");
        p(fresh, "Red Onions 1kg",                     "Essential cooking vegetables from Dambulla farms",       280, 300, "onion,red,vegetable");

        // ── FRESH FOOD ─ Fruits ────────────────────────────────────────────
        p(fresh, "King Coconut Thambili (each)",        "Refreshing king coconut — Sri Lanka's favourite fruits", 120, 200, "coconut,tropical,fruit");
        p(fresh, "Ripe Papaya Medium",                  "Sweet tropical fruits — naturally ripened",             350,  80, "papaya,tropical,fruit");
        p(fresh, "Ceylon Sweet Mango 1kg",              "Juicy seasonal tropical fruits at peak ripeness",       500, 100, "mango,tropical,fruit");
        p(fresh, "Ambul Banana Bunch (~12 pcs)",        "Small but sweet local fruits from southern Sri Lanka",  350, 150, "banana,yellow,fruit");
        p(fresh, "Fresh Woodapple (Beli) 500g",         "Exotic tropical fruits, excellent for beli juice",      300,  60, "exotic,tropical,fruit");
        p(fresh, "Pineapple (Whole)",                   "Sweet pineapple fruits grown in Sri Lanka",             450,  90, "pineapple,tropical,fruit");

        // ── FRESH FOOD ─ Fresh Seafood ─────────────────────────────────────
        p(fresh, "Fresh Tuna (Thalapath) 1kg",          "Premium ocean fresh seafood caught off the Sri Lankan coast",  1100, 50, "tuna,fish,seafood");
        p(fresh, "Tiger Prawns 500g",                   "Succulent jumbo seafood prawns — perfect for devilled dishes",  950, 60, "prawn,shrimp,seafood");
        p(fresh, "Fresh Cuttlefish 500g",               "Tender cuttlefish seafood excellent for black curry",           700, 70, "squid,seafood,ocean");
        p(fresh, "Seer Fish (Thora) Fillet 500g",       "Premium seafood fillet, Sri Lanka's most beloved table fish",   900, 45, "fish,fillet,seafood");
        p(fresh, "Fresh Sardines (Hurulla) 1kg",        "Small silver seafood fish ideal for spicy curry",               600, 80, "sardine,fish,seafood");

        // ── FRESH FOOD ─ Meat ──────────────────────────────────────────────
        p(fresh, "Keells Fresh Chicken 1kg",            "Farm-raised fresh chicken meat — antibiotic free",    900, 100, "chicken,raw,meat");
        p(fresh, "Fresh Beef (Boneless) 500g",          "Premium local beef meat cuts for curries",            850,  60, "beef,raw,meat");
        p(fresh, "Pork Belly Strips 500g",              "Succulent pork meat strips for black pork curry",     780,  50, "pork,meat,raw");
        p(fresh, "Fresh Mutton Curry Cut 500g",         "Tender goat meat pieces, ideal for traditional curry", 950,  40, "mutton,lamb,meat");
        p(fresh, "Chicken Drumsticks 6pc Pack",         "Juicy chicken meat drumsticks — great for grilling",  700,  80, "chicken,drumstick,meat");

        // ── BAKERY & SWEETS ─ Cakes ────────────────────────────────────────
        p(bakery, "Elephant House Black Forest Cake 500g",       "Classic chocolate cake layered with cream",         1450, 30, "chocolate,cake,dessert");
        p(bakery, "Traditional Love Cake 250g",                  "Rich Sri Lankan spiced love cake with cashews",      950, 40, "cake,slice,dessert");
        p(bakery, "Kothmale Vanilla Swiss Roll Cake",            "Light and fluffy cream-filled sponge cake",          350, 80, "swiss,roll,cake");
        p(bakery, "Watalappam Ceylon Coconut Cake 400g",         "Traditional Sri Lankan jaggery and coconut cake",    650, 50, "coconut,pudding,dessert");
        p(bakery, "Ribbon Cake Slice 200g",                      "Colourful layered almond cake — party favourite",    420, 60, "birthday,cake,slice");

        // ── BAKERY & SWEETS ─ Biscuits ─────────────────────────────────────
        p(bakery, "Munchee Super Cream Cracker 500g",   "Sri Lanka's #1 salted cream biscuits",                 350, 400, "cracker,biscuit,food");
        p(bakery, "Maliban Lemon Puff Biscuits 200g",   "Tangy citrus cream-filled crunchy biscuits",           150, 300, "biscuit,cookie,snack");
        p(bakery, "Munchee Tikiri Marie Biscuits 150g", "Classic thin plain marie-style biscuits",               80, 350, "biscuit,marie,snack");
        p(bakery, "Maliban Nice Biscuits 200g",          "Light coconut-flavoured biscuits for tea time",        120, 250, "biscuit,coconut,snack");
        p(bakery, "Munchee Butter Puff Biscuits 180g",  "Buttery flaky layered puff biscuits",                  130, 280, "biscuit,butter,snack");

        // ── BAKERY & SWEETS ─ Bread ────────────────────────────────────────
        p(bakery, "Gardenia Sandwich Loaf Bread 400g",  "Soft sliced sandwich bread, freshly baked daily",      250, 150, "bread,loaf,bakery");
        p(bakery, "Cargills Milk Bread 350g",            "Classic sweet milk bread — Sri Lankan breakfast staple", 220, 200, "bread,milk,bakery");
        p(bakery, "Whole Wheat Bread Loaf 400g",         "Nutritious wholegrain bread with added fibre",         280, 120, "bread,wheat,bakery");
        p(bakery, "Ceylon Roti (Pol Roti) 6pc",          "Traditional Sri Lankan coconut flatbread",             180, 100, "roti,flatbread,bakery");
        p(bakery, "Stuffed Bun Egg Bread 2pc",           "Soft filled egg bread rolls — local street food",      160,  80, "bun,bread,bakery");

        // ── BAKERY & SWEETS ─ Pastries ─────────────────────────────────────
        p(bakery, "Chicken Patties Pastries 2pc",        "Flaky golden pastries stuffed with spiced chicken",   250, 80, "pastry,pie,food");
        p(bakery, "Chinese Rolls Pastries 3pc",          "Crispy fried pastries with vegetable noodle filling", 220, 70, "springroll,pastry,fried");
        p(bakery, "Keells Cutlet Pastries 4pc",          "Deep fried potato cutlet pastries with local spices", 280, 60, "cutlet,fried,pastry");
        p(bakery, "Cream Puff Pastries 3pc",             "Light choux pastries filled with fresh whipped cream", 320, 50, "creampuff,pastry,dessert");
        p(bakery, "Beef Short Eats Pastries 4pc",        "Savoury stuffed short eats pastries — Sri Lankan style", 300, 65, "samosa,pastry,snack");

        // ── DAIRY & EGGS ─ Milk ────────────────────────────────────────────
        p(dairy, "Anchor Full Cream Milk Powder 400g",   "Premium milk powder — Sri Lanka's favourite",         1000, 300, "milk,powder,dairy");
        p(dairy, "Kotmale Fresh Pasteurized Milk 1L",    "100% locally sourced fresh chilled milk",              450, 200, "milk,bottle,dairy");
        p(dairy, "Nestomalt Malt Milk Powder 400g",      "Energy-boosting nutritious malt milk drink",           950, 150, "milk,malt,drink");
        p(dairy, "Ratthi Enriched Milk Powder 200g",     "Fortified milk powder with vitamins and minerals",     600, 180, "milk,powder,dairy");
        p(dairy, "Harischandra Soy Milk Powder 200g",    "Dairy-free soy milk alternative",                      550, 100, "soymilk,milk,dairy");

        // ── DAIRY & EGGS ─ Cheese ──────────────────────────────────────────
        p(dairy, "Kotmale Processed Cheese Slices 200g", "Meltable processed cheese slices for sandwiches",     650, 100, "cheese,slice,dairy");
        p(dairy, "Anchor Cheddar Cheese Block 250g",     "Mature cheddar cheese — great for cooking",           850,  80, "cheddar,cheese,dairy");
        p(dairy, "President Cheese Spread 140g",         "Creamy spreadable cheese for toast and crackers",      480, 120, "cheese,spread,dairy");
        p(dairy, "Milco Mozzarella Cheese 200g",         "Stretchy mozzarella cheese for pizza lovers",         750,  60, "mozzarella,cheese,dairy");
        p(dairy, "Danish Blue Cheese 150g",              "Bold creamy imported blue cheese",                    950,  40, "blue,cheese,dairy");

        // ── DAIRY & EGGS ─ Yogurt ──────────────────────────────────────────
        p(dairy, "Kotmale Plain Yogurt 200ml",           "Fresh creamy Sri Lankan yogurt — natural",            180, 150, "yogurt,plain,dairy");
        p(dairy, "Ambewela Strawberry Yogurt 100ml",     "Fruity flavoured yogurt from Highland Farm",           120, 200, "yogurt,strawberry,dairy");
        p(dairy, "Cargills Diva Set Yogurt 200g",        "Thick set yogurt with rich creamy texture",            200, 130, "yogurt,set,dairy");
        p(dairy, "Nestle Milo Flavoured Yogurt 150ml",   "Chocolate malt yogurt — kids love this treat",         160, 100, "yogurt,chocolate,dairy");
        p(dairy, "Kotmale Mango Yogurt Drink 180ml",     "Refreshing mango yogurt drink for on the go",          150, 120, "yogurt,mango,drink");

        // ── DAIRY & EGGS ─ Butter ──────────────────────────────────────────
        p(dairy, "Anchor Unsalted Butter 227g",          "Premium New Zealand butter — ideal for baking",       700, 120, "butter,dairy,food");
        p(dairy, "Keells Salted Butter 200g",            "Sri Lankan made butter with a hint of salt",           580, 100, "butter,salted,dairy");
        p(dairy, "Astra Margarine Butter Spread 500g",  "Light vegetable-based butter spread for toast",        750, 150, "margarine,butter,spread");
        p(dairy, "Lurpak Slightly Salted Butter 200g",  "Imported Danish butter — premium quality",             950,  60, "butter,lurpak,dairy");
        p(dairy, "Milco Ghee Clarified Butter 200g",    "Pure clarified butter ghee — traditional cooking fat", 650,  80, "ghee,butter,dairy");

        // ── PANTRY ─ Grains ────────────────────────────────────────────────
        p(pantry, "Keeri Samba Rice 5kg",               "Long-grain premium Sri Lankan rice grains",            1200, 100, "rice,grain,food");
        p(pantry, "Nadu Rice Grains 5kg",               "Traditional short-grain Sri Lankan white rice grains",  950, 120, "rice,white,grain");
        p(pantry, "Harischandra Red Raw Rice 5kg",      "Healthy unpolished red rice grains rich in fibre",     1050,  80, "red,rice,grain");
        p(pantry, "Harischandra Wheat Flour 1kg",       "All-purpose wheat flour grains for baking",             280, 200, "flour,wheat,grain");
        p(pantry, "Kurakkan Flour 500g",                "Traditional Sri Lankan finger millet grains flour",     320, 100, "millet,grain,flour");

        // ── PANTRY ─ Spices ────────────────────────────────────────────────
        p(pantry, "Wijaya Roasted Curry Powder 100g",  "Bold blend of roasted Sri Lankan spices",               180, 400, "spice,curry,powder");
        p(pantry, "Ceylon True Cinnamon Sticks 50g",   "Authentic Ceylon cinnamon — the finest cooking spices", 250, 200, "cinnamon,spice,stick");
        p(pantry, "Turmeric Powder Spices 100g",       "Vibrant golden Sri Lankan turmeric spices for curries",  120, 300, "turmeric,spice,yellow");
        p(pantry, "Black Pepper Spices 50g",           "Fiery whole black peppercorns — essential kitchen spices", 180, 250, "pepper,black,spice");
        p(pantry, "Cardamom Green Spices 25g",         "Aromatic green cardamom pods — luxury cooking spices",   350, 150, "cardamom,spice,herb");

        // ── PANTRY ─ Oils ──────────────────────────────────────────────────
        p(pantry, "Coconut Oil Cold Pressed 1L",       "Pure virgin coconut oil — Sri Lanka's traditional cooking oil", 850, 150, "coconut,oil,bottle");
        p(pantry, "Sunflower Cooking Oil 1L",          "Light and healthy sunflower cooking oil",                780, 200, "sunflower,oil,cooking");
        p(pantry, "Soya Vegetable Oil 1L",             "Refined soya vegetable oil — multipurpose oil",          650, 180, "vegetable,oil,bottle");
        p(pantry, "Sesame Gingelly Oil 200ml",         "Traditional Sri Lankan sesame oil with nutty aroma",     580,  80, "sesame,oil,bottle");
        p(pantry, "Extra Virgin Olive Oil 500ml",      "Premium imported extra virgin olive oil for dressings", 1450,  60, "olive,oil,bottle");

        // ── PANTRY ─ Canned Goods ──────────────────────────────────────────
        p(pantry, "Kist Tomato Sauce 400g Canned",     "Sweet and tangy canned tomato sauce — a kitchen staple",  500, 250, "tomato,sauce,can");
        p(pantry, "VA Mackerel in Brine Canned Fish 425g", "Healthy mackerel canned fish in brine — high protein", 520, 200, "canned,fish,tin");
        p(pantry, "MD Canned Fruit Cocktail 560g",     "Tropical mixed fruit canned in light syrup for desserts",  480, 150, "canned,fruit,tin");
        p(pantry, "Maggi Canned Peas & Carrots 400g",  "Ready-to-use canned mixed vegetables — convenient staple", 380, 180, "canned,vegetable,tin");
        p(pantry, "MD Corned Beef Canned 340g",        "Tender seasoned canned beef — popular breakfast item",     680, 120, "canned,beef,tin");

        // ── DRINKS ─ Coffee ────────────────────────────────────────────────
        p(drinks, "Nescafe Classic Instant Coffee 200g",   "Classic bold instant coffee — morning essential",    1050, 200, "coffee,instant,cup");
        p(drinks, "Bru Gold Instant Coffee 200g",          "Smooth South Asian instant coffee blend",             850, 150, "coffee,hot,mug");
        p(drinks, "Moccona Freeze Dried Coffee 100g",      "Premium freeze-dried coffee for rich brew",          1250,  80, "coffee,jar,premium");
        p(drinks, "Kotmale Gold Coffee 3in1 Sachets 20s",  "Convenient 3-in-1 coffee sachets with milk",          650, 120, "coffee,sachet,instant");
        p(drinks, "Lavazza Espresso Beans Coffee 250g",    "Authentic Italian espresso coffee beans",            1850,  50, "coffee,beans,espresso");

        // ── DRINKS ─ Tea ───────────────────────────────────────────────────
        p(drinks, "Dilmah Premium Ceylon Tea 100 Bags",    "World-famous 100% pure Ceylon tea bags",              750, 300, "tea,bag,ceylon");
        p(drinks, "Lipton Yellow Label Tea 100 Bags",      "Classic Lipton blended tea bags",                     650, 250, "tea,bag,cup");
        p(drinks, "Mlesna Earl Grey Loose Leaf Tea 100g",  "Luxury bergamot-infused Earl Grey loose leaf tea",    850, 100, "tea,loose,leaf");
        p(drinks, "Link Samahan Herbal Tea 10 Sachet",     "Ayurvedic herbal tea blend — Sri Lanka's cold remedy", 200, 400, "herbal,tea,sachet");
        p(drinks, "Basilur White Magic Green Tea 75g",     "Delicate white and green tea from Ceylon highlands",   680,  80, "green,tea,cup");

        // ── DRINKS ─ Juice ─────────────────────────────────────────────────
        p(drinks, "Elephant House Passion Fruit Juice 1L", "Tropical passion fruit juice — made in Sri Lanka",    380, 150, "juice,passion,tropical");
        p(drinks, "Kist Orange Juice Drink 500ml",         "Refreshing citrus orange juice drink",                250, 200, "orange,juice,drink");
        p(drinks, "Richlife Mango Juice 1L",               "Pure Sri Lankan mango juice — smooth and tropical",   420, 120, "mango,juice,drink");
        p(drinks, "Del Monte Pineapple Juice 250ml",       "100% pineapple juice with no added sugar",            180, 180, "pineapple,juice,drink");
        p(drinks, "Kist Mixed Fruit Juice Drink 200ml",    "Tropical mixed fruit juice blend",                    120, 250, "fruit,juice,tropical");

        // ── DRINKS ─ Soft Drinks ───────────────────────────────────────────
        p(drinks, "Elephant House EGB Ginger Beer 1.5L",   "Iconic Sri Lankan extra ginger beer soft drinks",     300, 300, "ginger,beer,soda");
        p(drinks, "Coca-Cola Can 330ml Soft Drinks",       "Classic Cola soft drinks — always refreshing",        150, 400, "cola,soda,drink");
        p(drinks, "Fanta Orange 1.5L Soft Drinks",         "Fun and fizzy orange flavoured soft drinks",          220, 250, "fanta,orange,soda");
        p(drinks, "Sprite Lemon 1.5L Soft Drinks",         "Crisp lemon-lime carbonated soft drinks",             220, 220, "sprite,lemon,soda");
        p(drinks, "Elephant House Cream Soda 1.5L Soft Drinks", "Pink sweet cream soda — a Sri Lankan classic",   220, 180, "soda,pink,drink");

        // ── SNACKS ─ Chips ─────────────────────────────────────────────────
        p(snacks, "Rancrisp Cassava Chips 100g",           "Crispy Sri Lankan tapioca chips",                     300, 200, "chips,crispy,snack");
        p(snacks, "Lays Classic Salted Chips 90g",         "Lightly salted potato chips",                         280, 300, "potato,chips,snack");
        p(snacks, "Prawn Crackers Chips 100g",             "Airy crunchy prawn-flavoured chips",                  200, 150, "cracker,prawn,snack");
        p(snacks, "Cheetos Crunchy Cheese Chips 80g",      "Cheesy puffed corn chips",                            280, 180, "cheetos,chips,snack");
        p(snacks, "Munchee Chick Bites Spicy Chips 90g",   "Spicy fried corn-based chips",                        180, 200, "corn,chips,spicy");

        // ── SNACKS ─ Nuts ──────────────────────────────────────────────────
        p(snacks, "Roasted Cashew Nuts 200g",              "Premium Sri Lankan cashew nuts — lightly roasted",    850, 150, "cashew,nuts,roasted");
        p(snacks, "Mixed Nuts Party Pack 200g",            "Premium assorted nuts — cashews, almonds and peanuts", 950, 100, "mixed,nuts,bowl");
        p(snacks, "Roasted Peanuts Kadala 300g",           "Crunchy dry-roasted groundnut peanuts nuts",          250, 300, "peanut,nuts,roasted");
        p(snacks, "Pistachios Roasted Nuts 150g",          "Imported premium pistachios nuts for snacking",      1150,  60, "pistachio,nuts,green");
        p(snacks, "Almonds Raw Nuts 200g",                 "Nutritious whole natural almonds nuts",               980,  80, "almond,nuts,raw");

        // ── SNACKS ─ Cookies ───────────────────────────────────────────────
        p(snacks, "Oreo Chocolate Cream Cookies 137g",    "Iconic sandwich cookies with sweet cream filling",     380, 250, "oreo,cookies,chocolate");
        p(snacks, "Maliban Ginger Snap Cookies 200g",     "Spicy ginger-flavoured snap cookies",                  150, 200, "ginger,cookies,biscuit");
        p(snacks, "Munchee Chocolate Cookies 150g",       "Rich chocolate chip flavoured crispy cookies",          180, 220, "chocolate,cookies,snack");
        p(snacks, "Digestive Biscuit Cookies 250g",       "Fibre-rich wholemeal digestive cookies",               320, 180, "digestive,cookies,biscuit");
        p(snacks, "Bourbon Cream Cookies 150g",           "Cocoa biscuit cookies with chocolate cream",           200, 160, "bourbon,cookies,chocolate");

        // ── SNACKS ─ Candy ─────────────────────────────────────────────────
        p(snacks, "Eclairs Toffee Candy Assorted 200g",  "Creamy caramel toffee candy — smooth and chewy",       280, 200, "toffee,candy,caramel");
        p(snacks, "Mi Land White Milk Candy 150g",        "Sri Lankan favourite milky white candy squares",        200, 250, "milk,candy,sweet");
        p(snacks, "Halls Mentholyptus Candy 9pc",         "Cooling menthol candy drops for throat relief",         80, 400, "mint,candy,menthol");
        p(snacks, "Polo Mint Candy Roll",                 "Classic minty polo ring candy — refreshing after meals", 50, 500, "mint,polo,candy");
        p(snacks, "Harpo's Fizzy Pop Candy Bag 100g",    "Fun fizzy popping candy assortment for kids",           180, 150, "candy,sweet,colourful");

        log.info("Seeded 100+ authentic Sri Lankan products with food-specific images!");
    }
}
