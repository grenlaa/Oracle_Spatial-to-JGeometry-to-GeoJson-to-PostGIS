# JGeometry(from Oracle geometry) to PostGIS(GeoJSON)


Oracle Spatial to JGeometry to GeoJSON to PostGIS

Для конвертации JGeometry в GeoJSON берите функию convert из convert_to_GeoJSON(JGeometry)

Обновлено до 3 версии. Из за проблем с отображением в QGis решено добавить приведение к типу 
* Теперь можно конвертировать в различные типы геометрии (использовать с осторожностью) convert_to_GeoJSON(JGeometry,geom_type) *


Если интересо что же там внутри JGeometry:
В файле JGeometry.java расположена неполная копия JGeometry из пакета sdoapi 
P.S. В данном файле распологаются только необходимые функции и конструкторы для класса JGeometry(по моему мнению).