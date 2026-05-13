# 📚 Nexus Online Bookstore Management System

## 📌 Project Overview
This project is an advanced, enterprise-grade Online Bookstore Management System developed using Spring Boot and MySQL. It features a complete e-commerce flow—allowing users to browse books, save to wishlists, apply promotional codes, and experience a reactive single-page checkout. 

The platform features a **Dual-Model Local AI Architecture**, utilizing **Ollama Phi-3 Mini** for real-time frontend customer service, and **Llama 3** for heavy backend data processing (Review Moderation, Content Generation, and Summarization).

---

## 🛠️ Technologies & Architecture
- **Backend:** Java, Spring Boot, Spring Data JPA, Spring Security
- **Database:** MySQL
- **Frontend:** HTML5, CSS3, Bootstrap 5, JavaScript, Thymeleaf
- **AI Integration:** Ollama (Llama 3 & Phi-3 Mini), Prompt Engineering, RAG
- **Design Patterns:** MVC Architecture, Strategy Design Pattern
- **Version Control:** Git & GitHub

---

## 👥 Team Members & Modules

| Member | Core Responsibilities & Modules |
|--------|---------------------------------|
| **Hamsana** | **Support Ticket Management:** Planning and architecture for the future customer support ticketing module. |
| **Sithumi** | **Author Management:** Engineered the core Data classes, Entities, and backend architecture for the Author portal. |
| **Akindu** | **Promo Management:** Dynamic Promo Code discount engine, validation logic, and cart recalculation. |
| **Thenujaya** | **Admin & Broadcast Management:** Premium "Nexus" Dashboard UI unification, comprehensive admin routing, frontend polishing, and the real-time Announcement Broadcast Network. |
| **Dulara** | **Book & Wishlist Management:** Inventory CRUD, catalog display, and the Wishlist module. |
| **Keshara** | **Order Management & AI (Tech Lead):** Reactive single-page checkout, Strategy Pattern Payment Gateway, and the complete Dual-Model AI Integration (Llama 3 & Phi-3 Mini),And rest parts. |

---

## ⚙️ Core Technical Achievements

### 1. Dual-Model AI Integration
- **AI Librarian Chatbot (Phi-3 Mini):** A lightweight, low-latency customer service widget that reads the live MySQL database to answer customer questions strictly based on current inventory, preventing AI hallucinations.
- **Auto-Moderator & Sentiment Analysis (Llama 3):** Automatically scans new reviews to hide inappropriate language and uses the heavy LLM to grade customer sentiment (Positive, Neutral, Negative).
- **Review Consensus Generator (Llama 3):** AI automatically reads all public reviews for a book and generates a 2-sentence summary of the general customer consensus for the details page.
- **AI Apology Email Generator (Llama 3):** Empowers admins with a 1-click system to instantly draft professional, context-aware apology emails in response to negative customer feedback.
- **AI Synopsis Enhancer (Llama 3):** A specialized tool allowing authors to automatically rewrite and elevate rough book drafts into professional, captivating retail descriptions.

### 2. The Strategy Pattern Payment Gateway
Utilized the **Strategy Design Pattern** (`Payment.java` interface) to dynamically execute either `CardPayment` or `CashPayment` processing at runtime. This adheres strictly to the Open/Closed Principle, allowing the system to easily scale to accept PayPal or Crypto in the future without modifying core controller logic.

### 3. Reactive Single-Page Checkout
Engineered a seamless payment selection UI with dynamic DOM manipulation. Switching between "Cash on Delivery" and "Credit Card" instantly renders the appropriate delivery or secure gateway forms without requiring a server reload. Included simulated banking delays for a premium UX.

### 4. Unified Premium Admin Dashboard
Completely polished and unified the administrative frontend. Integrated all backend modules (Announcements, Promos, Users, Orders) into a cohesive, animated "Nexus Admin" architecture with seamless routing, ensuring a professional and consistent user experience.

### 5. Smart Wishlist & Promo Engine
Implemented a "Smart Toggle" wishlist button with real-time state awareness. Engineered a robust promo code validation system with dynamic `BigDecimal` cart recalculations to prevent floating-point currency errors during checkout.

---

## 🧠 Core OOP Concepts Demonstrated
- **Polymorphism & Abstraction:** Executing different payment behaviors through a shared `Payment` interface.
- **Encapsulation:** Protecting entity data using specialized Data Transfer Objects (DTOs) to securely pass cart items, promo codes, and delivery addresses between the frontend and backend.
- **Inheritance:** Extending Spring Security components and base entities to reduce code redundancy.

---

## 🚀 How to Run the Project

1. Clone the repository to your local machine.
2. Open the project in your preferred IDE (IntelliJ IDEA / Eclipse).
3. Configure your local MySQL database credentials in `application.properties`.
4. **For the AI Architecture:** Download and install [Ollama](https://ollama.com/). Open your terminal and run the following commands to start the local AI servers:
   - `ollama run llama3` (For Backend Moderation & Content Generation)
   - `ollama run phi3:mini` (For Frontend Chatbot)
5. Run the Spring Boot application.
6. Open your browser and navigate to `http://localhost:8080`.

---

## 📊 Viva Evaluation Focus Areas
- Database Schema Design & CRUD Functionality
- Object-Oriented Programming (OOP) & Design Patterns
- File Handling & Dynamic Document Generation
- UI/UX Design, Dashboard Unification, & Front-End Polish
- Dual-Model AI Architecture & Prompt Engineering
- GitHub Contributions & Branch Management
