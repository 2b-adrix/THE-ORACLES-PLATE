# Further Polish and Feature Enhancement Plan

This plan aims to add high-value user features and further refine the project's adherence to Clean Architecture.

## Proposed Changes

### 1. Cart Badge System

Add a reactive badge to the Cart icon in the Bottom Navigation and on the Home Screen.

#### [MODIFY] [MainScreen.kt](file:///C:/Users/vadit/OneDrive/Desktop/Remote-Patient-Monitor/THE-ORACLES-PLATE/app/src/main/java/com/example/theoraclesplate/ui/main/presentation/MainScreen.kt)
- Use `CartViewModel` to collect the total item count.
- Implement `BadgedBox` with a `Badge` in the `NavigationBarItem` for the Cart.

### 2. Standardized UI Feedback

Create a centralized `Effect` and `Message` system to handle Snakbars and Errors across the app.

#### [NEW] `com.example.theoraclesplate.ui.common.UiMessenger.kt`
- A utility to handle global or screen-level messages.
- Standardize `UiEvent` sealed classes in ViewModels to include a common `ShowMessage` event.

### 3. Strict Dependency Isolation

Remove the remaining direct calls to Firebase from the UI entry points.

#### [MODIFY] [SellerDashboardScreen.kt](file:///C:/Users/vadit/OneDrive/Desktop/Remote-Patient-Monitor/THE-ORACLES-PLATE/app/src/main/java/com/example/theoraclesplate/ui/seller/presentation/SellerDashboardScreen.kt)
#### [MODIFY] [ProfileScreen.kt](file:///C:/Users/vadit/OneDrive/Desktop/Remote-Patient-Monitor/THE-ORACLES-PLATE/app/src/main/java/com/example/theoraclesplate/ui/profile/presentation/ProfileScreen.kt)
- Delegate authentication state and logout actions entirely to ViewModels using `AuthUseCases`.

### 4. Quality Assurance (Sample Tests)

Implement a robust unit test for a core ViewModel to serve as a template for future testing.

#### [NEW] `com.example.theoraclesplate.ui.cart.viewmodel.CartViewModelTest.kt`
- Test item addition, quantity updates, and total price calculation using mock repositories.

## Verification Plan

### Automated Tests
- Run the new unit tests using `./gradlew :app:testDebugUnitTest`.

### Manual Verification
- Add items to the cart and verify the badge updates immediately in the bottom navigation.
- Perform a logout and ensure it flow correctly through the ViewModel.
- Verify that Snakbars appear correctly for success and error states.
