Ticketing System (Gateway + Ticket Service)

Simple role-based ticket workflow with email + PDF notifications.

1. Overview / Workflow
- Roles 
- - USER: creates tickets, uploads attachment, can close/reopen.
- - MANAGER: reviews tickets, approves/rejects.
- - ADMIN: resolves approved tickets.
- - System log: Each operation is written to TicketHistory.


- Flow
- - USER creates a ticket (+ optional attachment).
- - MANAGER approves/rejects.
- - ADMIN resolves approved ticket. 
- - Email & PDF: on create → notify user & manager; on resolve → send PDF to user (optional cc admin).
- - “List OPEN” shows OPEN + REOPENED tickets.

- Modules: 
- ticket-system/
- - gateway-web/        # UI (Thymeleaf + fetch), runs on :8080
- - ticket-service/     # API + Security + DB + AOP notifications, runs on :8081


- Project Structure:


- ticket-service/
- ├─ controller/        # TicketController, DashboardController
- ├─ entity/            # Ticket, TicketHistory, Employee, Role
- ├─ repository/        # TicketRepo, TicketHistoryRepo, ...
- ├─ security/          # SecurityConfig, DbUserDetailsService, AuthController
- ├─ service/           # TicketService, FileStorageService, BootstrapData
- ├─ notify/            # NotificationAspect, EmailService, PdfService
- └─ resources/
- -  └─ application.yml


- gateway-web/
- └─ resources/templates
- - ├─ index.html    # After logging in, the front end jumps by role
- - ├─ dash-user.html
- - ├─ dash-manager.html
- - └─ dash-admin.html

2. Spring Security
- Form login at ticket-service (/login), BCrypt password encoder.
- SecurityFilterChain: allow /login, protect /api/**, enable CORS to :8080.
- Login redirect: After successful login, the backend redirects to http://localhost:8080/.
- Role-based UI routing: The gateway homepage script calls http://localhost:8081/api/me to determine the role and redirect to /user / /manager / /admin.
- Role frontend routing is performed by the gateway.

3. Notification
- EmailService.sendText(to, subject, text): Sends a plain text email (when creating).
- PdfService.ticketSummary(...): byte[]: Generates a PDF byte array (when resolving).
- EmailService.sendWithAttachment(to, subject, text, pdfBytes, filename): Sends an email with a PDF attachment.
- NotificationAspect (AOP) hooks after TicketService.create(...) and after TicketService.resolve(...)。

4. Frontend <---> Backend
- gateway-web: Thymeleaf + native fetch (cross-origin cookies).
- ticket-service: Spring Boot (Web, Security, Data JPA), MySQL, AOP, JavaMail, iText PDF.
