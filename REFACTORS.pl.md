# Opis Refaktoryzacji Projektu Automatu Sprzedającego

## O Projekcie

Aplikacja symulująca automat sprzedający (vending machine), umożliwiająca:
- **Tryb edycji**: dodawanie/usuwanie produktów, zarządzanie stanem automatu, zapisywanie konfiguracji do pliku
- **Tryb użytkownika**: przeglądanie dostępnych produktów, zakup produktów

Projekt został stworzony w Javie z interfejsem graficznym Swing i początkowo składał się z pojedynczych klas bez wyraźnej architektury.

---

## Główne Problemy Przed Refaktoryzacją

### - **Brak Separacji Odpowiedzialności**
- Klasy GUI zawierały logikę biznesową (np. `UserModeScreen` obsługiwała zarówno interfejs, jak i proces zakupu)
- Jedna klasa odpowiadała za wiele różnych zadań jednocześnie
- Trudność w testowaniu i modyfikacji kodu

### - **Ścisłe Powiązanie Komponentów**
- Bezpośrednie zależności między klasami (`ProductManager`, `FileOperations`)
- Brak abstrakcji - niemożliwa wymiana implementacji

### - **Hardcoded Wartości i Brak Konfiguracji**
- Ścieżki plików i limity wpisane bezpośrednio w kodzie

### - **Problemy z Typami Danych**
- Użycie `double` dla wartości finansowych (błędy zaokrągleń)

### - **Brak Walidacji**
- Możliwość utworzenia produktów z pustymi nazwami, ujemnymi cenami
- Walidacja (jeśli była) rozproszona po różnych miejscach
- Niespójny stan obiektów

### - **Niepotrzebne Abstrakcje**
- Puste klasy abstrakcyjne (`ManagingProducts`) bez realnej wartości
- Interfejsy wymuszające implementację niepotrzebnych metod (`ProductUtility`)

---

## 1. Przegląd Zmian

Projekt został poddany kompleksowej refaktoryzacji z monolitycznej aplikacji Swing do architektury wielowarstwowej. Nowa struktura dzieli aplikację na:
- **vending_machine_backend** - logika biznesowa, warstwa danych
- **vending_machine_gui** - warstwa prezentacji (interfejs użytkownika)

---

## 2. Wzorce Projektowe

Wzorce projektowe to sprawdzone rozwiązania typowych problemów w programowaniu obiektowym. W tym projekcie zastosowano 3 główne wzorce projektowe:

- **Strategy** - wymienne algorytmy persystencji
- **Builder** - konstrukcja złożonych obiektów
- **Singleton** - pojedyncza instancja konfiguracji

---

## 3. Przykłady użycia wzorców w kodzie

### 3.1 Strategy Design Pattern
**Lokalizacja:** `persistance/product/ProductPersistence.java`, `FileProductPersistence.java`

Strategia to behawioralny wzorzec projektowy, który pozwala definiować rodzinę algorytmów, hermetyzować każdy z nich i sprawiać, że są one wymienne. Wzorzec ten umożliwia zmianę algorytmu niezależnie od klienta, który go używa.

**Problem w starym kodzie:**
```java
public class FileOperations {
    private String filename = "TableData.txt";
    
    public void writeToFile(ArrayList<Product> products) {
        FileWriter writer = new FileWriter(filename);
        // Logika zapisu bezpośrednio w klasie
    }
}
```
- Konkretna implementacja zapisu do pliku na stałe wkompilowana
- Niemożliwa zmiana na inny mechanizm (baza danych, API) bez modyfikacji kodu
- Testowanie wymaga rzeczywistych operacji I/O

