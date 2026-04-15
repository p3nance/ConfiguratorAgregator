package controllers;

import com.example.authapp.models.Component;
import com.example.authapp.models.ComponentPrice;
import com.example.authapp.repositories.FavoritesRepository;
import com.example.authapp.services.PriceAggregatorService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.awt.Desktop;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * Экран карточки компонента: фото, характеристики, таблица цен магазинов.
 * Цены загружаются из Supabase (БД).
 */
public class ComponentDetailController {

    @FXML private Button backBtn;
    @FXML private Button favoriteBtn;
    @FXML private ImageView componentImage;
    @FXML private Label categoryLabel;
    @FXML private Label manufacturerLabel;
    @FXML private Label nameLabel;
    @FXML private Label descriptionLabel;
    @FXML private VBox specsContainer;
    @FXML private VBox pricesContainer;
    @FXML private Label minPriceLabel;
    @FXML private Label offerCountLabel;
    @FXML private Label statusLabel;

    private MainController mainController;
    private Component component;
    private final PriceAggregatorService aggregator = new PriceAggregatorService();

    public void initialize() {
        backBtn.setOnAction(e -> {
            if (mainController != null) mainController.showMainContent();
        });
        favoriteBtn.setOnAction(e -> toggleFavorite());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setComponent(Component component) {
        this.component = component;
        render();
        updateFavoriteButton();
        loadPricesAsync();
    }

    private void render() {
        if (component == null) return;

        nameLabel.setText(component.getName());
        categoryLabel.setText(component.getCategory());
        manufacturerLabel.setText(
                component.getManufacturer().isEmpty()
                        ? "Производитель не указан"
                        : "Производитель: " + component.getManufacturer());
        descriptionLabel.setText(
                component.getDescription().isEmpty()
                        ? "Описание отсутствует"
                        : component.getDescription());

        // Картинка
        try {
            String img = component.getImageUrl();
            if (img != null && !img.isEmpty()) {
                componentImage.setImage(new Image(img, true));
            } else {
                loadDefaultImage();
            }
        } catch (Exception e) {
            loadDefaultImage();
        }

        renderSpecs();
    }

    private void loadDefaultImage() {
        try {
            componentImage.setImage(new Image(
                    getClass().getResourceAsStream("/com/example/authapp/default-image.png")));
        } catch (Exception ignored) {}
    }

    /**
     * Парсит поле specs (формат key:value через пробел или перевод строки)
     * и рисует таблицу характеристик.
     */
    private void renderSpecs() {
        specsContainer.getChildren().clear();
        String specs = component.getSpecs();
        if (specs == null || specs.isBlank()) {
            // Попробуем достать характеристики из description (старый формат)
            specs = component.getDescription();
        }
        if (specs == null || specs.isBlank()) {
            Label none = new Label("Характеристики не указаны");
            none.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 13px;");
            specsContainer.getChildren().add(none);
            return;
        }

        String[] tokens = specs.split("[\\n,;]|\\s{2,}");
        boolean any = false;
        for (String token : tokens) {
            String t = token.trim();
            if (t.isEmpty()) continue;
            int colon = t.indexOf(':');
            if (colon <= 0 || colon == t.length() - 1) continue;
            String key = t.substring(0, colon).trim();
            String val = t.substring(colon + 1).trim();
            if (key.isEmpty() || val.isEmpty()) continue;

            HBox row = new HBox(10);
            row.setStyle("-fx-padding: 6 0; -fx-border-color: transparent transparent #f3f4f6 transparent; -fx-border-width: 0 0 1 0;");
            Label k = new Label(humanizeKey(key));
            k.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 13px;");
            k.setPrefWidth(180);
            Label v = new Label(val);
            v.setStyle("-fx-text-fill: #111827; -fx-font-size: 13px; -fx-font-weight: bold;");
            v.setWrapText(true);
            row.getChildren().addAll(k, v);
            specsContainer.getChildren().add(row);
            any = true;
        }

        if (!any) {
            Label raw = new Label(specs);
            raw.setWrapText(true);
            raw.setStyle("-fx-text-fill: #374151; -fx-font-size: 13px;");
            specsContainer.getChildren().add(raw);
        }
    }

    private String humanizeKey(String key) {
        switch (key.toLowerCase()) {
            case "socket": return "Сокет";
            case "tdp": return "TDP (Вт)";
            case "memory": return "Тип памяти";
            case "gpu_power": return "Потребление GPU (Вт)";
            case "psu_power": return "Мощность БП (Вт)";
            case "cores": return "Ядер";
            case "threads": return "Потоков";
            case "frequency": return "Частота";
            case "vram": return "Видеопамять";
            case "capacity": return "Объём";
            default: return key.substring(0, 1).toUpperCase() + key.substring(1);
        }
    }

    private void loadPricesAsync() {
        statusLabel.setText("Загрузка цен...");
        new Thread(() -> {
            try {
                List<ComponentPrice> prices = aggregator.getCachedPrices(component.getId());
                Platform.runLater(() -> renderPrices(prices));
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Ошибка загрузки цен: " + e.getMessage());
                });
            }
        }, "detail-prices-load").start();
    }

    private void renderPrices(List<ComponentPrice> prices) {
        pricesContainer.getChildren().clear();

        if (prices == null || prices.isEmpty()) {
            Label empty = new Label("Цен пока нет в базе данных.");
            empty.setWrapText(true);
            empty.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 13px; -fx-padding: 20;");
            pricesContainer.getChildren().add(empty);
            minPriceLabel.setText("—");
            offerCountLabel.setText("0 предложений");
            statusLabel.setText("");
            return;
        }

        prices.sort(Comparator.comparingDouble(ComponentPrice::getPrice));
        double min = prices.get(0).getPrice();
        int inStockCount = 0;

        // Заголовок таблицы
        HBox header = new HBox(10);
        header.setStyle("-fx-background-color: #f9fafb; -fx-padding: 10 15; -fx-background-radius: 8 8 0 0;");
        Label hPhoto = new Label(""); hPhoto.setPrefWidth(48);
        Label h1 = new Label("Магазин"); h1.setPrefWidth(150);
        h1.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-font-size: 12px;");
        Label h2 = new Label("Цена"); h2.setPrefWidth(120);
        h2.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-font-size: 12px;");
        Label h3 = new Label("Наличие"); h3.setPrefWidth(100);
        h3.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-font-size: 12px;");
        Label h4 = new Label("Обновлено"); h4.setPrefWidth(140);
        h4.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-font-size: 12px;");
        Region hSpacer = new Region(); HBox.setHgrow(hSpacer, Priority.ALWAYS);
        header.getChildren().addAll(hPhoto, h1, h2, h3, h4, hSpacer);
        pricesContainer.getChildren().add(header);

        boolean alt = false;
        for (ComponentPrice cp : prices) {
            if (cp.isInStock()) inStockCount++;

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: " + (alt ? "#ffffff" : "#fafafa")
                    + "; -fx-padding: 12 15; -fx-border-color: transparent transparent #f3f4f6 transparent; -fx-border-width: 0 0 1 0;");
            alt = !alt;

            ImageView storeImg = new ImageView();
            storeImg.setFitWidth(48);
            storeImg.setFitHeight(48);
            storeImg.setPreserveRatio(true);
            storeImg.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 6; -fx-background-insets: 2;");
            String imgUrl = cp.getImageUrl();
            if (imgUrl != null && !imgUrl.isEmpty()) {
                try {
                    storeImg.setImage(new Image(imgUrl, 48, 48, true, true));
                } catch (Exception e) {
                    // leave placeholder
                }
            }

            VBox storeInfo = new VBox(2);
            storeInfo.setPrefWidth(150);
            Label storeName = new Label(cp.getStoreName() != null ? cp.getStoreName() : "—");
            storeName.setStyle("-fx-font-weight: bold; -fx-text-fill: #111827; -fx-font-size: 14px;");
            storeInfo.getChildren().add(storeName);

            Label priceLabel = new Label(String.format("%,.0f ₽", cp.getPrice()));
            priceLabel.setPrefWidth(120);
            boolean isMin = Math.abs(cp.getPrice() - min) < 0.01;
            priceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: "
                    + (isMin ? "#10b981" : "#111827") + ";");

            Label stockLabel = new Label(cp.isInStock() ? "✅ В наличии" : "❌ Нет");
            stockLabel.setPrefWidth(100);
            stockLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: "
                    + (cp.isInStock() ? "#065f46" : "#991b1b") + ";");

            Label updLabel = new Label(formatAge(cp.getUpdatedAt()));
            updLabel.setPrefWidth(140);
            updLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button openBtn = new Button("Перейти →");
            openBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #3b82f6, #1d4ed8); "
                    + "-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; "
                    + "-fx-padding: 6 14; -fx-background-radius: 6; -fx-cursor: hand;");
            String targetUrl = cp.getProductUrl();
            openBtn.setDisable(targetUrl == null || targetUrl.isEmpty());
            openBtn.setOnAction(e -> openInBrowser(targetUrl));

            row.getChildren().addAll(storeImg, storeInfo, priceLabel, stockLabel, updLabel, spacer, openBtn);
            pricesContainer.getChildren().add(row);
        }

        minPriceLabel.setText(String.format("%,.0f ₽", min));
        offerCountLabel.setText(inStockCount + " из " + prices.size() + " в наличии");
        statusLabel.setText("");
    }

    private String formatAge(Instant updatedAt) {
        if (updatedAt == null) return "—";
        Duration d = Duration.between(updatedAt, Instant.now());
        long minutes = d.toMinutes();
        if (minutes < 1) return "только что";
        if (minutes < 60) return minutes + " мин. назад";
        long hours = d.toHours();
        if (hours < 24) return hours + " ч. назад";
        long days = d.toDays();
        return days + " дн. назад";
    }

    private void openInBrowser(String url) {
        if (url == null || url.isEmpty()) return;
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception ignored) {}
    }

    private void toggleFavorite() {
        if (component == null) return;
        new Thread(() -> {
            boolean nowFavorite = FavoritesRepository.toggleFavorite(component.getId());
            Platform.runLater(() -> {
                favoriteBtn.setText(nowFavorite ? "★ В избранном" : "☆ В избранное");
            });
        }, "toggle-favorite").start();
    }

    private void updateFavoriteButton() {
        if (component == null) return;
        new Thread(() -> {
            boolean fav = FavoritesRepository.isFavorite(component.getId());
            Platform.runLater(() -> {
                favoriteBtn.setText(fav ? "★ В избранном" : "☆ В избранное");
            });
        }, "check-favorite").start();
    }

}
