# 📚 Online Bookstore Management System

## 📌 Project Overview
This project is an advanced web-based Online Bookstore Management System developed using Spring Boot and MySQL. It features a complete e-commerce flow—allowing users to browse books, manage accounts, place orders, and leave reviews—supercharged with **Local AI Integration (Llama 3)** to provide enterprise-level customer service and automated admin moderation.

---

## 🛠️ Technologies Used
- **Backend:** Java, Spring Boot, Spring Data JPA
- **Database:** MySQL
- **Frontend:** HTML5, CSS3, Bootstrap 5, JavaScript, Thymeleaf
- **AI Integration:** Ollama (Local Llama 3 LLM), RAG (Retrieval-Augmented Generation) Architecture
- **Version Control:** GitHub

---

## 👥 Team Members & Modules

| Member | Module |
|--------|--------|
| Hamsana | User Management |
| Sethumi | Author Management |
| Akindu | Review Management |
| Thenujaya | Admin Management |
| Dulara | Book Management |
| Keshara | Order Management & AI Integration |

---

## ⚙️ Core Features
- **User Registration & Security:** Secure login and account management.
- **Inventory Management:** Add, update, and delete books (Admin/Author).
- **Author Portal:** Secure invite-code system for new authors to publish books.
- **Order Processing:** Cart management and seamless order checkout.
- **Dynamic Catalog:** Advanced search, category filtering, and sorting.

---

## 🤖 AI-Powered Features (Enterprise Integration)
- 📚 **AI Librarian Chatbot (Strict RAG):** A floating customer service widget that reads the live MySQL database to answer customer questions strictly based on current inventory, preventing AI hallucinations.
- 🛡️ **Auto-Moderator & Sentiment Analysis:** Automatically scans new reviews to hide inappropriate language and uses AI to grade customer sentiment (Positive, Neutral, Negative).
- ✉️ **AI Apology Email Generator:** Empowers admins with a 1-click button to instantly draft professional, contextual apology emails based on specific negative reviews.
- 📊 **Amazon-Style Review Consensus:** AI automatically reads all public reviews for a book and generates a 2-sentence summary of the general customer consensus.
- ✍️ **AI Synopsis Enhancer:** A tool for authors to automatically rewrite rough book drafts into professional, captivating retail descriptions.

---

## 🧠 OOP Concepts Used
- **Encapsulation:** Protecting entity data using DTOs, private fields, and getters/setters.
- **Inheritance:** Extending Spring Security components and Base entities.
- **Polymorphism:** Method overriding in controllers and service implementations.
- **Abstraction:** Utilizing interface-based service layers (e.g., `OrderService` interface implemented by `OrderServiceImpl`).

---

## 📁 File Handling
- `receipt.txt` → Stores order receipts and transaction histories.
- `system_log.txt` → Stores system logs and errors.

---

## 🔄 Git Workflow
- Each member works on dedicated feature branches.
- Changes are merged into `main` using Pull Requests.
- Commit history is maintained for individual contribution tracking and version control.

---

## 🚀 How to Run the Project

1. Clone the repository.
2. Open in your preferred IDE (IntelliJ IDEA / Eclipse).
3. Configure your local MySQL database credentials in `application.properties`.
4. **For AI Features:** Download and install [Ollama](https://ollama.com/), open your terminal, and run `ollama run llama3` to start the local AI server.
5. Run the Spring Boot application.
6. Open your browser and navigate to `http://localhost:8080`.

---

## 📊 Evaluation Criteria
- CRUD Functionality
- OOP Implementation
- File Handling
- UI Design & User Experience
- Advanced System Integrations (Local AI & RAG)
- GitHub Contributions
- Documentation & Viva Defense

---

## 📌 Notes
This project was developed by Group WD130 as part of academic coursework to demonstrate the practical implementation of software engineering concepts, database architecture, and modern AI integrations.