**Rozwiązanie - Strategia:**
```java
// Interfejs strategii
public interface ProductPersistence {
    void persist(Iterable<Product> product);
    List<Product> getPersisted();
}

// Konkretna strategia - plik
public class FileProductPersistence implements ProductPersistence {
    private final String filename = AppConfig.INSTANCE.getProductdataFilepath();
    
    @Override
    public void persist(Iterable<Product> products) {
        // Implementacja zapisu do pliku
    }
    
    @Override
    public List<Product> getPersisted() {
        // Implementacja odczytu z pliku
    }
}
```

**Zastosowanie w serwisie:**
```java
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductPersistence productPersistence; // Strategia wstrzykiwana
    
    public void persistProducts(Iterable<CreateProductDTO> products) {
        productPersistence.persist(...); // Użycie strategii
    }
}
```

**Korzyści:**
- **Wymienność**: Łatwo dodać `DatabaseProductPersistence` lub `ApiProductPersistence`
- **Testowalność**: Możliwość wstrzyknięcia mock'a
- **Open/Closed Principle**: Nowe strategie bez modyfikacji istniejącego kodu
- **Single Responsibility**: Każda strategia odpowiada tylko za swój mechanizm persystencji

---

### 3.2 Builder Design Pattern
**Lokalizacja:** `model/product/Product.java`, `CreateProductDTO.java`

Builder to kreacyjny wzorzec projektowy, który pozwala na stopniowe konstruowanie złożonych obiektów. Wzorzec ten oddziela proces tworzenia obiektu od jego reprezentacji, umożliwiając tworzenie różnych reprezentacji przy użyciu tego samego procesu konstrukcji.

**Implementacja - Builder DP:**
```java
public class Product {
    private UUID id;
    private String name;
    private int quantity;
    private BigDecimal price;
    
    // Wewnętrzna klasa Builder
    public static class ProductBuilder {
        private UUID id;
        private String name;
        private int quantity;
        private BigDecimal price;
        
        public ProductBuilder id(UUID id) {
            this.id = id;
            return this;
        }
        
        public ProductBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public ProductBuilder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }
        
        public ProductBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }
        
        public Product build() {
            return new Product(id, name, quantity, price);
        }
    }
    
    public static ProductBuilder builder() {
        return new ProductBuilder();
    }
}
```

**Przykład użycia:**
```java
// Czytelne tworzenie produktu
Product product = Product.builder()
    .name("Coca Cola")
    .quantity(10)
    .price(new BigDecimal("3.50"))
    .build();

// Można pominąć opcjonalne pola (np. id)
Product newProduct = Product.builder()
    .name("Pepsi")
    .quantity(5)
    .price(new BigDecimal("3.00"))
    .build(); // id będzie null, zostanie wygenerowane później
```

**Zastosowanie w kodzie:**
```java
// W FileProductPersistence podczas odczytu z pliku
products.add(
    Product.builder()
        .name(name)
        .quantity(quantity)
        .price(price)
        .build()
);
```

**Korzyści:**
- **Czytelność**: Jasne określenie, która wartość do czego służy
- **Opcjonalność**: Możliwość pominięcia niektórych parametrów

---

### 3.3 Singleton Design Pattern
**Lokalizacja:** `config/AppConfig.java`

Singleton to kreacyjny wzorzec projektowy, który gwarantuje, że klasa ma tylko jedną instancję i zapewnia globalny punkt dostępu do tej instancji. Jest to szczególnie przydatne dla obiektów współdzielonych w całej aplikacji, takich jak konfiguracja (Trzeba jednak z tym uważać, nadmierne użycie tego wzorca jest uznawane za antywzorzec).

**Problem w starym kodzie:**
```java
public class FileOperations {
    private String filename = "TableData.txt"; // Hardcoded
}

public class ProductManager {
    // Brak centralnej konfiguracji - każda klasa ma własne wartości
}
```
- Wartości konfiguracyjne rozproszone po całej aplikacji
- Brak spójności - różne klasy mogą mieć różne wartości
- Trudna zmiana konfiguracji - wymaga modyfikacji wielu plików
- Niemożliwe zarządzanie środowiskami (dev/test/prod)

