DROP TABLE IF EXISTS component_prices CASCADE;
DROP TABLE IF EXISTS components       CASCADE;
DROP TABLE IF EXISTS stores           CASCADE;

CREATE TABLE stores (
    id                  SERIAL PRIMARY KEY,
    code                TEXT NOT NULL UNIQUE,
    name                TEXT NOT NULL,
    website             TEXT NOT NULL DEFAULT '',
    logo_url            TEXT NOT NULL DEFAULT '',
    search_url_template TEXT NOT NULL DEFAULT ''
);

INSERT INTO stores (code, name, website, logo_url, search_url_template) VALUES
('dns',      'DNS',      'https://www.dns-shop.ru', '', 'https://www.dns-shop.ru/search/?q={query}'),
('citilink', 'Ситилинк', 'https://www.citilink.ru', '', 'https://www.citilink.ru/search/?text={query}'),
('ozon',     'Ozon',     'https://www.ozon.ru',     '', 'https://www.ozon.ru/search/?text={query}');
CREATE TABLE components (
    id           SERIAL PRIMARY KEY,
    name         TEXT NOT NULL,
    description  TEXT NOT NULL DEFAULT '',
    image_url    TEXT NOT NULL DEFAULT '',
    category     TEXT NOT NULL,
    manufacturer TEXT NOT NULL DEFAULT '',
    specs        TEXT NOT NULL DEFAULT ''
);

CREATE INDEX idx_components_category ON components (category);
INSERT INTO components (name, description, image_url, category, manufacturer, specs) VALUES
('Intel Core i5-13400F', 'Десктопный процессор Intel 13-го поколения, 10 ядер.',                '', 'Процессоры', 'Intel', 'socket:LGA1700 tdp:65 cores:10 threads:16'),
('Intel Core i7-13700K', 'Топовый Raptor Lake, 16 ядер, разблокированный множитель.',          '', 'Процессоры', 'Intel', 'socket:LGA1700 tdp:125 cores:16 threads:24'),
('AMD Ryzen 5 7600',     'Шестиядерный Zen 4 для платформы AM5.',                              '', 'Процессоры', 'AMD',   'socket:AM5 tdp:65 cores:6 threads:12'),
('AMD Ryzen 7 7800X3D',  'Восьмиядерный Zen 4 с 3D V-Cache — лучший выбор для игр.',           '', 'Процессоры', 'AMD',   'socket:AM5 tdp:120 cores:8 threads:16'),
('ASUS PRIME B760M-A',   'mATX материнская плата на чипсете B760, поддержка DDR5.',            '', 'Материнские платы', 'ASUS',     'socket:LGA1700 memory:DDR5'),
('MSI MAG B650 TOMAHAWK','ATX плата на B650 для платформы AM5.',                               '', 'Материнские платы', 'MSI',      'socket:AM5 memory:DDR5'),
('Gigabyte Z790 AORUS ELITE', 'ATX-флагман на Z790 с поддержкой DDR5 и PCIe 5.0.',             '', 'Материнские платы', 'Gigabyte', 'socket:LGA1700 memory:DDR5'),
('NVIDIA GeForce RTX 4060',     'Видеокарта среднего класса с DLSS 3.',                        '', 'Видеокарты', 'NVIDIA', 'gpu_power:115 vram:8GB'),
('NVIDIA GeForce RTX 4070 Super','Высокопроизводительная видеокарта поколения Ada Lovelace.',  '', 'Видеокарты', 'NVIDIA', 'gpu_power:220 vram:12GB'),
('NVIDIA GeForce RTX 4090',     'Топовая видеокарта Ada Lovelace, 24 ГБ GDDR6X.',              '', 'Видеокарты', 'NVIDIA', 'gpu_power:450 vram:24GB'),
('AMD Radeon RX 7800 XT',       'Конкурент RTX 4070 от AMD на архитектуре RDNA 3.',            '', 'Видеокарты', 'AMD',    'gpu_power:263 vram:16GB'),
('Kingston FURY Beast 32GB DDR5-5600', 'Комплект 2x16 ГБ DDR5, тайминги CL36.',                '', 'Оперативная память', 'Kingston', 'memory:DDR5 capacity:32GB'),
('Corsair Vengeance 32GB DDR5-6000',   'Комплект 2x16 ГБ DDR5 с радиаторами, CL30.',           '', 'Оперативная память', 'Corsair',  'memory:DDR5 capacity:32GB'),
('G.Skill Trident Z5 64GB DDR5-6400',  'Комплект 2x32 ГБ DDR5 для рабочих станций.',           '', 'Оперативная память', 'G.Skill',  'memory:DDR5 capacity:64GB'),
('Samsung 990 PRO 1TB',   'NVMe PCIe 4.0 SSD, до 7450 МБ/с чтения.',                           '', 'SSD-накопители', 'Samsung', 'capacity:1TB'),
('WD Black SN850X 2TB',   'NVMe PCIe 4.0 SSD для геймеров, 2 ТБ.',                             '', 'SSD-накопители', 'WD',      'capacity:2TB'),
('Kingston KC3000 1TB',   'Скоростной PCIe 4.0 NVMe SSD на контроллере Phison E18.',           '', 'SSD-накопители', 'Kingston','capacity:1TB'),
('be quiet! Pure Power 12 M 750W', 'Тихий модульный БП с сертификатом 80+ Gold.',              '', 'Блоки питания', 'be quiet!', 'psu_power:750'),
('Corsair RM850x 850W',           'Полностью модульный 80+ Gold блок питания.',                '', 'Блоки питания', 'Corsair',   'psu_power:850'),
('Seasonic Focus GX-1000 1000W',  'Премиум БП 80+ Gold для топовых сборок.',                   '', 'Блоки питания', 'Seasonic',  'psu_power:1000'),
('Noctua NH-D15',             'Флагманский башенный кулер, два вентилятора 140 мм, TDP до 250 Вт.',  '', 'Охлаждение', 'Noctua', 'tdp:250 socket:LGA1700 socket:AM5'),
('be quiet! Dark Rock Pro 5', 'Тихий двухбашенный кулер с вентилятором Silent Wings, TDP до 270 Вт.', '', 'Охлаждение', 'be quiet!', 'tdp:270 socket:LGA1700 socket:AM5'),
('DeepCool AK620',            'Двухбашенный кулер с двумя тепловыми трубками 6 мм, TDP до 260 Вт.',  '', 'Охлаждение', 'DeepCool', 'tdp:260 socket:LGA1700 socket:AM5'),
('ID-COOLING SE-226-XT',      'Бюджетная башня с одним вентилятором 120 мм, TDP до 180 Вт.',         '', 'Охлаждение', 'ID-COOLING', 'tdp:180 socket:LGA1700 socket:AM5');

