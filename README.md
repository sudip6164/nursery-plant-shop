# 🌱 Nursery Plant Shop

A Spring Boot-based e-commerce web application for selling nursery plants. This project provides a smooth shopping experience with features like product browsing, cart management, checkout, and an admin dashboard for managing users and products.

## 🚀 Features

### 🌿 User Features

#### Home Page
- Displays six latest products as featured items

#### Shop
- Browse products by category
- Search products by name
- Filter products using a price slider
- View product details by clicking on images
- Add products to the cart
- Update product quantity in the cart

#### Checkout & Payment
- Proceed to checkout and simulate payments using Stripe

#### User Account Management
- Login & Signup: Secure authentication using email and password
- Forgot Password: Reset password using OTP via Jakarta Mail API
- Session Management: Maintains login sessions using HttpSession
- Password Security: Uses Bcrypt for hashing
- Error Handling: Displays appropriate error messages for incorrect credentials or expired sessions

#### Additional Pages
- Contact Us: Includes a map and a contact form to send emails via Jakarta Mail API
- About Us: Static page with company details

### 🛠️ Admin & Staff Features

#### Dashboard
- Displays total customers, total products, and a table of customer users

#### User Management
- Admin only: Can access the user table to edit, delete, and assign roles
- Staff users: Cannot access the user management section

#### Product Management
- Add, edit, and delete products

#### Permissions & Access Control
- Displays error messages if users attempt to access restricted sections

## 🛑 Prerequisites

- Java 17
- Maven 3.2.4
- MySQL
- XAMPP (for MySQL)
- IDE (IntelliJ IDEA, Eclipse, Spring Tool Suite (recommended))
- Stripe Secret API Key

## 📂 Project Structure

```
src  
├── main  
│   ├── java/com/nurseryplantshop  
│   │   ├── controller  
│   │   ├── model  
│   │   ├── repository  
│   │   ├── service  
│   ├── resources  
│   │   ├── static  
│   │   ├── templates  
│   │   ├── application.properties  
├── test  
└── pom.xml
```

## 💾 Installation & Setup

1. Clone the repository:
```bash
git clone https://github.com/yourusername/nurseryplantshop.git
cd nurseryplantshop
```

2. Import Database:
   - Start XAMPP and enable MySQL and Apache
   - Open phpMyAdmin (http://localhost/phpmyadmin)
   - Create a new database named 'nurserydb'
   - Import the provided `nurserydb.sql` file into your database
   - This will set up all necessary tables, user roles, and create an admin account

3. Rename `application.properties.example` to `application.properties` and update:

Configure MySQL Database:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/#databasename/?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
```

Configure Jakarta Mail API:
```properties
spring.mail.username= # your email
spring.mail.password= # your app password (requires 2FA)
```
- How to get an app password: (https://itsupport.umd.edu/itsupport?id=kb_article_view&sysparm_article=KB0015112)

4. Get a Stripe API Key:
   - Visit Stripe's official website and sign up for an account
   - Go to the Developers section and click on API Keys
   - Copy the Secret Key and add it to your `application.properties` file:
   - Or - How to Stripe API Key: (https://docs.themeisle.com/article/2097-configuring-the-stripe-api-keys)
```properties
stripe.secretKey=your_stripe_secret_key
```

5. Start XAMPP and enable MySQL and Apache

6. Build and run the project:
   - Right click on project
   - Go to Run As
   - Select run as Spring Boot App

7. Access the application:
   - User Panel: http://localhost:8080
   - Admin Panel: http://localhost:8080/admin
   
### Admin Access
To access the admin panel, use these default credentials:
- Email: Admin@12345.com
- Password: Admin@12345

> Note: It's highly recommended to change these credentials after your first login or create a new admin account for security purposes.

## 💳 Simulating Stripe Payments (Test Mode)

### 1. Enable Test Mode in Stripe
- Log in to your Stripe Dashboard
- Toggle the "View test data" option to enable test mode

### 2. Use Stripe's Test Card Numbers

#### ✅ Successful Payment
```plaintext
Card Number: 4242 4242 4242 4242
Expiration Date: Any future date (e.g., 12/34)
CVC: Any 3 digits
```

#### ❌ Declined Payment
```plaintext
Card Number: 4000 0000 0000 0002
Expiration Date: Any future date
CVC: Any 3 digits
```

#### 🔒 Authentication Required (3D Secure)
```plaintext
Card Number: 4000 0025 0000 3155
Expiration Date: Any future date
CVC: Any 3 digits
```

#### 💰 Insufficient Funds
```plaintext
Card Number: 4000 0000 0000 9995
Expiration Date: Any future date
CVC: Any 3 digits
```

### 3. Perform the Test Payment
1. Navigate to your cart page (cart.html)
2. Proceed to checkout
3. Enter one of the test card numbers above
4. Complete the payment process

### 4. Verify Payment Status in Stripe Dashboard
- Go to Payments in your Stripe Dashboard
- Look for the recent transaction
- If successful, the status should be "Succeeded"
- If declined, the status will indicate the reason

## 🛠️ Technologies Used

- Backend: Spring Boot
- Frontend: Thymeleaf, HTML, CSS, JavaScript, Bootstrap
- Database: MySQL
- Authentication & Security: Jakarta Mail API, Bcrypt, HttpSession
- Payment Gateway: Stripe (simulated payments)