**Rozwiązanie - Singleton z inicjalizacją statyczną:**
```java
@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class AppConfig {
    private final int productContainerCapacity;
    private final String productdataFilepath;
    
    // Singleton - publiczna instancja
    public static final AppConfig INSTANCE;
    
    // Statyczny blok inicjalizacyjny - wykonuje się raz przy ładowaniu klasy
    static {
        Properties properties = new Properties();
        try (InputStream input = AppConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load application.properties", e);
        }
        
        int capacity = Integer.parseInt(
            properties.getProperty("product.container.capacity", "20")
        );
        String filepath = properties.getProperty(
            "productdata.filepath", "TableData.txt"
        );
        
        INSTANCE = new AppConfig(capacity, filepath);
    }
    
    // Prywatny konstruktor - niemożliwe tworzenie nowych instancji
    private AppConfig(int productContainerCapacity, String productdataFilepath) {
        this.productContainerCapacity = productContainerCapacity;
        this.productdataFilepath = productdataFilepath;
    }
}
```

**Plik konfiguracyjny `application.properties`:**
```properties
product.container.capacity=50
productdata.filepath=TableData.txt
```

**Użycie w kodzie:**
```java
// W ProductServiceImpl
private final int storageSize = AppConfig.INSTANCE.getProductContainerCapacity();

// W FileProductPersistence
private final String filename = AppConfig.INSTANCE.getProductdataFilepath();
```

**Korzyści:**
- **Centralizacja**: Cała konfiguracja w jednym miejscu
- **Spójność**: Wszystkie klasy używają tych samych wartości
- **Łatwa zmiana**: Modyfikacja tylko pliku `.properties`
- **Bezpieczeństwo**: Niemożliwe przypadkowe utworzenie nowej instancji
- **Globalny dostęp**: `AppConfig.INSTANCE` dostępne wszędzie

---

## 4. Zasady SOLID w Projekcie

### 4.1 Single Responsibility Principle (SRP)
**"Klasa powinna mieć tylko jeden powód do zmiany"**

#### Problem w starym kodzie:
Klasa `UserModeScreen` łamała zasadę SRP, będąc odpowiedzialną za:
1. Zarządzanie interfejsem graficznym
2. Logikę biznesową zakupu produktów
3. Zarządzanie stanem pieniędzy użytkownika
4. Interakcję z ProductManager

```java
public class UserModeScreen {
    private Money money = new Money();
    private ProductManager productManager = new ProductManager();
    
    // GUI + logika biznesowa w jednej klasie
    private void buyProduct(Product product) {
        if (product.getCount() <= 0) {
            throw new ProductOutOfStockException();
        }
        if ((money.getUserMoney() - product.getPrice()) < 0) {
            throw new NotEnoughMoneyException();
        }
        money.setUserMoney(money.getUserMoney() - product.getPrice());
        product.setCount(product.getCount() - 1);
        // + logika GUI
    }
}
```

#### Rozwiązanie - podział odpowiedzialności:

**ProductService** - tylko logika biznesowa:
```java
public interface ProductService {
    ProductDTO register(CreateProductDTO product);
    ProductDTO update(ProductDTO product);
    List<ProductDTO> findAll();
    void deleteById(UUID id);
}
```

**ProductRepository** - tylko dostęp do danych:
```java
public interface ProductRepository {
    List<Product> findAll();
    Product findById(UUID id);
    Product save(Product product);
    void deleteById(UUID id);
}
```

**ProductPersistence** - tylko persystencja:
```java
public interface ProductPersistence {
    void persist(Iterable<Product> product);
    List<Product> getPersisted();
}
```

**ProductMapper** - tylko konwersja:
```java
public class ProductMapper {
    public Product toEntity(CreateProductDTO dto) { ... }
    public ProductDTO toDTO(Product product) { ... }
}
```

