# Deep Feature Standardization Plan

This plan aims to apply the final layer of architectural consistency by standardizing the sub-feature directories within the **Admin** and **Seller** modules.

## Proposed Changes

### 1. Admin Sub-Feature Standardization
Every sub-feature under `ui.admin` will be reorganized to include `presentation` and `viewmodel` packages.

- **`allusers`**: Move `AllUsersScreen` to `presentation` and `AllUsersViewModel` to `viewmodel`.
- **`allorders`**: Move `AllOrdersScreen` and cards to `presentation`, and `AllOrdersViewModel` to `viewmodel`.
- **`allmenuitems`**: Move `AllMenuItemsScreen` and cards to `presentation`, and `AllMenuItemsViewModel` to `viewmodel`.
- **`analytics`**: Move `AnalyticsScreen` and views to `presentation`, and `AnalyticsViewModel` to `viewmodel`.
- **`deliverymanagement`**: Move `DeliveryManagementScreen` and views to `presentation`, and `DeliveryManagementViewModel` to `viewmodel`.
- **`pendingsellers`**: Move `PendingSellersScreen` and views to `presentation`, and `PendingSellersViewModel` to `viewmodel`.

### 2. Seller Sub-Feature Standardization
Standardize the menu management and order management for sellers.

- **`menu`**: Move `AddMenuItemScreen` and `EditMenuItemScreen` to `presentation`. (ViewModels are already in `ui.seller.viewmodel`).
- **`orders`**: Move `SellerOrdersScreen` and list/cards to `presentation`. (ViewModel is already in `ui.seller.viewmodel`).

### 3. Cleanup
- Remove any remaining stray files in the root of these sub-feature directories.
- Update all imports in `MainApp.kt` and cross-feature references.

## Verification Plan

### Automated Tests
- Ensure the project builds successfully after the final deep package reorganization.

### Manual Verification
- Spot-check Previews for `AddMenuItemScreen`, `AllOrdersScreen`, and `AnalyticsScreen` to ensure they render correctly with the standardized structure.
