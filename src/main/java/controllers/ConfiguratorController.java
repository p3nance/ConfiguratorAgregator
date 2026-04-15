package controllers;

import com.example.authapp.models.Component;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Конфигуратор сборки ПК. Работает с объектами Component (не товарами),
 * подставляет минимальную цену из агрегатора в итоговую стоимость.
 */
public class ConfiguratorController {

    @FXML private BorderPane mainContent;
    @FXML private Button backBtn;
    @FXML private VBox categoriesContainer;

    @FXML private Label totalPrice;
    @FXML private HBox compatStatusBox;
    @FXML private Label compatIcon;
    @FXML private Label compatStatusText;

    @FXML private ListView<String> componentsList;
    @FXML private Button sortBtn;
    @FXML private Button viewBuildDetailsBtn;

    @FXML private HBox cartStatusBox;
    @FXML private Label cartStatusIcon;
    @FXML private Label cartStatusText;

    @FXML private VBox overlayPane;
    @FXML private Label overlayTitle;
    @FXML private Button closeOverlayBtn;
    @FXML private Button clearSelectionBtn;
    @FXML private TilePane overlayProductsGrid;

    private MainController mainController;
    private final List<Component> allComponents = new ArrayList<>();
    private final Map<String, Component> selectedComponents = new LinkedHashMap<>();

    private final String[] categoryNames = {
            "Процессоры", "Материнские платы", "Видеокарты",
            "Оперативная память", "SSD-накопители", "Блоки питания", "Охлаждение"
    };

    private enum SortMode { BY_CATEGORY, BY_PRICE, BY_NAME }
    private SortMode currentSort = SortMode.BY_CATEGORY;
    private SortMode overlaySort = SortMode.BY_PRICE;

    private String currentEditingCategory = null;