**Efekt:** Każda klasa ma teraz **jeden powód do zmiany**:
- Repository zmienia się, gdy zmienia sposób przechowywania danych
- Service zmienia się, gdy zmienia logika biznesowa
- Persistence zmienia się, gdy zmienia format persystencji
- Mapper zmienia się, gdy zmienia struktura DTO/Entity

---

### 4.2 Open/Closed Principle (OCP)
**"Klasy powinny być otwarte na rozszerzenia, ale zamknięte na modyfikacje"**

#### Zastosowanie - Strategy Pattern dla Persistence:

**Stary kod - zamknięty na rozszerzenia:**
```java
public class FileOperations {
    private String filename = "TableData.txt";
    
    public void writeToFile(ArrayList<Product> products) {
        // Konkretna implementacja pliku
        // Aby dodać bazę danych, trzeba modyfikować klasę
    }
}
```

**Nowy kod - otwarty na rozszerzenia:**
```java
// Interfejs - nie wymaga modyfikacji
public interface ProductPersistence {
    void persist(Iterable<Product> product);
    List<Product> getPersisted();
}

// Implementacja 1 - plik
public class FileProductPersistence implements ProductPersistence {
    @Override
    public void persist(Iterable<Product> products) {
        // Zapis do pliku
    }
}

// Można dodać nową implementację BEZ modyfikacji istniejącego kodu
public class DatabaseProductPersistence implements ProductPersistence {
    @Override
    public void persist(Iterable<Product> products) {
        // Zapis do bazy danych
    }
}

// Można dodać kolejną implementację
public class ApiProductPersistence implements ProductPersistence {
    @Override
    public void persist(Iterable<Product> products) {
        // Wysłanie do API
    }
}
```

**Użycie w serwisie - bez zmian kodu:**
```java
public class ProductServiceImpl implements ProductService {
    private final ProductPersistence productPersistence; // Dowolna implementacja
    
    public void persistProducts(Iterable<CreateProductDTO> products) {
        productPersistence.persist(...); // Działa z każdą strategią
    }
}
```

**Efekt:** Można dodawać nowe strategie persystencji (rozszerzenia) bez modyfikacji istniejących klas.

---

### 4.3 Liskov Substitution Principle (LSP)
**"Obiekty klasy bazowej powinny móc być zastąpione obiektami klas pochodnych bez wpływu na poprawność programu"**

#### Zastosowanie w Repository:

```java
public interface ProductRepository {
    List<Product> findAll();
    Product findById(UUID id);
    Product save(Product product);
    void deleteById(UUID id);
}

// Implementacja in-memory
public class SyncInMemoryProductRepository implements ProductRepository {
    private Map<UUID, Product> products = new LinkedHashMap<>();
    
    @Override
    public Product save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("product can't be null");
        }
        if (product.getId() == null) {
            product.setId(UUID.randomUUID());
        }
        products.put(product.getId(), product);
        return product;
    }
    
    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}
```

**Użycie w serwisie:**
```java
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository; // Może być dowolna implementacja
    
    @Override
    public ProductDTO register(CreateProductDTO createProductDTO) {
        Product product = productMapper.toEntity(createProductDTO);
        return productMapper.toDTO(productRepository.save(product));
    }
}
```

**Możliwa przyszła implementacja - DatabaseProductRepository:**
```java
public class DatabaseProductRepository implements ProductRepository {
    private final DataSource dataSource;
    
    @Override
    public Product save(Product product) {
        // Zapis do bazy danych
        // Zachowuje ten sam kontrakt co SyncInMemoryProductRepository
    }
    
    @Override
    public List<Product> findAll() {
        // Odczyt z bazy danych
    }
}
```

**Efekt:** `ProductServiceImpl` działa identycznie niezależnie od implementacji Repository. Można zamienić `SyncInMemoryProductRepository` na `DatabaseProductRepository` bez zmiany zachowania serwisu.

---

