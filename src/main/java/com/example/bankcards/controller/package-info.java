/**
 * REST controllers for the banking application.
 * <p>
 * This package contains classes that handle incoming HTTP requests and expose
 * the API endpoints for user management, authentication, card operations, and
 * other business logic. Controllers delegate requests to the corresponding
 * services and return responses in a standardized format.
 * </p>
 *
 * <h2>Contents</h2>
 * <ul>
 *   <li>{@code AuthController} – handles user registration and authentication</li>
 *   <li>{@code UserController} – provides CRUD operations for users (ADMIN only)</li>
 *   <li>{@code CardController} – manages banking cards and card operations</li>
 * </ul>
 *
 * <p>
 * All controllers are annotated with {@link org.springframework.web.bind.annotation.RestController}
 * and follow RESTful principles.
 * </p>
 */
package com.example.bankcards.controller;