# About

You could see tests in the test branch.
Migrated to gradle 8.0 created libs.toml and testLibs.toml

This app was made to learn about Api calls, Pagination, Local and Network Caching, Handling Connectivity and Reconnection issues and properly display it to the user. As the name says it's an interop: Single Activity with Fragments + Compose. I released it for developers to see performance with Compose. Overall it's my second app and first where i used compose.

Icon was chosen after thoroughly reading compose navigation, attempt to use compose theming and experiencing all xml render problems in the world thanks to "Stable" ComposeView and AbstractComposeView.

## Google Play
[Download](https://play.google.com/store/apps/details?id=gentle.hilt.interop)

## Screenshots

![App Screenshots](https://cdn.discordapp.com/attachments/278891946439868426/1112367844622086254/image_2023-05-28_181217845.webp)
![Tests Screenshots](https://cdn.discordapp.com/attachments/841644707666460683/1112356672090951701/image.png)

## Features
- Support configuration changes
- Search characters
- Local and Network caching
- Saving character as favorite
- Light/dark mode toggle
## Main Tech Stack

[Navigation Components](https://developer.android.com/guide/navigation/navigation-getting-started)   
[Coroutines](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow), [LiveData](https://developer.android.com/reference/kotlin/androidx/lifecycle/package-summary#(kotlinx.coroutines.flow.Flow).asLiveData(kotlin.coroutines.CoroutineContext,java.time.Duration))  
[Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android)   
[DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)   
[Room](https://developer.android.com/training/data-storage/room)   
[Retrofit-Moshi](https://developer.android.com/codelabs/basic-android-kotlin-training-getting-data-internet#0)   
[Paging](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)   
[Compose](https://developer.android.com/jetpack/compose)   
[ComposeView](https://developer.android.com/reference/kotlin/androidx/compose/ui/platform/ComposeView)   
[AbstractComposeView](https://developer.android.com/reference/kotlin/androidx/compose/ui/platform/AbstractComposeView)

## License

[MIT](https://choosealicense.com/licenses/mit/)