### 4.4 Interface Segregation Principle (ISP)
**"Klienci nie powinni być zmuszani do zależności od interfejsów, których nie używają"**

#### Problem w starym kodzie:
```java
// Niepotrzebny interfejs wymuszający implementację
public interface ProductUtility {
    public String getName();
    public int getCount();
    public double getPrice();
}

public class Product implements ProductUtility {
    // Klasa zmuszona implementować wszystkie metody
    // nawet jeśli część nie jest potrzebna
}
```

#### Rozwiązanie - precyzyjne interfejsy:

**Interfejs Persistence - tylko operacje persystencji:**
```java
public interface ProductPersistence {
    void persist(Iterable<Product> product);
    List<Product> getPersisted();
    // Tylko metody związane z persystencją
}
```

**Interfejs Repository - tylko operacje CRUD:**
```java
public interface ProductRepository {
    List<Product> findAll();
    Product findById(UUID id);
    Product save(Product product);
    void deleteById(UUID id);
    // Tylko metody związane z dostępem do danych
}
```

**Interfejs Service - tylko logika biznesowa:**
```java
public interface ProductService {
    ProductDTO register(CreateProductDTO product);
    ProductDTO update(ProductDTO product);
    List<ProductDTO> findAll();
    void deleteById(UUID id);
    void reloadData();
    void persistProducts(Iterable<CreateProductDTO> products);
    // Tylko metody logiki biznesowej
}
```

**Efekt:** Każdy interfejs zawiera tylko metody potrzebne dla konkretnego użytkownika:
- `FileProductPersistence` implementuje tylko `ProductPersistence` (nie potrzebuje metod CRUD)
- `SyncInMemoryProductRepository` implementuje tylko `ProductRepository` (nie potrzebuje metod persystencji)
- Klient używający serwisu nie widzi szczegółów repository czy persistence

---

### 4.5 Dependency Inversion Principle (DIP)
**"Moduły wysokiego poziomu nie powinny zależeć od modułów niskiego poziomu. Oba powinny zależeć od abstrakcji"**

#### Problem w starym kodzie:
```java
public class ProductManager extends ManagingProducts {
    private List<Product> products = new ArrayList<>(); // Zależność od konkretnej implementacji
    
    public void addProduct(String name, int count, double price) {
        Product product = new Product(name, count, price);
        products.add(product); // Bezpośrednia manipulacja ArrayList
    }
}

// UserModeScreen zależny od konkretnych klas
public class UserModeScreen {
    private Money money = new Money();                      // Konkretna klasa
    private ProductManager productManager = new ProductManager(); // Konkretna klasa
}
```

#### Rozwiązanie - zależność od abstrakcji:

**Warstwa serwisowa - zależność od interfejsów:**
```java
@RequiredArgsConstructor // Dependency Injection przez konstruktor
public class ProductServiceImpl implements ProductService {
    // Zależność od ABSTRAKCJI, nie od konkretnych implementacji
    private final ProductRepository productRepository;      // Interfejs
    private final ProductPersistence productPersistence;    // Interfejs
    private final ProductMapper productMapper;              // Konkretna, ale bez stanu
    
    @Override
    public ProductDTO register(CreateProductDTO createProductDTO) {
        Product product = productMapper.toEntity(createProductDTO);
        // Używamy abstrakcji - nie wiemy czy to in-memory czy database
        return productMapper.toDTO(productRepository.save(product));
    }
    
    @Override
    public void persistProducts(Iterable<CreateProductDTO> products) {
        // Używamy abstrakcji - nie wiemy czy to plik, baza czy API
        productPersistence.persist(
            StreamSupport.stream(products.spliterator(), false)
                .map(productMapper::toEntity)
                .toList()
        );
    }
}
```

