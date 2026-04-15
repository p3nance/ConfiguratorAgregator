package controllers;

import com.example.authapp.models.Component;
import com.example.authapp.repositories.ComponentPriceRepository;
import com.example.authapp.repositories.ComponentRepository;
import com.example.authapp.repositories.FavoritesRepository;
import com.example.authapp.repositories.StoreRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.*;

/**
 * Главный контроллер: каталог компонентов + кнопка конфигуратора.
 * Никакой авторизации, корзины, заказов — только агрегатор.
 */
public class MainController implements Initializable {

    @FXML private VBox categoryItemsPane;
    @FXML private TilePane productPane;
    @FXML private TextField searchField;
    @FXML private Button configuratorBtn;
    @FXML private Button favoritesBtn;
    @FXML private HBox headerPane, searchBox;
    @FXML private VBox categoryPane;
    @FXML private ScrollPane contentScroll;
    @FXML private BorderPane mainPane;

    private final List<Component> allComponents = new ArrayList<>();
    private String selectedCategory = "Все";
    private String currentSearch = "";

    private final List<String> categories = Arrays.asList(
            "Все", "Процессоры", "Материнские платы", "Видеокарты",
            "Оперативная память", "SSD-накопители", "Блоки питания",
            "Охлаждение"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCategories();
        setupSearch();
        setupConfiguratorButton();

        contentScroll.widthProperty().addListener((obs, oldVal, newVal) ->
                updateCardSize(newVal.doubleValue()));

        Thread t = new Thread(this::loadDataFromSupabase, "main-loader");
        t.setDaemon(true);
        t.start();
    }

    private void loadDataFromSupabase() {
        try {
            StoreRepository.loadAll();
            List<Component> components = ComponentRepository.loadAll();

            Map<Integer, double[]> minPrices;
            try {
                minPrices = ComponentPriceRepository.loadMinPricesMap();
            } catch (Exception e) {
                minPrices = new HashMap<>();
            }

            for (Component c : components) {
                double[] agg = minPrices.get(c.getId());
                if (agg != null) {
                    c.setMinPrice(agg[0]);
                    c.setOfferCount((int) agg[1]);
                }
            }

            synchronized (allComponents) {
                allComponents.clear();
                allComponents.addAll(components);
            }
            Platform.runLater(this::applyFilter);
        } catch (Exception e) {
            Platform.runLater(() -> showErrorMessage("Ошибка загрузки данных",
                    "Не удалось получить данные из Supabase.\n" + e.getMessage()
                            + "\n\nПроверьте, что SQL-миграция накатана (sql/schema.sql)."));
        }
    }

    private void updateCardSize(double scrollWidth) {
        double padding = 48;
        double hgap = 16;
        double available = scrollWidth - padding - 20;

        int columns;
        if (available < 400) columns = 1;
        else if (available < 620) columns = 2;
        else if (available < 900) columns = 3;
        else columns = 4;

        double cardWidth = (available - hgap * (columns - 1)) / columns;
        cardWidth = Math.max(180, cardWidth);

        productPane.setPrefColumns(columns);
        productPane.setPrefTileWidth(cardWidth);
    }

    private void loadCategories() {
        categoryItemsPane.getChildren().clear();
        categoryItemsPane.setAlignment(Pos.CENTER);
        for (String cat : categories) {
            Button btn = new Button(cat);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setMinHeight(34);
            btn.setStyle(getCategoryButtonStyle(cat.equals(selectedCategory)));
            btn.setOnAction(e -> {
                selectedCategory = cat;
                loadCategories();
                applyFilter();
            });
            categoryItemsPane.getChildren().add(btn);
        }
    }

    private String getCategoryButtonStyle(boolean selected) {
        if (selected) {
            return "-fx-background-color: #3b82f6; -fx-text-fill: #fff;"
                    + "-fx-font-size: 13px; -fx-font-weight: bold;"
                    + "-fx-background-radius: 9; -fx-border-radius: 9;"
                    + "-fx-border-width: 2; -fx-border-color: #2563eb;"
                    + "-fx-padding: 6 20; -fx-cursor: hand;";
        }
        return "-fx-background-color: transparent; -fx-text-fill: #1f2937;"
                + "-fx-font-size: 13px; -fx-font-weight: bold;"
                + "-fx-background-radius: 9; -fx-padding: 6 20; -fx-cursor: hand;";
    }

    private void setupSearch() {
        searchField.setOnKeyReleased(event -> {
            currentSearch = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
            applyFilter();
        });
    }

    private void setupConfiguratorButton() {
        if (configuratorBtn != null) {
            configuratorBtn.setOnAction(e -> openConfigurator());
        }
        if (favoritesBtn != null) {
            favoritesBtn.setOnAction(e -> openFavorites());
        }
    }

    private void applyFilter() {
        List<Component> filtered;
        synchronized (allComponents) {
            filtered = new ArrayList<>(allComponents);
        }
        if (!"Все".equals(selectedCategory)) {
            filtered.removeIf(c -> c.getCategory() == null
                    || !c.getCategory().equalsIgnoreCase(selectedCategory));
        }
        if (!currentSearch.isEmpty()) {
            filtered.removeIf(c -> {
                String name = c.getName() == null ? "" : c.getName().toLowerCase();
                String man = c.getManufacturer() == null ? "" : c.getManufacturer().toLowerCase();
                return !name.contains(currentSearch) && !man.contains(currentSearch);
            });
        }
        showComponents(filtered);
    }

