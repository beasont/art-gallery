# Art Gallery Project

## Table of Contents
- [Overview](#overview)
- [Purpose](#purpose)
- [Key Features](#key-features)
- [Application Architecture](#application-architecture)
  - [Backend](#backend)
  - [Database](#database)
  - [Frontend](#frontend)
- [Technologies Used](#technologies-used)
- [REST API Documentation](#rest-api-documentation)
  - [Artworks API](#artworks-api)
  - [Proxy API](#proxy-api)
  - [Comments API](#comments-api)
  - [User Art API](#user-art-api)
- [Installation and Setup](#installation-and-setup)
  - [Prerequisites](#prerequisites)
  - [Steps](#steps)
- [Usage](#usage)
  - [Gallery Browsing](#gallery-browsing)
  - [Commenting System](#commenting-system)
  - [User Gallery](#user-gallery)
  - [Random Artwork Discovery](#random-artwork-discovery)
  - [Drawing Feature](#drawing-feature)
  - [Security Features](#security-features)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Overview
### Purpose
This project aims to provide users with an interactive and intuitive way to explore, contribute, and engage with a comprehensive collection of famous artworks. The user may also upload and interact with their own submitted artworks, or even use a custom widget to draw something and save it to the user gallery.

### Key Features
1. **Gallery Browsing**
   - Extensive Collection
   - Interactive Carousel
   - Sorting and Filtering
   - Responsive Design

2. **Commenting System**
   - Guest and Registered Users
   - Secure Comment Management
   - Real-time Updates
   - Moderation Tools

3. **User Gallery**
   - Artwork Upload
   - Drawing Widget
   - Editable Submissions
   - Community Engagement

4. **Random Artwork Discovery**
   - Surprise Element
   - Enhanced User Experience

5. **Drawing Feature**
   - Intuitive Interface
   - Save and Share
   - Customization

## Application Architecture
### Backend
The backend of the Art Gallery Project is built using Spring Boot, providing a robust foundation for handling all server-side operations.

Key Components:
- **Spring Data JPA**
- **Spring Web**
- **Spring Security**
- **Maven Wrapper**

### Database
The project leverages an H2 in-memory database for development and testing purposes.

### Frontend
The frontend is developed using Angular, delivering a dynamic and responsive user interface.

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

## REST API Documentation
### Artworks API (/api/artworks)
- **GET /api/artworks**: Retrieves a list of all artworks in the main gallery.

### Proxy API (/api/proxy)
- **GET /api/proxy?url={imageUrl}**: Proxies requests to external image URLs.

### Comments API (/api/comments)
- **POST /api/comments**: Adds a comment to an artwork.
- **GET /api/comments/recent**: Fetches the most recent comments from all artworks.

### User Art API (/api/user-art)
- **GET /api/user-art**: Retrieves all user-submitted artworks.
- **POST /api/user-art/upload**: Uploads a new user artwork.

## Installation and Setup
### Prerequisites
- Java Development Kit (JDK) 17 or Higher
- Maven 3.9.9 or Higher
- Node.js and npm
- Git

### Steps

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
