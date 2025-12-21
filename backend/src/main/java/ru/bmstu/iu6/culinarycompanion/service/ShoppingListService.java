package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.*;
import ru.bmstu.iu6.culinarycompanion.domain.*;
import ru.bmstu.iu6.culinarycompanion.dto.response.ShoppingListResponse;
import ru.bmstu.iu6.culinarycompanion.exception.ForbiddenException;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;

import java.sql.SQLException;
import java.util.*;

public class ShoppingListService {

    private final ShoppingListDAO shoppingListDAO;
    private final RecipeIngredientDAO recipeIngredientDAO;
    private final PantryDAO pantryDAO;

    public ShoppingListService(ShoppingListDAO shoppingListDAO,
                               RecipeIngredientDAO recipeIngredientDAO,
                               PantryDAO pantryDAO) {
        this.shoppingListDAO = shoppingListDAO;
        this.recipeIngredientDAO = recipeIngredientDAO;
        this.pantryDAO = pantryDAO;
    }

    public ShoppingListResponse getShoppingListById(Long userId, Long shoppingListId)
            throws NotFoundException, ForbiddenException, SQLException {

        Optional<ShoppingList> shoppingListOpt = shoppingListDAO.findById(shoppingListId);

        if (shoppingListOpt.isEmpty()) {
            throw new NotFoundException("Shopping list not found");
        }

        ShoppingList shoppingList = shoppingListOpt.get();

        if (!shoppingList.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only access your own shopping lists");
        }

        ShoppingListResponse response = new ShoppingListResponse();
        response.setId(shoppingList.getId());
        response.setUserId(shoppingList.getUserId());
        response.setCreatedAt(shoppingList.getCreatedAt().toString());

        return response;
    }

    public ShoppingListResponse updateShoppingList(Long userId, Long shoppingListId, String body)
            throws NotFoundException, ForbiddenException, SQLException {

        Optional<ShoppingList> shoppingListOpt = shoppingListDAO.findById(shoppingListId);

        if (shoppingListOpt.isEmpty()) {
            throw new NotFoundException("Shopping list not found");
        }

        ShoppingList shoppingList = shoppingListOpt.get();

        if (!shoppingList.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only update your own shopping lists");
        }

        ShoppingListResponse response = new ShoppingListResponse();
        response.setId(shoppingList.getId());
        response.setUserId(shoppingList.getUserId());
        response.setCreatedAt(shoppingList.getCreatedAt().toString());

        return response;
    }

    public void deleteShoppingList(Long userId, Long shoppingListId)
            throws NotFoundException, ForbiddenException, SQLException {

        Optional<ShoppingList> shoppingListOpt = shoppingListDAO.findById(shoppingListId);

        if (shoppingListOpt.isEmpty()) {
            throw new NotFoundException("Shopping list not found");
        }

        ShoppingList shoppingList = shoppingListOpt.get();

        if (!shoppingList.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only delete your own shopping lists");
        }

        shoppingListDAO.delete(shoppingListId);
    }
}