**Konfiguracja zależności (przykład):**
```java
// Tworzenie konkretnych implementacji
ProductRepository repository = new SyncInMemoryProductRepository();
ProductPersistence persistence = new FileProductPersistence();

// Wstrzykiwanie przez konstruktor (Dependency Injection)
ProductService service = new ProductServiceImpl(
    repository,  // Implementacja abstrakcji
    persistence  // Implementacja abstrakcji
);

// Łatwa zamiana implementacji bez zmiany ProductServiceImpl:
ProductRepository dbRepository = new DatabaseProductRepository();
ProductPersistence apiPersistence = new ApiProductPersistence();
ProductService newService = new ProductServiceImpl(dbRepository, apiPersistence);
```

**Diagram zależności:**
```
[ProductServiceImpl] (moduł wysokiego poziomu)
        ↓ zależy od
[ProductRepository Interface] ← implementuje ← [SyncInMemoryProductRepository]
[ProductPersistence Interface] ← implementuje ← [FileProductPersistence]
        ↑ (moduły niskiego poziomu zależą od tej samej abstrakcji)
```

**Korzyści DIP w projekcie:**
1. **Testowanie:** Łatwe mockowanie zależności
```java
// Test z mockami
ProductRepository mockRepo = mock(ProductRepository.class);
ProductPersistence mockPersist = mock(ProductPersistence.class);
ProductService service = new ProductServiceImpl(mockRepo, mockPersist);
```

2. **Elastyczność:** Zmiana implementacji bez modyfikacji serwisu
```java
// Produkcja - baza danych
new ProductServiceImpl(new DatabaseRepository(), new FilePersistence());

// Testy - in-memory
new ProductServiceImpl(new InMemoryRepository(), new MockPersistence());
```

3. **Loose Coupling:** Serwis nie zna szczegółów implementacji repository/persistence

---

## 5. Clean Code

### 5.1 Model Domenowy z Walidacją (Domain-Driven Design)

W projekcie zastosowano **bogaty model domenowy** (Rich Domain Model) w przeciwieństwie do **modelu anemicznego** (Anemic Domain Model). Podejście to przenosi logikę walidacji i reguł biznesowych bezpośrednio do obiektów domenowych, co zapewnia spójność danych i lepszą kontrolę nad stanem aplikacji.

#### Problem - Model Anemiczny (stary kod):
```java
public class Product {
    private String name;
    private int count;
    private double price;
    
    // Tylko gettery i settery - brak walidacji
    public void setName(String name) {
        this.name = name; // Akceptuje null i puste stringi
    }
    
    public void setCount(int count) {
        this.count = count; // Akceptuje liczby ujemne
    }
}
```

**Problemy:**
- Brak walidacji danych przy ustawianiu wartości
- Możliwość utworzenia produktu z nieprawidłowymi danymi
- Walidacja musiałaby być rozrzucona po całej aplikacji
- Niespójna kontrola nad stanem obiektu

#### Rozwiązanie - Bogaty Model Domenowy:
```java
@Getter
@Setter
public class Product {
    private UUID id;
    private String name;
    private int quantity;
    private BigDecimal price;

    // Walidacja w konstruktorze - niemożliwe utworzenie nieprawidłowego produktu
    public Product(UUID id, String name, int quantity, BigDecimal price) {
        this.id = id;
        setName(name);       // Używa settera z walidacją
        setQuantity(quantity); // Używa settera z walidacją
        setPrice(price);      // Używa settera z walidacją
    }

    // Walidacja w setterze - niemożliwe ustawienie nieprawidłowej nazwy
    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new ProductDomainDataException("Name cannot be null or empty");
        }
        this.name = name;
    }

    // Walidacja - tylko nieujemne wartości
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new ProductDomainDataException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    // Walidacja - cena nie może być null ani ujemna
    public void setPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductDomainDataException("Price cannot be null or negative");
        }
        this.price = price;
    }
}
```

**Efekt:** Warstwy wyższe (Service, Repository) nie muszą się martwić o walidację - obiekt domenowy sam dba o swoją poprawność.

---
