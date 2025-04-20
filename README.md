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
   git clone https://github.com/yourusername/budget-mate.git](https://github.com/zahura0/Budget_Mate.git
   cd budget-mate
   ```

2. **Open in Android Studio**

   - Launch Android Studio.
   - Select `File > Open` and navigate to the cloned `budget-mate` folder.
   - Let Gradle sync the project (this may take a few minutes).

3. **Add Dependencies**

   Ensure the `app/build.gradle` includes the following dependencies (already configured in the project):

   ```gradle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.gson)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation(libs.activity)
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

Get a glimpse of Budget Mate’s clean and modern interface!

| Transaction List | Add Transaction | Monthly Analysis |
|:---------------:|:---------------:|:----------------:|
| ![Transaction List](https://raw.githubusercontent.com/zahura0/Budget_Mate/master/screenshots/transaction_list.png) | ![Add Transaction](https://raw.githubusercontent.com/zahura0/Budget_Mate/master/screenshots/add_transaction.png) | ![Monthly Analysis](https://raw.githubusercontent.com/zahura0/Budget_Mate/master/screenshots/monthly_analysis.png) |

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
