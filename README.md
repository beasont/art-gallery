# Art Gallery Project

## Overview
### Purpose
The Art Gallery Project offers an engaging and interactive platform for exploring a vast collection of famous artworks. Users can contribute their own creations, comment on existing works, or even use an intuitive drawing tool to save custom art directly into the gallery.

### Key Features
1. **Gallery Browsing**
   - Extensive and dynamic collection of famous artworks
   - Interactive carousel for seamless navigation
   - Sorting and filtering options for personalized exploration
   - Fully responsive design for desktop and mobile devices

2. **Commenting System**
   - Supports both guest and registered users
   - Secure and efficient comment management
   - Real-time updates for a smooth user experience
   - Moderation tools for maintaining community integrity

3. **User Gallery**
   - Simple and fast artwork upload
   - Editable submissions for customization
   - Interactive drawing widget for unique creations
   - Community-focused features to encourage engagement

4. **Random Artwork Button**
   - Picks a random piece from the 60 available artworks
   - Offers an easy way to explore diverse art

5. **Drawing Feature**
   - Easy-to-use interface for creating original art
   - Save and share creations to the interface

---

## Application Architecture
### Backend
The backend leverages Spring Boot for a robust, efficient, and secure architecture, handling server-side operations seamlessly.

Key Components:
- **Spring Data JPA** for efficient database interactions
- **Spring Web** for building RESTful APIs
- **Spring Security** for authentication and authorization
- **Maven Wrapper** for simplified dependency management

### Database
An H2 in-memory database is used for development and testing, providing quick and lightweight persistence.

### Frontend
Developed with Angular, the frontend delivers a modern, dynamic, and responsive user experience.

---

## Technologies Used
- **Backend**
  - Java 17
  - Spring Boot 3.4.1
  - Spring Data JPA
  - Spring Web
  - Spring Security
  - H2 Database
  - Maven 3.9.9
  - Lombok 1.18.30

- **Frontend**
  - Angular 16
  - TypeScript
  - HTML5 & CSS3
  - RxJS
  - Bootstrap (Optional)

- **Development Tools**
  - Visual Studio Code
  - Git
  - Postman
  - Docker (Optional)

---

## REST API Documentation

### Overview
This API provides access to the Art Gallery application functionality, allowing users to retrieve artworks, submit comments, and manage user-submitted artworks. Below are the available endpoints for interaction.

### Artworks API (/api/artworks)
- **GET /api/artworks**
  - Retrieves a comprehensive list of all artworks in the main gallery. Each artwork entry includes details such as title, artist, year of creation, and associated image URL.

### Proxy API (/api/proxy)
- **GET /api/proxy?url={imageUrl}**
  - Proxies requests to external image URLs. This allows for the retrieval of images without exposing the original source URLs, enhancing security and avoiding CORS issues.

### Comments API (/api/comments)
- **POST /api/comments**
  - Adds a new comment to an artwork. Requires the `artId`, `text`, optional `username`, and optional `password` (for authenticated users). If no username is provided, the comment will be associated with a "Guest" account.

- **POST /api/comments/update**
  - Updates an existing comment. Requires the `commentId`, `text`, optional `username`, and optional `password` for authentication. This endpoint uses the HTTP `POST` method for updates.

- **POST /api/comments/delete**
  - Deletes an existing comment. Requires the `commentId`, optional `username`, and optional `password` for authentication. This endpoint uses the HTTP `POST` method for deletions.

- **GET /api/comments/recent**
  - Fetches the most recent comments from all artworks. Each comment returned includes details about the commenter, the associated artwork, and the date created.

### User Art API (/api/user-art)
- **GET /api/user-art**
  - Retrieves all artworks submitted by users. This endpoint allows users to view their own contributions to the gallery.

- **POST /api/user-art/upload**
  - Uploads a new artwork submitted by a user. Requires the `title`, `artist` name, `year`, and `image` file. Optionally, it can include a `username` and `password` for authenticated submissions.

- **PUT /api/user-art/edit**
  - Updates an existing user-submitted artwork. Requires the unique `artId` and the new details (`title`, `artist`, `year`, and optional `image` file). Authentication is necessary to ensure user ownership. This endpoint uses the HTTP `PUT` method to conform to RESTful principles.

- **DELETE /api/user-art/delete**
  - Deletes a user-submitted artwork. Requires the `artId`, `username`, and `password` for authentication. Only the original uploader can delete their artwork. This endpoint uses the HTTP `DELETE` method to conform to RESTful principles.

### Authentication and Security
Some endpoints (such as comment addition and user art management) require authentication via `username` and hashed `password` to ensure that only authorized users can make modifications or deletions. Passwords are not stored in plain text; they are securely hashed.

---

## Installation and Setup

### Prerequisites
- Java Development Kit (JDK) 17 or Higher
- Maven 3.9.9 or Higher
- Node.js and npm
- Git

### Bash Script
- In the root directory, you will find the file **open_project.sh**.
- Simply run this bash script (`./open_project.sh`) in a Git Bash terminal, and the entire site will be loaded.

### Alternative Steps

1. **Clone the Repository**
```bash
git clone <repository_url>
cd art-gallery
```

2. **Backend Setup**
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

3. **Frontend Setup**
```bash
cd ../frontend
npm install
ng serve
```

Access the application at `http://localhost:4200`

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
