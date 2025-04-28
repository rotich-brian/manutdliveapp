# manutdliveapp

**manutdliveapp** is a Java-based Android application that retrieves articles from a Blogger website and displays them in a `RecyclerView`, using the **Blogger API**.

This is my **first complete Java project** that I built while learning Android development with Java!

---

## Overview

The app fetches news articles, transfer updates, and videos related to Manchester United from a Blogger website and presents them in a simple, clean UI.  
It was designed to practice Android fundamentals like networking, RecyclerView, Fragments, and API integration.

---

## Features

- **RecyclerView Integration**: Display articles dynamically.
- **Blogger API Usage**: Fetches posts directly from a Blogger-hosted blog.
- **Tabbed Navigation**: Separate sections for News, Transfers, and Videos.
- **Swipe-to-Refresh**: Refresh content with a simple gesture.
- **Pagination**: Load more posts as the user scrolls.
- **WebView**: Open full blog articles inside the app.
- **Connectivity Feedback**: Displays messages when the device is offline.
- **Loading Animations**: Shimmer effect while fetching data.

---

## Tech Stack

- **Java** — Primary programming language.
- **AndroidX** — Modern Android components and libraries.
- **Volley** — HTTP requests and API handling.
- **Shimmer** — Beautiful loading animations.
- **Material Design Components** — For a modern and clean UI.

---

## Project Structure

- **Activities**:
  - `MainActivity`: Manages the main interface and tab navigation.
  - `PostDetailsActivity`: Displays full blog posts using WebView.

- **Fragments**:
  - `HomeFragment`
  - `TransfersFragment`
  - `VideosFragment`

- **Adapters**:
  - `AdapterPost`: Handles populating RecyclerView with blog posts.
  - `MyViewPagerAdapter`: Switches between Fragments.

- **Models**:
  - `ModelPost`: Represents individual blog posts.

- **Utilities**:
  - `Constants`: Stores API-related constants.
  - `SelectListener`: Interface for handling item clicks.

---

## How to Run

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   ```

2. **Open Project**:
   - Open in **Android Studio**.
   - Sync Gradle to install all required dependencies.

3. **Set Up Blogger API**:
   - Obtain a Blogger API key.
   - Add your API key in the `Constants.java` file.

4. **Build and Run**:
   - Connect an Android device or use an emulator.
   - Run the application from Android Studio.

---

## Screenshots

*(Insert some screenshots here if you want! It makes the README even better.)*

---

## License

This project is licensed under the MIT License.  
See the [LICENSE](LICENSE) file for more information.

---

## Acknowledgments

- Special thanks to the Android community and documentation that helped during development.
- Inspired by my passion for Manchester United and Android development!

---

> 🚀 Built with passion while learning — "Glory Glory Man United!"
