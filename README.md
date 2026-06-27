# NexCart Web - Multi-Vendor Enterprise Management Platform 🌐🛒

NexCart Web is a robust, full-stack enterprise e-commerce portal engineered with vanilla Java EE web technologies. The platform delivers a highly secure, high-performance web architecture featuring role-based dashboards tailored for Customers, Independent Vendors, and Platform Administrators to manage massive retail workflows seamlessly.

---

## 🛠️ Tech Stack & Architecture

- **Backend Logic:** Java Servlets (Jakarta EE / Java EE), Object-Oriented Design (OOD)
- **Architecture Pattern:** Model-View-Controller (MVC) Architecture
- **Data Persistence:** Native JDBC, MySQL Relational Database (Optimized Schemas)
- **Frontend Layer:** Vanilla JavaScript, HTML5, CSS3, Bootstrap
- **Application Middleware:** Payara Enterprise Server / GlassFish Apache Tomcat
- **Security Protocols:** Session Tokenization, Role-Based Access Control (RBAC)

---

## 🚀 Key Engineering Features

### 1. Robust Core Java Servlet Middleware
- Engineered an **MVC-compliant web architecture** using native **Java Servlets** to handle synchronous and asynchronous HTTP requests cleanly.
- Implemented global **Servlet Filters** to intercept incoming traffic, enforce proper character encoding, and manage cross-origin request handling.

### 2. Enterprise Session Management & RBAC Security
- Designed a secure **Role-Based Access Control (RBAC)** matrix to strictly enforce data isolation between 3+ user groups (Vendors, Consumers, Admins).
- Implemented robust stateful session management using `HTTPSession` tokens, safely binding authorization attributes to prevent cross-site scripting (XSS) actions or unauthorized route bypasses.

### 3. Highly Optimized JDBC Data Persistence
- Bypassed bulky wrappers to write raw, high-performance **JDBC query logic**, avoiding unnecessary memory footprints during concurrent database operations.
- Effectively mitigated **SQL Injection (SQLi)** vulnerabilities across all vendor input matrices by rigorously enforcing parameterized queries via `PreparedStatement`.
- Applied tactical database optimization strategies including **MySQL Indexing** on relational transaction keys, drastically lowering query execution latency during high-traffic reads.

### 4. Dynamic Administrative & Vendor Analytics Dashboards
- Developed a fast web dashboard powered by **AJAX asynchronous operations** to support real-time data binding without forcing complete page refetches.
- Built complex administrative query reporting structures to fetch granular sales analytics and inventory movements across 5+ primary merchant product categories.

---

## 📊 Performance Tuning & Deployment Hardening

During continuous deployment evaluations on enterprise environments (**Payara Server**), several infrastructure challenges were successfully resolved:
- **Port Conflict & Thread Allocation:** Effectively configured underlying server worker threads and socket bindings on port `8080` to prevent request starvation under bulk concurrent traffic.
- **Data-Mapping Integrity:** Resolved structural mapping bugs related to column naming inconsistencies and JDBC public key retrieval exceptions during initial database handshakes.

---

## ⚙️ Local Setup Instructions

### Prerequisites
- Java Development Kit (JDK 11 or higher)
- Apache Maven
- Apache Tomcat 9+ or Payara Server
- MySQL Server 8.0+

### Database Configuration
1. Instantiate your MySQL local service and execute the provided structural database migration script (`schema.sql`).
2. Update the environment resource connection credentials inside your dedicated database routing utility class:
   ```java
   String url = "jdbc:mysql://localhost:3306/nexcart_web_db?allowPublicKeyRetrieval=true&useSSL=false";
