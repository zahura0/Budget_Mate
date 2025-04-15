# Budget Mate 💰

Welcome to **Budget Mate**, a sleek and intuitive finance tracking app for Android, built with Kotlin and Jetpack Compose. Take control of your finances by adding, updating, and deleting transactions, and gain insights with monthly budget analysis. Perfect for anyone looking to manage their money with ease and style!

## ✨ Features

- **Add Transactions**: Quickly log income or expenses with details like label, amount, and description.
- **Update Transactions**: Edit existing transactions to keep your records accurate.
- **Delete Transactions**: Remove transactions with a single tap.
- **Monthly Budget Analysis**: View total income, expenses, and balance for any month, with a clear breakdown of transactions.
- **Modern UI**: Built with Jetpack Compose for a smooth, responsive experience.
- **Local Storage**: Securely store data using Room Database.
- **Dark Mode Support**: Comfortable budgeting in any lighting.

## 🚀 Getting Started

Follow these steps to set up and run Budget Mate on your local machine.

### Prerequisites

- **Android Studio**: Latest version (e.g., Koala | 2024.1.1 or newer).
- **Kotlin**: Version 1.9.22 or higher.
- **Minimum SDK**: API 21 (Android 5.0 Lollipop).
- **Git**: For cloning the repository.

### Installation

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/budget-mate.git
   cd budget-mate
   ```

2. **Open in Android Studio**

   - Launch Android Studio.
   - Select `File > Open` and navigate to the cloned `budget-mate` folder.
   - Let Gradle sync the project (this may take a few minutes).

3. **Add Dependencies**

   Ensure the `app/build.gradle` includes the following dependencies (already configured in the project):

   ```gradle
   implementation "androidx.room:room-runtime:2.6.1"
   kapt "androidx.room:room-compiler:2.6.1"
   implementation "androidx.room:room-ktx:2.6.1"
   implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0"
   implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
   implementation "androidx.navigation:navigation-compose:2.7.7"
   implementation platform('androidx.compose:compose-bom:2024.02.00')
   implementation 'androidx.compose.ui:ui'
   implementation 'androidx.compose.material3:material3'
   ```

   Sync the project after verifying.

4. **Run the App**

   - Connect an Android device or start an emulator (API 21 or higher).
   - Click the `Run` button in Android Studio or use:

     ```bash
     ./gradlew installDebug
     ```

   - The app will launch on your device/emulator.

## 📸 Screenshots

Get a glimpse of Budget Mate’s clean and modern interface! (Replace the placeholder URLs with your actual screenshot URLs.)

| Transaction List | Add Transaction | Monthly Analysis |
|:---------------:|:---------------:|:----------------:|
| ![Transaction List](([https://github.com/zahura0/Budget_Mate/blob/f071e9a02226dbaa8a8de061a724cf6ed2fccedb/screenshots/add_transaction.png](https://raw.githubusercontent.com/zahura0/Budget_Mate/main/screenshots/transaction_list.png))) | ![Add Transaction](https://raw.githubusercontent.com/yourusername/budget-mate/main/screenshots/add_transaction.png) | ![Monthly Analysis](https://raw.githubusercontent.com/yourusername/budget-mate/main/screenshots/monthly_analysis.png) |

### How to Add Your Screenshots

1. **Upload Images**:
   - Create a `screenshots/` folder in your repository.
   - Add your images (e.g., `transaction_list.png`, `add_transaction.png`, `monthly_analysis.png`) to this folder.
   - Push the images to GitHub.

2. **Get Image URLs**:
   - Navigate to each image in your GitHub repository.
   - Click the image, then select `Raw` to get the URL (e.g., `https://raw.githubusercontent.com/yourusername/budget-mate/main/screenshots/your_image.png`).
   - Alternatively, upload images to an external host like Imgur and copy the direct URL (ending in `.png`, `.jpg`, etc.).

3. **Update the README**:
   - Replace the placeholder URLs in the table above with your actual image URLs.
   - Use Markdown syntax: `![Alt text](your-image-url)`.

4. **Test the README**:
   - Push the updated `README.md` to GitHub.
   - Check the rendered view on GitHub to ensure images load correctly.

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Database**: Room Database
- **Architecture**: MVVM (Model-View-ViewModel)
- **Navigation**: Jetpack Navigation Compose
- **Asynchronous Operations**: Kotlin Coroutines
- **Build Tool**: Gradle

## 🤝 Contributing

We welcome contributions to make Budget Mate even better! Here’s how to get started:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m 'Add your feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a Pull Request.

See our Contributing Guidelines for more details.

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙌 Acknowledgments

- **Jetpack Compose**: For a modern and delightful UI experience.
- **Room Database**: For robust and efficient data storage.
- **Community**: Thanks to all testers and contributors who help shape Budget Mate!

---

🌟 **Budget Mate** - Your money, your mate, always in sync! 🌟
