package ru.bmstu.iu6.culinarycompanion;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.http.HandlerType;
import ru.bmstu.iu6.culinarycompanion.controller.*;
import ru.bmstu.iu6.culinarycompanion.dao.*;
import ru.bmstu.iu6.culinarycompanion.middleware.AdminMiddleware;
import ru.bmstu.iu6.culinarycompanion.middleware.AuthMiddleware;
import ru.bmstu.iu6.culinarycompanion.service.*;
import ru.bmstu.iu6.culinarycompanion.util.DatabaseConnection;
import ru.bmstu.iu6.culinarycompanion.util.JwtUtil;

import javax.sql.DataSource;

public class Main {

    public static void main(String[] args) {
        DataSource dataSource = DatabaseConnection.getDataSource();

        UserDAO userDAO = new UserDAO(dataSource);
        RecipeDAO recipeDAO = new RecipeDAO(dataSource);
        IngredientDAO ingredientDAO = new IngredientDAO(dataSource);
        RecipeIngredientDAO recipeIngredientDAO = new RecipeIngredientDAO(dataSource);
        MealPlanDAO mealPlanDAO = new MealPlanDAO(dataSource);
        MealPlanRecipeDAO mealPlanRecipeDAO = new MealPlanRecipeDAO(dataSource);
        MealPlanEntryDAO mealPlanEntryDAO = new MealPlanEntryDAO(dataSource);
        ShoppingListDAO shoppingListDAO = new ShoppingListDAO(dataSource);
        PantryDAO pantryDAO = new PantryDAO(dataSource);
        CommentDAO commentDAO = new CommentDAO(dataSource);
        RatingDAO ratingDAO = new RatingDAO(dataSource);
        ReportDAO reportDAO = new ReportDAO(dataSource);
        SavedRecipeDAO savedRecipeDAO = new SavedRecipeDAO(dataSource);

        JwtUtil jwtUtil = new JwtUtil();

        AuthService authService = new AuthService(userDAO, jwtUtil);
        UserService userService = new UserService(userDAO, recipeDAO, savedRecipeDAO);
        RecipeService recipeService = new RecipeService(recipeDAO, ingredientDAO, recipeIngredientDAO, userDAO, ratingDAO);
        IngredientService ingredientService = new IngredientService(ingredientDAO);
        MealPlanService mealPlanService = new MealPlanService(mealPlanDAO, mealPlanEntryDAO, recipeDAO, recipeIngredientDAO, ingredientDAO);
        ShoppingListService shoppingListService = new ShoppingListService(shoppingListDAO, mealPlanDAO, mealPlanRecipeDAO, recipeIngredientDAO, pantryDAO);
        PantryService pantryService = new PantryService(pantryDAO, ingredientDAO);
        CommentService commentService = new CommentService(commentDAO, recipeDAO);
        RatingService ratingService = new RatingService(ratingDAO, recipeDAO);
        AdminService adminService = new AdminService(userDAO, recipeDAO, commentDAO, reportDAO);
        SavedRecipeService savedRecipeService = new SavedRecipeService(savedRecipeDAO, recipeDAO);

        AuthController authController = new AuthController(authService);
        UserController userController = new UserController(userService);
        RecipeController recipeController = new RecipeController(recipeService);
        IngredientController ingredientController = new IngredientController(ingredientService);
        MealPlanController mealPlanController = new MealPlanController(mealPlanService);
        ShoppingListController shoppingListController = new ShoppingListController(shoppingListService);
        CommentController commentController = new CommentController(commentService);
        RatingController ratingController = new RatingController(ratingService);
        AdminController adminController = new AdminController(adminService);
        SavedRecipeController savedRecipeController = new SavedRecipeController(savedRecipeService);

        AuthMiddleware authMiddleware = new AuthMiddleware(jwtUtil);
        AdminMiddleware adminMiddleware = new AdminMiddleware(jwtUtil, userDAO);

        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                    it.allowCredentials = true;
                });
            });
        }).start(7070);

        app.post("/api/auth/register", authController::register);
        app.post("/api/auth/login", authController::login);
        app.post("/api/auth/logout", authController::logout);

        app.get("/api/recipes", recipeController::getAll);
        app.get("/api/recipes/{id}", recipeController::getById);

        app.get("/api/ingredients", ingredientController::getAllIngredients);
        app.get("/api/ingredients/search", ingredientController::searchIngredients);

        app.get("/api/recipes/{recipeId}/comments", commentController::getRecipeComments);
        app.get("/api/recipes/{recipeId}/rating", ratingController::getRecipeRating);

        app.before("/api/user/*", authMiddleware);
        app.before("/api/mealplans", authMiddleware);
        app.before("/api/mealplans/*", authMiddleware);
        app.before("/api/shopping-lists/*", authMiddleware);
        app.before("/api/pantry/*", authMiddleware);

        app.get("/api/user/profile", userController::getCurrentUser);
        app.get("/api/user/stats", userController::getUserStats);
        app.get("/api/user/recipes", recipeController::getUserRecipes);
        app.get("/api/user/saved-recipes", savedRecipeController::getSavedRecipes);
        app.put("/api/user/profile", userController::updateProfile);
        app.delete("/api/user/profile", userController::deleteProfile);

        app.get("/api/user/{id}", userController::getUserById);

        app.before("/api/recipes", ctx -> {
            if (ctx.method() == HandlerType.POST) {
                authMiddleware.handle(ctx);
            }
        });

        app.before("/api/recipes/{id}", ctx -> {
            if (ctx.method() == HandlerType.PUT || ctx.method() == HandlerType.DELETE) {
                authMiddleware.handle(ctx);
            }
        });

        app.post("/api/recipes", recipeController::create);
        app.put("/api/recipes/{id}", recipeController::update);
        app.delete("/api/recipes/{id}", recipeController::delete);
        app.post("/api/recipes/{id}/add", recipeController::addToMyRecipes);

        app.before("/api/recipes/{recipeId}/save", authMiddleware);
        app.before("/api/recipes/{recipeId}/is-saved", authMiddleware);

        app.post("/api/recipes/{recipeId}/save", savedRecipeController::saveRecipe);
        app.delete("/api/recipes/{recipeId}/save", savedRecipeController::unsaveRecipe);
        app.get("/api/recipes/{recipeId}/is-saved", savedRecipeController::isSaved);

        app.before("/api/recipes/{recipeId}/comments", ctx -> {
            if (ctx.method() == HandlerType.POST) {
                authMiddleware.handle(ctx);
            }
        });

        app.before("/api/comments/{id}", authMiddleware);
        app.before("/api/recipes/{recipeId}/rate", authMiddleware);

        app.post("/api/recipes/{recipeId}/comments", commentController::createComment);
        app.delete("/api/comments/{id}", commentController::deleteComment);
        app.post("/api/recipes/{recipeId}/rate", ratingController::rateRecipe);

        app.get("/api/mealplans", mealPlanController::getUserMealPlans);
        app.post("/api/mealplans", mealPlanController::create);
        app.get("/api/mealplans/{id}", mealPlanController::getMealPlanById);
        app.delete("/api/mealplans/{id}", mealPlanController::delete);
        app.get("/api/mealplans/{id}/entries", mealPlanController::getEntries);
        app.post("/api/mealplans/{id}/entries", mealPlanController::addEntry);
        app.delete("/api/mealplans/{id}/entries/{entryId}", mealPlanController::deleteEntry);
        app.get("/api/mealplans/{id}/nutrition", mealPlanController::getDayNutrition);

        app.post("/api/mealplans/{mealPlanId}/shopping-list", shoppingListController::generateFromMealPlan);
        app.get("/api/shopping-lists/{id}", shoppingListController::getShoppingList);
        app.put("/api/shopping-lists/{id}", shoppingListController::updateShoppingList);
        app.delete("/api/shopping-lists/{id}", shoppingListController::deleteShoppingList);

        app.before("/api/admin/*", adminMiddleware);

        app.get("/api/admin/users", adminController::getAllUsers);
        app.post("/api/admin/users/{id}/block", adminController::blockUser);
        app.post("/api/admin/users/{id}/unblock", adminController::unblockUser);
        app.get("/api/admin/reports", adminController::getAllReports);
        app.post("/api/admin/reports/{id}/resolve", adminController::resolveReport);
        app.delete("/api/admin/recipes/{recipeId}", adminController::deleteRecipe);
        app.delete("/api/admin/comments/{commentId}", adminController::deleteComment);

        System.out.println("Server started on http://localhost:7070");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            app.stop();
            DatabaseConnection.close();
            System.out.println("Server stopped");
        }));
    }
}