    private void showComponents(List<Component> components) {
        productPane.getChildren().clear();
        if (components.isEmpty()) {
            Label empty = new Label("Компоненты не найдены");
            empty.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280; -fx-padding: 40;");
            productPane.getChildren().add(empty);
            return;
        }
        for (Component c : components) {
            productPane.getChildren().add(buildCard(c));
        }
    }

    private VBox buildCard(Component c) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(CARD_STYLE);
        card.setPrefWidth(220);

        ImageView iv = new ImageView();
        iv.setFitWidth(180);
        iv.setFitHeight(140);
        iv.setPreserveRatio(true);
        try {
            String url = c.getImageUrl();
            if (url != null && !url.trim().isEmpty()) {
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

        HBox priceRow = new HBox(6);
        priceRow.setAlignment(Pos.CENTER);
        Label from = new Label("от");
        from.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");
        Label price = new Label(c.getMinPrice() > 0
                ? String.format("%,.0f ₽", c.getMinPrice())
                : "цен нет");
        price.setStyle("-fx-font-weight: bold; -fx-text-fill: "
                + (c.getMinPrice() > 0 ? "#10b981" : "#9ca3af")
                + "; -fx-font-size: 16px;");
        priceRow.getChildren().addAll(from, price);

        Label offers = new Label(c.getOfferCount() > 0
                ? (c.getOfferCount() + " предлож.")
                : "нет предложений");
        offers.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");

        Button detailBtn = new Button("Сравнить цены →");
        detailBtn.setMaxWidth(Double.MAX_VALUE);
        detailBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #3b82f6, #1d4ed8);"
                + "-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;"
                + "-fx-padding: 8 14; -fx-background-radius: 8; -fx-cursor: hand;");
        detailBtn.setOnAction(e -> {
            e.consume();
            openComponentDetail(c);
        });

        card.getChildren().addAll(iv, cat, name, priceRow, offers, detailBtn);

        card.setOnMouseEntered(e -> card.setStyle(CARD_HOVER_STYLE));
        card.setOnMouseExited(e -> card.setStyle(CARD_STYLE));
        card.setOnMouseClicked(e -> openComponentDetail(c));

        return card;
    }

    private void loadDefaultImage(ImageView iv) {
        try {
            iv.setImage(new Image(getClass().getResourceAsStream("/com/example/authapp/default-image.png")));
        } catch (Exception ignored) {}
    }

    public void openComponentDetail(Component c) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/component-detail.fxml"));
            Node node = loader.load();
            ComponentDetailController ctrl = loader.getController();
            ctrl.setMainController(this);
            ctrl.setComponent(c);
            hideTopAndLeft();
            mainPane.setCenter(node);
        } catch (Exception e) {
            showErrorMessage("Ошибка", "Не удалось открыть карточку: " + e.getMessage());
        }
    }

    public void openConfigurator() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/configurator.fxml"));
            Node node = loader.load();
            ConfiguratorController ctrl = loader.getController();
            ctrl.setMainController(this);
            synchronized (allComponents) {
                ctrl.setComponents(new ArrayList<>(allComponents));
            }
            hideTopAndLeft();
            mainPane.setCenter(node);
        } catch (Exception e) {
            showErrorMessage("Ошибка", "Не удалось открыть конфигуратор: " + e.getMessage());
        }
    }

    private void hideTopAndLeft() {
        headerPane.setVisible(false); headerPane.setManaged(false);
        categoryPane.setVisible(false); categoryPane.setManaged(false);
        mainPane.setTop(null);
        mainPane.setLeft(null);
    }

    public void showMainContent() {
        headerPane.setVisible(true); headerPane.setManaged(true);
        categoryPane.setVisible(true); categoryPane.setManaged(true);
        mainPane.setTop(headerPane);
        mainPane.setLeft(categoryPane);
        mainPane.setCenter(contentScroll);
        applyFilter();
    }

    /**
     * Показать экран избранных компонентов.
     */
    public void openFavorites() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/favorites.fxml"));
            Node node = loader.load();
            FavoritesController ctrl = loader.getController();
            ctrl.setMainController(this);
            ctrl.loadFavorites();
            hideTopAndLeft();
            mainPane.setCenter(node);
        } catch (Exception e) {
            showErrorMessage("Ошибка", "Не удалось открыть избранное: " + e.getMessage());
        }
    }

    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    static final String CARD_STYLE =
            "-fx-background-color: #ffffff; -fx-border-color: #e5e7eb;"
                    + "-fx-border-width: 1; -fx-border-radius: 14;"
                    + "-fx-background-radius: 14; -fx-padding: 12;"
                    + "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.10), 8, 0, 0, 2);"
                    + "-fx-cursor: hand;";

    static final String CARD_HOVER_STYLE =
            "-fx-background-color: #ffffff; -fx-border-color: #bfdbfe;"
                    + "-fx-border-width: 1; -fx-border-radius: 14;"
                    + "-fx-background-radius: 14; -fx-padding: 12;"
                    + "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.25), 14, 0, 0, 5);"
                    + "-fx-cursor: hand;";
}
