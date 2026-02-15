package com.example.theoraclesplate.domain.use_case

data class MenuUseCases(
    val getMenuItems: GetMenuItemsUseCase,
    val getMenuItem: GetMenuItemUseCase,
    val addMenuItem: AddMenuItemUseCase,
    val deleteMenuItem: DeleteMenuItemUseCase,
    val updateMenuItem: UpdateMenuItemUseCase
)
