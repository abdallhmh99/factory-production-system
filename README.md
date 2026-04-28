# Factory Production Management System

> Built individually — originally assigned as a 3-4 person team project.

A full-featured desktop application that simulates and manages 
a real factory's production lifecycle, built with Java OOP, 
Multithreading, and a custom Swing GUI.

---

## Features

- **Real-Time Production Simulation** — Multiple production lines 
  run as parallel Threads, with live progress tracking (%)
- **Smart Inventory Management** — Raw material reservation system 
  prevents conflicts between concurrent production lines
- **Bill of Materials** — Define products with exact raw material 
  recipes and quantities
- **Role-Based Access Control** — Manager and Supervisor roles 
  with different permissions and dashboards
- **Auto-Save System** — Automatic periodic data persistence 
  using Java Serialization
- **Report Export** — Generate detailed inventory and system 
  status reports as timestamped text files

---

## Tech Stack

- **Java SE** — Core OOP principles (Inheritance, Polymorphism, 
  Encapsulation, Abstraction)
- **Java Swing & AWT** — Custom modern GUI components
- **Multithreading & Concurrency** — Threads, synchronized blocks, 
  volatile, ConcurrentHashMap
- **Java Serialization API** — Persistent data storage
- **MVC Architecture** — Clean separation of Models, 
  Controllers, and UI layers

---

## System Architecture
├── Models      → Item, Product, Task, ProductLine
├── Controllers → InventoryManager (Singleton), ProductionManager,
│                 ThreadManager, FileManager
├── Users       → User, UserManager, UserRole
└── UI          → LoginScreen, ManagerDashboard, SupervisorDashboard

---

## How It Works

1. User creates a production **Task** requesting a quantity of a Product
2. **ProductionManager** assigns the task to an available ProductLine
3. **ProductLine** (running as a Thread) reserves raw materials 
   via InventoryManager
4. Production runs in real-time — materials consumed gradually, 
   progress updated live
5. System auto-saves every few seconds to prevent data loss

---

## Screenshots

![Login](screenshots/Screenshot%201%20(login).png)
![Manager Dashboard](screenshots/Screenshot%202%20(manger%20dashboard).png)
![Supervisor Dashboard](screenshots/Screenshot%203%20(SupervisorDashboard).png)
![Supervisor Dashboard 2](screenshots/Screenshot%204%20(SupervisorDashboard).png)
![Manager Dashboard 2](screenshots/Screenshot%205%20(manger%20Dashboard).png)
![User Management](screenshots/Screenshot%206%20(users%20manger).png)
 
---

## Academic Context

This project was assigned as a team project (3-4 students) 
at university. I completed it entirely on my own as a solo 
developer, which required designing and implementing all 
system layers independently.