CREATE TABLE component_prices (
    id           SERIAL PRIMARY KEY,
    component_id INTEGER NOT NULL REFERENCES components(id) ON DELETE CASCADE,
    store_id     INTEGER NOT NULL REFERENCES stores(id)     ON DELETE CASCADE,
    price        NUMERIC(12, 2) NOT NULL CHECK (price >= 0),
    product_url  TEXT NOT NULL DEFAULT '',
    in_stock     BOOLEAN NOT NULL DEFAULT TRUE,
    image_url    TEXT NOT NULL DEFAULT '',
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (component_id, store_id)
);

CREATE INDEX idx_prices_component ON component_prices (component_id);
CREATE INDEX idx_prices_store     ON component_prices (store_id);
CREATE TABLE IF NOT EXISTS favorites (
    id           BIGSERIAL PRIMARY KEY,
    component_id INTEGER NOT NULL REFERENCES components(id) ON DELETE CASCADE,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (component_id)
);

ALTER TABLE favorites ENABLE ROW LEVEL SECURITY;
CREATE POLICY "public read favorites"   ON favorites FOR SELECT USING (TRUE);
CREATE POLICY "public write favorites"  ON favorites FOR INSERT WITH CHECK (TRUE);
CREATE POLICY "public delete favorites" ON favorites FOR DELETE USING (TRUE);
WITH s AS (
    SELECT id, code FROM stores
), c AS (
    SELECT id, name FROM components
)
INSERT INTO component_prices (component_id, store_id, price, product_url, in_stock) VALUES
((SELECT id FROM c WHERE name='Intel Core i5-13400F'), (SELECT id FROM s WHERE code='dns'),       17990, 'https://www.dns-shop.ru/search/?q=Intel%20Core%20i5-13400F', TRUE),
((SELECT id FROM c WHERE name='Intel Core i5-13400F'), (SELECT id FROM s WHERE code='citilink'),  18290, 'https://www.citilink.ru/search/?text=Intel%20Core%20i5-13400F', TRUE),
((SELECT id FROM c WHERE name='Intel Core i5-13400F'), (SELECT id FROM s WHERE code='ozon'),      17500, 'https://www.ozon.ru/search/?text=Intel%20Core%20i5-13400F', TRUE),
((SELECT id FROM c WHERE name='Intel Core i7-13700K'), (SELECT id FROM s WHERE code='dns'),       38990, 'https://www.dns-shop.ru/search/?q=Intel%20Core%20i7-13700K', TRUE),
((SELECT id FROM c WHERE name='Intel Core i7-13700K'), (SELECT id FROM s WHERE code='citilink'),  39490, 'https://www.citilink.ru/search/?text=Intel%20Core%20i7-13700K', TRUE),
((SELECT id FROM c WHERE name='Intel Core i7-13700K'), (SELECT id FROM s WHERE code='ozon'),      38200, 'https://www.ozon.ru/search/?text=Intel%20Core%20i7-13700K', TRUE),
((SELECT id FROM c WHERE name='AMD Ryzen 5 7600'),     (SELECT id FROM s WHERE code='dns'),       19990, 'https://www.dns-shop.ru/search/?q=AMD%20Ryzen%205%207600', TRUE),
((SELECT id FROM c WHERE name='AMD Ryzen 5 7600'),     (SELECT id FROM s WHERE code='citilink'),  20490, 'https://www.citilink.ru/search/?text=AMD%20Ryzen%205%207600', TRUE),
((SELECT id FROM c WHERE name='AMD Ryzen 7 7800X3D'),  (SELECT id FROM s WHERE code='dns'),       42990, 'https://www.dns-shop.ru/search/?q=AMD%20Ryzen%207%207800X3D', TRUE),
((SELECT id FROM c WHERE name='AMD Ryzen 7 7800X3D'),  (SELECT id FROM s WHERE code='ozon'),      41500, 'https://www.ozon.ru/search/?text=AMD%20Ryzen%207%207800X3D', TRUE),
((SELECT id FROM c WHERE name='ASUS PRIME B760M-A'),         (SELECT id FROM s WHERE code='dns'),      13990, 'https://www.dns-shop.ru/search/?q=ASUS%20PRIME%20B760M-A', TRUE),
((SELECT id FROM c WHERE name='ASUS PRIME B760M-A'),         (SELECT id FROM s WHERE code='citilink'), 14290, 'https://www.citilink.ru/search/?text=ASUS%20PRIME%20B760M-A', TRUE),
((SELECT id FROM c WHERE name='MSI MAG B650 TOMAHAWK'),      (SELECT id FROM s WHERE code='dns'),      18990, 'https://www.dns-shop.ru/search/?q=MSI%20MAG%20B650%20TOMAHAWK', TRUE),
((SELECT id FROM c WHERE name='MSI MAG B650 TOMAHAWK'),      (SELECT id FROM s WHERE code='ozon'),     18500, 'https://www.ozon.ru/search/?text=MSI%20MAG%20B650%20TOMAHAWK', TRUE),
((SELECT id FROM c WHERE name='Gigabyte Z790 AORUS ELITE'),  (SELECT id FROM s WHERE code='dns'),      27990, 'https://www.dns-shop.ru/search/?q=Gigabyte%20Z790%20AORUS%20ELITE', TRUE),
((SELECT id FROM c WHERE name='Gigabyte Z790 AORUS ELITE'),  (SELECT id FROM s WHERE code='citilink'), 28490, 'https://www.citilink.ru/search/?text=Gigabyte%20Z790%20AORUS%20ELITE', TRUE),
((SELECT id FROM c WHERE name='NVIDIA GeForce RTX 4060'),       (SELECT id FROM s WHERE code='dns'),      32990, 'https://www.dns-shop.ru/search/?q=RTX%204060', TRUE),
((SELECT id FROM c WHERE name='NVIDIA GeForce RTX 4060'),       (SELECT id FROM s WHERE code='citilink'), 33490, 'https://www.citilink.ru/search/?text=RTX%204060', TRUE),
((SELECT id FROM c WHERE name='NVIDIA GeForce RTX 4060'),       (SELECT id FROM s WHERE code='ozon'),     32200, 'https://www.ozon.ru/search/?text=RTX%204060', TRUE),
((SELECT id FROM c WHERE name='NVIDIA GeForce RTX 4070 Super'), (SELECT id FROM s WHERE code='dns'),      59990, 'https://www.dns-shop.ru/search/?q=RTX%204070%20Super', TRUE),
((SELECT id FROM c WHERE name='NVIDIA GeForce RTX 4070 Super'), (SELECT id FROM s WHERE code='citilink'), 60490, 'https://www.citilink.ru/search/?text=RTX%204070%20Super', TRUE),
((SELECT id FROM c WHERE name='NVIDIA GeForce RTX 4090'),       (SELECT id FROM s WHERE code='dns'),     179990, 'https://www.dns-shop.ru/search/?q=RTX%204090', TRUE),
((SELECT id FROM c WHERE name='NVIDIA GeForce RTX 4090'),       (SELECT id FROM s WHERE code='ozon'),    178500, 'https://www.ozon.ru/search/?text=RTX%204090', TRUE),
((SELECT id FROM c WHERE name='AMD Radeon RX 7800 XT'),         (SELECT id FROM s WHERE code='dns'),      54990, 'https://www.dns-shop.ru/search/?q=Radeon%20RX%207800%20XT', TRUE),
((SELECT id FROM c WHERE name='AMD Radeon RX 7800 XT'),         (SELECT id FROM s WHERE code='citilink'), 55490, 'https://www.citilink.ru/search/?text=Radeon%20RX%207800%20XT', TRUE),
((SELECT id FROM c WHERE name='Kingston FURY Beast 32GB DDR5-5600'), (SELECT id FROM s WHERE code='dns'),       9990, 'https://www.dns-shop.ru/search/?q=Kingston%20FURY%20Beast%2032GB%20DDR5', TRUE),
((SELECT id FROM c WHERE name='Kingston FURY Beast 32GB DDR5-5600'), (SELECT id FROM s WHERE code='citilink'), 10290, 'https://www.citilink.ru/search/?text=Kingston%20FURY%20Beast%2032GB%20DDR5', TRUE),
((SELECT id FROM c WHERE name='Corsair Vengeance 32GB DDR5-6000'),   (SELECT id FROM s WHERE code='dns'),      11990, 'https://www.dns-shop.ru/search/?q=Corsair%20Vengeance%2032GB%20DDR5', TRUE),
((SELECT id FROM c WHERE name='Corsair Vengeance 32GB DDR5-6000'),   (SELECT id FROM s WHERE code='ozon'),     11500, 'https://www.ozon.ru/search/?text=Corsair%20Vengeance%2032GB%20DDR5', TRUE),
((SELECT id FROM c WHERE name='G.Skill Trident Z5 64GB DDR5-6400'),  (SELECT id FROM s WHERE code='dns'),      24990, 'https://www.dns-shop.ru/search/?q=G.Skill%20Trident%20Z5%2064GB', TRUE),
((SELECT id FROM c WHERE name='G.Skill Trident Z5 64GB DDR5-6400'),  (SELECT id FROM s WHERE code='citilink'), 25490, 'https://www.citilink.ru/search/?text=G.Skill%20Trident%20Z5%2064GB', TRUE),
((SELECT id FROM c WHERE name='Samsung 990 PRO 1TB'),  (SELECT id FROM s WHERE code='dns'),      11990, 'https://www.dns-shop.ru/search/?q=Samsung%20990%20PRO%201TB', TRUE),
((SELECT id FROM c WHERE name='Samsung 990 PRO 1TB'),  (SELECT id FROM s WHERE code='citilink'), 12290, 'https://www.citilink.ru/search/?text=Samsung%20990%20PRO%201TB', TRUE),
((SELECT id FROM c WHERE name='Samsung 990 PRO 1TB'),  (SELECT id FROM s WHERE code='ozon'),     11500, 'https://www.ozon.ru/search/?text=Samsung%20990%20PRO%201TB', TRUE),
((SELECT id FROM c WHERE name='WD Black SN850X 2TB'),  (SELECT id FROM s WHERE code='dns'),      19990, 'https://www.dns-shop.ru/search/?q=WD%20Black%20SN850X%202TB', TRUE),
((SELECT id FROM c WHERE name='WD Black SN850X 2TB'),  (SELECT id FROM s WHERE code='ozon'),     19200, 'https://www.ozon.ru/search/?text=WD%20Black%20SN850X%202TB', TRUE),
((SELECT id FROM c WHERE name='Kingston KC3000 1TB'),  (SELECT id FROM s WHERE code='dns'),       9990, 'https://www.dns-shop.ru/search/?q=Kingston%20KC3000%201TB', TRUE),
((SELECT id FROM c WHERE name='Kingston KC3000 1TB'),  (SELECT id FROM s WHERE code='citilink'), 10190, 'https://www.citilink.ru/search/?text=Kingston%20KC3000%201TB', TRUE),
((SELECT id FROM c WHERE name='be quiet! Pure Power 12 M 750W'),  (SELECT id FROM s WHERE code='dns'),       9990, 'https://www.dns-shop.ru/search/?q=be%20quiet%20Pure%20Power%2012%20M%20750W', TRUE),
((SELECT id FROM c WHERE name='be quiet! Pure Power 12 M 750W'),  (SELECT id FROM s WHERE code='citilink'), 10290, 'https://www.citilink.ru/search/?text=be%20quiet%20Pure%20Power%2012%20M%20750W', TRUE),
((SELECT id FROM c WHERE name='Corsair RM850x 850W'),             (SELECT id FROM s WHERE code='dns'),      14990, 'https://www.dns-shop.ru/search/?q=Corsair%20RM850x', TRUE),
((SELECT id FROM c WHERE name='Corsair RM850x 850W'),             (SELECT id FROM s WHERE code='ozon'),     14500, 'https://www.ozon.ru/search/?text=Corsair%20RM850x', TRUE),
((SELECT id FROM c WHERE name='Seasonic Focus GX-1000 1000W'),    (SELECT id FROM s WHERE code='dns'),      19990, 'https://www.dns-shop.ru/search/?q=Seasonic%20Focus%20GX-1000', TRUE),
((SELECT id FROM c WHERE name='Seasonic Focus GX-1000 1000W'),    (SELECT id FROM s WHERE code='citilink'), 20490, 'https://www.citilink.ru/search/?text=Seasonic%20Focus%20GX-1000', TRUE),
((SELECT id FROM c WHERE name='Noctua NH-D15'),              (SELECT id FROM s WHERE code='dns'),       8990, 'https://www.dns-shop.ru/search/?q=Noctua%20NH-D15', TRUE),
((SELECT id FROM c WHERE name='Noctua NH-D15'),              (SELECT id FROM s WHERE code='citilink'),  9290, 'https://www.citilink.ru/search/?text=Noctua%20NH-D15', TRUE),
((SELECT id FROM c WHERE name='Noctua NH-D15'),              (SELECT id FROM s WHERE code='ozon'),      8700, 'https://www.ozon.ru/search/?text=Noctua%20NH-D15', TRUE),
((SELECT id FROM c WHERE name='be quiet! Dark Rock Pro 5'),  (SELECT id FROM s WHERE code='dns'),       7490, 'https://www.dns-shop.ru/search/?q=be%20quiet%20Dark%20Rock%20Pro%205', TRUE),
((SELECT id FROM c WHERE name='be quiet! Dark Rock Pro 5'),  (SELECT id FROM s WHERE code='ozon'),      7200, 'https://www.ozon.ru/search/?text=be%20quiet%20Dark%20Rock%20Pro%205', TRUE),
((SELECT id FROM c WHERE name='DeepCool AK620'),             (SELECT id FROM s WHERE code='dns'),       4490, 'https://www.dns-shop.ru/search/?q=DeepCool%20AK620', TRUE),
((SELECT id FROM c WHERE name='DeepCool AK620'),             (SELECT id FROM s WHERE code='citilink'),  4690, 'https://www.citilink.ru/search/?text=DeepCool%20AK620', TRUE),
((SELECT id FROM c WHERE name='ID-COOLING SE-226-XT'),       (SELECT id FROM s WHERE code='dns'),       2490, 'https://www.dns-shop.ru/search/?q=ID-COOLING%20SE-226-XT', TRUE),
((SELECT id FROM c WHERE name='ID-COOLING SE-226-XT'),       (SELECT id FROM s WHERE code='citilink'),  2590, 'https://www.citilink.ru/search/?text=ID-COOLING%20SE-226-XT', TRUE),
((SELECT id FROM c WHERE name='ID-COOLING SE-226-XT'),       (SELECT id FROM s WHERE code='ozon'),      2350, 'https://www.ozon.ru/search/?text=ID-COOLING%20SE-226-XT', TRUE);
ALTER TABLE stores            ENABLE ROW LEVEL SECURITY;
ALTER TABLE components        ENABLE ROW LEVEL SECURITY;
ALTER TABLE component_prices  ENABLE ROW LEVEL SECURITY;

CREATE POLICY "public read stores"     ON stores           FOR SELECT USING (TRUE);
CREATE POLICY "public read components" ON components       FOR SELECT USING (TRUE);
CREATE POLICY "public read prices"     ON component_prices FOR SELECT USING (TRUE);
CREATE POLICY "public write prices"    ON component_prices FOR INSERT WITH CHECK (TRUE);
CREATE POLICY "public update prices"   ON component_prices FOR UPDATE USING (TRUE) WITH CHECK (TRUE);
.
