<div align="center">

<h1>MangaDex Reader</h1>

<img src="https://img.shields.io/badge/Jetpack%20Compose-1.6+-blue?style=for-the-badge&logo=android&logoColor=white" alt="Jetpack Compose"/>
<img src="https://img.shields.io/badge/Material%203-1.3+-success?style=for-the-badge&logo=android&logoColor=white" alt="Material 3"/>
<img src="https://img.shields.io/badge/MangaDex%20API-v5-orange?style=for-the-badge" alt="MangaDex API"/>
<img src="https://img.shields.io/badge/Kotlin-2.0+-purple?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>

**Beautiful, modern, smooth manga reader app for MangaDex using Jetpack Compose**

</div>

<br>

## 📱 Screenshots

<div align="center">
  <table>
    <tr>
      <td><img src="demo/d1.jpeg" width="200" alt="Home Screen"/></td>
      <td><img src="demo/d1.jpeg" width="200" alt="Chapter Screen"/></td>
      <td><img src="demo/d1.jpeg" width="200" alt="Reader"/></td>
      <td><img src="demo/d1.jpeg" width="200" alt="Library"/></td>
    </tr>
  </table>
</div>

<br>

## ✨ Features

- **Modern Material 3 UI** with dynamic theming support
- **Smooth horizontal pager reader** with pinch-to-zoom & double-tap zoom
- **Pull-to-refresh** on home screen
- **Shimmer loading** effect for images
- **Fixed top bar** with chapter info & page counter
- **Favorites / Library** tab (in-memory for now)
- **Robust error handling** with retry buttons
- **Clean architecture** (data → repository → presentation)
- **Jetpack Compose Navigation** with bottom navigation bar
- **Coil** for fast & cached image loading

<br>

## 🛠️ Tech Stack

| Category             | Technology                                 |
|----------------------|--------------------------------------------|
| UI                   | Jetpack Compose, Material 3                |
| Architecture         | MVVM + Clean Architecture                  |
| Networking           | Retrofit + Gson                            |
| Image Loading        | Coil Compose                               |
| State Management     | StateFlow + UiState sealed class           |
| Navigation           | Navigation Compose                         |
| Coroutines           | Kotlin Coroutines + viewModelScope         |
| API                  | MangaDex v5 API                            |

<br>
