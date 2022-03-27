package com.stringconcat.ddd.shop.rest.menu

import com.stringconcat.ddd.shop.rest.API_V1_MENU_GET_ALL
import com.stringconcat.ddd.shop.usecase.menu.GetMenu
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(tags = ["Menu"])
class GetMenuEndpoint(private val getMenu: GetMenu) {

    @ApiOperation("Get the menu")
    @GetMapping(path = [API_V1_MENU_GET_ALL])
    fun execute(): ResponseEntity<Collection<MealModel>> {
        return ResponseEntity.ok(getMenu.execute().map { MealModel.from(it) })
    }
}