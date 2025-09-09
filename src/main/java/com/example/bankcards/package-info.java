/**
 * Bank Cards Management API.
 *
 * <p>This package contains the core
 * classes of the REST API for managing bank cards.
 * It provides functionality for creating,
 * activating, blocking, and deleting cards,
 * as well as for performing transfers between
 * cards and handling user authentication and authorization.
 *
 * <p>Main features include:
 * <ul>
 *     <li>CRUD operations for bank cards</li>
 *     <li>Card status management: Active, Blocked, Expired</li>
 *     <li>Secure storage of card numbers (encrypted) and masked display</li>
 *     <li>User roles: ADMIN and USER</li>
 *     <li>Transfers between own cards and balance checks</li>
 *     <li>Requests for card blocking</li>
 *     <li>Pagination and filtering when listing cards</li>
 *     <li>Validation of input data with informative error messages</li>
 * </ul>
 *
 * <p>Security and data protection:
 * <ul>
 *     <li>Spring Security + JWT authentication</li>
 *     <li>Role-based access control</li>
 *     <li>Encryption of sensitive data</li>
 *     <li>Card number masking for safe display</li>
 * </ul>
 *
 * <p>Technologies used:
 * <ul>
 *     <li>Java 17+</li>
 *     <li>Spring Boot, Spring Data JPA</li>
 *     <li>PostgreSQL or MySQL</li>
 *     <li>Liquibase for database migrations</li>
 *     <li>Swagger UI for API documentation</li>
 *     <li>Docker and Docker Compose for deployment and testing</li>
 * </ul>
 *
 * <p>This package contains controllers, services,
 * repositories, and entities that form
 * the backbone of the Bank Cards Management API.
 */
package com.example.bankcards;
