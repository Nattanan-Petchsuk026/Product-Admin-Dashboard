# Produt Admin dashborad Spring Boot Application

This project is a Spring Boot application for managing a product store. It uses XAMPP as the database server and supports CRUD operations on products, including file uploads for product images.

## Prerequisites
- XAMPP
- Visual Studio Code (VS Code)


## Database Setup

1. Open XAMPP Control Panel and start the MySQL module.
2. Open phpMyAdmin (usually accessible at [http://localhost/phpmyadmin/](http://localhost/phpmyadmin/)).
3. Create a new database named `firststore`.

## Configuration

1. Add MySQL dependency in `pom.xml`:
    ```xml
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    ```

2. Configure database connection in `src/main/resources/application.properties`:
    ```properties
    # Database connection settings
    spring.datasource.url=jdbc:mysql://localhost:3306/firststore?useSSL=false&serverTimezone=UTC
    spring.datasource.username=root
    spring.datasource.password=your_password

    # Hibernate settings
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
    ```
    Replace `your_password` with the actual password for your MySQL root user.

## Running the Application

1. Open a terminal and navigate to the project directory.
2. Run the application using Maven:
    ```sh
    mvn spring-boot:run
    ```
3. Open a web browser and go to [http://localhost:8080/products](http://localhost:8080/products) to view the product list.

## Usage

- **View Products**: Navigate to `/products` to see a list of all products.
- **Create Product**: Navigate to `/products/create` to add a new product.
- **Edit Product**: Navigate to `/products/edit/{id}` to edit an existing product.
- **Delete Product**: Navigate to `/products/delete/{id}` to delete a product.

## Classes Overview

### Controllers

- **ProductsControllers.java**
    - Handles HTTP requests related to product operations.
    - Methods:
        - `showProductList(Model model)`: Displays a list of all products.
        - `showCreatePage(Model model)`: Displays the page to create a new product.
        - `createProduct(ProductDto productDto, BindingResult result)`: Handles the creation of a new product.
        - `showEditPage(Model model, int id)`: Displays the page to edit an existing product.
        - `updateProduct(Model model, int id, ProductDto productDto, BindingResult result)`: Handles updating an existing product.
        - `deleteProduct(int id, Model model)`: Handles deleting a product.

### Models

- **Product.java**
    - Represents the `Product` entity stored in the database.
    - Fields: `id`, `name`, `brand`, `category`, `price`, `description`, `createdAt`, `imageFileName`
    - Getters and Setters for each field.

- **ProductDto.java**
    - Data Transfer Object for product data, used for validation during create and update operations.
    - Fields: `name`, `brand`, `category`, `price`, `description`, `imageFile`
    - Getters and Setters for each field.
    - Validation annotations to enforce constraints on the fields.

### Services

- **ProductRepository.java**
    - Repository interface for performing CRUD operations on `Product` entities.
    - Extends `JpaRepository<Product, Integer>` to inherit methods for basic CRUD operations.


