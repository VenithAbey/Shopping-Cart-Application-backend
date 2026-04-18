package com.shopcart;

import com.shopcart.entity.Category;
import com.shopcart.entity.Product;
import com.shopcart.repository.CategoryRepository;
import com.shopcart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        // Detect if old generic products exist. If they do, wipe the tables to make room for Sri Lankan products!
        boolean needsWipe = productRepository.findAll().stream()
                .anyMatch(p -> p.getName().contains("Australian Cavendish"));
        
        if (needsWipe) {
            log.warn("Detecting old generic database! Wiping tables to migrate to Cargills Sri Lanka Database...");
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
            jdbcTemplate.execute("TRUNCATE TABLE order_items;");
            jdbcTemplate.execute("TRUNCATE TABLE orders;");
            jdbcTemplate.execute("TRUNCATE TABLE products;");
            jdbcTemplate.execute("TRUNCATE TABLE categories;");
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
            log.warn("Wipe Complete!");
        } else if (categoryRepository.count() > 0) {
            log.info("Sri Lankan database already seeded. Skipping initialization.");
            return;
        }

        seedCategories();
        seedProducts();
    }

    private void seedCategories() {
        categoryRepository.save(Category.builder().name("Dairy & Essentials").description("Milk, Eggs, and Butter").build());
        categoryRepository.save(Category.builder().name("Biscuits & Snacks").description("Crackers, Cookies, and Bites").build());
        categoryRepository.save(Category.builder().name("Beverages").description("Tea, Coffee, and Soft Drinks").build());
        categoryRepository.save(Category.builder().name("Pantry & Spices").description("Rice, Jam, and Condiments").build());
        categoryRepository.save(Category.builder().name("Personal Care").description("Soap, Shampoo, and Sanitary").build());

        log.info("Seeded 5 Sri Lankan Categories");
    }

    private void seedProducts() {
        Category dairy = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Dairy & Essentials")).findFirst().orElse(null);
        Category snacks = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Biscuits & Snacks")).findFirst().orElse(null);
        Category beverages = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Beverages")).findFirst().orElse(null);
        Category pantry = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Pantry & Spices")).findFirst().orElse(null);
        Category care = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Personal Care")).findFirst().orElse(null);

        // NOTE: ALL PRICES DISCOUNTED 100 LKR FROM STANDARD CARGILLS RETAIL LIST PRICE!

        // --- DAIRY ---
        if (dairy != null) {
            // Anchor Full Cream 400g - Retail ~1100, Our Price 1000
            productRepository.save(Product.builder().category(dairy)
                    .name("Anchor Full Cream Milk Powder 400g").description("Premium Sri Lankan favorite milk powder").price(new BigDecimal("1000.00")).stock(500)
                    .imageUrl("https://anchorbackend.vishwa.digital/wp-content/uploads/2023/07/FCM_400g_Carton-449x600.png").build());
            // Kothmale Fresh Milk 1L - Retail ~550, Our Price 450
            productRepository.save(Product.builder().category(dairy)
                    .name("Kotmale Fresh Milk Pasteurized 1L").description("100% locally sourced fresh milk").price(new BigDecimal("450.00")).stock(200)
                    .imageUrl("https://superbox.lk/cdn/shop/files/kothmale-freshmilk.jpg").build());
            // Astra Margarine 500g - Retail ~850, Our Price 750
            productRepository.save(Product.builder().category(dairy)
                    .name("Astra Margarine Fat Spread 500g").description("Ideal for baking and spreading").price(new BigDecimal("750.00")).stock(150)
                    .imageUrl("https://m.media-amazon.com/images/I/71rIe4h5c+L.jpg").build());
            // Kothmale Swiss Rolls - Retail ~400, Our price 300
            productRepository.save(Product.builder().category(dairy)
                    .name("Kothmale Vanilla Swiss Roll").description("Soft, jam and cream filled").price(new BigDecimal("300.00")).stock(100)
                    .imageUrl("https://www.kist.lk/images/products/kist-swiss-roll-vanilla-350g.jpg").build()); // generic roll
        }

        // --- BISCUITS & SNACKS ---
        if (snacks != null) {
            // Munchee Super Cream Cracker 500g - Retail ~450, Our Price 350
            productRepository.save(Product.builder().category(snacks)
                    .name("Munchee Super Cream Cracker 500g").description("Sri Lanka's number 1 cream cracker").price(new BigDecimal("350.00")).stock(400)
                    .imageUrl("https://tristarstore.lk/wp-content/uploads/2021/08/MUNCHEE-SUPER-CREAM-CRACKER-500g.png").build());
            // Maliban Lemon Puff 200g - Retail ~250, Our Price 150
            productRepository.save(Product.builder().category(snacks)
                    .name("Maliban Lemon Puff 200g").description("Tangy cream filled biscuits").price(new BigDecimal("150.00")).stock(300)
                    .imageUrl("https://cdn.goodiebite.com/media/catalog/product/cache/0316310243be4f57c5a939408d6d6787/m/a/maliban-lemon-puff-200g.jpg").build());
            // Munchee Chocolate Tikiri Marie - Retail ~180, Our Price 80
            productRepository.save(Product.builder().category(snacks)
                    .name("Munchee Chocolate Tikiri Marie").description("Chocolate coated marie magic").price(new BigDecimal("80.00")).stock(200)
                    .imageUrl("https://lassana.com/files/1660370617VlR7-1875-1033.jpg").build());
            // Cassava Chips - Retail ~400, Our Price 300
            productRepository.save(Product.builder().category(snacks)
                    .name("Rancrisp Cassava Chips 100g").description("Sri Lankan Manioc crisps").price(new BigDecimal("300.00")).stock(120)
                    .imageUrl("https://glomark.lk/media/images/product/p/rancrisp-cassava-chips-regular-100g.2e8e9db171f28b2e1bfbb1e3fdbe8bc6.jpg").build());
        }

        // --- BEVERAGES ---
        if (beverages != null) {
            // Elephant House Ginger Beer 1.5L - Retail ~400, Our Price 300
            productRepository.save(Product.builder().category(beverages)
                    .name("Elephant House EGB 1.5L").description("Authentic Sri Lankan ginger recipe").price(new BigDecimal("300.00")).stock(500)
                    .imageUrl("https://lassana.com/files/16551139420O1t-0242.jpg").build());
            // Dilmah Premium Ceylon Tea 400g - Retail ~950, Our Price 850
            productRepository.save(Product.builder().category(beverages)
                    .name("Dilmah Premium Ceylon Tea Loose Leaf 400g").description("100% Pure Ceylon Tea").price(new BigDecimal("850.00")).stock(250)
                    .imageUrl("https://m.media-amazon.com/images/I/81e5oGz4hKL.jpg").build());
            // Samahan - Retail ~300, Our Price 200
            productRepository.save(Product.builder().category(beverages)
                    .name("Link Samahan Herbal Tea (10 Pack)").description("Natural immunity booster").price(new BigDecimal("200.00")).stock(600)
                    .imageUrl("https://i.ebayimg.com/images/g/Jp4AAOSwy3dg39gT/s-l1200.jpg").build());
            // Milo 400g - Retail ~1300, Our Price 1200
            productRepository.save(Product.builder().category(beverages)
                    .name("Milo Chocolate Malt Drink 400g").description("Nourishing energy drink").price(new BigDecimal("1200.00")).stock(200)
                    .imageUrl("https://lassana.com/files/1654326083U33r-4011-1051.jpg").build());
        }

        // --- PANTRY & SPICES ---
        if (pantry != null) {
            // Kist Tomato Sauce 400g - Retail ~600, Our Price 500
            productRepository.save(Product.builder().category(pantry)
                    .name("Kist Tomato Sauce 400g").description("Classic sweet tomato ketchup").price(new BigDecimal("500.00")).stock(250)
                    .imageUrl("https://glomark.lk/media/images/product/p/kist-tomato-sauce-400g-383748.jpg").build());
            // MD Mixed Fruit Jam 485g - Retail ~650, Our Price 550
            productRepository.save(Product.builder().category(pantry)
                    .name("MD Mixed Fruit Jam 485g").description("Rich fruit jam from Sri Lanka").price(new BigDecimal("550.00")).stock(300)
                    .imageUrl("https://glomark.lk/media/images/product/p/md-mixed-fruit-jam-485g.011f0a202dc851b4feeb693994344d5d.jpg").build());
            // Keells Krest Sausages - Retail ~950, Our Price 850
            productRepository.save(Product.builder().category(pantry)
                    .name("Keells Krest Chicken Sausages 500g").description("Tastiest chicken sausages").price(new BigDecimal("850.00")).stock(150)
                    .imageUrl("https://keellskrest.lk/wp-content/uploads/2021/04/Chicken-M-500g.png").build());
            // Wijaya Curry Powder 100g - Retail ~280, Our Price 180
            productRepository.save(Product.builder().category(pantry)
                    .name("Wijaya Roasted Curry Powder 100g").description("Authentic blend of strong spices").price(new BigDecimal("180.00")).stock(400)
                    .imageUrl("https://wijayaproducts.lk/wp-content/uploads/2020/06/Roasted-Curry-Powder-e1591880927891.png").build());
            // Keeri Samba Rice - Retail ~1300, Our Price 1200
            productRepository.save(Product.builder().category(pantry)
                    .name("Cargills Quality Keeri Samba 5kg").description("Premium locally harvested rice").price(new BigDecimal("1200.00")).stock(100)
                    .imageUrl("https://tristarstore.lk/wp-content/uploads/2022/07/Keels-Keeri-Samba-5-.jpg").build());
        }

        // --- PERSONAL CARE ---
        if (care != null) {
            // Signal Toothpaste - Retail ~280, Our Price 180
            productRepository.save(Product.builder().category(care)
                    .name("Signal Strong Teeth Toothpaste 120g").description("Trusted cavity protection").price(new BigDecimal("180.00")).stock(300)
                    .imageUrl("https://lassana.com/files/1660370005m40A-0402-1463.jpg").build());
            // Sunlight Care - Retail ~380, Our Price 280
            productRepository.save(Product.builder().category(care)
                    .name("Sunlight Care Detergent Soap").description("Iconic fabric wash").price(new BigDecimal("280.00")).stock(400)
                    .imageUrl("https://kandosstore.com/wp-content/uploads/2023/10/Sunlight-Care-Detergent-Soap-115g.jpg").build());
            // Dettol Soap - Retail ~250, Our Price 150
            productRepository.save(Product.builder().category(care)
                    .name("Dettol Original Soap 100g").description("Antibacterial everyday protection").price(new BigDecimal("150.00")).stock(350)
                    .imageUrl("https://www.glomark.lk/media/images/product/p/dettol-original-soap-75g.3e614d99c4bd0c9f13d8036d649abfed.jpg").build());
        }

        log.info("Seeded 20 AUTHENTIC SRI LANKAN products successfully!");
    }
}
