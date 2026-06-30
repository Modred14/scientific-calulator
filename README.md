# Scientific Calculator — Android Application

### University Assignment | SEN 104 & SEN 214
### Submitted by: **Favour Omirin**

---

A fully functional Android Scientific Calculator built with **Kotlin**, **Android Studio**, and **Material Design**, demonstrating core Android app development concepts and the complete Activity lifecycle.

---

## 📱 About This Project

This app was developed as a university assignment to demonstrate practical understanding of:
- Android application architecture (MVVM)
- Activity lifecycle methods (`onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onDestroy`)
- XML-based UI design with Material Design components
- Clean, reusable Kotlin code structure

---

## ✅ Features

### Core Requirements
- Addition, Subtraction, Multiplication, Division
- Clear (AC) and Delete (⌫) functionality
- Decimal number support
- Error handling (e.g. division by zero)

### Bonus Features Implemented
- **Trigonometric functions:** sin, cos, tan (Degrees/Radians toggle)
- **Hyperbolic functions:** sinh, cosh, tanh
- **Square root, powers, exponents**
- **Factorial**
- **Logarithm (log) and Natural Log (ln)**
- **Percentage**
- **Permutations (nPr) and Combinations (nCr)**
- **Matrix operations** — Addition, Subtraction, Multiplication, Determinant for 2×2, 3×3, and 4×4 matrices
- **Statistics** — Mean, Median, Mode, Standard Deviation
- **Calculation History** with timestamps

---

## 🏗️ Architecture

Built using the **MVVM (Model-View-ViewModel)** pattern:

```
View (Activities/XML)  →  observes  →  ViewModel (LiveData)  →  calls  →  Utility Engines
```

| Layer | Responsibility |
|-------|----------------|
| **View** | Activities + XML layouts — UI only, no business logic |
| **ViewModel** | Holds UI state, survives screen rotation |
| **Utility/Engine** | Pure Kotlin math logic — no Android dependencies |

---

## 🔄 Android Lifecycle Methods Demonstrated

Every Activity in this project logs and comments each lifecycle callback:

| Method | What It Does |
|--------|---------------|
| `onCreate()` | Initializes the UI, ViewBinding, and observers |
| `onStart()` | Activity becomes visible |
| `onResume()` | Activity becomes interactive |
| `onPause()` | Activity loses focus (saves lightweight state) |
| `onStop()` | Activity is no longer visible (persists data) |
| `onDestroy()` | Final cleanup before the Activity is destroyed |

View these in action via **Logcat** in Android Studio while using the app.

---

## 🛠️ Tech Stack

| Tool | Purpose |
|------|---------|
| Kotlin | Programming language |
| Android Studio | IDE |
| XML Layouts | UI design (no Jetpack Compose) |
| Material Design 3 | UI components and theming |
| ViewBinding | Type-safe view access |
| LiveData + ViewModel | Reactive state management |
| RecyclerView | History list display |

---

## 📂 Project Structure

```
ScientificCalculator/
├── app/src/main/java/com/university/scientificcalculator/
│   ├── ui/activities/      → MainActivity, MatrixActivity, StatisticsActivity, HistoryActivity
│   ├── ui/adapters/        → HistoryAdapter
│   ├── viewmodel/          → CalculatorViewModel, MatrixViewModel, StatisticsViewModel
│   ├── model/               → CalculationHistory
│   └── utils/                → CalculatorEngine, MatrixEngine, StatisticsEngine
└── app/src/main/res/
    ├── layout/               → All XML screens
    ├── values/               → colors, strings, themes, dimens
    └── drawable/             → Icons and backgrounds
```

---

## ▶️ How to Run

1. Open the project in **Android Studio**
2. Let Gradle sync complete
3. Connect an Android device or start an emulator
4. Press **Run ▶**

---

## 🎓 Submission Info

| Field | Detail |
|-------|--------|
| Course | SEN 104 & SEN 214 |
| Assignment | Scientific Calculator Mobile App |
| Student | Favour Omirin |
| Department | Software Engineering |

---

## 📄 License

This project was developed solely for academic purposes as part of a university coursework assignment.
