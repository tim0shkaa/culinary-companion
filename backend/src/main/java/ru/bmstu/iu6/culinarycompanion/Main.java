package ru.bmstu.iu6.culinarycompanion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.javalin.Javalin;
import io.javalin.http.HandlerType;
import io.javalin.json.JavalinJackson;
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

        // DAO initialization
        UserDAO userDAO = new UserDAO(dataSource);
        RecipeDAO recipeDAO = new RecipeDAO(dataSource);
        IngredientDAO ingredientDAO = new IngredientDAO(dataSource);
        RecipeIngredientDAO recipeIngredientDAO = new RecipeIngredientDAO(dataSource);
        UserMealEntryDAO userMealEntryDAO = new UserMealEntryDAO(dataSource);  // НОВЫЙ DAO
        ShoppingListDAO shoppingListDAO = new ShoppingListDAO(dataSource);
        PantryDAO pantryDAO = new PantryDAO(dataSource);
        CommentDAO commentDAO = new CommentDAO(dataSource);
        RatingDAO ratingDAO = new RatingDAO(dataSource);
        ReportDAO reportDAO = new ReportDAO(dataSource);
        SavedRecipeDAO savedRecipeDAO = new SavedRecipeDAO(dataSource);

        JwtUtil jwtUtil = new JwtUtil();

        // Service initialization
        AuthService authService = new AuthService(userDAO, jwtUtil);
        UserService userService = new UserService(userDAO, recipeDAO, savedRecipeDAO);
        RecipeService recipeService = new RecipeService(recipeDAO, ingredientDAO, recipeIngredientDAO, userDAO, ratingDAO);
        IngredientService ingredientService = new IngredientService(ingredientDAO);
        UserMealPlanningService mealPlanningService = new UserMealPlanningService(  // НОВЫЙ SERVICE
                userMealEntryDAO, recipeDAO, recipeIngredientDAO, ingredientDAO
        );
        ShoppingListService shoppingListService = new ShoppingListService(shoppingListDAO, recipeIngredientDAO, pantryDAO);
        PantryService pantryService = new PantryService(pantryDAO, ingredientDAO);
        CommentService commentService = new CommentService(commentDAO, recipeDAO);
        RatingService ratingService = new RatingService(ratingDAO, recipeDAO);
        AdminService adminService = new AdminService(userDAO, recipeDAO, commentDAO, reportDAO);
        SavedRecipeService savedRecipeService = new SavedRecipeService(savedRecipeDAO, recipeDAO);

        // Controller initialization
        AuthController authController = new AuthController(authService);
        UserController userController = new UserController(userService);
        RecipeController recipeController = new RecipeController(recipeService);
        IngredientController ingredientController = new IngredientController(ingredientService);
        UserMealPlanningController mealPlanningController = new UserMealPlanningController(mealPlanningService);  // НОВЫЙ CONTROLLER
        ShoppingListController shoppingListController = new ShoppingListController(shoppingListService);
        CommentController commentController = new CommentController(commentService);
        RatingController ratingController = new RatingController(ratingService);
        AdminController adminController = new AdminController(adminService);
        SavedRecipeController savedRecipeController = new SavedRecipeController(savedRecipeService);

        // Middleware
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

            config.jsonMapper(new JavalinJackson(
                    new ObjectMapper()
                            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            ));

        }).start(7070);

        // Auth routes
        app.post("/api/auth/register", authController::register);
        app.post("/api/auth/login", authController::login);
        app.post("/api/auth/logout", authController::logout);

        // Public recipe routes
        app.get("/api/recipes", recipeController::getAll);
        app.get("/api/recipes/{id}", recipeController::getById);

        // Ingredient routes
        app.get("/api/ingredients", ingredientController::getAllIngredients);
        app.get("/api/ingredients/search", ingredientController::searchIngredients);

        // Public comment and rating routes
        app.get("/api/recipes/{recipeId}/comments", commentController::getRecipeComments);
        app.get("/api/recipes/{recipeId}/rating", ratingController::getRecipeRating);

        // Protected routes - middleware
        app.before("/api/user/*", authMiddleware);
        app.before("/api/meal-planning/*", authMiddleware);  // НОВЫЙ middleware
        app.before("/api/shopping-lists/*", authMiddleware);
        app.before("/api/pantry/*", authMiddleware);

        // User profile routes
        app.get("/api/user/profile", userController::getCurrentUser);
        app.get("/api/user/stats", userController::getUserStats);
        app.get("/api/user/recipes", recipeController::getUserRecipes);
        app.get("/api/user/saved-recipes", savedRecipeController::getSavedRecipes);
        app.put("/api/user/profile", userController::updateProfile);
        app.delete("/api/user/profile", userController::deleteProfile);
        app.get("/api/user/{id}", userController::getUserById);

        // Recipe CRUD - conditional auth
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

        // Saved recipes routes
        app.before("/api/recipes/{recipeId}/save", authMiddleware);
        app.before("/api/recipes/{recipeId}/is-saved", authMiddleware);

        app.post("/api/recipes/{recipeId}/save", savedRecipeController::saveRecipe);
        app.delete("/api/recipes/{recipeId}/save", savedRecipeController::unsaveRecipe);
        app.get("/api/recipes/{recipeId}/is-saved", savedRecipeController::isSaved);

        // Comment routes
        app.before("/api/recipes/{recipeId}/comments", ctx -> {
            if (ctx.method() == HandlerType.POST) {
                authMiddleware.handle(ctx);
            }
        });

        app.before("/api/comments/{id}", authMiddleware);

        app.post("/api/recipes/{recipeId}/comments", commentController::createComment);
        app.delete("/api/comments/{id}", commentController::deleteComment);

        // Rating routes
        app.before("/api/recipes/{recipeId}/rate", authMiddleware);
        app.post("/api/recipes/{recipeId}/rate", ratingController::rateRecipe);

        // НОВЫЕ ROUTES для планирования рациона
        app.get("/api/meal-planning/week", mealPlanningController::getWeekEntries);
        app.get("/api/meal-planning/day", mealPlanningController::getDayEntries);
        app.post("/api/meal-planning/entries", mealPlanningController::addEntry);
        app.delete("/api/meal-planning/entries/{entryId}", mealPlanningController::deleteEntry);
        app.get("/api/meal-planning/nutrition", mealPlanningController::getDayNutrition);

        // Shopping list routes
        app.get("/api/shopping-lists/{id}", shoppingListController::getShoppingList);
        app.put("/api/shopping-lists/{id}", shoppingListController::updateShoppingList);
        app.delete("/api/shopping-lists/{id}", shoppingListController::deleteShoppingList);

        // Admin routes
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
