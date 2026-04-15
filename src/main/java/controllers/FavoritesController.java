package controllers;

import com.example.authapp.models.Component;
import com.example.authapp.repositories.FavoritesRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Контроллер экрана избранных компонентов.
 */
public class FavoritesController {

    @FXML private Button backBtn;
    @FXML private TilePane favoritesPane;

    private MainController mainController;

    public void initialize() {
        if (backBtn != null) {
            backBtn.setOnAction(e -> {
                if (mainController != null) mainController.showMainContent();
            });
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Загрузить и отобразить избранные компоненты.
     */
    public void loadFavorites() {
        new Thread(() -> {
            List<Component> favorites = FavoritesRepository.getFavorites();
            Platform.runLater(() -> renderFavorites(favorites));
        }, "load-favorites").start();
    }

    private void renderFavorites(List<Component> components) {
        favoritesPane.getChildren().clear();

        if (components == null || components.isEmpty()) {
            Label empty = new Label("Избранное пусто.\nНажмите ☆ на карточке компонента, чтобы добавить его сюда.");
            empty.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280; -fx-padding: 60; -fx-alignment: center;");
            favoritesPane.getChildren().add(empty);
            return;
        }

        for (Component c : components) {
            favoritesPane.getChildren().add(buildCard(c));
        }
    }

    private VBox buildCard(Component c) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(MainController.CARD_STYLE);
        card.setPrefWidth(220);

        ImageView iv = new ImageView();
        iv.setFitWidth(180);
        iv.setFitHeight(140);
        iv.setPreserveRatio(true);
        try {
            String url = c.getImageUrl();
            if (url != null && !url.isEmpty()) {
                iv.setImage(new Image(url, true));
            } else {
                loadDefaultImage(iv);
            }
        } catch (Exception e) {
            loadDefaultImage(iv);
        }

        Label cat = new Label(c.getCategory() == null ? "" : c.getCategory());
        cat.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label name = new Label(c.getName());
        name.setWrapText(true);
        name.setAlignment(Pos.CENTER);
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #111827;");
        name.setMinHeight(35);
        name.setMaxHeight(45);

        Label price = new Label(c.getMinPrice() > 0
                ? String.format("от %,.0f ₽", c.getMinPrice())
                : "цен нет");
        price.setStyle("-fx-font-weight: bold; -fx-text-fill: "
                + (c.getMinPrice() > 0 ? "#10b981" : "#9ca3af")
                + "; -fx-font-size: 16px;");

        HBox btnRow = new HBox(8);
        btnRow.setAlignment(Pos.CENTER);

        Button detailBtn = new Button("Сравнить цены →");
        detailBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #3b82f6, #1d4ed8);"
                + "-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;"
                + "-fx-padding: 6 12; -fx-background-radius: 6; -fx-cursor: hand;");
        detailBtn.setOnAction(e -> {
            e.consume();
            if (mainController != null) mainController.openComponentDetail(c);
        });

        Button removeBtn = new Button("★ Убрать");
        removeBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b;"
                + "-fx-font-size: 11px; -fx-font-weight: bold;"
                + "-fx-padding: 6 12; -fx-background-radius: 6; -fx-cursor: hand;");
        removeBtn.setOnAction(e -> {
            e.consume();
            FavoritesRepository.removeFavorite(c.getId());
            loadFavorites(); // reload
        });

        btnRow.getChildren().addAll(detailBtn, removeBtn);

        card.getChildren().addAll(iv, cat, name, price, btnRow);

        card.setOnMouseEntered(e -> card.setStyle(MainController.CARD_HOVER_STYLE));
        card.setOnMouseExited(e -> card.setStyle(MainController.CARD_STYLE));

        return card;
    }

    private void loadDefaultImage(ImageView iv) {
        try {
            iv.setImage(new Image(getClass().getResourceAsStream("/com/example/authapp/default-image.png")));
        } catch (Exception ignored) {}
    }
}