    public void initialize() {
        backBtn.setOnAction(e -> goBack());
        if (viewBuildDetailsBtn != null) {
            viewBuildDetailsBtn.setOnAction(e -> openCheapestComponentDetail());
        }

        closeOverlayBtn.setOnAction(e -> closeOverlay());
        clearSelectionBtn.setOnAction(e -> {
            if (currentEditingCategory != null) {
                selectedComponents.put(currentEditingCategory, null);
                renderCategories();
                recalcBuild();
                closeOverlay();
            }
        });

        for (String cat : categoryNames) {
            selectedComponents.put(cat, null);
        }

        compatStatusText.setWrapText(true);
        compatStatusText.maxWidthProperty().bind(compatStatusBox.widthProperty().subtract(40));
        if (cartStatusText != null) cartStatusText.setWrapText(true);

        if (sortBtn != null) {
            sortBtn.setOnAction(e -> cycleSortMode());
            updateSortButton();
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void goBack() {
        if (mainController != null) mainController.showMainContent();
    }

    public void setComponents(List<Component> components) {
        this.allComponents.clear();
        this.allComponents.addAll(components);
        renderCategories();
        recalcBuild();
    }

    private void renderCategories() {
        categoriesContainer.getChildren().clear();

        for (String category : categoryNames) {
            HBox catBox = new HBox(15);
            catBox.setAlignment(Pos.CENTER_LEFT);
            catBox.setStyle("-fx-background-color: white; -fx-padding: 15 20; "
                    + "-fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-background-radius: 10;");

            Label icon = new Label(getIconForCategory(category));
            icon.setStyle("-fx-font-size: 24px;");

            VBox nameBox = new VBox(4);
            Label catLabel = new Label(category);
            catLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

            Component selected = selectedComponents.get(category);
            Label selectedLabel = new Label(selected != null ? selected.getName() : "Не выбрано");
            selectedLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: "
                    + (selected != null ? "#3b82f6" : "#9ca3af") + ";");
            nameBox.getChildren().addAll(catLabel, selectedLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label priceLabel = new Label(selected != null && selected.getMinPrice() > 0
                    ? String.format("от %,.0f ₽", selected.getMinPrice())
                    : (selected != null ? "цен нет" : ""));
            priceLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: "
                    + (selected != null && selected.getMinPrice() > 0 ? "#10b981" : "#9ca3af") + ";");

            Button selectBtn = new Button(selected != null ? "Изменить" : "Выбрать");

            String btnStyle = selected != null
                    ? "-fx-background-color: #f3f4f6; -fx-text-fill: #374151; "
                      + "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 8 16; "
                      + "-fx-background-radius: 8; -fx-border-radius: 8; "
                      + "-fx-border-width: 1; -fx-border-color: #d1d5db; -fx-cursor: hand;"
                    : "-fx-background-color: linear-gradient(to bottom, #3b82f6, #1d4ed8); "
                      + "-fx-text-fill: #ffffff; -fx-font-size: 13px; -fx-font-weight: bold; "
                      + "-fx-padding: 8 16; -fx-background-radius: 8; -fx-border-radius: 8; "
                      + "-fx-border-width: 1.5; -fx-border-color: #93c5fd; -fx-cursor: hand;";
            selectBtn.setStyle(btnStyle);
            selectBtn.setOnAction(e -> openOverlayForCategory(category));

            catBox.getChildren().addAll(icon, nameBox, spacer, priceLabel, selectBtn);

            // Клик по всей плашке открывает карточку компонента
            if (selected != null) {
                catBox.setOnMouseClicked(e -> {
                    if (e.getTarget() instanceof Button) return;
                    if (mainController != null) mainController.openComponentDetail(selected);
                });
                catBox.setStyle(catBox.getStyle() + " -fx-cursor: hand;");
            }

            categoriesContainer.getChildren().add(catBox);
        }
    }

    private String getIconForCategory(String cat) {
        switch (cat) {
            case "Процессоры": return "⚙";
            case "Материнские платы": return "🖧";
            case "Видеокарты": return "📺";
            case "Оперативная память": return "☷";
            case "SSD-накопители": return "🖴";
            case "Блоки питания": return "🔋";
            default: return "📦";
        }
    }

    private void openOverlayForCategory(String category) {
        currentEditingCategory = category;
        overlaySort = SortMode.BY_PRICE; // default overlay sort
        renderOverlayProducts(category);

        mainContent.setDisable(true);
        overlayPane.setVisible(true);
        if (cartStatusBox != null) {
            cartStatusBox.setVisible(false);
            cartStatusBox.setManaged(false);
        }
    }

    private void renderOverlayProducts(String category) {
        List<Component> available = allComponents.stream()
                .filter(p -> p.getCategory() != null && p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        switch (overlaySort) {
            case BY_PRICE -> available.sort(Comparator.comparingDouble(Component::getMinPrice));
            case BY_NAME -> available.sort(Comparator.comparing(Component::getName, String.CASE_INSENSITIVE_ORDER));
            case BY_CATEGORY -> {} // natural order
        }

        overlayProductsGrid.getChildren().clear();

        for (Component p : available) {
            VBox card = new VBox(8);
            card.setAlignment(Pos.TOP_CENTER);
            card.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; "
                    + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 15; -fx-cursor: hand;");
            card.setPrefWidth(210);

            ImageView iv = new ImageView();
            iv.setFitWidth(150); iv.setFitHeight(100); iv.setPreserveRatio(true);
            try {
                if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
                    iv.setImage(new Image(p.getImageUrl(), true));
                }
            } catch (Exception ignored) {}

            Label name = new Label(p.getName());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            name.setWrapText(true);
            name.setAlignment(Pos.CENTER);
            name.setMinHeight(35);

            Label price = new Label(p.getMinPrice() > 0
                    ? String.format("от %,.0f ₽", p.getMinPrice())
                    : "цен нет");
            price.setStyle("-fx-font-weight: bold; -fx-text-fill: "
                    + (p.getMinPrice() > 0 ? "#10b981" : "#9ca3af") + "; -fx-font-size: 14px;");

            Label offers = new Label(p.getOfferCount() > 0 ? (p.getOfferCount() + " магазинов") : "");
            offers.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280;");

            card.getChildren().addAll(iv, name, price, offers);

            card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #3b82f6; "
                    + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 15; -fx-cursor: hand;"));
            card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; "
                    + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 15; -fx-cursor: hand;"));

            card.setOnMouseClicked(e -> {
                selectedComponents.put(category, p);
                renderCategories();
                recalcBuild();
                closeOverlay();
            });

            overlayProductsGrid.getChildren().add(card);
        }

        if (available.isEmpty()) {
            Label empty = new Label("Компоненты не найдены");
            empty.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");
            overlayProductsGrid.getChildren().add(empty);
        }
    }

    private void closeOverlay() {
        overlayPane.setVisible(false);
        mainContent.setDisable(false);
        currentEditingCategory = null;
    }

    private void recalcBuild() {
        componentsList.getItems().clear();
        double sum = 0;
        int count = 0;
        boolean anyMissingPrice = false;

        Component cpu = selectedComponents.get("Процессоры");
        Component mobo = selectedComponents.get("Материнские платы");
        Component gpu = selectedComponents.get("Видеокарты");
        Component ram = selectedComponents.get("Оперативная память");
        Component storage = selectedComponents.get("SSD-накопители");
        Component psu = selectedComponents.get("Блоки питания");

        // Collect selected components with their category
        List<Map.Entry<String, Component>> buildList = new ArrayList<>();
        for (String cat : categoryNames) {
            Component p = selectedComponents.get(cat);
            if (p != null) {
                buildList.add(new AbstractMap.SimpleEntry<>(cat, p));
                String priceStr;
                if (p.getMinPrice() > 0) {
                    priceStr = String.format("от %,.0f ₽", p.getMinPrice());
                    sum += p.getMinPrice();
                } else {
                    priceStr = "цены нет";
                    anyMissingPrice = true;
                }
                count++;
            }
        }

        // Sort according to current mode
        switch (currentSort) {
            case BY_NAME -> buildList.sort(Map.Entry.comparingByValue(
                    Comparator.comparing(Component::getName, String.CASE_INSENSITIVE_ORDER)));
            case BY_PRICE -> buildList.sort(Map.Entry.comparingByValue(
                    Comparator.comparingDouble(Component::getMinPrice)));
            case BY_CATEGORY -> {} // already in category order
        }

        for (var entry : buildList) {
            Component p = entry.getValue();
            String priceStr = p.getMinPrice() > 0
                    ? String.format("от %,.0f ₽", p.getMinPrice())
                    : "цены нет";
            componentsList.getItems().add(entry.getKey() + ": " + p.getName() + " — " + priceStr);
        }

        totalPrice.setText(sum > 0 ? String.format("от %,.0f ₽", sum) : "—");

        List<String> issues = checkCompatibility(cpu, mobo, gpu, ram, storage, psu);

        compatStatusBox.setMinHeight(Region.USE_PREF_SIZE);

        if (count == 0) {
            compatStatusBox.setStyle("-fx-background-color: #f3f4f6; -fx-padding: 10 15; "
                    + "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #d1d5db;");
            compatIcon.setText("ℹ");
            compatIcon.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 16px;");
            compatStatusText.setText("Сборка пуста. Начните выбор компонентов.");
            compatStatusText.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 13px; -fx-font-weight: bold;");
        } else if (issues.isEmpty()) {
            String msg = "Все выбранные компоненты совместимы!";
            if (anyMissingPrice) msg += "\n(у некоторых компонентов нет цен — итог приблизительный)";
            compatStatusBox.setStyle("-fx-background-color: #ecfdf5; -fx-padding: 10 15; "
                    + "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #10b981;");
            compatIcon.setText("✅");
            compatIcon.setStyle("-fx-text-fill: #10b981; -fx-font-size: 16px;");
            compatStatusText.setText(msg);
            compatStatusText.setStyle("-fx-text-fill: #065f46; -fx-font-size: 13px; -fx-font-weight: bold;");
        } else {
            compatStatusBox.setStyle("-fx-background-color: #fef2f2; -fx-padding: 10 15; "
                    + "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #ef4444;");
            compatIcon.setText("❌");
            compatIcon.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 16px;");
            compatStatusText.setText(String.join("\n", issues));
            compatStatusText.setStyle("-fx-text-fill: #991b1b; -fx-font-size: 13px; -fx-font-weight: bold;");
        }
    }

    private String getParam(Component c, String key) {
        if (c == null) return null;
        String src = c.getSpecs();
        if (src == null || src.isBlank()) src = c.getDescription();
        if (src == null) return null;
        String desc = src.toLowerCase();
        String searchKey = key.toLowerCase() + ":";
        int idx = desc.indexOf(searchKey);
        if (idx == -1) return null;
        int start = idx + searchKey.length();
        int end = desc.indexOf(" ", start);
        return end == -1 ? desc.substring(start).trim() : desc.substring(start, end).trim();
    }

    private int getIntParam(Component c, String key) {
        String val = getParam(c, key);
        if (val == null) return 0;
        try { return Integer.parseInt(val.replaceAll("[^0-9]", "")); } catch (Exception e) { return 0; }
    }

    private List<String> checkCompatibility(Component cpu, Component mobo, Component gpu,
                                            Component ram, Component storage, Component psu) {
        List<String> issues = new ArrayList<>();

        if (cpu != null && mobo != null) {
            String cpuSocket = getParam(cpu, "socket");
            String moboSocket = getParam(mobo, "socket");
            if (cpuSocket != null && moboSocket != null && !cpuSocket.equalsIgnoreCase(moboSocket)) {
                issues.add("Сокет процессора не подходит к плате");
            }
        }

        if (mobo != null && ram != null) {
            String moboMem = getParam(mobo, "memory");
            String ramMem = getParam(ram, "memory");
            if (moboMem != null && ramMem != null && !moboMem.equalsIgnoreCase(ramMem)) {
                issues.add("Память не поддерживается платой");
            }
        }

        if (psu != null) {
            int psuPower = getIntParam(psu, "psu_power");
            int cpuTdp = getIntParam(cpu, "tdp");
            int gpuPower = getIntParam(gpu, "gpu_power");
            int totalNeed = cpuTdp + gpuPower;

            if (psuPower > 0 && totalNeed > 0 && psuPower < totalNeed) {
                issues.add("Блок питания слабоват. Нужно минимум " + totalNeed + "Вт");
            }
        }
        return issues;
    }

    /**
     * Открывает карточку первого выбранного компонента — это доступ к сравнению
     * цен без необходимости возвращаться в каталог.
     */
    private void openCheapestComponentDetail() {
        for (String cat : categoryNames) {
            Component c = selectedComponents.get(cat);
            if (c != null && mainController != null) {
                mainController.openComponentDetail(c);
                return;
            }
        }
    }

    private void cycleSortMode() {
        SortMode[] modes = SortMode.values();
        currentSort = modes[(currentSort.ordinal() + 1) % modes.length];
        updateSortButton();
        recalcBuild();
    }

    private void updateSortButton() {
        if (sortBtn == null) return;
        switch (currentSort) {
            case BY_CATEGORY -> sortBtn.setText("По категории");
            case BY_NAME -> sortBtn.setText("По имени");
            case BY_PRICE -> sortBtn.setText("По цене");
        }
    }
}
