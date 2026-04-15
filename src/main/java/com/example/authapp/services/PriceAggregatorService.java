package com.example.authapp.services;

import com.example.authapp.models.Component;
import com.example.authapp.models.ComponentPrice;
import com.example.authapp.models.Store;
import com.example.authapp.repositories.ComponentPriceRepository;
import com.example.authapp.repositories.StoreRepository;

import java.util.List;

/**
 * Сервис-агрегатор. Держит кэш цен в БД.
 * <p>
 * Поведение на защите курсовой:
 * - При открытии карточки цены показываются МГНОВЕННО из БД (сид-данные).
 * - Парсинг отключён (нет парсеров).
 */
public class PriceAggregatorService {

    /**
     * Загружает кэшированные цены из БД.
     */
    public List<ComponentPrice> getCachedPrices(int componentId) throws Exception {
        return ComponentPriceRepository.loadForComponent(componentId);
    }

    /**
     * Заглушка для refresh (парсеры отключены).
     * @return 0
     */
    public int refreshPricesFor(Component component) throws Exception {
        return 0;
    }
}