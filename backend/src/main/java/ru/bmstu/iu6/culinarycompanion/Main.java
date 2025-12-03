package ru.bmstu.iu6.culinarycompanion;

import io.javalin.Javalin;
import ru.bmstu.iu6.culinarycompanion.controller.*;
import ru.bmstu.iu6.culinarycompanion.dao.*;
import ru.bmstu.iu6.culinarycompanion.middleware.AdminMiddleware;
import ru.bmstu.iu6.culinarycompanion.middleware.AuthMiddleware;
import ru.bmstu.iu6.culinarycompanion.middleware.CorsMiddleware;
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
        ShoppingListDAO shoppingListDAO = new ShoppingListDAO(dataSource);
        PantryDAO pantryDAO = new PantryDAO(dataSource);
        CommentDAO commentDAO = new CommentDAO(dataSource);
        RatingDAO ratingDAO = new RatingDAO(dataSource);
        ReportDAO reportDAO = new ReportDAO(dataSource);
        
        JwtUtil jwtUtil = new JwtUtil();
        
        AuthService authService = new AuthService(userDAO, jwtUtil);
        UserService userService = new UserService(userDAO);
        RecipeService recipeService = new RecipeService(recipeDAO, ingredientDAO, recipeIngredientDAO, userDAO, ratingDAO);
        IngredientService ingredientService = new IngredientService(ingredientDAO);
        MealPlanService mealPlanService = new MealPlanService(mealPlanDAO, mealPlanRecipeDAO);
        ShoppingListService shoppingListService = new ShoppingListService(shoppingListDAO, mealPlanDAO, mealPlanRecipeDAO, recipeIngredientDAO, pantryDAO);
        PantryService pantryService = new PantryService(pantryDAO, ingredientDAO);
        CommentService commentService = new CommentService(commentDAO, recipeDAO);
        RatingService ratingService = new RatingService(ratingDAO, recipeDAO);
        AdminService adminService = new AdminService(userDAO, recipeDAO, commentDAO, reportDAO);
        
        AuthController authController = new AuthController(authService);
        UserController userController = new UserController(userService);
        RecipeController recipeController = new RecipeController(recipeService);
        IngredientController ingredientController = new IngredientController(ingredientService);
        MealPlanController mealPlanController = new MealPlanController(mealPlanService);
        ShoppingListController shoppingListController = new ShoppingListController(shoppingListService);
        CommentController commentController = new CommentController(commentService);
        RatingController ratingController = new RatingController(ratingService);
        AdminController adminController = new AdminController(adminService);
        
        AuthMiddleware authMiddleware = new AuthMiddleware(jwtUtil);
        AdminMiddleware adminMiddleware = new AdminMiddleware(jwtUtil, userDAO);
        CorsMiddleware corsMiddleware = new CorsMiddleware();
        
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        }).start(7070);
        
        app.before(corsMiddleware);
        
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
        app.before("/api/recipes", authMiddleware);
        app.before("/api/recipes/*", authMiddleware);
        app.before("/api/mealplans/*", authMiddleware);
        app.before("/api/shopping-lists/*", authMiddleware);
        app.before("/api/pantry/*", authMiddleware);
        app.before("/api/recipes/{recipeId}/comments/create", authMiddleware);
        app.before("/api/comments/{id}/delete", authMiddleware);
        app.before("/api/recipes/{recipeId}/rate", authMiddleware);
        
        app.get("/api/user/profile", userController::getCurrentUser);
        app.get("/api/user/{id}", userController::getUserById);
        app.put("/api/user/profile", userController::updateProfile);
        app.delete("/api/user/profile", userController::deleteProfile);
        
        app.post("/api/recipes", recipeController::create);
        app.put("/api/recipes/{id}", recipeController::update);
        app.delete("/api/recipes/{id}", recipeController::delete);
        app.get("/api/user/recipes", recipeController::getUserRecipes);
        app.post("/api/recipes/{id}/add", recipeController::addToMyRecipes);
        
        app.get("/api/mealplans", mealPlanController::getUserMealPlans);
        app.get("/api/mealplans/{id}", mealPlanController::getMealPlanById);
        app.post("/api/mealplans", mealPlanController::create);
        app.delete("/api/mealplans/{id}", mealPlanController::delete);
        
        app.post("/api/mealplans/{mealPlanId}/shopping-list", shoppingListController::generateFromMealPlan);
        app.get("/api/shopping-lists/{id}", shoppingListController::getShoppingList);
        app.put("/api/shopping-lists/{id}", shoppingListController::updateShoppingList);
        app.delete("/api/shopping-lists/{id}", shoppingListController::deleteShoppingList);
        
        app.post("/api/recipes/{recipeId}/comments", commentController::createComment);
        app.delete("/api/comments/{id}", commentController::deleteComment);
        
        app.post("/api/recipes/{recipeId}/rate", ratingController::rateRecipe);
        
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
