package com.example.theoraclesplate.domain.use_case

data class MenuUseCases(
    val getMyMenuItems: GetMyMenuItemsUseCase,
    val addMenuItem: AddMenuItemUseCase,
    val updateMenuItem: UpdateMenuItemUseCase,
    val deleteMenuItem: DeleteMenuItemUseCase
)
