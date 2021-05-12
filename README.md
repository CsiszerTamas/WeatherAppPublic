# Weather App

This app fetches and displays information about weather at different locations.

When the app is opened, it tries to download the latest weather data and forecasted weather data for the next 5 days from the OpenWeather API.
When there is no internet connection, the app will provide cached data from local data source (Room) if it previously could be successfully downloaded.
The app shows a SnackBar which informs the user about the offline usage.

The project uses the Model-View-ViewModel (MVVM) architecture, using the Clean Architecture principles.
It has a Single Activity and Navigation Component for navigating between the different fragments.

The app supports dark mode, which can be selected in Settings.

## Libraries used:
* **Dagger Hilt** for dependency injection
* **Retrofit, OkHttp** for networking
* **Gson** for JSON parsing
* **Room** for local data storage
* **RxJava 2** for asynchronous operations
* **Google Play Services - Location** for getting user location
* **Stetho** for inspecting local database
* **Glide** for image loading
* **Shimmer** for loading placeholder animation
* **JUnit 4, Truth** for adding unit tests for database operations
* **ktlint, detekt** for Kotlin code verification

The icons are from
https://www.flaticon.com/

Architecture diagram:

Screenshots:

![Alt text](https://user-images.githubusercontent.com/31385348/117978067-27138d80-b33a-11eb-9805-979881388739.png "Architecture diagram")

![Alt text](https://user-images.githubusercontent.com/31385348/117977919-f9c6df80-b339-11eb-9ced-5d90f3e99436.png "Main screen - in Dark mode")

![Alt text](https://user-images.githubusercontent.com/31385348/117977910-f7fd1c00-b339-11eb-9735-e227080171c7.png "Location Details - in Dark mode")

![Alt text](https://user-images.githubusercontent.com/31385348/117977922-fa5f7600-b339-11eb-882d-93ba10a1e780.png "Main screen - in Light mode")

![Alt text](https://user-images.githubusercontent.com/31385348/117977917-f92e4900-b339-11eb-8031-2b9cb540e005.png "Location search screen - in Light mode")